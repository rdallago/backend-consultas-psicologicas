package com.mycompany.consultas.psicologicas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mycompany.consultas.psicologicas.model.Visita;
import com.mycompany.consultas.psicologicas.model.TipoVisita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VisitaRepository
        extends JpaRepository<Visita, Long> {

    // =========================
    // VALIDAR SI EXISTE VISITA
    // PARA UN TURNO
    // =========================

    boolean existsByTurnoId(Long turnoId);

    // =========================
    // BUSCAR VISITAS
    // POR HISTORIA CLINICA
    // =========================

    @Query("""
    SELECT v
    FROM Visita v
    LEFT JOIN FETCH v.turno t
    LEFT JOIN FETCH t.paciente
    WHERE v.historia.id = :historiaId
    ORDER BY v.fecha DESC
    """)
    List<Visita> findByHistoriaId(Long historiaId);

    // =========================
    // LISTAR COMPLETO
    // =========================

    @Query("""
    SELECT v
    FROM Visita v
    JOIN FETCH v.historia h
    JOIN FETCH h.paciente
    LEFT JOIN FETCH v.turno t
    LEFT JOIN FETCH t.paciente
    ORDER BY v.fecha DESC
    """)
    List<Visita> findAllConPaciente();

    // =========================
    // BUSCAR POR ID COMPLETO
    // =========================

    @Query("""
    SELECT v
    FROM Visita v
    JOIN FETCH v.historia h
    JOIN FETCH h.paciente
    LEFT JOIN FETCH v.turno t
    LEFT JOIN FETCH t.paciente
    WHERE v.id = :id
    """)
    Optional<Visita> findCompletaById(Long id);

    // =========================
    // BUSCAR PACIENTE
    // =========================

    @Query("""
    SELECT v
    FROM Visita v
    JOIN FETCH v.historia h
    JOIN FETCH h.paciente p
    LEFT JOIN FETCH v.turno t
    LEFT JOIN FETCH t.paciente
    WHERE LOWER(p.nombre)
    LIKE LOWER(CONCAT('%', :nombre, '%'))
    ORDER BY v.fecha DESC
    """)
    List<Visita> buscarPorPaciente(String nombre);

    // =========================
    // BUSCAR FECHA
    // =========================

    @Query("""
    SELECT v
    FROM Visita v
    JOIN FETCH v.historia h
    JOIN FETCH h.paciente
    LEFT JOIN FETCH v.turno t
    LEFT JOIN FETCH t.paciente
    WHERE v.fecha = :fecha
    ORDER BY v.fecha DESC
    """)
    List<Visita> buscarPorFecha(LocalDate fecha);
    
    boolean existsByHistoriaIdAndTipoVisita(Long historiaId, TipoVisita tipoVisita);
}