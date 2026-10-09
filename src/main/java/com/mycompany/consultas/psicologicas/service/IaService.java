package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;

import java.util.List;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IaService {

@Value("${openrouter.api.key}")
private String openrouterApiKey;

@Value("${groq.api.key}")
private String groqApiKey;

@Value("${ia.historia-clinica.modelos:openrouter/free}")
private String modelosHistoriaClinica;

@Value("${ia.primera-entrevista.modelos:openrouter/free}")
private String modelosPrimeraEntrevista;

@Value("${ia.primera-entrevista.timeout:60000}")
private int timeoutPrimeraEntrevista;




    // =========================================
    // REST TEMPLATE
    // =========================================

    private final RestTemplate restTemplate;

    // =========================================
    // WEBSOCKET
    // =========================================

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // =========================================
    // REPOSITORY
    // =========================================

    @Autowired
    private VisitaRepository visitaRepository;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public IaService() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        // conexión
        factory.setConnectTimeout(5000);

        // lectura IA
        factory.setReadTimeout(30000);

        this.restTemplate = new RestTemplate(factory);
    }

    // =========================================
    // IA PRINCIPAL
    // =========================================
public String resumirObservacion(String texto) {
    if (texto == null || texto.isBlank()) {
        throw new IllegalArgumentException("No hay observaciones para resumir.");
    }

    if (groqApiKey == null || groqApiKey.isBlank()) {
        throw new IllegalStateException("Falta configurar GROQ_API_KEY.");
    }

    // No registrar el contenido clínico en los logs.
    String textoLimpio = texto.replaceAll("<[^>]*>", " ")
            .replace("&nbsp;", " ")
            .replaceAll("\\s+", " ").trim();

    String prompt = """
            Resumí la siguiente observación psicológica en una frase clínica breve.
            Reglas:
            - Máximo 6 palabras, cuando sea posible.
            - No inventar diagnósticos, síntomas ni hechos.
            - No convertir síntomas en diagnósticos.
            - No añadir recomendaciones ni explicaciones.
            - Responder solamente con el resumen.

            Observación:
            %s
            """.formatted(textoLimpio);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(groqApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = Map.of(
            "model", "openai/gpt-oss-20b",
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "temperature", 0,
            "max_completion_tokens", 300,
            "reasoning_effort", "low"
    );

    try {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                new HttpEntity<>(body, headers), Map.class);

        if (response.getBody() == null) {
            throw new IllegalStateException("Groq devolvió una respuesta vacía.");
        }
        Object choicesObj = response.getBody().get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()
                || !(choices.get(0) instanceof Map<?, ?> choice)
                || !(choice.get("message") instanceof Map<?, ?> message)) {
            throw new IllegalStateException("Groq devolvió una estructura inesperada.");
        }

        Object contentObj = message.get("content");
        if (!(contentObj instanceof String contenido) || contenido.isBlank()) {
            System.err.println("Groq devolvió contenido vacío. finish_reason="
                    + choice.get("finish_reason"));
            throw new IllegalStateException("Groq no produjo un resumen visible.");
        }

        String resumen = contenido.trim()
                .replaceAll("(?i)^resumen(?: clínico)?:\\s*", "")
                .replace("*", "")
                .replace("\"", "")
                .trim();

        if (resumen.contains("\n")) {
            resumen = resumen.split("\n", 2)[0].trim();
        }
        if (resumen.isBlank()) {
            throw new IllegalStateException("Groq devolvió un resumen vacío.");
        }
        if (resumen.equalsIgnoreCase(textoLimpio)) {
            throw new IllegalStateException("Groq no sintetizó la observación.");
        }
        if (!resumen.endsWith(".")) {
            resumen += ".";
        }
        return resumen;
    } catch (org.springframework.web.client.HttpStatusCodeException e) {
        // No imprimir la respuesta completa: podría contener información sensible.
        System.err.println("Error HTTP de Groq: " + e.getStatusCode());
        throw new IllegalStateException("Groq no pudo generar el resumen (HTTP "
                + e.getStatusCode().value() + ").", e);
    }
}

    // =========================================
    // GENERACION ASINCRONICA
    // =========================================

    @Async
    public void resumirObservacionAsync(
            Long visitaId,
            String texto
    ) {

        System.out.println("========== IA INICIADA ==========");

        try {

            Visita visita =
                    visitaRepository.findById(visitaId)
                            .orElse(null);

            if (visita == null) {

                return;
            }

            visita.setEstadoIa("PROCESANDO");

            visitaRepository.save(visita);

            String resumen =
                    resumirObservacion(texto);

            if (resumen == null || resumen.isBlank()) {

                resumen =
                        "No se pudo generar resumen IA.";
            }

            visita.setResumenIa(resumen);

            visita.setEstadoIa("OK");

            visitaRepository.save(visita);

            messagingTemplate.convertAndSend(
                    "/topic/visitas",
                    visitaId
            );

            System.out.println("========== IA FINALIZADA ==========");

        } catch (Exception e) {

            System.out.println("========== ERROR IA ==========");

            e.printStackTrace();

            try {

                Visita visita =
                        visitaRepository.findById(visitaId)
                                .orElse(null);

                if (visita != null) {

                    visita.setEstadoIa("OK");

                    visita.setResumenIa(
                            "Resumen clínico no disponible."
                    );

                    visitaRepository.save(visita);
                }

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }

public String generarHistoriaClinica(List<Visita> visitas) {
    if (visitas == null || visitas.isEmpty()) {
        throw new IllegalStateException("No hay visitas clínicas registradas.");
    }

    String texto = visitas.stream()
            .map(Visita::getObservacion)
            .filter(v -> v != null && !v.isBlank())
            .map(v -> v.replaceAll("<[^>]*>", " ")
                    .replace("&nbsp;", " ")
                    .replaceAll("\\s+", " ").trim())
            .reduce("", (a, b) -> a + "\n" + b).trim();

    if (texto.isBlank()) {
        throw new IllegalStateException("Las visitas no contienen observaciones clínicas.");
    }

    String prompt = """
            Elaborá un BORRADOR de historia clínica psicológica, basado EXCLUSIVAMENTE
            en los registros de visitas que siguen.
            - No inventes hechos, diagnósticos, medicación ni evaluaciones de riesgo.
            - No atribuyas al paciente datos que no estén explícitos.
            - Si falta información, no la completes por inferencia.
            - Usá lenguaje clínico profesional, sin viñetas.
            - Resumí la evolución y los aspectos relevantes; máximo 250 palabras.
            - Respondé solamente con el borrador, para revisión del profesional.

            Visitas:
            %s
            """.formatted(texto);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openrouterApiKey);

    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(10000);
    factory.setReadTimeout(timeoutPrimeraEntrevista);
    RestTemplate clienteHistoria = new RestTemplate(factory);

    Exception ultimoError = null;
    for (String modelo : modelosHistoriaClinica.split(",")) {
        modelo = modelo.trim();
        if (modelo.isEmpty()) continue;
        try {
            Map<String, Object> body = Map.of(
                    "model", modelo,
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0.2,
                    "max_tokens", 1200
            );
            ResponseEntity<Map> response = clienteHistoria.postForEntity(
                    "https://openrouter.ai/api/v1/chat/completions",
                    new HttpEntity<>(body, headers), Map.class);

            if (response.getBody() == null) {
                throw new IllegalStateException("Respuesta vacía del proveedor");
            }
            Object choicesObj = response.getBody().get("choices");
            if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()
                    || !(choices.get(0) instanceof Map<?, ?> choice)
                    || !(choice.get("message") instanceof Map<?, ?> message)
                    || !(message.get("content") instanceof String contenido)
                    || contenido.isBlank()) {
                throw new IllegalStateException("Respuesta sin texto utilizable");
            }
            String borrador = contenido.trim();
            if (borrador.isBlank()) {
                throw new IllegalStateException("Borrador vacío");
            }
            return borrador;
        } catch (Exception e) {
            ultimoError = e;
            // No registrar respuestas del proveedor ni contenido clínico.
            String codigo = e instanceof org.springframework.web.client.HttpStatusCodeException httpError
                    ? " HTTP " + httpError.getStatusCode().value() : "";
            System.err.println("Falló borrador de historia clínica con modelo " + modelo
                    + ": " + e.getClass().getSimpleName() + codigo);
        }
    }
    throw new IllegalStateException(
            "No se pudo generar la historia clínica con IA. Se conservó el contenido anterior.",
            ultimoError);
}
// =========================================
// ORGANIZAR PRIMERA ENTREVISTA
// =========================================

