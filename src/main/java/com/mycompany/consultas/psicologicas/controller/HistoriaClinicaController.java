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

@RestController
@RequestMapping("/api/historias")
@CrossOrigin
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService service;

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
                service.crear(hc)
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
        return service.listar();
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
            service.actualizarFecha(id, fechaStr)
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
            service.eliminar(id);
            return ResponseEntity.ok("Historia eliminada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}