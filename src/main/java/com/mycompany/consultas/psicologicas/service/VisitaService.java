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

        visita.setEstadoIa("PENDIENTE");

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

            if (visita.getTipoVisita() == null) {

        boolean yaExistePrimera =
            visitaRepo.existsByHistoriaIdAndTipoVisita(
                historia.getId(),
                TipoVisita.PRIMERA_ENTREVISTA
            );

        if (!yaExistePrimera) {
            
      
            visita.setTipoVisita(TipoVisita.PRIMERA_ENTREVISTA);
        } else {
            visita.setTipoVisita(TipoVisita.SEGUIMIENTO);
        }
    }
        Visita guardada = visitaRepo.save(visita);
        guardada.setEstadoIa("PROCESANDO");
        visitaRepo.save(guardada);

        // =========================
        // GENERAR IA ASINCRONA
        // =========================

        if (
                guardada.getObservacion() != null &&
                !guardada.getObservacion().isBlank()
        ) {

            iaService.resumirObservacionAsync(
                    guardada.getId(),
                    guardada.getObservacion()
            );
        }

        return guardada;
    }

    // =========================
    // EDITAR
    // =========================

   public Visita editar(Long id, Visita datos) {

    Visita visita = visitaRepo.findCompletaById(id)
            .orElseThrow(() ->
                    new RuntimeException("Visita inexistente")
            );

    // =====================================
    // VALIDAR OBSERVACION
    // =====================================

    if (datos.getObservacion() == null ||
        datos.getObservacion().trim().isEmpty()) {

        throw new RuntimeException("La observación es obligatoria");
    }

    // =========================
// DETERMINAR TIPO DE VISITA (SOLO EN CREACIÓN)
// =========================

/*if (visita.getTipoVisita() == null) {

    boolean yaExistePrimera =
            visitaRepo.existsByHistoriaIdAndTipoVisita(
                    historia.getId(),
                    TipoVisita.PRIMERA_ENTREVISTA
            );

    if (!yaExistePrimera) {
        visita.setTipoVisita(TipoVisita.PRIMERA_ENTREVISTA);
    } else {
        visita.setTipoVisita(TipoVisita.SEGUIMIENTO);
    }
}*/

// =====================================
// NO MODIFICAR TIPO DE VISITA EN EDICIÓN
// =====================================
    // =====================================
    // DETECTAR CAMBIO OBSERVACION
    // =====================================

    boolean observacionCambio =
            !datos.getObservacion().trim().equals(
                    visita.getObservacion() != null
                            ? visita.getObservacion().trim()
                            : ""
            );

    boolean regenerarIa =
            observacionCambio || visita.getResumenIa() != null;

    // =====================================
    // ACTUALIZAR OBSERVACION
    // =====================================

    visita.setObservacion(datos.getObservacion());

    // mantener fecha del turno
    if (visita.getTurno() != null &&
        visita.getTurno().getFechaHora() != null) {

        visita.setFecha(
                visita.getTurno()
                        .getFechaHora()
                        .toLocalDate()
        );
    }

    // =====================================
    // IA
    // =====================================

    if (regenerarIa) {

        visita.setEstadoIa("PROCESANDO");
        visita.setResumenIa(null);
    }

    Visita actualizada = visitaRepo.save(visita);

    if (regenerarIa) {

        iaService.resumirObservacionAsync(
                actualizada.getId(),
                actualizada.getObservacion()
        );
    }

    return actualizada;
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

    try {

        Visita visita = visitaRepo.findCompletaById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Visita inexistente"
                        )
                );

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        Document document =
                new Document();

        PdfWriter.getInstance(
                document,
                baos
        );

        document.open();

        // =========================
        // FUENTES
        // =========================

        Font titulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        BaseColor.BLACK
                );

        Font encabezado =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        20,
                        BaseColor.BLACK
                );

        Font texto =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        12,
                        BaseColor.BLACK
                );

        Font fechaFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        BaseColor.DARK_GRAY
                );

        // =========================
        // ENCABEZADO
        // =========================

        com.itextpdf.text.pdf.PdfPTable tablaEncabezado =
                new com.itextpdf.text.pdf.PdfPTable(2);

        tablaEncabezado.setWidthPercentage(100);

        tablaEncabezado.setWidths(new float[]{70, 30});

        // =========================
        // IZQUIERDA
        // =========================

        com.itextpdf.text.pdf.PdfPCell izquierda =
                new com.itextpdf.text.pdf.PdfPCell();

        izquierda.setBorder(0);

       com.itextpdf.text.Image logo =
        com.itextpdf.text.Image.getInstance(
                getClass().getResource("/static/brain.png"));

