/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.DepositoRequest;
import com.sepi.sepi_backend.dto.InvestimentoAutomaticoRequest;
import com.sepi.sepi_backend.dto.InvestimentoResponse;
import com.sepi.sepi_backend.dto.InvestirRequest;
import com.sepi.sepi_backend.entity.Investimento;
import com.sepi.sepi_backend.service.InvestimentoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author azm
 */
@RestController
@RequestMapping("/api/investimentos")
@RequiredArgsConstructor
public class InvestimentoController
{

    private final InvestimentoService investimentoService;

    /**
     * Endpoint para realizar um investimento manual num pedido específico.POST
     * /api/investimentos/investir
     *
     * @param request
     * @param authentication
     * @return
     */
    @PostMapping("/investir")
    @PreAuthorize("hasAuthority('EMPRESTADOR')")
    public ResponseEntity<String> realizarInvestimento (
            @Valid @RequestBody InvestirRequest request,
            Authentication authentication)
    {

        String emailEmprestador = authentication.getName();

        Investimento investimento = investimentoService.realizarInvestimentoManual(emailEmprestador, request);

        return new ResponseEntity<>("Investimento de " + investimento.getValorInvestido() + " Kz realizado com sucesso no empréstimo #" + request.getEmprestimoId(), HttpStatus.CREATED);
    }

    /**
     * Lista todos os investimentos do emprestador logado (Carteira).GET
     * /api/investimentos/minha-carteira
     *
     * @param authentication
     * @return
     */
    @GetMapping("/minha-carteira")
    @PreAuthorize("hasAuthority('EMPRESTADOR')")
    public ResponseEntity<List<InvestimentoResponse>> getMinhaCarteira (Authentication authentication)
    {
        String email = authentication.getName();
        List<InvestimentoResponse> carteira = investimentoService.listarCarteiraDoEmprestador(email)
                .stream()
                .map(InvestimentoResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(carteira);
    }

    /**
     * Permite ao emprestador adicionar fundos à sua carteira.POST
     * /api/investimentos/depositar
     *
     * @param request
     * @param authentication
     * @return
     */
    @PostMapping("/depositar")
    @PreAuthorize("hasAuthority('EMPRESTADOR')")
    public ResponseEntity<String> carregarCarteira (
            @Valid @RequestBody DepositoRequest request,
            Authentication authentication)
    {

        String email = authentication.getName();
        BigDecimal novoSaldo = investimentoService.realizarDeposito(email, request);

        return ResponseEntity.ok("Depósito realizado com sucesso. Novo saldo: " + novoSaldo + " Kz");
    }

    /**
     * Endpoint para investimento automático (Distribuição Proporcional).POST
     * /api/investimentos/investir-automatico
     *
     * @param request
     * @param authentication
     * @return
     */
    @PostMapping("/investir-automatico")
    @PreAuthorize("hasAuthority('EMPRESTADOR')")
    public ResponseEntity<String> realizarInvestimentoAutomatico (
            @Valid @RequestBody InvestimentoAutomaticoRequest request,
            Authentication authentication)
    {

        String email = authentication.getName();
        List<Investimento> investimentos = investimentoService.realizarInvestimentoAutomatico(email, request);

        BigDecimal totalAlocado = investimentos.stream()
                .map(Investimento::getValorInvestido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok("Sucesso! " + totalAlocado + " Kz distribuídos por " + investimentos.size() + " empréstimos.");
    }
}
