/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author azm
 */
/**
 * Implementação do UserDetailsService do Spring Security. Carrega o usuário a
 * partir do email (Username).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException
    {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        // Mapeia o TipoUsuario para a Authority do Spring Security
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPalavraPasse(),
                usuario.isAtivo(), // A conta está ativa (RNF21)
                true,
                true,
                usuario.isAtivo(), // Bloqueio se inativo
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getTipoUsuario().name()))
        );
    }
}
