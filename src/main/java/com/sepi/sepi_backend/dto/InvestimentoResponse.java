/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.entity.Investimento;
import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class InvestimentoResponse
{

    private Long id;
    private Long emprestimoId;
    private String nomeSolicitante;
    private BigDecimal valorInvestido;
    private BigDecimal totalAReceberEstimado;
    private EstadoEmprestimo estadoEmprestimo;
    private LocalDateTime dataInvestimento;

    public InvestimentoResponse (Investimento investimento)
    {
        this.id = investimento.getId();
        this.emprestimoId = investimento.getEmprestimo().getId();
        this.nomeSolicitante = investimento.getEmprestimo().getSolicitante().getNomeCompleto();
        this.valorInvestido = investimento.getValorInvestido();
        this.totalAReceberEstimado = investimento.getTotalAReceberEstimado();
        this.estadoEmprestimo = investimento.getEmprestimo().getEstado();
        this.dataInvestimento = investimento.getDataInvestimento();
    }
}
