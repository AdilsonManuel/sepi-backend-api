/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.enums.NivelRisco;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class AvaliacaoRiscoRequest
{

    @NotNull(message = "O nível de risco é obrigatório.")
    private NivelRisco nivelRisco;

    @NotNull(message = "O limite aprovado é obrigatório.")
    @PositiveOrZero(message = "O limite deve ser positivo ou zero.")
    private BigDecimal limiteAprovado;
}
