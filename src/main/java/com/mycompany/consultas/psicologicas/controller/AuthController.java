package com.mycompany.consultas.psicologicas.controller;
import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.repository.UsuarioRepository;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.mycompany.consultas.psicologicas.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UsuarioRepository usuarioRepo;

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

    String username = body.get("username");
    String password = body.get("password");

    authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
    );

    Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    String token = jwtService.generarToken(
            username,
            usuario.getRol().name()
    );

   return ResponseEntity.ok(
        Map.of(
                "token", token,
                "rol", usuario.getRol().name()
        )
);
}
}