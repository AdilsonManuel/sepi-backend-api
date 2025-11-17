/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import com.sepi.sepi_backend.enums.MotivoEmprestimo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author azm
 */
@Entity
@Table(name = "emprestimo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_solicitante", nullable = false)
    private Solicitante solicitante;

    @Column(nullable = false)
    private BigDecimal valorSolicitado;

    @Column(nullable = false)
    private Integer prazoPagamentoMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoEmprestimo motivo;

    private String descricaoMotivoOutro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEmprestimo estado;

    @CreationTimestamp
    private LocalDateTime dataSolicitacao;

    private Double taxaJuroDefinida;
    private BigDecimal limiteAprovado;
    private LocalDateTime dataAprovacao;
    private String notasAnaliseRisco;

}
