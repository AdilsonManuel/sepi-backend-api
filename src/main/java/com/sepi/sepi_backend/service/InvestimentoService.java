/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.dto.DepositoRequest;
import com.sepi.sepi_backend.dto.InvestimentoAutomaticoRequest;
import com.sepi.sepi_backend.dto.InvestirRequest;
import com.sepi.sepi_backend.entity.Emprestador;
import com.sepi.sepi_backend.entity.Emprestimo;
import com.sepi.sepi_backend.entity.Investimento;
import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import com.sepi.sepi_backend.exception.RegraNegocioException;
import com.sepi.sepi_backend.repository.EmprestadorRepository;
import com.sepi.sepi_backend.repository.EmprestimoRepository;
import com.sepi.sepi_backend.repository.InvestimentoRepository;
import com.sepi.sepi_backend.util.CalculoFinanceiro;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author azm
 */
@Service
@RequiredArgsConstructor
public class InvestimentoService
{

    private final UsuarioService usuarioService;
    private final EmprestimoRepository emprestimoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final EmprestadorRepository emprestadorRepository; // Para atualizar saldo

    private static final BigDecimal INVESTIMENTO_MINIMO = new BigDecimal("50000");
    private static final BigDecimal INVESTIMENTO_MAXIMO = new BigDecimal("2500000");

    @Transactional
    public Investimento realizarInvestimentoManual (String emailEmprestador, InvestirRequest request)
    {
        Emprestador emprestador = (Emprestador) usuarioService.obterPorEmail(emailEmprestador);

        // 1. Validar Emprestador (Contrato e Saldo)
        if (!emprestador.isContratoAceite())
        {
            throw new RegraNegocioException("Deve aceitar os termos e condições antes de investir.");
        }
        if (emprestador.getSaldo().compareTo(request.getValorInvestimento()) < 0)
        {
            throw new RegraNegocioException("Saldo insuficiente na carteira.");
        }

        // 2. Validar Limites de Valor (Regra de Negócio)
        if (request.getValorInvestimento().compareTo(INVESTIMENTO_MINIMO) < 0)
        {
            throw new RegraNegocioException("O valor mínimo de investimento é 50.000 Kz.");
        }
        if (request.getValorInvestimento().compareTo(INVESTIMENTO_MAXIMO) > 0)
        {
            throw new RegraNegocioException("O valor máximo de investimento é 2.500.000 Kz.");
        }

        // 3. Buscar e Validar Empréstimo
        Emprestimo emprestimo = emprestimoRepository.findById(request.getEmprestimoId())
                .orElseThrow(() -> new RegraNegocioException("Empréstimo não encontrado."));

        if (emprestimo.getEstado() != EstadoEmprestimo.EM_FINANCIAMENTO)
        {
            throw new RegraNegocioException("Este pedido não está disponível para financiamento.");
        }

        // 4. Validar "Overfunding" (Não investir mais do que o necessário)
        BigDecimal valorFalta = emprestimo.getValorSolicitado().subtract(emprestimo.getTotalFinanciado());
        if (request.getValorInvestimento().compareTo(valorFalta) > 0)
        {
            throw new RegraNegocioException("O valor excede o necessário. Restam apenas: " + valorFalta + " Kz.");
        }

        // === EXECUÇÃO DA TRANSAÇÃO ===
        // A. Debitar Emprestador
        emprestador.setSaldo(emprestador.getSaldo().subtract(request.getValorInvestimento()));
        emprestadorRepository.save(emprestador);

        // B. Atualizar Empréstimo
        emprestimo.setTotalFinanciado(emprestimo.getTotalFinanciado().add(request.getValorInvestimento()));

        // C. Verificar se completou
        if (emprestimo.getTotalFinanciado().compareTo(emprestimo.getValorSolicitado()) >= 0)
        {
            emprestimo.setEstado(EstadoEmprestimo.FINANCIADO);
        }
        emprestimoRepository.save(emprestimo);

        Investimento investimento = Investimento.builder()
                .emprestador(emprestador)
                .emprestimo(emprestimo)
                .valorInvestido(request.getValorInvestimento())
                // Regra: Recebe o investido + 10% de juros
                .totalAReceberEstimado(CalculoFinanceiro.calcularTotalReceberEmprestador(request.getValorInvestimento()))
                .build();

        return investimentoRepository.save(investimento);
    }

    /**
     * Lista todos os investimentos feitos por um emprestador específico.
     *
     * @param emailEmprestador Email do usuário autenticado.
     * @return Lista de investimentos.
     */
    @Transactional(readOnly = true)
    public List<Investimento> listarCarteiraDoEmprestador (String emailEmprestador)
    {
        Emprestador emprestador = (Emprestador) usuarioService.obterPorEmail(emailEmprestador);
        return investimentoRepository.findByEmprestadorId(emprestador.getId());
    }

