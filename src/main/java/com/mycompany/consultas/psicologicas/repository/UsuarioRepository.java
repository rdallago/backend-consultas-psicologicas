/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.consultas.psicologicas.repository;
import java.util.List;

/**
 *
 * @author Informatica
 */


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycompany.consultas.psicologicas.model.Usuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRolIn(List<String> roles);
}