public Map<String, String> organizarPrimeraEntrevista(
        String texto
) {

    try {

        System.out.println(
                "========== ORGANIZAR PRIMERA ENTREVISTA =========="
        );

        if (texto == null || texto.isBlank()) {

            return Map.of();
        }

        texto = texto
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();

        // =====================================
        // PROMPT
        // =====================================

        String prompt = """
Organizá la siguiente transcripción de una primera entrevista psicológica
dentro de los campos indicados.

IMPORTANTE:
- Trabajá EXCLUSIVAMENTE con la información explícitamente presente en la transcripción.
- No inventes información.
- No realices diagnósticos.
- No realices interpretaciones clínicas.
- No supongas información que no fue mencionada.
- No completes campos por inferencia.
- Si un dato no fue mencionado, devolver "".
- En SEGURIDAD Y RIESGO solamente colocar información explícitamente mencionada.
- Conservá el sentido de lo expresado por la persona.
- Podés corregir errores propios de la transcripción automática.
- No agregues explicaciones.
- No agregues campos nuevos.
- Respondé ÚNICAMENTE con un objeto JSON válido.
- Todos los valores deben ser cadenas de texto.
- No utilices markdown.
- No utilices ```json.
- No escribas texto antes ni después del JSON.

CAMPOS:

nombre_completo
edad
fecha_nacimiento
dni
direccion_actual
telefono
contacto_emergencia
parentesco
telefono_emergencia
estudios_previos
ocupacion_actual
estado_civil
derivado_por

tratamientos_anteriores
tratamiento_farmacologico_psiquiatrico
medicacion_psicotropica_actual
historial_internaciones_urgencias
balance_tratamientos_anteriores

motivo_principal
hecho_reciente
expectativas

inicio_evolucion
situacion_desencadenante
sintomas
frecuencia_intensidad
momentos_empeoramiento
pensamiento
emocion
conducta
impacto_funcional
estrategias_afrontamiento

antecedentes_familiares
enfermedades_medicas_cronicas
chequeos
medicacion

composicion_familiar_actual
familia_origen
dinamica_relacional_historia_familiar
tiene_pareja
tiempo_pareja
descripcion_relacion_pareja
tiene_hijos
cantidad_hijos
edades_nombres_vinculo_hijos
red_apoyo

horas_sueno
calidad_sueno
alimentacion
consumo_sustancias
actividad_fisica
actividades_recreativas
frecuencia_actividades_recreativas
vida_social

ideacion_pensamientos_dano
capacidad_mantenerse_a_salvo

TRANSCRIPCIÓN:

%s
""".formatted(texto);

        // =====================================
        // HEADERS
        // =====================================

        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(openrouterApiKey);

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        // La lista puede contener modelos separados por comas.
        List<String> modelos = Arrays.stream(modelosPrimeraEntrevista.split(","))
                .map(String::trim)
                .filter(m -> !m.isEmpty())
                .toList();

        if (modelos.isEmpty()) {
            throw new IllegalStateException("No hay modelos de primera entrevista configurados");
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(timeoutPrimeraEntrevista);
        RestTemplate clienteEntrevista = new RestTemplate(factory);

        com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
        Exception ultimoError = null;
        Map<String, String> resultado = null;

        for (String modelo : modelos) {
            try {
                Map<String, Object> body = Map.of(
                        "model", modelo,
                        "messages", List.of(Map.of("role", "user", "content", prompt)),
                        "temperature", 0,
                        "max_tokens", 3500
                );

                ResponseEntity<Map> response = clienteEntrevista.postForEntity(
                        "https://openrouter.ai/api/v1/chat/completions",
                        new HttpEntity<>(body, headers), Map.class);

                if (response.getBody() == null) {
                    throw new IllegalStateException("Respuesta vacía");
                }
                Object choicesObj = response.getBody().get("choices");
                if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()
                        || !(choices.get(0) instanceof Map<?, ?> choice)
                        || !(choice.get("message") instanceof Map<?, ?> message)
                        || !(message.get("content") instanceof String contenido)) {
                    throw new IllegalStateException("Respuesta sin contenido válido");
                }

                // Algunos modelos agregan una introducción (por ejemplo, "Here is...")
                // o cercos Markdown. Extraer un objeto JSON completo sin alterar
                // el contenido de las cadenas, que pueden contener llaves o comillas.
                com.fasterxml.jackson.databind.JsonNode node =
                        extraerObjetoJsonEntrevista(contenido, mapper);
                if (!node.isObject()) {
                    throw new IllegalStateException("La respuesta no es un objeto JSON");
                }

                // Exigir campos conocidos y valores de texto; evitar respuestas vacías
                // que parezcan exitosas. No inventar datos faltantes.
                String[] campos = {
                    "nombre_completo", "edad", "fecha_nacimiento", "dni",
                    "direccion_actual", "telefono", "contacto_emergencia",
                    "parentesco", "telefono_emergencia", "estudios_previos",
                    "ocupacion_actual", "estado_civil", "derivado_por",
                    "tratamientos_anteriores", "tratamiento_farmacologico_psiquiatrico",
                    "medicacion_psicotropica_actual", "historial_internaciones_urgencias",
                    "balance_tratamientos_anteriores", "motivo_principal",
                    "hecho_reciente", "expectativas", "inicio_evolucion",
                    "situacion_desencadenante", "sintomas", "frecuencia_intensidad",
                    "momentos_empeoramiento", "pensamiento", "emocion", "conducta",
                    "impacto_funcional", "estrategias_afrontamiento",
                    "antecedentes_familiares", "enfermedades_medicas_cronicas",
                    "chequeos", "medicacion", "composicion_familiar_actual",
                    "familia_origen", "dinamica_relacional_historia_familiar",
                    "tiene_pareja", "tiempo_pareja", "descripcion_relacion_pareja",
                    "tiene_hijos", "cantidad_hijos", "edades_nombres_vinculo_hijos",
                    "red_apoyo", "horas_sueno", "calidad_sueno", "alimentacion",
                    "consumo_sustancias", "actividad_fisica", "actividades_recreativas",
                    "frecuencia_actividades_recreativas", "vida_social",
                    "ideacion_pensamientos_dano", "capacidad_mantenerse_a_salvo"
                };
                Map<String, String> candidato = new LinkedHashMap<>();
                boolean tieneDatos = false;
                for (String campo : campos) {
                    com.fasterxml.jackson.databind.JsonNode valor = node.get(campo);
                    if (valor != null && !valor.isNull() && !valor.isTextual()) {
                        throw new IllegalStateException("Valor no textual: " + campo);
                    }
                    String dato = valor == null || valor.isNull() ? "" : valor.asText();
                    candidato.put(campo, dato);
                    if (!dato.isBlank()) tieneDatos = true;
                }
                if (!tieneDatos) {
                    throw new IllegalStateException("La IA no clasificó ningún dato");
                }
                resultado = candidato;
                break;
            } catch (Exception error) {
                ultimoError = error;
                System.err.println("Falló modelo de entrevista " + modelo
                        + ": " + error.getClass().getSimpleName());
            }
        }
        if (resultado == null) {
            throw new IllegalStateException(
                    "Ningún modelo pudo organizar la entrevista", ultimoError);
        }

        System.out.println(
                "ENTREVISTA ORGANIZADA CORRECTAMENTE"
        );

        return resultado;

    } catch (Exception e) {

        System.out.println(
                "========== ERROR ORGANIZANDO ENTREVISTA =========="
        );

        e.printStackTrace();

        throw new RuntimeException(
                "No se pudo organizar la primera entrevista",
                e
        );
    }
}

    /**
     * Encuentra un objeto JSON válido dentro de una respuesta con texto adicional.
     * Respeta comillas y escapes para no cortar un JSON que incluya llaves en valores.
     * No registra el contenido clínico en logs.
     */
    private com.fasterxml.jackson.databind.JsonNode extraerObjetoJsonEntrevista(
            String contenido,
            com.fasterxml.jackson.databind.ObjectMapper mapper) throws Exception {

        if (contenido == null || contenido.isBlank()) {
            throw new IllegalStateException("La IA devolvió contenido vacío");
        }

        for (int inicio = 0; inicio < contenido.length(); inicio++) {
            if (contenido.charAt(inicio) != '{') {
                continue;
            }

            int profundidad = 0;
            boolean enCadena = false;
            boolean escapado = false;

            for (int i = inicio; i < contenido.length(); i++) {
                char c = contenido.charAt(i);
                if (enCadena) {
                    if (escapado) {
                        escapado = false;
                    } else if (c == '\\') {
                        escapado = true;
                    } else if (c == '"') {
                        enCadena = false;
                    }
                    continue;
                }
                if (c == '"') {
                    enCadena = true;
                } else if (c == '{') {
                    profundidad++;
                } else if (c == '}') {
                    profundidad--;
                    if (profundidad == 0) {
                        String candidato = contenido.substring(inicio, i + 1);
                        try {
                            com.fasterxml.jackson.databind.JsonNode node =
                                    mapper.readTree(candidato);
                            if (node != null && node.isObject()) {
                                return node;
                            }
                        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
                            // Buscar el siguiente posible objeto sin exponer datos.
                        }
                        break;
                    }
                }
            }
        }
        throw new IllegalStateException(
                "La IA no devolvió un objeto JSON válido y completo");
    }

}