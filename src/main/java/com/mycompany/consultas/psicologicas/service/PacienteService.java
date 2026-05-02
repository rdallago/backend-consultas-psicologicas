/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Informatica
 */

package com.mycompany.consultas.psicologicas.service;

import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Paciente;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import com.mycompany.consultas.psicologicas.repository.PacienteRepository;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private HistoriaClinicaRepository historiaRepository;

    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    public Paciente guardar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

  
public Paciente obtenerPorId(Long id) {
    return pacienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
}

public void eliminar(Long id) {

    if (historiaRepository.contarHistoriasPorPaciente(id) > 0) {
        throw new RuntimeException("No se puede eliminar: el paciente tiene historia clínica");
    }

    if (turnoRepository.countByPacienteIdAndEstado(id, EstadoTurno.ASISTIO) > 0) {
        throw new RuntimeException("No se puede eliminar: el paciente tiene turnos asistidos");
    }

    pacienteRepository.deleteById(id);
}
}