package com.mycompany.consultas.psicologicas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "visitas")
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // HISTORIA CLINICA
    // =========================

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    @JsonIgnoreProperties({
            "visitas",
            "paciente"
    })
    private HistoriaClinica historia;

    // =========================
    // TURNO
    // =========================

    @OneToOne
@JoinColumn(
        name = "turno_id",
        unique = true
)
private Turno turno;
    
    @JsonIgnoreProperties({
            "historiaClinica",
           "visitas",
         "hibernateLazyInitializer",
            "handler"
    })
    
    // =========================
    // FECHA
    // =========================
    @Column(nullable = false)
    private LocalDate fecha;

    // =========================
    // OBSERVACION
    // =========================

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String observacion;

    // =========================
    // RESUMEN IA
    // =========================

    @Lob
    @Column(name = "resumen_ia", columnDefinition = "LONGTEXT")
    private String resumenIa;

    private String estadoIa;

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoriaClinica getHistoria() {
        return historia;
    }

    public void setHistoria(HistoriaClinica historia) {
        this.historia = historia;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getResumenIa() {
        return resumenIa;
    }

    public void setResumenIa(String resumenIa) {
        this.resumenIa = resumenIa;
    }

    public String getEstadoIa() {
        return estadoIa;
    }

    public void setEstadoIa(String estadoIa) {
        this.estadoIa = estadoIa;
    }
}