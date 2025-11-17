/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.entity.Emprestimo;
import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import com.sepi.sepi_backend.enums.MotivoEmprestimo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class EmprestimoResponse
{

    private Long id;
    private Long solicitanteId;
    private String solicitanteNome;
    private BigDecimal valorSolicitado;
    private Integer prazoPagamentoMeses;
    private MotivoEmprestimo motivo;
    private String descricaoMotivoOutro;
    private EstadoEmprestimo estado;
    private LocalDateTime dataSolicitacao;
    private Double taxaJuroDefinida; // Pode ser nulo se pendente

    public EmprestimoResponse (Emprestimo emprestimo)
    {
        this.id = emprestimo.getId();
        this.solicitanteId = emprestimo.getSolicitante().getId();
        this.solicitanteNome = emprestimo.getSolicitante().getNomeCompleto();
        this.valorSolicitado = emprestimo.getValorSolicitado();
        this.prazoPagamentoMeses = emprestimo.getPrazoPagamentoMeses();
        this.motivo = emprestimo.getMotivo();
        this.descricaoMotivoOutro = emprestimo.getDescricaoMotivoOutro();
        this.estado = emprestimo.getEstado();
        this.dataSolicitacao = emprestimo.getDataSolicitacao();
        this.taxaJuroDefinida = emprestimo.getTaxaJuroDefinida();
    }
}
