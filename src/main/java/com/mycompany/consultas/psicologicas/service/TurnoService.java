/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.service;

/**
 *
 * @author Informatica
 */

import com.mycompany.consultas.psicologicas.model.Turno;
import com.mycompany.consultas.psicologicas.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    public List<Turno> listar() {
        return turnoRepository.findAll();
    }

    public Turno guardar(Turno turno) {

        if (turnoRepository.existsByFechaHora(turno.getFechaHora())) {
            throw new RuntimeException("Ya existe un turno en esa fecha y hora");
        }

        return turnoRepository.save(turno);
    }

 
public Turno obtenerPorId(Long id) {
    return turnoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado"));
}
    public void eliminar(Long id) {
        turnoRepository.deleteById(id);
    }
}