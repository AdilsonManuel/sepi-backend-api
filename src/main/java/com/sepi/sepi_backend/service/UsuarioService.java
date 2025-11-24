package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.dto.AtualizacaoUsuarioRequest;
import com.sepi.sepi_backend.dto.RegistroUsuarioRequest;
import com.sepi.sepi_backend.dto.ResetPasswordRequest;
import com.sepi.sepi_backend.entity.Emprestador;
import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.entity.Solicitante;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.enums.NivelRisco;
import com.sepi.sepi_backend.enums.StatusUsuario;
import com.sepi.sepi_backend.exception.RecursoNaoEncontradoException;
import com.sepi.sepi_backend.exception.RegraNegocioException;
import com.sepi.sepi_backend.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        novoUsuario.setTipoDocumento(request.getTipoIdentificacao()); // Preenche a coluna que estava NULL
        novoUsuario.setDataNascimento(request.getDataNascimento());
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

    /**
     * Atualiza o status de verificação de um usuário (RF20). Simula a aprovação
     * de documentos pelo Administrador/IA.
     *
     * * @param id ID do usuário.
     * @param aprovado true para aprovar (muda para VERIFICADO), false para
     * reprovar/resetar.
     * @return Usuário atualizado.
     */
    @Transactional
    public Usuario atualizarStatusVerificacao (Long id, boolean aprovado)
    {
        Usuario usuario = obterPorId(id);

        StatusUsuario novoStatus = aprovado ? StatusUsuario.VERIFICADO : StatusUsuario.NAO_VERIFICADO;

        if (usuario.getStatusVerificacao() == novoStatus)
        {
            throw new IllegalArgumentException("O status de verificação já é " + novoStatus);
        }

        if (!aprovado && usuario.getStatusVerificacao() == StatusUsuario.AVALIADO)
        {
        }

        usuario.setStatusVerificacao(novoStatus);
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

    @Transactional(readOnly = true)
    public Solicitante getSolicitanteAutenticado (String email)
    {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!(usuario instanceof Solicitante))
        {
            throw new IllegalArgumentException("O usuário autenticado não é um Solicitante.");
        }
        return (Solicitante) usuario;
    }

    /**
     * Define o nível de risco e o limite de crédito de um Solicitante.Executado
     * pela IA ou Administrador.Transita o estado de VERIFICADO para AVALIADO.
     *
     * @param id
     * @param nivelRisco
     * @param limiteAprovado
     * @return
     */
    @Transactional
    public Usuario avaliarRiscoSolicitante (Long id, NivelRisco nivelRisco, java.math.BigDecimal limiteAprovado)
    {
        Usuario usuario = obterPorId(id);

        if (!(usuario instanceof Solicitante))
        {
            throw new IllegalArgumentException("Apenas Solicitantes podem ser avaliados quanto ao risco.");
        }

        // Regra: Só pode avaliar se já estiver VERIFICADO (Documentos OK)
        // Opcional: Se quiser permitir re-avaliação, remova a checagem estrita ou adapte.
        if (usuario.getStatusVerificacao() == StatusUsuario.NAO_VERIFICADO)
        {
            throw new RegraNegocioException("O usuário precisa ter os documentos verificados antes da análise de risco.");
        }

        Solicitante solicitante = (Solicitante) usuario;
        solicitante.setNivelRisco(nivelRisco);
        solicitante.setLimiteCreditoAprovado(limiteAprovado);

        // Atualiza o status global
        solicitante.setStatusVerificacao(StatusUsuario.AVALIADO);

        return usuarioRepository.save(solicitante);
    }

}
