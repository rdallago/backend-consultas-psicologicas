/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Informatica
 */
package com.mycompany.consultas.psicologicas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mycompany.consultas.psicologicas.model.Paciente;
import com.mycompany.consultas.psicologicas.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.listar();
    }

    @PostMapping
    public Paciente guardar(@RequestBody Paciente p) {
        return pacienteService.guardar(p);
    }

    @GetMapping("/{id}")
public Paciente obtener(@PathVariable Long id) {
    return pacienteService.obtenerPorId(id);
}

    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @RequestBody Paciente p) {
        p.setId(id);
        return pacienteService.guardar(p);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
    }
}