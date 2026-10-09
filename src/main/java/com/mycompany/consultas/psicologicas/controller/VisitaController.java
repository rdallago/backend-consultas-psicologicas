package com.mycompany.consultas.psicologicas.controller;
import org.springframework.http.ResponseEntity;
import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;
import com.mycompany.consultas.psicologicas.service.VisitaService;
import com.mycompany.consultas.psicologicas.service.IaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitas")
@CrossOrigin("*")
public class VisitaController {

    @Autowired
    private VisitaService service;

    @Autowired
    private IaService iaService;

    @Autowired
    private VisitaRepository visitaRepository;
    // =========================
    // LISTAR
    // =========================

    @GetMapping
    public List<Visita> listar() {
        return service.listar();
    }

    
    
    @GetMapping("/{id}")
public ResponseEntity<?> obtenerPorId(
        @PathVariable Long id
) {

    return visitaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}


@GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> descargarPdf(
        @PathVariable Long id
) {

    byte[] pdf =
            service.generarPdf(id);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=visita.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
    // =========================
    // CREAR (IA DESACOPLADA)
    // =========================

  @PostMapping
public Visita crear(@RequestBody Visita visita) {

    return service.crear(visita);
}

    // =========================
    // EDITAR (IA DESACOPLADA)
    // =========================

   @PutMapping("/{id}")
public Visita editar(
        @PathVariable Long id,
        @RequestBody Visita visita
) {

    return service.editar(id, visita);
}
    // =========================
    // ELIMINAR
    // =========================

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    // =========================
    // BUSCAR PACIENTE
    // =========================

    @GetMapping("/buscar")
    public List<Visita> buscar(
            @RequestParam String nombre
    ) {
        return service.buscarPorPaciente(nombre);
    }

    // =========================
    // BUSCAR FECHA
    // =========================

    @GetMapping("/fecha")
    public List<Visita> buscarPorFecha(
            @RequestParam String fecha
    ) {
        return service.buscarPorFecha(fecha);
    }
    
  @GetMapping("/es-primera/{pacienteId}")
public boolean esPrimera(
        @PathVariable Long pacienteId
) {

    return service.esPrimeraVisita(
            pacienteId
    );
}
// =====================================
// GENERAR RESUMEN IA DE UNA VISITA
// (SOLO CUANDO EL PSICOLOGO LO SOLICITA)
// =====================================

@PostMapping("/{id}/generar-resumen-ia")
public ResponseEntity<?> generarResumenIa(
        @PathVariable Long id
) {

    Visita visita = visitaRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Visita inexistente")
            );

    // Validar que exista una observación
    if (visita.getObservacion() == null ||
            visita.getObservacion().trim().isEmpty()) {

        return ResponseEntity.badRequest()
                .body(
                        java.util.Map.of(
                                "error",
                                "La visita no tiene observación para resumir"
                        )
                );
    }

    try {

        // Generar resumen solamente a pedido
        String resumen = iaService.resumirObservacion(
                visita.getObservacion()
        );

        // No guardar respuestas vacías ni mensajes de error
        if (resumen == null ||
                resumen.isBlank() ||
                resumen.equalsIgnoreCase("Resumen clínico no disponible.") ||
                resumen.equalsIgnoreCase("Síntesis clínica no concluyente.") ||
                resumen.equalsIgnoreCase("Sin observaciones clínicas.")) {

            return ResponseEntity.internalServerError()
                    .body(
                            java.util.Map.of(
                                    "error",
                                    "No se pudo generar un resumen clínico válido"
                            )
                    );
        }

        // Guardar resumen en la visita
        visita.setResumenIa(resumen);
        visita.setEstadoIa("OK");

        Visita actualizada = visitaRepository.save(visita);

        return ResponseEntity.ok(actualizada);

    } catch (Exception e) {

        return ResponseEntity.internalServerError()
                .body(
                        java.util.Map.of(
                                "error",
                                "No se pudo generar el resumen IA"
                        )
                );
    }
}
}