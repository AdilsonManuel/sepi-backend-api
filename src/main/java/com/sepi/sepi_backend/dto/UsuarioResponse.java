package com.sepi.sepi_backend.dto;

import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.enums.TipoUsuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse
{
	private Long id;
	private String nomeCompleto;
	private String email;
	private String telefone;
	private TipoUsuario tipoUsuario;
	private String localidade;
	private String numeroDocumento;
	private LocalDateTime dataCadastro;
	
	public UsuarioResponse(Usuario usuario)
	{
		this.id = usuario.getId();
		this.nomeCompleto = usuario.getNomeCompleto();
		this.email = usuario.getEmail();
		this.telefone = usuario.getTelefone();
		this.tipoUsuario = usuario.getTipoUsuario();
		this.localidade = usuario.getLocalidade() != null ? usuario.getLocalidade().getDesignacao() : "Não Informada";
		this.numeroDocumento = usuario.getNumeroDocumento();
		this.dataCadastro = usuario.getDataCadastro();
	}
}
