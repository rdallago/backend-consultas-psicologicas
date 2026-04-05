/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.controller;

/**
 *
 * @author Informatica
 */

import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    // LISTAR
    @GetMapping
    public List<Turno> listar() {
        return turnoService.listar();
    }

    // CREAR
    @PostMapping
    public Turno guardar(@RequestBody Turno turno) {
        return turnoService.guardar(turno);
    }

    // OBTENER
    @GetMapping("/{id}")
    public Turno obtener(@PathVariable Long id) {
        return turnoService.obtenerPorId(id);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Turno actualizar(@PathVariable Long id, @RequestBody Turno turno) {
        turno.setId(id);
        return turnoService.guardar(turno);
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        turnoService.eliminar(id);
    }
}