/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.sepi.sepi_backend.enums;

import java.math.BigDecimal;

/**
 *
 * @author azm
 */
public enum NivelRisco
{
    BAIXO(new BigDecimal("1.00")), // 100% do teto
    MEDIO(new BigDecimal("0.70")), // 70% do teto
    ALTO(new BigDecimal("0.40")), // 40% do teto
    MUITO_ALTO(BigDecimal.ZERO);        // 0% do teto (Recusado)

    private final BigDecimal fatorLimite;

    NivelRisco (BigDecimal fatorLimite)
    {
        this.fatorLimite = fatorLimite;
    }

    public BigDecimal getFatorLimite ()
    {
        return fatorLimite;
    }
}
