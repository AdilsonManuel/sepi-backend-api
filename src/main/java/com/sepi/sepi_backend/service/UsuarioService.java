package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.dto.AtualizacaoUsuarioRequest;
import com.sepi.sepi_backend.dto.RegistroUsuarioRequest;
import com.sepi.sepi_backend.dto.ResetPasswordRequest;
import com.sepi.sepi_backend.entity.Emprestador;
import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.entity.Solicitante;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.exception.RecursoNaoEncontradoException;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService
{

    private final UsuarioRepository usuarioRepository;
    private final LocalidadeService localidadeService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarNovoUsuario (RegistroUsuarioRequest request)
    {

        int idade = Period.between(request.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 18)
        {
            throw new IllegalArgumentException("O usuário deve ter 18 anos ou mais para se registrar.");
        }

        if (usuarioRepository.existsByEmail(request.getEmail()))
        {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }

        Localidade localidade = localidadeService.findByPk(request.getPkLocalidade())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Localidade com PK " + request.getPkLocalidade() + " não encontrada."));

        String palavraPasseCifrada = passwordEncoder.encode(request.getPalavraPasse());

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

        novoUsuario.setNomeCompleto(request.getNomeCompleto());
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setPalavraPasse(palavraPasseCifrada);
        novoUsuario.setTelefone(request.getTelefone());
        novoUsuario.setTipoUsuario(request.getTipoUsuario());
        novoUsuario.setLocalidade(localidade);
        novoUsuario.setNumeroDocumento(request.getNumeroDocumento());
        novoUsuario.setDataNascimento(request.getDataNascimento());
        novoUsuario.setVerificado(false);
        novoUsuario.setAtivo(true);

        novoUsuario.setTokenRecuperacaoSenha(null);
        novoUsuario.setDataExpiracaoToken(null);

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario obterPorId (Long id)
    {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    public List<Usuario> listarTodos ()
    {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario actualizarPerfil (Long id, AtualizacaoUsuarioRequest request)
    {
        Usuario usuario = obterPorId(id);

        usuario.setNomeCompleto(request.getNomeCompleto());
        usuario.setTelefone(request.getTelefone());

        if (!usuario.getEmail().equalsIgnoreCase(request.getEmail()))
        {
            if (usuarioRepository.existsByEmail(request.getEmail()))
            {
                throw new DataIntegrityViolationException("O novo email já está em uso por outro usuário.");
            }
            usuario.setEmail(request.getEmail());
        }

        Localidade novaLocalidade = localidadeService.findByPk(request.getPkLocalidade())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nova Localidade com PK " + request.getPkLocalidade() + " não encontrada."));
        usuario.setLocalidade(novaLocalidade);

        return usuarioRepository.save(usuario);
    }

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

    @Transactional
    public Usuario desativarConta (Long id)
    {
        Usuario usuario = obterPorId(id);
        if (!usuario.isAtivo())
        {
            throw new IllegalArgumentException("A conta do usuário com ID " + id + " já está desativada.");
        }
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario reactivarConta (Long id)
    {
        Usuario usuario = obterPorId(id);
        if (usuario.isAtivo())
        {
            throw new IllegalArgumentException("A conta do usuário com ID " + id + " já está ativa.");
        }
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public String processarEsqueciSenha (String email)
    {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com email: " + email));

        String token = UUID.randomUUID().toString();

        usuario.setTokenRecuperacaoSenha(token);
        usuario.setDataExpiracaoToken(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        return token;
    }

    @Transactional
    public void resetarSenha (ResetPasswordRequest request)
    {
        Usuario usuario = usuarioRepository.findByTokenRecuperacaoSenha(request.getToken())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Token de recuperação inválido ou não encontrado."));

        if (usuario.getDataExpiracaoToken().isBefore(LocalDateTime.now()))
        {
            usuario.setTokenRecuperacaoSenha(null);
            usuario.setDataExpiracaoToken(null);
            usuarioRepository.save(usuario);

            throw new RuntimeException("Token de recuperação expirado.");
        }

        usuario.setPalavraPasse(passwordEncoder.encode(request.getNovaSenha()));

        usuario.setTokenRecuperacaoSenha(null);
        usuario.setDataExpiracaoToken(null);

        usuarioRepository.save(usuario);
    }

}
