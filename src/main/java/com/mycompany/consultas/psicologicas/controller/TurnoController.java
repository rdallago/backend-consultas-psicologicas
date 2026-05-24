package com.mycompany.consultas.psicologicas.controller;

import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;
import com.mycompany.consultas.psicologicas.service.TurnoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private TurnoRepository turnoRepository;

    // ==========================
    // LISTAR
    // ==========================
    @GetMapping
    public List<Turno> listar() {
        return turnoService.listar();
    }

    // ==========================
    // OBTENER
    // ==========================
    @GetMapping("/{id}")
    public Turno obtener(@PathVariable Long id) {
        return turnoService.obtenerPorId(id);
    }
    

    // ==========================
    // CREAR
    // ==========================
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Turno> guardar(@RequestBody Turno turno) {

        System.out.println("======== POST ========");
        System.out.println(turno.getFechaHora());
        System.out.println(turno.getEstado());

        Turno nuevo = turnoService.guardar(turno);

        return ResponseEntity.ok(nuevo);
    }

    // ==========================
    // MODIFICAR
    // ==========================
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Turno> modificar(
            @PathVariable Long id,
            @RequestBody Turno turno
    ) {
        Turno actualizado = turnoService.actualizar(id, turno);
        return ResponseEntity.ok(actualizado);
    }

    // ==========================
    // CAMBIAR ESTADO
    // ==========================
    @PutMapping("/{id}/estado/{estado}")
    public Turno cambiarEstado(
            @PathVariable Long id,
            @PathVariable EstadoTurno estado
    ) {
        return turnoService.cambiarEstado(id, estado);
    }

    // ==========================
    // ELIMINAR
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        try {
            turnoService.eliminar(id);
            return ResponseEntity.ok("Turno eliminado");

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno");
        }
    }

    // ==========================
    // TURNOS POR MES
    // ==========================
    @GetMapping("/mes/{anio}/{mes}")
    public List<Turno> turnosDelMes(@PathVariable int anio, @PathVariable int mes) {

        YearMonth ym = YearMonth.of(anio, mes);

        LocalDateTime desde = ym.atDay(1).atStartOfDay();
        LocalDateTime hasta = ym.atEndOfMonth().atTime(23, 59);

        return turnoService.buscarEntreFechas(desde, hasta);
    }

    // ==========================
    // ESTADÍSTICAS
    // ==========================
    @GetMapping("/estadisticas/resumen")
    public Map<String, Long> getResumen() {

        return Map.of(
                "totales", turnoRepository.count(),
                "asistidos", turnoRepository.countByEstado(EstadoTurno.ASISTIO),
                "ausentes", turnoRepository.countByEstado(EstadoTurno.AUSENTE),
                "pendientes", turnoRepository.countByEstado(EstadoTurno.AGENDADO)
        );
    }

    
    @GetMapping("/estadisticas/ausencias")
    public List<Map<String, Object>> ausenciasPorPaciente() {
        return turnoService.obtenerAusenciasPorPaciente();
    }
// =========================================================
    // ✔ ENDPOINT CORRECTO PARA VISITAS
    // =========================================================
    @GetMapping("/para-visita")
public List<Turno> turnosParaVisita() {

    return turnoService.listar().stream()
            .filter(t -> {
              /*  System.out.println(
                "Turno ID: " + t.getId()
             + " Estado: " + t.getEstado()
                );*/

                // 1. SOLO TURNOS AGENDADOS
                if (t.getEstado() != EstadoTurno.AGENDADO) {
                    return false;
                }

                // 2. debe tener paciente
                if (t.getPaciente() == null || t.getPaciente().getId() == null) {
                    return false;
                }

                // 3. debe tener historia clínica (consulta segura)
                boolean tieneHistoria =
                        turnoService
                                .existeHistoriaClinicaPorPacienteId(
                                        t.getPaciente().getId()
                                );

                return tieneHistoria;
            })
            .toList();
}
}