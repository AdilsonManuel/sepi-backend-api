package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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

    private Double saldo = 0.0; // Saldo disponível para empréstimos (RF23/RF24)

    public Emprestador (String nomeCompleto, String email, String palavraPasse, String telefone, TipoUsuario tipoUsuario, Localidade localidade, String numeroDocumento)
    {
        super(nomeCompleto, email, palavraPasse, telefone, tipoUsuario, localidade, numeroDocumento);
    }

    // Poderia ter mais atributos específicos como preferência de risco, etc.
}
