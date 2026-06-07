package com.mycompany.consultas.psicologicas.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.mycompany.consultas.psicologicas.dto.UsuarioRequest;
import com.mycompany.consultas.psicologicas.model.RolUsuario;
import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    
    @PostMapping
    public Usuario crear(
            @Valid @RequestBody UsuarioRequest request,
            Authentication authentication
    ) {

        RolUsuario rolLogueado =
                RolUsuario.valueOf(
                        authentication
                                .getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority()
                                .replace("ROLE_", "")
                );

        return usuarioService.crearUsuario(
                request,
                rolLogueado
        );
    }
    @PutMapping("/{id}")
public Usuario actualizar(
        @PathVariable Long id,
        @RequestBody UsuarioRequest request,
        Authentication authentication
) {

    RolUsuario rolLogueado =
            RolUsuario.valueOf(
                    authentication
                            .getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority()
                            .replace("ROLE_", "")
            );

    return usuarioService.actualizarUsuario(
            id,
            request,
            rolLogueado
    );
}
@DeleteMapping("/{id}")
public void eliminar(
        @PathVariable Long id,
        Authentication authentication
) {

    RolUsuario rolLogueado =
            RolUsuario.valueOf(
                    authentication
                            .getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority()
                            .replace("ROLE_", "")
            );

    usuarioService.eliminarUsuario(
            id,
            rolLogueado
    );
}

@PutMapping("/{id}/reactivar")
public void reactivar(
        @PathVariable Long id
) {

    usuarioService.reactivarUsuario(id);
}
@GetMapping
public List<Usuario> listar(Authentication auth) {

    String rol = auth.getAuthorities()
            .iterator()
            .next()
            .getAuthority();

    System.out.println("ROL LOGUEADO: " + rol);

    if (rol.equals("ROLE_ADMIN")) {
        return usuarioService.listar();
    }

    if (rol.equals("ROLE_PROFESIONAL")) {
        return usuarioService.findByRolIn(
                List.of("PROFESIONAL", "OPERADOR")
        );
    }

    return List.of();
}
}