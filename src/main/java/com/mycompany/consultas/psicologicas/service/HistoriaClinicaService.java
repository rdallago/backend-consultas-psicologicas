package com.mycompany.consultas.psicologicas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository repository;
    
    @Autowired
    private HistoriaClinicaRepository historiaRepo;

    @Autowired
    private VisitaRepository visitaRepo;

    @Autowired
    private IaService iaService;

    @Transactional
    public HistoriaClinica crear(HistoriaClinica hc) {
        Optional<HistoriaClinica> existente = repository.findByPacienteId(hc.getPaciente().getId());
        if (existente.isPresent()) {
            throw new RuntimeException("El paciente ya posee una historia clínica.");
        }

        hc.setFechaCreacion(LocalDate.now());
        if (hc.getVisitas() != null) {
            hc.getVisitas().forEach(v -> v.setHistoria(hc));
        }
        return repository.save(hc);
    }

    public List<HistoriaClinica> listar() {
        return repository.findAll();
    }

    public Optional<HistoriaClinica> buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    public HistoriaClinica actualizarFecha(Long id, String fechaStr) {
        HistoriaClinica hc = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        LocalDate nuevaFecha = LocalDate.parse(fechaStr);

        if (hc.getVisitas() != null && !hc.getVisitas().isEmpty()) {
            LocalDate fechaMaxVisita = hc.getVisitas().stream()
                .map(v -> v.getFecha())
                .max(LocalDate::compareTo)
                .orElseThrow();

            if (nuevaFecha.isAfter(fechaMaxVisita)) {
                throw new RuntimeException(
                    "La fecha de la historia clínica no puede ser posterior a las visitas registradas"
                );
            }
        }

        hc.setFechaCreacion(nuevaFecha);
        return repository.save(hc);
    }

    @Transactional
    public void eliminar(Long id) {
        HistoriaClinica hc = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("No existe"));

        if (hc.getVisitas() != null && !hc.getVisitas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar: tiene visitas asociadas");
        }

        repository.eliminarHistoria(id);
        System.out.println("🔥 HISTORIA ELIMINADA REALMENTE");
    }

    public HistoriaClinica generarResumenClinico(Long historiaId) {

    HistoriaClinica historia = historiaRepo.findById(historiaId)
            .orElseThrow(() ->
                    new RuntimeException("Historia clínica inexistente"));

    List<Visita> visitas = visitaRepo.findByHistoriaId(historiaId);

    if (visitas == null || visitas.isEmpty()) {
        throw new IllegalStateException(
                "No se puede generar la historia clínica: " +
                "el paciente no posee visitas registradas."
        );
    }

    // Ordenar cronológicamente las entrevistas.
    visitas.sort(
            java.util.Comparator
                    .comparing(
                            Visita::getFecha,
                            java.util.Comparator.nullsLast(
                                    java.util.Comparator.naturalOrder()
                            )
                    )
                    .thenComparing(
                            Visita::getId,
                            java.util.Comparator.nullsLast(
                                    java.util.Comparator.naturalOrder()
                            )
                    )
    );

    String resumen = iaService.generarHistoriaClinica(visitas);

    // No reemplazar una historia clínica por un mensaje de error.
    if (resumen == null
            || resumen.isBlank()
            || resumen.equals("No se pudo generar historia clínica.")
            || resumen.equals("Sin respuesta IA.")
            || resumen.equals("Sin registros clínicos.")
            || resumen.equals("Sin contenido clínico.")) {

        throw new IllegalStateException(
                "La IA no pudo generar la historia clínica. " +
                "Se conservó el contenido anterior."
        );
    }

    historia.setResumenClinico(resumen);

    return historiaRepo.save(historia);
}
    
    public HistoriaClinica guardarResumenClinico(Long id, String resumen) {
        HistoriaClinica historia = historiaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia inexistente"));

        historia.setResumenClinico(resumen);
        return historiaRepo.save(historia);
    }

public byte[] generarPdfHistoriaClinica(Long id) {
    HistoriaClinica historia = historiaRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Historia clínica inexistente"));
    try {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = PdfClinicoFormato.documento();
        PdfWriter.getInstance(documento, salida);
        documento.open();
        String paciente = historia.getPaciente().getNombre() + " "
                + historia.getPaciente().getApellido();
        PdfClinicoFormato.cabecera(documento, "Historia Clínica Psicológica", paciente,
                "Fecha de apertura: " + (historia.getFechaCreacion() == null
                        ? "Sin fecha" : historia.getFechaCreacion().toString()));
        PdfClinicoFormato.contenido(documento, historia.getResumenClinico());
        documento.close();
        return salida.toByteArray();
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error generando PDF de historia clínica", e);
    }
}

public String generarBorradorHistoriaClinica(Long historiaId) {

    HistoriaClinica historia = historiaRepo.findById(historiaId)
            .orElseThrow(() ->
                    new RuntimeException("Historia clínica inexistente"));

    List<Visita> visitas = visitaRepo.findByHistoriaId(historiaId);

    if (visitas == null || visitas.isEmpty()) {
        throw new IllegalStateException(
                "El paciente no tiene entrevistas registradas."
        );
    }

    visitas.sort(
            java.util.Comparator
                    .comparing(
                            Visita::getFecha,
                            java.util.Comparator.nullsLast(
                                    java.util.Comparator.naturalOrder()
                            )
                    )
                    .thenComparing(
                            Visita::getId,
                            java.util.Comparator.nullsLast(
                                    java.util.Comparator.naturalOrder()
                            )
                    )
    );

    String borrador = iaService.generarHistoriaClinica(visitas);

    if (borrador == null
            || borrador.isBlank()
            || borrador.equals("No se pudo generar historia clínica.")
            || borrador.equals("Sin respuesta IA.")
            || borrador.equals("Sin registros clínicos.")
            || borrador.equals("Sin contenido clínico.")) {

        throw new IllegalStateException(
                "No se pudo generar el borrador. " +
                "La historia clínica guardada no fue modificada."
        );
    }

    return borrador;
}
}