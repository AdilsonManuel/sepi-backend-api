/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.EmprestimoResponse;
import com.sepi.sepi_backend.dto.SolicitacaoEmprestimoRequest;
import com.sepi.sepi_backend.entity.Emprestimo;
import com.sepi.sepi_backend.service.EmprestimoService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController
{

    private final EmprestimoService emprestimoService;

    /**
     * Endpoint para um SOLICITANTE criar um novo pedido de empréstimo.
     *
     * @param request
     * @param authentication
     * @return
     */
    @PostMapping("/solicitar")
    @PreAuthorize("hasAuthority('SOLICITANTE')")
    public ResponseEntity<EmprestimoResponse> solicitarEmprestimo (
            @Valid @RequestBody SolicitacaoEmprestimoRequest request,
            Authentication authentication)
    {

        String emailSolicitante = authentication.getName();
        Emprestimo novoEmprestimo = emprestimoService.criarSolicitacao(request, emailSolicitante);

        return new ResponseEntity<>(new EmprestimoResponse(novoEmprestimo), HttpStatus.CREATED);
    }

    /**
     *
     * @param authentication
     * @return
     */
    @GetMapping("/meus-pedidos")
    @PreAuthorize("hasAuthority('SOLICITANTE')")
    public ResponseEntity<List<EmprestimoResponse>> getMeusPedidos (Authentication authentication)
    {
        String email = authentication.getName();
        List<EmprestimoResponse> response = emprestimoService.listarEmprestimosPorSolicitante(email)
                .stream()
                .map(EmprestimoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @return
     */
    @GetMapping("/disponiveis")
    @PreAuthorize("hasAuthority('EMPRESTADOR')")
    public ResponseEntity<List<EmprestimoResponse>> getPedidosDisponiveisParaFinanciar ()
    {
        List<EmprestimoResponse> response = emprestimoService.listarEmprestimosParaFinanciamento()
                .stream()
                .map(EmprestimoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
