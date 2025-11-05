/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.JwtAuthenticationResponse;
import com.sepi.sepi_backend.dto.LoginRequest;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import com.sepi.sepi_backend.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
/**
 * Controller responsável pelos endpoints de autenticação (Login, Logout, etc).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    /**
     * RF01: Endpoint de Login.POST /api/auth/login
     *
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginRequest loginRequest)
    {

        // 1. Autenticar no Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPalavraPasse()
                )
        );

        // 2. Definir a autenticação no contexto de segurança
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gerar o Token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // 4. Obter dados do usuário para a resposta
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação."));

        // 5. Retornar o token e dados do usuário
        return ResponseEntity.ok(new JwtAuthenticationResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNomeCompleto(),
                usuario.getTipoUsuario()
        ));
    }

    // NOTA: Em um ambiente real, seria necessário implementar a lógica de 
    // "Esqueci Minha Senha" (geração de token de redefinição).
}
