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
import com.sepi.sepi_backend.enums.StatusUsuario;
import com.sepi.sepi_backend.exception.RegraNegocioException;
import com.sepi.sepi_backend.repository.EmprestimoRepository;
import com.sepi.sepi_backend.util.CalculoFinanceiro;
import java.math.BigDecimal;
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

        // 1. Validações Prévias (Estado e Empréstimos Ativos)
        validarElegibilidadeSolicitante(solicitante);

        // 2. Validação de Limite de Crédito (CalculoLimiteEmprestimo.txt)
        validarLimiteCredito(solicitante, request.getValorSolicitado());

        // 3. Validação Motivo Outro
        if (request.getMotivo() == MotivoEmprestimo.OUTRO
                && (request.getDescricaoMotivoOutro() == null || request.getDescricaoMotivoOutro().isBlank()))
        {
            throw new RegraNegocioException("A descrição é obrigatória quando o motivo for 'Outro'.");
        }

        // 4. Cálculo Automático do Prazo (SobreModuloEmprestimos.txt)
        int prazoDias = CalculoFinanceiro.determinarPrazoPorValor(request.getValorSolicitado());

        // 5. Criação da Entidade
        Emprestimo emprestimo = Emprestimo.builder()
                .solicitante(solicitante)
                .valorSolicitado(request.getValorSolicitado())
                .prazoPagamentoDias(prazoDias)
                .motivo(request.getMotivo())
                .descricaoMotivoOutro(request.getDescricaoMotivoOutro())
                .estado(EstadoEmprestimo.AGUARDANDO_APROVACAO) // Estado inicial
                // Definição de taxas fixas no contrato (FormulasDosJuros&Comissoes.txt)
                .taxaJuroAplicada(CalculoFinanceiro.TAXA_JURO_EMPRESTIMO)
                .comissaoPlataforma(CalculoFinanceiro.TAXA_COMISSAO_TOTAL)
                .valorJurosMoraDia(CalculoFinanceiro.TAXA_JURO_MORA_DIA)
                .build();

        // 6. Aprovação Automática (SobreModuloEmprestimos.txt - Passo 4)
        // Como já validamos limite, estado e dados, podemos aprovar automaticamente para financiamento.
        emprestimo.setEstado(EstadoEmprestimo.EM_FINANCIAMENTO);

        return emprestimoRepository.save(emprestimo);
    }

    private void validarElegibilidadeSolicitante (Solicitante solicitante)
    {
        if (solicitante.getStatusVerificacao() != StatusUsuario.AVALIADO)
        {
            throw new RegraNegocioException("O usuário precisa estar 'VERIFICADO' e 'AVALIADO' (Risco calculado) antes de pedir empréstimo.");
        }

        List<EstadoEmprestimo> estadosAtivos = Arrays.asList(
                EstadoEmprestimo.EM_FINANCIAMENTO,
                EstadoEmprestimo.FINANCIADO,
                EstadoEmprestimo.EM_PAGAMENTO,
                EstadoEmprestimo.EM_ATRASO
        );

        boolean temEmprestimoAtivo = emprestimoRepository.existsBySolicitanteIdAndEstadoIn(solicitante.getId(), estadosAtivos);

        if (temEmprestimoAtivo)
        {
            throw new RegraNegocioException("Solicitante já possui um empréstimo ativo ou em processo.");
        }
    }

    private void validarLimiteCredito (Solicitante solicitante, BigDecimal valorSolicitado)
    {
        // O limiteCreditoAprovado no Solicitante já deve ter sido calculado previamente (Na fase de Análise de Risco/IA)
        // usando a fórmula: Teto da faixa * Fator do risco
        if (solicitante.getLimiteCreditoAprovado() == null || valorSolicitado.compareTo(solicitante.getLimiteCreditoAprovado()) > 0)
        {
            throw new RegraNegocioException("O valor solicitado excede o seu limite permitido de " + solicitante.getLimiteCreditoAprovado() + " Kz.");
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
