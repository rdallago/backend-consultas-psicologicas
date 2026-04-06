package com.mycompany.consultas.psicologicas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.service.HistoriaClinicaService;
import java.util.List;

@RestController
@RequestMapping("/historias")
@CrossOrigin
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService service;

    @PostMapping
    public HistoriaClinica crear(@RequestBody HistoriaClinica hc) {
        return service.crear(hc);
    }

    @GetMapping("/paciente/{id}")
    public HistoriaClinica obtenerPorPaciente(@PathVariable Long id) {
        return service.buscarPorPaciente(id).orElse(null);
    }
    
    @GetMapping
   public List<HistoriaClinica> listar() {
    return service.listar();
}
}