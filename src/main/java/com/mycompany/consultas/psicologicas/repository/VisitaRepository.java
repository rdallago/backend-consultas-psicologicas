package com.mycompany.consultas.psicologicas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.consultas.psicologicas.model.Visita;

import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    List<Visita> findByHistoriaId(Long historiaId);
}