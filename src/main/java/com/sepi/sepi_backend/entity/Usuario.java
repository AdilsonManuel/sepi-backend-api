/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.StatusUsuario;
import com.sepi.sepi_backend.enums.TipoDocumento;
import com.sepi.sepi_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author azm
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeCompleto;

    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String palavraPasse; // Armazenar HASH - Simplificado para String no modelo

    @Column(nullable = false, length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @ManyToOne
    @JoinColumn(name = "fk_localidade", nullable = false)
    private Localidade localidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoDocumento tipoDocumento;

    private String numeroDocumento;
    private boolean ativo = true;

    @Column(name = "token_recuperacao_senha")
    private String tokenRecuperacaoSenha;

    @Column(name = "data_expiracao_token")
    private LocalDateTime dataExpiracaoToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario statusVerificacao = StatusUsuario.NAO_VERIFICADO;

    // Construtor para registro (sem ID) - Actualizado
    public Usuario (String nomeCompleto, String email, String palavraPasse, String telefone, TipoUsuario tipoUsuario, Localidade localidade, String numeroDocumento, LocalDate dataNascimento)
    {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.palavraPasse = palavraPasse;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.localidade = localidade;
        this.numeroDocumento = numeroDocumento;
        this.dataNascimento = dataNascimento;
    }
}
