/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.consultas.psicologicas.repository;

/**
 *
 * @author Informatica
 */
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    // ✔ verificar si tiene historia clínica
    boolean existsByPacienteId(Long pacienteId);
}