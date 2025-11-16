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
@Data
public class ForgotPasswordRequest
{

    @Email(message = "Email deve ser válido.")
    @NotBlank(message = "Email é obrigatório.")
    private String email;
}
