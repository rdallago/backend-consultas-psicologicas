package com.mycompany.consultas.psicologicas.controller;

import com.mycompany.consultas.psicologicas.service.IaService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*")
public class IaController {

    @Autowired
    private IaService iaService;

    // =====================================
    // POST RESUMIR IA
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
    // ORGANIZAR PRIMERA ENTREVISTA
    // =====================================

    @PostMapping("/organizar-entrevista")
    public ResponseEntity<?> organizarEntrevista(
            @RequestBody Map<String, String> request
    ) {

        try {

            String texto =
                    request.get("texto");

            if (texto == null || texto.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "No hay transcripción para organizar"
                                )
                        );
            }

            Map<String, String> resultado =
                    iaService.organizarPrimeraEntrevista(texto);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "No se pudo organizar la entrevista"
                            )
                    );
        }
    }

    // =====================================
    // GET TEST
    // =====================================

    @GetMapping("/resumir")
    public ResponseEntity<String> resumirGet() {

        String resultado =
                iaService.resumirObservacion(
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