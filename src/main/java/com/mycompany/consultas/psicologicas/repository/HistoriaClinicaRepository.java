package com.mycompany.consultas.psicologicas.repository;

import com.mycompany.consultas.psicologicas.model.HistoriaClinica;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    // método que ya usa HistoriaClinicaService
    Optional<HistoriaClinica> findByPacienteId(Long pacienteId);

    // método nuevo para validar eliminación
    @Query("SELECT COUNT(h) FROM HistoriaClinica h WHERE h.paciente.id = :pacienteId")
    long contarHistoriasPorPaciente(@Param("pacienteId") Long pacienteId);

}