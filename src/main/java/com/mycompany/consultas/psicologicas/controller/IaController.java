package com.mycompany.consultas.psicologicas.controller;

import com.mycompany.consultas.psicologicas.service.IaService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*")
public class IaController {

    @Autowired
    private IaService iaService;

    // =====================================
    // POST RESUMIR IA (NO BLOQUEANTE REAL)
    // =====================================

   @PostMapping("/resumir")
public ResponseEntity<?> resumir(
        @RequestBody Map<String, String> request
) {

    String resultado =
            iaService.resumirObservacion(
                    request.get("texto")
            );

    return ResponseEntity.ok(
            Map.of("resumen", resultado)
    );
}
    // =====================================
    // GET TEST (bloqueante SOLO para pruebas)
    // =====================================

    @GetMapping("/resumir")
    public ResponseEntity<String> resumirGet() {

        String resultado = iaService
        .resumirObservacion(
                "Paciente con ansiedad y estrés laboral."
        );

        return ResponseEntity.ok(resultado);
    }

    // =====================================
    // DTO
    // =====================================

    public static class TextoRequest {

        private String texto;

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }
    }
}