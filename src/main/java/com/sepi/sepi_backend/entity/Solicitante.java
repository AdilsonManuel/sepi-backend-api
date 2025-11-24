package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.NivelRisco;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitante")
@PrimaryKeyJoinColumn(name = "solicitante_id")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Solicitante extends Usuario
{

    // Definido pela IA ou Admin na fase de "AVALIADO"
    @Enumerated(EnumType.STRING)
    private NivelRisco nivelRisco;

    private BigDecimal limiteCreditoAprovado = BigDecimal.ZERO;
    private Double indicadorConfianca = 0.0; // Score 0-5
    private String motivoEmprestimo;
}
