package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.model.*;
import com.mycompany.consultas.psicologicas.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository visitaRepo;

    @Autowired
    private HistoriaClinicaRepository historiaRepo;

    @Autowired
    private TurnoRepository turnoRepo;

    @Autowired
    private IaService iaService;

    // =========================
    // LISTAR
    // =========================

    public List<Visita> listar() {

        return visitaRepo.findAllConPaciente();
    }

    // =========================
    // CREAR
    // =========================

    public Visita crear(Visita visita) {

        // =========================
        // VALIDAR TURNO
        // =========================

        if (
                visita.getTurno() == null ||
                visita.getTurno().getId() == null
        ) {

            throw new RuntimeException(
                    "Debe seleccionar un turno"
            );
        }
        
        

        Turno turno = turnoRepo.findById(
                visita.getTurno().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Turno inexistente"
                )
        );

        // =========================
        // VALIDAR ESTADO
        // =========================

        if (turno.getEstado() != EstadoTurno.AGENDADO) {

            throw new RuntimeException(
                    "Solo se puede crear visita desde turnos AGENDADOS"
            );
        }

        // =========================
        // EVITAR DUPLICADOS
        // =========================

        if (visitaRepo.existsByTurnoId(turno.getId())) {

            throw new RuntimeException(
                    "Ese turno ya tiene visita"
            );
        }

        // =========================
        // BUSCAR HISTORIA CLINICA
        // =========================

        HistoriaClinica historia = historiaRepo
                .findByPacienteId(
                        turno.getPaciente().getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paciente sin historia clínica"
                        )
                );

        // =========================
        // FECHA DESDE EL TURNO
        // =========================

        visita.setFecha(
                turno.getFechaHora().toLocalDate()
        );

        // =========================
        // RELACIONES
        // =========================

        visita.setTurno(turno);

        visita.setHistoria(historia);

        // =========================
        // ESTADO IA INICIAL
        // =========================

            visita.setEstadoIa("NO_SOLICITADO");
            visita.setResumenIa(null);

        // =========================
        // CAMBIAR ESTADO TURNO
        // =========================

        turno.setEstado(
                EstadoTurno.ASISTIO
        );

        turnoRepo.save(turno);

        // =========================
        // GUARDAR VISITA
        // =========================

            // El tipo se determina siempre desde las visitas ya registradas.
            // Nunca confiar en el tipo enviado por el navegador.
            boolean tieneVisitasPrevias = visitaRepo.existsByHistoriaId(historia.getId());
            visita.setTipoVisita(tieneVisitasPrevias
                    ? TipoVisita.SEGUIMIENTO
                    : TipoVisita.PRIMERA_ENTREVISTA);

        // El origen de la organización es independiente del resumen opcional.
        boolean primera = visita.getTipoVisita() == TipoVisita.PRIMERA_ENTREVISTA;
        boolean organizada = primera && Boolean.TRUE.equals(visita.getOrganizadaConIa());
        validarContenidoPrimeraEntrevista(visita.getObservacion(), primera);
        visita.setOrganizadaConIa(organizada);

        Visita guardada = visitaRepo.save(visita);

        return guardada;

    }
// =========================
// EDITAR
// =========================

