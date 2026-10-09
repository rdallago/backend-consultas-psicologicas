/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Informatica
 */
package com.mycompany.consultas.psicologicas.controller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.mycompany.consultas.psicologicas.model.Paciente;
import com.mycompany.consultas.psicologicas.service.PacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin
public class PacienteController {
    @GetMapping("/test")
    public String test() {

        return new BCryptPasswordEncoder()
                .encode("1234");
    }
    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.listar();
    }

   @PostMapping
    public Paciente guardar(@Valid @RequestBody Paciente p) {
        return pacienteService.guardar(p);
    }

    @GetMapping("/{id}")
public Paciente obtener(@PathVariable Long id) {
    return pacienteService.obtenerPorId(id);
}



    @PutMapping("/{id}")
public Paciente actualizar(
        @PathVariable Long id,
        @Valid @RequestBody Paciente p) {

    p.setId(id);
    return pacienteService.guardar(p);
}

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
    }
}