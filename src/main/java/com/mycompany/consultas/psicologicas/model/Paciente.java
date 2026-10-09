
package com.mycompany.consultas.psicologicas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private String dni;
    private String email;
    private String telefono;
    private String direccion;

    // Fecha de nacimiento persistente
    private LocalDate fechaNacimiento;

    // Nuevo atributo persistente
    @Column(name = "fecha_inicio_tratamiento")
    private LocalDate fechaInicioTratamiento;

    // =========================
    // HISTORIA CLINICA
    // =========================

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL)
    @JsonIgnore
    private HistoriaClinica historiaClinica;

    // =========================
    // EDAD CALCULADA
    // NO SE GUARDA EN MYSQL
    // =========================

    @Transient
    public Integer getEdad() {

        if (fechaNacimiento == null) {
            return null;
        }

        LocalDate hoy = LocalDate.now();

        if (fechaNacimiento.isAfter(hoy)) {
            return null;
        }

        return Period.between(
                fechaNacimiento,
                hoy
        ).getYears();
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(
            LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaInicioTratamiento() {
        return fechaInicioTratamiento;
    }

    public void setFechaInicioTratamiento(
            LocalDate fechaInicioTratamiento) {
        this.fechaInicioTratamiento =
                fechaInicioTratamiento;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(
            HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }
}