logo.scaleToFit(40, 40);
izquierda.addElement(logo);

Paragraph nombreSistema =
        new Paragraph(
                "Gestión Psicológica",
                encabezado
        );

izquierda.addElement(nombreSistema);
        // =========================
        // DERECHA
        // =========================

        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        String fechaHora =
                java.time.LocalDateTime.now()
                        .format(formatter);

        com.itextpdf.text.pdf.PdfPCell derecha =
                new com.itextpdf.text.pdf.PdfPCell();

        derecha.setBorder(0);

        Paragraph fechaParrafo =
                new Paragraph(
                        "Generado: " + fechaHora,
                        fechaFont
                );

        fechaParrafo.setAlignment(
                com.itextpdf.text.Element.ALIGN_RIGHT
        );

        derecha.addElement(fechaParrafo);

        // =========================
        // AGREGAR TABLA
        // =========================

        tablaEncabezado.addCell(izquierda);

        tablaEncabezado.addCell(derecha);

        document.add(tablaEncabezado);

        document.add(new Paragraph(" "));

        // =========================
        // TITULO
        // =========================

        Paragraph tituloPdf =
                new Paragraph(
                        "Registro de Visita Clínica",
                        titulo
                );

        tituloPdf.setAlignment(
                com.itextpdf.text.Element.ALIGN_CENTER
        );

        document.add(tituloPdf);

        document.add(new Paragraph(" "));

        // =========================
        // PACIENTE
        // =========================

        String paciente =
                visita.getHistoria()
                        .getPaciente()
                        .getNombre()
                + " "
                +
                visita.getHistoria()
                        .getPaciente()
                        .getApellido();

        document.add(
                new Paragraph(
                        "Paciente: " + paciente,
                        texto
                )
        );

        // =========================
        // FECHA
        // =========================

        document.add(
                new Paragraph(
                        "Fecha de visita: "
                        + visita.getFecha(),
                        texto
                )
        );

        document.add(new Paragraph(" "));

        // =========================
        // OBSERVACION
        // =========================

        document.add(
                new Paragraph(
                        "Observación clínica:",
                        titulo
                )
        );

        String observacionLimpia =
                visita.getObservacion()

                        // eliminar tags HTML
                        .replaceAll("<[^>]*>", " ")

                        // espacios html
                        .replace("&nbsp;", " ")

                        // limpiar espacios múltiples
                        .replaceAll("\\s+", " ")

                        .trim();

        document.add(
                new Paragraph(
                        observacionLimpia,
                        texto
                )
        );

        document.add(new Paragraph(" "));

        // =========================
        // RESUMEN IA
        // =========================

        document.add(
                new Paragraph(
                        "Resumen IA:",
                        titulo
                )
        );

        document.add(
                new Paragraph(
                        visita.getResumenIa() != null
                                ? visita.getResumenIa()
                                : "Sin resumen IA",
                        texto
                )
        );

        document.close();

        return baos.toByteArray();

    } catch (Exception e) {

        e.printStackTrace();

        throw new RuntimeException(
                "Error generando PDF"
        );
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

    return !visitaRepo.existsByHistoriaIdAndTipoVisita(
            historia.getId(),
            TipoVisita.PRIMERA_ENTREVISTA
    );
}
    
}