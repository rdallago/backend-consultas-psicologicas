package com.mycompany.consultas.psicologicas.dto;
import jakarta.validation.constraints.NotBlank;
import com.mycompany.consultas.psicologicas.model.RolUsuario;

public class UsuarioRequest {

    

    
        @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    private String password;

    private RolUsuario rol;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}