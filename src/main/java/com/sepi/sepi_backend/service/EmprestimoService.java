/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.dto.SolicitacaoEmprestimoRequest;
import com.sepi.sepi_backend.entity.Emprestimo;
import com.sepi.sepi_backend.entity.Solicitante;
import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import com.sepi.sepi_backend.enums.MotivoEmprestimo;
import com.sepi.sepi_backend.exception.RegraNegocioException;
import com.sepi.sepi_backend.repository.EmprestimoRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService
{

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService; // Para obter o usuário logado

    /**
     * Cria uma nova solicitação de empréstimo.
     *
     * @param request DTO com os dados da solicitação.
     * @param emailSolicitante O email do usuário autenticado (JWT).
     * @return A entidade Emprestimo salva.
     */
    @Transactional
    public Emprestimo criarSolicitacao (SolicitacaoEmprestimoRequest request, String emailSolicitante)
    {

        Solicitante solicitante = usuarioService.getSolicitanteAutenticado(emailSolicitante);

        validarSolicitante(solicitante);

        if (request.getMotivo() == MotivoEmprestimo.OUTRO
                && (request.getDescricaoMotivoOutro() == null || request.getDescricaoMotivoOutro().isBlank()))
        {
            throw new RegraNegocioException("A descrição é obrigatória quando o motivo for 'Outro'.");
        }

        Emprestimo emprestimo = Emprestimo.builder()
                .solicitante(solicitante)
                .valorSolicitado(request.getValorSolicitado())
                .prazoPagamentoMeses(request.getPrazoPagamentoMeses())
                .motivo(request.getMotivo())
                .descricaoMotivoOutro(request.getDescricaoMotivoOutro())
                .estado(EstadoEmprestimo.PENDENTE_APROVACAO)
                .build();

        return emprestimoRepository.save(emprestimo);
    }

    /**
     * Valida se o solicitante está apto a pedir um novo empréstimo.
     */
    private void validarSolicitante (Solicitante solicitante)
    {
        if (!solicitante.isVerificado())
        {
            throw new RegraNegocioException("Sua conta ainda não foi verificada. Por favor, aguarde a aprovação dos documentos.");
        }

        List<EstadoEmprestimo> estadosBloqueados = Arrays.asList(
                EstadoEmprestimo.EM_PAGAMENTO,
                EstadoEmprestimo.ATRASADO
        );

        boolean temEmprestimoAtivoOuAtrasado = emprestimoRepository.existsBySolicitanteIdAndEstadoIn(solicitante.getId(), estadosBloqueados);

        if (temEmprestimoAtivoOuAtrasado)
        {
            throw new RegraNegocioException("Não pode solicitar um novo empréstimo enquanto tiver um empréstimo ativo ou em atraso.");
        }
    }

    /**
     * RF08: Lista o histórico de empréstimos de um solicitante específico.
     *
     * @param emailSolicitante Email do usuário autenticado.
     * @return Lista de empréstimos do solicitante.
     */
    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimosPorSolicitante (String emailSolicitante)
    {
        Solicitante solicitante = usuarioService.getSolicitanteAutenticado(emailSolicitante);
        return emprestimoRepository.findBySolicitanteId(solicitante.getId());
    }

    /**
     * RF05: Lista todos os empréstimos aprovados e que aguardam financiamento.
     * Visível para Emprestadores.
     *
     * @return Lista de empréstimos disponíveis para investir.
     */
    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimosParaFinanciamento ()
    {
        return emprestimoRepository.findByEstado(EstadoEmprestimo.APROVADO_AGUARDANDO_FINANCIAMENTO);
    }
}
