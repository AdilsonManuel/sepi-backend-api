package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
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

    private Double limiteCredito = 0.0; // Definido pela IA/Administrador
    private Double indicadorConfianca = 0.0; // Baseado em feedback/histórico (RF19)
    private String motivoEmprestimo; // RF22

    public Solicitante (String nomeCompleto, String email, String palavraPasse, String telefone, TipoUsuario tipoUsuario, Localidade localidade, String numeroDocumento, LocalDate dataNascimento)
    {
        super(nomeCompleto, email, palavraPasse, telefone, tipoUsuario, localidade, numeroDocumento, dataNascimento);
    }
}
