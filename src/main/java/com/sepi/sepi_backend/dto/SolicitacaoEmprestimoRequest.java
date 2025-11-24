/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.enums.MotivoEmprestimo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author azm
 */
@Data
public class SolicitacaoEmprestimoRequest
{

    @NotNull(message = "O valor solicitado é obrigatório.")
    @Positive(message = "O valor solicitado deve ser positivo.")
    private BigDecimal valorSolicitado;

    @NotNull(message = "O motivo é obrigatório.")
    private MotivoEmprestimo motivo;

    private String descricaoMotivoOutro;
}
