/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @author azm
 */
/**
 * DTO para receber dados de atualização do perfil do usuário. Não inclui a
 * senha, que deve ser atualizada através de um endpoint separado. Todos os
 * campos podem ser opcionais, exceto se houver validação específica no Service.
 */
@Data
public class AtualizacaoUsuarioRequest
{

    @NotBlank(message = "O nome completo não pode estar vazio.")
    private String nomeCompleto;

    @Email(message = "O email deve ser válido.")
    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "O código da localidade é obrigatório.")
    private String pkLocalidade;

    // Outros campos do perfil que o usuário pode querer atualizar (como morada detalhada, etc.) podem ser adicionados aqui.
}
