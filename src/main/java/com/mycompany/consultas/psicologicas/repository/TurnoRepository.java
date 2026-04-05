/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.consultas.psicologicas.repository;

/**
 *
 * @author Informatica
 */

import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // ✔ verificar si tiene turnos asistidos
    boolean existsByPacienteIdAndEstado(Long pacienteId, EstadoTurno estado);

       // ✔ validar superposición
    boolean existsByFechaHora(LocalDateTime fechaHora);
}