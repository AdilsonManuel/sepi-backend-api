/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class InvestirRequest
{

    @NotNull(message = "O ID do empréstimo é obrigatório.")
    private Long emprestimoId;

    @NotNull(message = "O valor a investir é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    private BigDecimal valorInvestimento;
}
