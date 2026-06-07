package com.mycompany.consultas.psicologicas.controller;
import com.mycompany.consultas.psicologicas.model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.service.HistoriaClinicaService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/historias")
@CrossOrigin
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaRepository historiaRepo;
    
    @Autowired
    private HistoriaClinicaService historiaService;

    //EndPoint Generar IA

    @PostMapping("/{id}/generar-resumen")
public HistoriaClinica generarResumen(
        @PathVariable Long id
) {

    return historiaService
            .generarResumenClinico(id);
}

//EndPoint Guardar
@PutMapping("/{id}/guardar-resumen")
public HistoriaClinica guardarResumen(
        @PathVariable Long id,
        @RequestBody HistoriaClinica body) {

    return historiaService.guardarResumenClinico(
            id,
            body.getResumenClinico()
    );
}

@GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> pdfHistoriaClinica(
        @PathVariable Long id
) {

    byte[] pdf =
            historiaService
                    .generarPdfHistoriaClinica(id);

    return ResponseEntity.ok()

            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=historia-clinica.pdf"
            )

            .contentType(
                    MediaType.APPLICATION_PDF
            )

            .body(pdf);
}

     // =========================
    // CREAR
    // =========================
    @PostMapping(
    consumes = "application/json",
    produces = "application/json"
)
public ResponseEntity<?> crear(
        @RequestBody Map<String, Object> body) {

    try {

        System.out.println("🔥 JSON RECIBIDO:");
        System.out.println(body);

        HistoriaClinica hc = new HistoriaClinica();

        // FECHA
        String fecha = body.get("fechaCreacion").toString();
        hc.setFechaCreacion(LocalDate.parse(fecha));

        // PACIENTE
        Map<String, Object> pacienteMap =
                (Map<String, Object>) body.get("paciente");

        Long pacienteId =
                Long.valueOf(pacienteMap.get("id").toString());

        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);

        hc.setPaciente(paciente);

        return ResponseEntity.ok(
                historiaService.crear(hc)
        );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "message",
                        e.getMessage()
                ));
    }
}
    // =========================
    // LISTAR
    // =========================
    @GetMapping
    public List<HistoriaClinica> listar() {
        return historiaService.listar();
    }

    // =========================
    // ACTUALIZAR (CLAVE)
    // =========================
  @PutMapping("/{id}")
public ResponseEntity<?> actualizar(
        @PathVariable Long id,
        @RequestBody Map<String, Object> body) {

    try {
        System.out.println("🔥 ENTRÓ AL CONTROLLER PUT");

        String fechaStr = body.get("fechaCreacion").toString();

        return ResponseEntity.ok(
            historiaService.actualizarFecha(id, fechaStr)
        );

    } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }
}

    // =========================
    // ELIMINAR
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            historiaService.eliminar(id);
            return ResponseEntity.ok("Historia eliminada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    
    public byte[] generarPdfHistoriaClinica(
        Long id
) {

    try {

        HistoriaClinica historia =
                historiaRepo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Historia inexistente"
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

        // =====================================
        // FUENTES
        // =====================================

        Font titulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        BaseColor.BLACK
                );

        Font texto =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        12,
                        BaseColor.BLACK
                );

        Font encabezado =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14,
                        BaseColor.BLACK
                );

        // =====================================
        // LOGO
        // =====================================

        try {

            Image logo =
                    Image.getInstance(
                            "src/main/resources/static/logo.png"
                    );

            logo.scaleToFit(50, 50);

            logo.setAlignment(Image.LEFT);

            document.add(logo);

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar logo"
            );
        }

        // =====================================
        // FECHA Y HORA
        // =====================================

        String fechaHora =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        );

        // =====================================
        // ENCABEZADO
        // =====================================

        document.add(
                new Paragraph(
                        "Gestión Psicológica",
                        encabezado
                )
        );

        document.add(
                new Paragraph(
                        "Fecha de impresión: "
                                + fechaHora,
                        texto
                )
        );

        document.add(new Paragraph(" "));

        // =====================================
        // TITULO
        // =====================================

        document.add(
                new Paragraph(
                        "Historia Clínica",
                        titulo
                )
        );

        document.add(new Paragraph(" "));

        // =====================================
        // PACIENTE
        // =====================================

        String paciente =
                historia.getPaciente().getNombre()
                +
                " "
                +
                historia.getPaciente().getApellido();

        document.add(
                new Paragraph(
                        "Paciente: " + paciente,
                        texto
                )
        );

        document.add(new Paragraph(" "));

        // =====================================
        // HISTORIA CLINICA
        // =====================================

        String contenido =
                historia.getResumenClinico() != null
                        ? historia.getResumenClinico()
                        : "Sin historia clínica.";

        document.add(
                new Paragraph(
                        contenido,
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
}