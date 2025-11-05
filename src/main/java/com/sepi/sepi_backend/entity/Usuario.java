/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.sepi.sepi_backend.entity;

import com.sepi.sepi_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
	
	@ManyToOne
	@JoinColumn(name = "fk_localidade", nullable = false)
	private Localidade localidade; // Onde o usuário reside (Município ou Comuna)
	
	// Atributos de verificação e status
	private String numeroDocumento; // BI, Passaporte, etc.
	private boolean verificado = false;
	private boolean ativo = true;
	
	// Construtor para registro (sem ID)
	public Usuario(String nomeCompleto, String email, String palavraPasse, String telefone, TipoUsuario tipoUsuario, Localidade localidade, String numeroDocumento)
	{
		this.nomeCompleto = nomeCompleto;
		this.email = email;
		this.palavraPasse = palavraPasse;
		this.telefone = telefone;
		this.tipoUsuario = tipoUsuario;
		this.localidade = localidade;
		this.numeroDocumento = numeroDocumento;
	}
}