    /**
     * Adiciona saldo à carteira do Emprestador.
     *
     * @param emailEmprestador Email do usuário logado.
     * @param request DTO com o valor a depositar.
     * @return Saldo atualizado.
     */
    @Transactional
    public BigDecimal realizarDeposito (String emailEmprestador, DepositoRequest request)
    {
        Emprestador emprestador = (Emprestador) usuarioService.obterPorEmail(emailEmprestador);

        // Regra: Só pode depositar/investir se aceitou o contrato
        if (!emprestador.isContratoAceite())
        {

            throw new RegraNegocioException("Deve aceitar os termos e condições antes de carregar a carteira.");
        }

        BigDecimal novoSaldo = emprestador.getSaldo().add(request.getValor());
        emprestador.setSaldo(novoSaldo);

        emprestadorRepository.save(emprestador);

        return novoSaldo;
    }

    /**
     * Distribui o valor do investimento por vários empréstimos abertos.Fonte:
     * Modulo de investimentos.txt
     *
     * @param emailEmprestador
     * @param request
     * @return
     */
    @Transactional
    public List<Investimento> realizarInvestimentoAutomatico (String emailEmprestador, InvestimentoAutomaticoRequest request)
    {
        Emprestador emprestador = (Emprestador) usuarioService.obterPorEmail(emailEmprestador);
        BigDecimal valorTotalInvestir = request.getValorInvestimento();

        // 1. Validações Básicas
        validarEmprestador(emprestador, valorTotalInvestir);

        // 2. Buscar empréstimos elegíveis (EM_FINANCIAMENTO)
        List<Emprestimo> emprestimosDisponiveis = emprestimoRepository.findByEstado(EstadoEmprestimo.EM_FINANCIAMENTO);

        if (emprestimosDisponiveis.isEmpty())
        {
            throw new RegraNegocioException("Não existem pedidos de empréstimo disponíveis para financiamento no momento.");
        }

        // 3. Preparar Mapa para Cálculo: ID -> Valor que Falta para completar
        // (Não usamos o valor total do pedido, mas sim o que falta preencher)
        Map<Long, BigDecimal> mapaFaltante = emprestimosDisponiveis.stream()
                .collect(Collectors.toMap(
                        Emprestimo::getId,
                        e -> e.getValorSolicitado().subtract(e.getTotalFinanciado())
                ));

        // 4. Executar Cálculo Matemático (Proporcional)
        Map<Long, BigDecimal> distribuicao = CalculoFinanceiro.calcularDistribuicaoProporcional(valorTotalInvestir, mapaFaltante);

        // 5. Aplicar Investimentos
        List<Investimento> novosInvestimentos = new ArrayList<>();
        BigDecimal totalRealmenteAlocado = BigDecimal.ZERO;

        for (Map.Entry<Long, BigDecimal> entry : distribuicao.entrySet())
        {
            Long emprestimoId = entry.getKey();
            BigDecimal valorParaEste = entry.getValue();

            // Ignora valores zero ou muito baixos
            if (valorParaEste.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }

            Emprestimo emprestimo = emprestimosDisponiveis.stream()
                    .filter(e -> e.getId().equals(emprestimoId))
                    .findFirst()
                    .orElseThrow();

            // Atualiza Empréstimo
            emprestimo.setTotalFinanciado(emprestimo.getTotalFinanciado().add(valorParaEste));
            if (emprestimo.getTotalFinanciado().compareTo(emprestimo.getValorSolicitado()) >= 0)
            {
                emprestimo.setEstado(EstadoEmprestimo.FINANCIADO);
            }
            emprestimoRepository.save(emprestimo);

            // Cria Registo de Investimento
            Investimento investimento = Investimento.builder()
                    .emprestador(emprestador)
                    .emprestimo(emprestimo)
                    .valorInvestido(valorParaEste)
                    .totalAReceberEstimado(CalculoFinanceiro.calcularTotalReceberEmprestador(valorParaEste))
                    .build();

            novosInvestimentos.add(investimentoRepository.save(investimento));
            totalRealmenteAlocado = totalRealmenteAlocado.add(valorParaEste);
        }

        // 6. Debitar do Saldo do Emprestador (Apenas o que foi realmente alocado)
        emprestador.setSaldo(emprestador.getSaldo().subtract(totalRealmenteAlocado));
        emprestadorRepository.save(emprestador);

        return novosInvestimentos;
    }

    private void validarEmprestador (Emprestador emprestador, BigDecimal valor)
    {
        if (!emprestador.isContratoAceite())
        {
            throw new RegraNegocioException("Deve aceitar os termos e condições antes de investir.");
        }
        if (emprestador.getSaldo().compareTo(valor) < 0)
        {
            throw new RegraNegocioException("Saldo insuficiente na carteira.");
        }
        // Validação específica: Se < 500k, o sistema força automático (mas este método É o automático, então OK).
        // Se for > 2.5M, bloqueia.
        if (valor.compareTo(INVESTIMENTO_MAXIMO) > 0)
        {
            throw new RegraNegocioException("O valor máximo de investimento é 2.500.000 Kz.");
        }
    }

}
