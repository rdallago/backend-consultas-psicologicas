package com.mycompany.consultas.psicologicas.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario u = usuarioRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario inexistente"));

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