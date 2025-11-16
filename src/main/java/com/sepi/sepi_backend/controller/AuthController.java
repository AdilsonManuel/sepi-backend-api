/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.ForgotPasswordRequest;
import com.sepi.sepi_backend.dto.JwtAuthenticationResponse;
import com.sepi.sepi_backend.dto.LoginRequest;
import com.sepi.sepi_backend.dto.ResetPasswordRequest;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import com.sepi.sepi_backend.security.jwt.JwtTokenProvider;
import com.sepi.sepi_backend.service.UsuarioService;
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
    private final UsuarioService usuarioService; // Injetado para a lógica de reset

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginRequest loginRequest)
    {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPalavraPasse()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação."));

        return ResponseEntity.ok(new JwtAuthenticationResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNomeCompleto(),
                usuario.getTipoUsuario()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword (@Valid @RequestBody ForgotPasswordRequest request)
    {
        String token = usuarioService.processarEsqueciSenha(request.getEmail());

        // Em produção, a resposta seria genérica. Em dev, retornamos o token para facilitar.
        String responseMessage = "Se o email estiver registado, um token de recuperação foi enviado. (Token: " + token + ")";

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword (@Valid @RequestBody ResetPasswordRequest request)
    {
        try
        {
            usuarioService.resetarSenha(request);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
