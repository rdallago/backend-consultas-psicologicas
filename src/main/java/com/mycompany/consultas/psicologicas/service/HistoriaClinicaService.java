package com.mycompany.consultas.psicologicas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import java.util.List;

import java.util.Optional;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository repository;

    public HistoriaClinica crear(HistoriaClinica hc) {
        return repository.save(hc);
    }

    public Optional<HistoriaClinica> buscarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }
     public List<HistoriaClinica> listar() {
        return repository.findAll();
    }
}