/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class DepositoRequest
{

    @NotNull(message = "O valor do depósito é obrigatório.")
    @DecimalMin(value = "20000.0", message = "O depósito mínimo é de 20.000 Kz.")
    private BigDecimal valor;
}
