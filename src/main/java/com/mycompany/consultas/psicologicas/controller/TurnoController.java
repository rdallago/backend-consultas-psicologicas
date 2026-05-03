package com.mycompany.consultas.psicologicas.controller;

import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin("*")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping
    public List<Turno> listar() {
        return turnoService.listar();
    }

    @GetMapping("/{id}")
    public Turno obtener(@PathVariable Long id) {
        return turnoService.obtenerPorId(id);
    }

    @PostMapping
    public Turno guardar(@RequestBody Turno turno) {
        return turnoService.guardar(turno);
    }

    @PutMapping("/{id}")
    public Turno modificar(@PathVariable Long id,
                           @RequestBody Turno turno) {

        return turnoService.actualizar(id, turno);
    }

    @PutMapping("/{id}/estado/{estado}")
    public Turno cambiarEstado(@PathVariable Long id,
                               @PathVariable EstadoTurno estado) {

        return turnoService.cambiarEstado(id, estado);
    }

   
    
    @DeleteMapping("/{id}")
public ResponseEntity<String> eliminar(@PathVariable Long id) {

    try {

        turnoService.eliminar(id);

        return ResponseEntity.ok("Turno eliminado");

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(400)
                .body(e.getMessage());

    } catch (Exception e) {

        return ResponseEntity
                .status(500)
                .body("Error interno al eliminar turno");
    }
}
    
    
    
    @GetMapping("/mes/{anio}/{mes}")
    public List<Turno> turnosDelMes(@PathVariable int anio,
                                    @PathVariable int mes) {

        YearMonth ym = YearMonth.of(anio, mes);

        LocalDateTime desde = ym.atDay(1).atStartOfDay();
        LocalDateTime hasta = ym.atEndOfMonth().atTime(23, 59);

        return turnoService.buscarEntreFechas(desde, hasta);
    }
}