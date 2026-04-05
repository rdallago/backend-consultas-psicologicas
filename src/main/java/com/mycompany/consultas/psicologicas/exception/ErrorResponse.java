/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.exception;

/**
 *
 * @author Informatica
 */
import java.time.LocalDateTime;

public class ErrorResponse {

    private String mensaje;
    private int codigo;
    private LocalDateTime fecha;

    // Constructor
    public ErrorResponse(String mensaje, int codigo) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.fecha = LocalDateTime.now();
    }

    // Getters
    public String getMensaje() {
        return mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}