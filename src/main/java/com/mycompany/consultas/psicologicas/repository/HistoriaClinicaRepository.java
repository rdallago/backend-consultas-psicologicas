package com.mycompany.consultas.psicologicas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.consultas.psicologicas.model.HistoriaClinica;

import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    Optional<HistoriaClinica> findByPacienteId(Long pacienteId);

    boolean existsByPacienteId(Long pacienteId);
}