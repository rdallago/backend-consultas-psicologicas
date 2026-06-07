package com.mycompany.consultas.psicologicas.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // PACIENTE
    // =========================

    @OneToOne
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({
            "historiaClinica"
    })
    private Paciente paciente;

    // =========================
    // FECHA CREACION
    // =========================

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaCreacion;

    // =========================
    // HISTORIA CLINICA 
    // =========================

    @Column(columnDefinition = "LONGTEXT")
    private String resumenClinico;
    
    // =========================
    // VISITAS
    // =========================

    @OneToMany(
            mappedBy = "historia",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("fecha ASC")
    @JsonIgnoreProperties({
            "historia"
    })
    private List<Visita> visitas;

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Visita> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<Visita> visitas) {
        this.visitas = visitas;
    }
    
    public String getResumenClinico() {
    return resumenClinico;
}

public void setResumenClinico(String resumenClinico) {
    this.resumenClinico = resumenClinico;
}
}