/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.exception;

/**
 *
 * @author Informatica
 */
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 Error 404 (no encontrado)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                404
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 🟠 Error general (validaciones, reglas de negocio)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(RuntimeException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                400
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}