public Visita editar(Long id, Visita datos) {

    // =====================================
    // BUSCAR VISITA EXISTENTE
    // =====================================

    Visita visita = visitaRepo.findCompletaById(id)
            .orElseThrow(() ->
                    new RuntimeException("Visita inexistente")
            );

    // =====================================
    // VALIDAR OBSERVACION
    // =====================================

    if (datos.getObservacion() == null ||
        datos.getObservacion().trim().isEmpty()) {

        throw new RuntimeException(
                "La observación es obligatoria"
        );
    }

    validarContenidoPrimeraEntrevista(datos.getObservacion(),
            visita.getTipoVisita() == TipoVisita.PRIMERA_ENTREVISTA);

    // Mantener el origen de la entrevista, sin aceptar que una edición lo borre.
    if (visita.getTipoVisita() == TipoVisita.PRIMERA_ENTREVISTA
            && Boolean.TRUE.equals(datos.getOrganizadaConIa())) {
        visita.setOrganizadaConIa(true);
    }

    // =====================================
    // DETECTAR CAMBIO EN LA OBSERVACION
    // =====================================

    boolean observacionCambio =
            !datos.getObservacion().trim().equals(
                    visita.getObservacion() != null
                            ? visita.getObservacion().trim()
                            : ""
            );

    // =====================================
    // ACTUALIZAR OBSERVACION
    // =====================================

    visita.setObservacion(datos.getObservacion());

    // =====================================
    // MANTENER FECHA DEL TURNO
    // =====================================

    if (visita.getTurno() != null &&
        visita.getTurno().getFechaHora() != null) {

        visita.setFecha(
                visita.getTurno()
                        .getFechaHora()
                        .toLocalDate()
        );
    }

    // =====================================
    // RESUMEN IA OPCIONAL
    // =====================================

    // Si cambió la observación, el resumen anterior
    // deja de corresponder al contenido actualizado.

    if (observacionCambio) {

        visita.setResumenIa(null);
        visita.setEstadoIa("NO_SOLICITADO");
    }

    // =====================================
    // GUARDAR VISITA SIN GENERAR IA
    // =====================================

    return visitaRepo.save(visita);
}
    // Rechazar plantillas vacías: la plantilla contiene numerosas preguntas
    // en cursiva, pero ninguna respuesta clínica. Se admiten ediciones manuales.
    private void validarContenidoPrimeraEntrevista(String html, boolean primera) {
        if (!primera) return;
        if (html == null || html.isBlank()) {
            throw new IllegalArgumentException("La primera entrevista no puede estar vacía");
        }
        boolean contienePlantilla = html.contains("ENTREVISTA CLÍNICA INICIAL")
                && html.contains("Datos de identificación")
                && html.contains("Seguridad y riesgo");
        boolean respuestaIa = html.matches("(?s).*class=[\"']respuesta-entrevista[\"'][^>]*>\\s*[^<\\s].*");
        boolean preguntasSinRespuestas = contienePlantilla
                && !respuestaIa
                && html.split("<em>", -1).length > 10;
        if (preguntasSinRespuestas) {
            throw new IllegalArgumentException(
                    "La primera entrevista parece contener solo la plantilla. Completá las respuestas antes de guardar.");
        }
    }

    // =========================
    // ELIMINAR
    // =========================

   public void eliminar(Long id) {

    Visita visita = visitaRepo.findCompletaById(id)
            .orElseThrow(() ->
                    new RuntimeException("Visita inexistente")
            );

    // =========================
    // GUARDAR REFERENCIA TURNO
    // =========================

    Turno turno = visita.getTurno();

    // =========================
    // ELIMINAR VISITA
    // =========================

    visitaRepo.delete(visita);

    // =========================
    // RESTAURAR TURNO A AGENDADO
    // PARA CUALQUIER TIPO DE VISITA
    // =========================

    if (turno != null) {

        turno.setEstado(
                EstadoTurno.AGENDADO
        );

        turnoRepo.save(turno);
    }
}
    // =========================
    // BUSCAR
    // =========================

    public List<Visita> buscarPorPaciente(
            String nombre
    ) {

        return visitaRepo.buscarPorPaciente(
                nombre
        );
    }

    public List<Visita> buscarPorFecha(
            String fecha
    ) {

        return visitaRepo.buscarPorFecha(
                LocalDate.parse(fecha)
        );
    }
    public byte[] generarPdf(Long id) {
        Visita visita = visitaRepo.findCompletaById(id)
                .orElseThrow(() -> new RuntimeException("Visita inexistente"));
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document documento = PdfClinicoFormato.documento();
            PdfWriter.getInstance(documento, salida);
            documento.open();
            String paciente = visita.getHistoria().getPaciente().getNombre() + " "
                    + visita.getHistoria().getPaciente().getApellido();
            String tipo = visita.getTipoVisita() == TipoVisita.PRIMERA_ENTREVISTA
                    ? "Primera entrevista clínica" : "Visita de seguimiento";
            PdfClinicoFormato.cabecera(documento, tipo, paciente,
                    "Fecha de visita: " + (visita.getFecha() == null ? "Sin fecha" : visita.getFecha().toString()));
            PdfClinicoFormato.seccion(documento, "Registro de la entrevista");
            PdfClinicoFormato.contenido(documento, visita.getObservacion());
            if (visita.getResumenIa() != null && !visita.getResumenIa().isBlank()) {
                PdfClinicoFormato.seccion(documento, "Resumen IA");
                PdfClinicoFormato.contenido(documento, visita.getResumenIa());
            }
            documento.close();
            return salida.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generando PDF de visita", e);
        }
    }

public boolean esPrimeraVisita(Long pacienteId) {

    HistoriaClinica historia = historiaRepo
            .findByPacienteId(pacienteId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Historia clínica inexistente"
                    )
            );

    return !visitaRepo.existsByHistoriaId(historia.getId());
}
    
}