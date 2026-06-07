package com.mycompany.consultas.psicologicas.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.repository.UsuarioRepository;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepo;
     
    public JwtFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepo
    ) {
        this.jwtService = jwtService;
        this.usuarioRepo = usuarioRepo;
    }
    
    @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
) throws ServletException, IOException {

    String path = request.getRequestURI();

if (path.startsWith("/api/backups")) {
    filterChain.doFilter(request, response);
    return;
}

    System.out.println("=== JWT FILTER ===");

    String authHeader =
            request.getHeader("Authorization");

    System.out.println(
            "HEADER: " + authHeader
    );

    if (authHeader != null &&
            authHeader.startsWith("Bearer ")) {

        String token =
                authHeader.substring(7);

        if (jwtService.validar(token)) {

            System.out.println(
                    "TOKEN VALIDO"
            );

            String username =
                    jwtService.extraerUsername(token);

            System.out.println(
                    "USUARIO: " + username
            );

            Usuario usuario =
                    usuarioRepo.findByUsername(username)
                            .orElse(null);

            if (usuario != null) {

                System.out.println(
                        "ROL BD: " + usuario.getRol()
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + usuario.getRol().name()
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "AUTENTICADO CON: "
                        + authentication.getAuthorities()
                );
            } else {

                System.out.println(
                        "USUARIO NO ENCONTRADO EN BD"
                );
            }

        } else {

            System.out.println(
                    "TOKEN INVALIDO"
            );
        }
    } else {

        System.out.println(
                "SIN HEADER AUTHORIZATION"
        );
    }

    filterChain.doFilter(request, response);
}


}