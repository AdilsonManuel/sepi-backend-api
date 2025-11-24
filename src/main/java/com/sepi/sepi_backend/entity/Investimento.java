/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "investimento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investimento
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_emprestador", nullable = false)
    private Emprestador emprestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_emprestimo", nullable = false)
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private BigDecimal valorInvestido;

    @CreationTimestamp
    private LocalDateTime dataInvestimento;

    // Calculado no momento do investimento (Valor + 10%)
    // Importante guardar isto fixo para evitar problemas se a taxa mudar no futuro.
    @Column(nullable = false)
    private BigDecimal totalAReceberEstimado;
}
