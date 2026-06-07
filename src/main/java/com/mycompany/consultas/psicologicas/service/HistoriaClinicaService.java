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
                .orElseThrow(() -> new RuntimeException("Historia clínica inexistente"));

        List<Visita> visitas = visitaRepo.findByHistoriaId(historiaId);

        if (visitas == null || visitas.isEmpty()) {
            historia.setResumenClinico("El paciente no posee visitas clínicas registradas.");
            return historiaRepo.save(historia);
        }

        String resumen = iaService.generarHistoriaClinica(visitas);

        if (resumen == null || resumen.isBlank()) {
            resumen = "No se pudo generar historia clínica.";
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

    try {

        HistoriaClinica historia = historiaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia inexistente"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // =====================================
        // LOGO CEREBRO
        // =====================================
        try {
            Image logo = Image.getInstance(getClass().getResource("/static/brain.png"));
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("No se pudo cargar brain.png");
        }

        // =====================================
        // FUENTES
        // =====================================
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Font texto = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font encabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

        // =====================================
        // FECHA Y HORA
        // =====================================
        String fechaHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        // =====================================
        // ENCABEZADO
        // =====================================
        document.add(new Paragraph("Gestión Psicológica", encabezado));
        document.add(new Paragraph("Fecha de impresión: " + fechaHora, texto));
        document.add(new Paragraph(" "));

        // =====================================
        // TITULO
        // =====================================
        document.add(new Paragraph("Historia Clínica", titulo));
        document.add(new Paragraph(" "));

        // =====================================
        // PACIENTE
        // =====================================
        String paciente = historia.getPaciente().getNombre() + " " + historia.getPaciente().getApellido();
        document.add(new Paragraph("Paciente: " + paciente, texto));
        document.add(new Paragraph(" "));

        // =====================================
        // CONTENIDO HTML (QUILL + FIX)
        // =====================================
        String contenido = historia.getResumenClinico() != null
                ? historia.getResumenClinico()
                : "<p>Sin historia clínica.</p>";

        // 🔥 LIMPIEZA DE QUILL
        contenido = contenido
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .replace("<br>", "<br/>");

        // 🔥 WRAPPER CON CSS GLOBAL (CLAVE PARA JUSTIFICADO)
        contenido =
                "<html>"
              + "<head>"
              + "<style>"
              + "body { font-family: Helvetica; }"
              + "p { text-align: justify; line-height: 1.4; }"
              + "</style>"
              + "</head>"
              + "<body>"
              + contenido
              + "</body>"
              + "</html>";

        // =====================================
        // PARSEO HTML → PDF
        // =====================================
        XMLWorkerHelper.getInstance().parseXHtml(
                writer,
                document,
                new StringReader(contenido)
        );

        document.close();
        return baos.toByteArray();

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error generando PDF de historia clínica");
    }
}
}