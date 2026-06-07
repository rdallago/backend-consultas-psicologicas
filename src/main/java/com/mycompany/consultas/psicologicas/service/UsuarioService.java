package com.mycompany.consultas.psicologicas.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mycompany.consultas.psicologicas.dto.UsuarioRequest;
import com.mycompany.consultas.psicologicas.model.RolUsuario;
import com.mycompany.consultas.psicologicas.model.Usuario;
import com.mycompany.consultas.psicologicas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
   
    public UsuarioService(
            UsuarioRepository usuarioRepo,
            PasswordEncoder encoder
    ) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
    }

    public Usuario guardar(Usuario u) {

        u.setPassword(
                encoder.encode(u.getPassword())
        );

        return usuarioRepo.save(u);
    }

    public List<Usuario> listar() {
        return usuarioRepo.findAll();
    }
    public Usuario crearUsuario(
        UsuarioRequest request,
        RolUsuario rolLogueado
) {
        
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            
    throw new RuntimeException("El nombre de usuario es obligatorio");
}
        if (!passwordValida(request.getPassword())) {

    throw new RuntimeException(
        "La contraseña debe tener al menos 6 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    );
}

if (request.getPassword() == null || request.getPassword().isBlank()) {
    throw new RuntimeException("La contraseña es obligatoria");
}

    if (rolLogueado == RolUsuario.OPERADOR) {
        throw new RuntimeException(
                "No tiene permisos para crear usuarios"
        );
    }

    if (rolLogueado == RolUsuario.PROFESIONAL
            && request.getRol() == RolUsuario.ADMIN) {

        throw new RuntimeException(
                "Un profesional no puede crear administradores"
        );
    }

    if (usuarioRepo.findByUsername(
            request.getUsername()
    ).isPresent()) {

        throw new RuntimeException(
                "El usuario ya existe"
        );
    }

    Usuario usuario = new Usuario();

    usuario.setUsername(
            request.getUsername()
    );

    usuario.setPassword(
            encoder.encode(
                    request.getPassword()
            )
    );

    usuario.setRol(
            request.getRol()
    );

    usuario.setActivo(true);

    return usuarioRepo.save(usuario);
}
    public Usuario buscarPorId(Long id) {

    return usuarioRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Usuario no encontrado"
                    )
            );
}
    public Usuario actualizarUsuario(
        Long id,
        UsuarioRequest request,
        RolUsuario rolLogueado
) {

    Usuario usuario = buscarPorId(id);

    if (rolLogueado == RolUsuario.OPERADOR) {

        throw new RuntimeException(
                "No tiene permisos"
        );
    }

    if (rolLogueado == RolUsuario.PROFESIONAL
            && usuario.getRol() == RolUsuario.ADMIN) {

        throw new RuntimeException(
                "No puede modificar administradores"
        );
    }

    if (rolLogueado == RolUsuario.PROFESIONAL
            && request.getRol() == RolUsuario.ADMIN) {

        throw new RuntimeException(
                "No puede asignar rol ADMIN"
        );
    }
if (!passwordValida(request.getPassword())) {

    throw new RuntimeException(
        "La contraseña debe tener al menos 6 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    );
}

usuarioRepo.findByUsername(request.getUsername())
    .ifPresent(existente -> {

        if (!existente.getId().equals(id)) {

            throw new RuntimeException(
                "Ya existe un usuario registrado con ese nombre"
            );
        }
    });
    usuario.setUsername(
            request.getUsername()
    );

    if (request.getPassword() != null
            && !request.getPassword().isBlank()) {

        usuario.setPassword(
                encoder.encode(
                        request.getPassword()
                )
        );
    }

    usuario.setRol(
            request.getRol()
    );

    return usuarioRepo.save(usuario);
}
   public void eliminarUsuario(
        Long id,
        RolUsuario rolLogueado
) {

    Usuario usuario = buscarPorId(id);

    if (rolLogueado == RolUsuario.OPERADOR) {

        throw new RuntimeException(
                "No tiene permisos"
        );
    }

    if (rolLogueado == RolUsuario.PROFESIONAL
            && usuario.getRol() == RolUsuario.ADMIN) {

        throw new RuntimeException(
                "No puede eliminar administradores"
        );
    }

    usuario.setActivo(false);

    usuarioRepo.save(usuario);
}
   
   public void reactivarUsuario(Long id) {

    Usuario usuario = buscarPorId(id);

    usuario.setActivo(true);

    usuarioRepo.save(usuario);
}
   public List<Usuario> findByRolIn(List<String> roles) {
    return usuarioRepo.findByRolIn(roles);
}
   private boolean passwordValida(String password) {

    return password.matches(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_-]).{6,}$"
    );
}
    
}