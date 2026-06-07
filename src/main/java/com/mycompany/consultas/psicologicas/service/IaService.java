package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;

import java.util.List;
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
        factory.setReadTimeout(10000);

        this.restTemplate = new RestTemplate(factory);
    }

    // =========================================
    // IA PRINCIPAL
    // =========================================
public String resumirObservacion(String texto) {

    try {

        System.out.println("========== RESUMIR OBSERVACION ==========");

        if (texto == null || texto.isBlank()) {

            return "Sin observaciones clínicas.";
        }

        // =====================================
        // LIMPIAR INPUT
        // =====================================

        texto = texto
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();

        // =====================================
        // PROMPT
        // =====================================

       String prompt = """
Resumí clínicamente el siguiente texto.

Reglas:
- máximo 6 palabras
- usar terminología psicológica real
- no inventar diagnósticos
- no convertir síntomas en diagnósticos formales
- no agregar información
- responder solo el resumen

Texto:
%s
""".formatted(texto);
       
       
        // =====================================
        // HEADERS
        // =====================================

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(openrouterApiKey);

        headers.setContentType(MediaType.APPLICATION_JSON);

        // =====================================
        // BODY OPENROUTER
        // =====================================

        Map<String, Object> body = Map.of(

            "model", "deepseek/deepseek-chat",

            "messages", List.of(

                Map.of(
                    "role", "user",
                    "content", prompt
                )
            ),

            "temperature", 0,

            "max_tokens", 20
        );

        // =====================================
        // REQUEST
        // =====================================

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        // =====================================
        // URL OPENROUTER
        // =====================================

        String url =
                "https://openrouter.ai/api/v1/chat/completions";

        System.out.println("ANTES DE OPENROUTER");

        // =====================================
        // REQUEST IA
        // =====================================

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        Map.class
                );

        System.out.println("DESPUES DE OPENROUTER");

        // =====================================
        // VALIDAR BODY
        // =====================================

        if (response.getBody() == null) {

            throw new RuntimeException(
                    "Respuesta IA vacía"
            );
        }

        // =====================================
        // EXTRAER RESPUESTA
        // =====================================

        Object choicesObj =
                response.getBody().get("choices");

        if (choicesObj == null) {

            throw new RuntimeException(
                    "IA sin choices"
            );
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) choicesObj;

        if (choices.isEmpty()) {

            throw new RuntimeException(
                    "IA vacía"
            );
        }

        Map<String, Object> choice =
                choices.get(0);

        Map<String, Object> message =
                (Map<String, Object>) choice.get("message");

        if (message == null) {

            throw new RuntimeException(
                    "IA sin message"
            );
        }

        Object contentObj =
                message.get("content");

        if (contentObj == null) {

            throw new RuntimeException(
                    "IA sin content"
            );
        }

        String resumen =
                contentObj.toString().trim();

        System.out.println("RESPUESTA CRUDA OPENROUTER:");
        System.out.println(resumen);

        // =====================================
        // LIMPIEZA
        // =====================================

        resumen = resumen
                .replace("\"", "")
                .replace("*", "")
                .replace("-", "")
                .replace("Resumen:", "")
                .replace("Resumen clínico:", "")
                .trim();

        // =====================================
        // PRIMERA LINEA
        // =====================================

        if (resumen.contains("\n")) {

            resumen =
                    resumen.split("\n")[0]
                            .trim();
        }

        // =====================================
        // VALIDAR RESPUESTAS MALAS
        // =====================================

        String resumenLower =
                resumen.toLowerCase();

        if (resumen.length() < 4) {

            resumen =
                    "Resumen clínico no disponible.";
        }

        if (

            resumenLower.contains("artículo") ||
            resumenLower.contains("investigación") ||
            resumenLower.contains("tratamiento") ||
            resumenLower.contains("terapia") ||
            resumenLower.contains("ayuda profesional")

        ) {

            resumen =
                    "Resumen clínico no disponible.";
        }

        // =====================================
        // EVITAR COPIA EXACTA
        // =====================================

        String textoLower = texto.toLowerCase();

        if (

            !resumenLower.isBlank()

            &&

            resumenLower.equals(textoLower)

        ) {

            resumen =
                    "Síntesis clínica no concluyente.";
        }

        // =====================================
        // LIMITAR PALABRAS
        // =====================================

        if (resumen.split(" ").length > 10) {

            resumen = String.join(
                    " ",
                    resumen.split(" ", 10)
            );
        }

        // =====================================
        // PUNTO FINAL
        // =====================================

        if (!resumen.endsWith(".")) {

            resumen += ".";
        }

        // =====================================
        // VALIDAR VACIO
        // =====================================

        if (resumen.isBlank()) {

            resumen =
                    "Resumen clínico no disponible.";
        }

        System.out.println("RESUMEN IA FINAL:");
        System.out.println(resumen);

        return resumen;

    } catch (Exception e) {

        System.out.println("========== ERROR resumirObservacion ==========");

        e.printStackTrace();

        return "Resumen clínico no disponible.";
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

public String generarHistoriaClinica(
        List<Visita> visitas
) {

    try {

        if (
                visitas == null ||
                visitas.isEmpty()
        ) {

            return "Sin registros clínicos.";
        }

        // =====================================
        // CONCATENAR OBSERVACIONES
        // =====================================

        String texto = visitas.stream()

                .map(Visita::getObservacion)

                .filter(v ->
                        v != null &&
                        !v.isBlank()
                )

                .map(v ->

                        v.replaceAll("<[^>]*>", " ")
                         .replace("&nbsp;", " ")
                         .replaceAll("\\s+", " ")
                         .trim()
                )

                .reduce(
                        "",
                        (a, b) -> a + "\n" + b
                );

        if (texto.isBlank()) {

            return "Sin contenido clínico.";
        }

        // =====================================
        // PROMPT
        // =====================================

        String prompt = """
Generá una historia clínica psicológica profesional
basada EXCLUSIVAMENTE en las siguientes visitas.

Reglas:
- no inventar diagnósticos
- no agregar medicación
- usar lenguaje clínico profesional
- resumir evolución emocional y conductual
- detectar patrones relevantes
- máximo 250 palabras
- redactar como informe clínico
- no usar viñetas

Visitas:
%s
""".formatted(texto);

        // =====================================
        // HEADERS
        // =====================================

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(openrouterApiKey);

        // =====================================
        // BODY OPENROUTER
        // =====================================

        Map<String, Object> body = Map.of(

                "model", "deepseek/deepseek-chat",

                "messages", List.of(

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),

                "temperature", 0.3,

                "max_tokens", 300
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        // =====================================
        // REQUEST
        // =====================================
        ResponseEntity<Map> response =
        restTemplate.postForEntity(
                "https://openrouter.ai/api/v1/chat/completions",
                request,
                Map.class
        );

        if (response.getBody() == null) {

            return "No se pudo generar historia clínica.";
        }

        // =====================================
        // EXTRAER RESPUESTA
        // =====================================

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>)
                        response.getBody().get("choices");

        if (
                choices == null ||
                choices.isEmpty()
        ) {

            return "Sin respuesta IA.";
        }

        Map<String, Object> message =
                (Map<String, Object>)
                        choices.get(0).get("message");

        String resumen =
                message.get("content")
                        .toString()
                        .trim();

        // =====================================
        // LIMPIEZA
        // =====================================

        resumen = resumen

                .replace("*", "")

                .replace("#", "")

                .replaceAll("\\s+", " ")

                .trim();

        if (!resumen.endsWith(".")) {

            resumen += ".";
        }

        return resumen;

    } catch (Exception e) {

        e.printStackTrace();

        return "No se pudo generar historia clínica.";
    }
}

}