package com.mycompany.consultas.psicologicas.repository;

import com.mycompany.consultas.psicologicas.model.EstadoTurno;
import com.mycompany.consultas.psicologicas.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsByFechaHora(LocalDateTime fechaHora);

    List<Turno> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    long countByPacienteIdAndEstado(Long pacienteId, EstadoTurno estado);

}