/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author Informatica
 */
package com.mycompany.consultas.psicologicas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.consultas.psicologicas.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}

