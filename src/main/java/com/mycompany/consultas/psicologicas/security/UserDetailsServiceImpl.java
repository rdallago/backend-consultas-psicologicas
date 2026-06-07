package com.mycompany.consultas.psicologicas.security;

import java.util.List;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UserDetailsServiceImpl(
            UsuarioRepository usuarioRepo
    ) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        Usuario u = usuarioRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        // Verificar que el usuario esté activo
        if (!u.isActivo()) {

            throw new DisabledException(
                    "Usuario inactivo"
            );
        }

        return new User(
                u.getUsername(),
                u.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + u.getRol().name()
                        )
                )
        );
    }
}