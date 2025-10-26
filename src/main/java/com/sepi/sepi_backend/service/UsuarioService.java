package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.exception.BadRequestException;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService
{
	@Autowired
	private final UsuarioRepository usuarioRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	
	public List<Usuario> listarTodos()
	{
		return usuarioRepository.findAll();
	}
	
	public Usuario buscarPorId(Long id)
	{
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Usuário não encontrado com ID: " + id));
	}
	
	public List<Usuario> buscarPorTipo(Usuario.TipoUsuario tipo)
	{
		return usuarioRepository.findByTipoUsuario(tipo);
	}
	
	public long contarPorTipo(Usuario.TipoUsuario tipo)
	{
		return usuarioRepository.findByTipoUsuario(tipo).size();
	}
	
	public boolean emailJaExiste(String email)
	{
		return usuarioRepository.existsByEmail(email);
	}
	
	@Transactional
	public Usuario salvar(Usuario usuario)
	{
		if (usuario.getId() == null && emailJaExiste(usuario.getEmail()))
		{
			throw new BadRequestException("Email já está em uso");
		}
		
		if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$"))
		{
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		}
		
		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public Usuario atualizar(Long id, Usuario atualizacao)
	{
		Usuario existente = buscarPorId(id);
		existente.setNome(atualizacao.getNome());
		existente.setEmail(atualizacao.getEmail());
		existente.setDocumentoIdentificacao(atualizacao.getDocumentoIdentificacao());
		return usuarioRepository.save(existente);
	}
	
	@Transactional
	public void alterarSenha(Long id, String senhaAtual, String novaSenha)
	{
		Usuario usuario = buscarPorId(id);
		if (!passwordEncoder.matches(senhaAtual, usuario.getSenha()))
		{
			throw new BadRequestException("Senha atual incorreta");
		}
		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);
	}
	
	@Transactional
	public void excluir(Long id)
	{
		Usuario usuario = buscarPorId(id);
		usuarioRepository.delete(usuario);
	}
}
