/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.service;

/**
 *
 * @author Informatica
 */

import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import com.mycompany.consultas.psicologicas.repository.HistoriaClinicaRepository;
import com.mycompany.consultas.psicologicas.exception.RecursoNoEncontradoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaRepository;

    public List<HistoriaClinica> listar() {
        return historiaRepository.findAll();
    }

    public HistoriaClinica guardar(HistoriaClinica h) {
        return historiaRepository.save(h);
    }

    public HistoriaClinica obtenerPorId(Long id) {
        return historiaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Historia clínica no encontrada"));
    }

    public void eliminar(Long id) {
        historiaRepository.deleteById(id);
    }
}