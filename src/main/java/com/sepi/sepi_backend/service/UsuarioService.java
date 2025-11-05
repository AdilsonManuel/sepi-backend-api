package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.dto.AtualizacaoUsuarioRequest;
import com.sepi.sepi_backend.dto.RegistroUsuarioRequest;
import com.sepi.sepi_backend.entity.Emprestador;
import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.entity.Solicitante;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.exception.RecursoNaoEncontradoException;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela lógica de negócio do Usuário (CRUD, Validações,
 * Registo).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService
{

    private final UsuarioRepository usuarioRepository;
    private final LocalidadeService localidadeService;
    private final PasswordEncoder passwordEncoder;

//	public UsuarioService(UsuarioRepository usuarioRepository, LocalidadeService localidadeService)
//	{
//		this.usuarioRepository = usuarioRepository;
//		this.localidadeService = localidadeService;
//	}
    // Nota: Em um projeto real, injetaríamos um PasswordEncoder.
    /**
     * Processa o registro de um novo usuário (Solicitante ou Emprestador).
     *
     * @param request DTO com os dados do usuário.
     * @return O objeto Usuario salvo.
     */
    @Transactional
    public Usuario registrarNovoUsuario (RegistroUsuarioRequest request)
    {
        if (usuarioRepository.existsByEmail(request.getEmail()))
        {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }

        Localidade localidade = localidadeService.findByPk(request.getPkLocalidade())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Localidade com PK " + request.getPkLocalidade() + " não encontrada."));

        // Nota: A palavra-passe deve ser encriptada aqui (ex: BCryptPasswordEncoder.encode(request.getPalavraPasse())).
        String palavraPasseCifrada = passwordEncoder.encode(request.getPalavraPasse()); // Simplificado para fins de modelagem

        Usuario novoUsuario;
        if (null == request.getTipoUsuario())
        {
            throw new IllegalArgumentException("Tipo de usuário inválido para registro.");
        }
        else
        {
            switch (request.getTipoUsuario())
            {
                case SOLICITANTE ->
                    novoUsuario = new Solicitante();
                case EMPRESTADOR ->
                    novoUsuario = new Emprestador();
                default ->
                    throw new IllegalArgumentException("Tipo de usuário inválido para registro.");
            }
        }

        // Mapeamento dos campos base
        novoUsuario.setNomeCompleto(request.getNomeCompleto());
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setPalavraPasse(palavraPasseCifrada);
        novoUsuario.setTelefone(request.getTelefone());
        novoUsuario.setTipoUsuario(request.getTipoUsuario());
        novoUsuario.setLocalidade(localidade);
        novoUsuario.setNumeroDocumento(request.getNumeroDocumento());
        novoUsuario.setVerificado(false); // RF03: A verificação de documentos será manual ou por IA.

        return usuarioRepository.save(novoUsuario);
    }

    // =========================================================================
    // Métodos de CRUD Adicionais / Gestão de Perfil
    // =========================================================================
    /**
     * Obtém um usuário pelo seu ID.
     *
     * @param id ID do usuário.
     * @return Usuário encontrado.
     */
    public Usuario obterPorId (Long id)
    {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Lista todos os usuários. (Uso Admin)
     *
     * @return Lista de todos os usuários.
     */
    public List<Usuario> listarTodos ()
    {
        return usuarioRepository.findAll();
    }

    /**
     * Atualiza os dados do perfil (nome, email, telefone, localidade).
     *
     * @param id ID do usuário a ser atualizado.
     * @param request DTO com os novos dados.
     * @return Usuário atualizado.
     */
    @Transactional
    public Usuario atualizarPerfil (Long id, AtualizacaoUsuarioRequest request)
    {
        Usuario usuario = obterPorId(id);

        // 1. Atualiza dados básicos
        usuario.setNomeCompleto(request.getNomeCompleto());
        usuario.setTelefone(request.getTelefone());

        // 2. Valida e atualiza Email (se alterado)
        if (!usuario.getEmail().equalsIgnoreCase(request.getEmail()))
        {
            if (usuarioRepository.existsByEmail(request.getEmail()))
            {
                throw new DataIntegrityViolationException("O novo email já está em uso por outro usuário.");
            }
            usuario.setEmail(request.getEmail());
        }

        // 3. Valida e atualiza Localidade
        Localidade novaLocalidade = localidadeService.findByPk(request.getPkLocalidade())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nova Localidade com PK " + request.getPkLocalidade() + " não encontrada."));
        usuario.setLocalidade(novaLocalidade);

        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza o status de verificação de um usuário (RF20). Simula a aprovação
     * de documentos pelo Administrador/IA.
     *
     * @param id ID do usuário.
     * @param status Novo status de verificação.
     * @return Usuário atualizado.
     */
    @Transactional
    public Usuario atualizarStatusVerificacao (Long id, boolean status)
    {
        Usuario usuario = obterPorId(id);

        if (usuario.isVerificado() == status)
        {
            throw new IllegalArgumentException("O status de verificação já é " + (status ? "VERIFICADO" : "NÃO VERIFICADO"));
        }

        usuario.setVerificado(status);
        return usuarioRepository.save(usuario);
    }

    /**
     * Atualiza o status de atividade (ativo/inativo) de um usuário (Remoção
     * Lógica). (Uso Admin)
     *
     * @param id ID do usuário.
     * @param status Novo status (ativo=true/inativo=false).
     * @return Usuário atualizado.
     */
    @Transactional
    public Usuario desativarConta (Long id)
    {
        Usuario usuario = obterPorId(id);
        if (!usuario.isAtivo())
        {
            throw new IllegalArgumentException("A conta do usuário com ID " + id + " já está desativada.");
        }
        usuario.setAtivo(false); // Remoção Lógica
        return usuarioRepository.save(usuario);
    }

    /**
     * Reativa a conta de um usuário (Uso Admin).
     *
     * @param id ID do usuário.
     * @return Usuário reativado.
     */
    @Transactional
    public Usuario reativarConta (Long id)
    {
        Usuario usuario = obterPorId(id);
        if (usuario.isAtivo())
        {
            throw new IllegalArgumentException("A conta do usuário com ID " + id + " já está ativa.");
        }
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

}
