package com.sepi.sepi_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emprestador")
@PrimaryKeyJoinColumn(name = "emprestador_id")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Emprestador extends Usuario
{

    private BigDecimal saldo = BigDecimal.ZERO;

    // Novos campos conforme "Nota importante.txt"
    private boolean contratoAceite = false;
    private LocalDateTime dataAceiteContrato;
    private String versaoContratoAceite;
}
