/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.controller;

/**
 *
 * @author Informatica
 */
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.service.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historias-clinicas")
@CrossOrigin(origins = "*")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService service;

    @GetMapping
    public List<HistoriaClinica> listar() {
        return service.listar();
    }

    @PostMapping
    public HistoriaClinica guardar(@RequestBody HistoriaClinica hc) {
        return service.guardar(hc);
    }

    @GetMapping("/{id}")
    public HistoriaClinica obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public HistoriaClinica actualizar(@PathVariable Long id, @RequestBody HistoriaClinica hc) {
        hc.setId(id);
        return service.guardar(hc);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}