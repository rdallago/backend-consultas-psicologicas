package com.mycompany.consultas.psicologicas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // PACIENTE
    // =========================

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    @JsonIgnoreProperties({
            "historiaClinica"
    })
    private Paciente paciente;

    // =========================
    // FECHA Y HORA
    // =========================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    // =========================
    // MOTIVO
    // =========================

    @Column(columnDefinition = "TEXT")
    private String motivo;

    // =========================
    // ESTADO
    // =========================

    @Enumerated(EnumType.STRING)
    private EstadoTurno estado;

    // =========================
    // VISITAS
    // =========================

    @OneToMany(mappedBy = "turno")
    @JsonIgnore
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }
}