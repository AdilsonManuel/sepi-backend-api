/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author azm
 */
public class CalculoFinanceiro
{

    // Taxas Fixas
    public static final BigDecimal TAXA_JURO_EMPRESTIMO = new BigDecimal("0.10"); // 10%
    public static final BigDecimal TAXA_COMISSAO_TOTAL = new BigDecimal("0.05");  // 5%
    public static final BigDecimal TAXA_JURO_MORA_DIA = new BigDecimal("0.0002"); // 0.02%

    // Divisão da Comissão (2/5 para Fundo, 3/5 para Plataforma)
    private static final BigDecimal FATOR_FUNDO_GARANTIA = new BigDecimal("0.40"); // 2 de 5 = 40%
    private static final BigDecimal FATOR_RECEITA_PLATAFORMA = new BigDecimal("0.60"); // 3 de 5 = 60%

    /**
     * Calcula o Juro Normal do Empréstimo (10%).
     */
    public static BigDecimal calcularJuroNormal (BigDecimal valorEmprestimo)
    {
        return valorEmprestimo.multiply(TAXA_JURO_EMPRESTIMO).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o Total a Receber pelo Emprestador (Capital + Juros).
     */
    public static BigDecimal calcularTotalReceberEmprestador (BigDecimal valorEmprestimo)
    {
        return valorEmprestimo.add(calcularJuroNormal(valorEmprestimo));
    }

    /**
     * Calcula a Comissão Total paga pelo Solicitante (5%).
     */
    public static BigDecimal calcularComissaoTotal (BigDecimal valorEmprestimo)
    {
        return valorEmprestimo.multiply(TAXA_COMISSAO_TOTAL).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula a parte da comissão que vai para o Fundo de Garantia (2%).
     */
    public static BigDecimal calcularParteFundoGarantia (BigDecimal comissaoTotal)
    {
        return comissaoTotal.multiply(FATOR_FUNDO_GARANTIA).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula a parte da comissão que é Receita da Plataforma (3%).
     */
    public static BigDecimal calcularParteReceitaPlataforma (BigDecimal comissaoTotal)
    {
        return comissaoTotal.multiply(FATOR_RECEITA_PLATAFORMA).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula Juros de Mora por atraso (0.02% ao dia).
     */
    public static BigDecimal calcularJurosMora (BigDecimal valorEmprestimo, long diasAtraso)
    {
        if (diasAtraso <= 0)
        {
            return BigDecimal.ZERO;
        }
        BigDecimal fatorDias = new BigDecimal(diasAtraso);
        return valorEmprestimo.multiply(TAXA_JURO_MORA_DIA).multiply(fatorDias).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula proporção para distribuição automática de investimento. Fórmula:
     * proporcaoSolicitante = valorPedidoSolicitante / totalPedidos
     */
    public static BigDecimal calcularProporcaoSolicitante (BigDecimal valorPedidoSolicitante, BigDecimal totalPedidos)
    {
        if (totalPedidos.compareTo(BigDecimal.ZERO) == 0)
        {
            return BigDecimal.ZERO;
        }
        return valorPedidoSolicitante.divide(totalPedidos, 4, RoundingMode.HALF_UP); // 4 casas decimais para precisão
    }

    /**
     * Calcula o prazo em dias baseado na faixa de valor (Regra de Negócio 1.1).
     */
    public static int determinarPrazoPorValor (BigDecimal valor)
    {
        double v = valor.doubleValue();
        if (v <= 100_000)
        {
            return 30;
        }
        if (v <= 300_000)
        {
            return 60;
        }
        if (v <= 600_000)
        {
            return 90;
        }
        if (v <= 1_200_000)
        {
            return 180;
        }
        if (v <= 2_500_000)
        {
            return 360;
        }
        throw new IllegalArgumentException("Valor excede o limite máximo permitido de 2.500.000 Kz");
    }
}
