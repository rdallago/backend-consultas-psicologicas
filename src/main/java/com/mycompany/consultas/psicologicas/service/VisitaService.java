package com.mycompany.consultas.psicologicas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.repository.VisitaRepository;

import java.util.List;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository repository;

    public Visita guardar(Visita v) {
        return repository.save(v);
    }
     public List<Visita> listar() {
        return repository.findAll();
    }

    public List<Visita> listarPorHistoria(Long historiaId) {
        return repository.findByHistoriaId(historiaId);
    }
}