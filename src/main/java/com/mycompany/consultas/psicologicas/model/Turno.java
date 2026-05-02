package com.mycompany.consultas.psicologicas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.mycompany.consultas.psicologicas.model.EstadoTurno;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private LocalDateTime fechaHora;

   @Enumerated(EnumType.STRING)
   private EstadoTurno estado;

    // GETTERS Y SETTERS

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

   public EstadoTurno getEstado() {
    return estado;
}

public void setEstado(EstadoTurno estado) {
    this.estado = estado;
}
}