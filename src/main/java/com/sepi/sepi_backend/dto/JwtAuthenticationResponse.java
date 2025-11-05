/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.enums.TipoUsuario;
import lombok.Data;

/**
 *
 * @author azm
 */
/**
 * DTO para retornar o token JWT e informações básicas do usuário após o login.
 */
@Data
public class JwtAuthenticationResponse
{

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String nomeCompleto;
    private TipoUsuario tipoUsuario;

    public JwtAuthenticationResponse (String accessToken, Long userId, String email, String nomeCompleto, TipoUsuario tipoUsuario)
    {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.nomeCompleto = nomeCompleto;
        this.tipoUsuario = tipoUsuario;
    }
}
