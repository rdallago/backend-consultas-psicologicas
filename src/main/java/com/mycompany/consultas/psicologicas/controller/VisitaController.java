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
}