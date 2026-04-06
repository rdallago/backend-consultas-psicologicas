package com.mycompany.consultas.psicologicas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.service.VisitaService;

import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin
public class VisitaController {

    @Autowired
    private VisitaService service;
    
    @GetMapping
    public List<Visita> listar() {
        return service.listar();
    }

    @PostMapping
    public Visita guardar(@RequestBody Visita v) {
        return service.guardar(v);
    }

    @GetMapping("/historia/{id}")
    public List<Visita> listar(@PathVariable Long id) {
        return service.listarPorHistoria(id);
    }
}