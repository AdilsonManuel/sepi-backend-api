/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sepi.sepi_backend.repository;

import com.sepi.sepi_backend.entity.Investimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, Long>
{

    // Encontrar todos os investimentos de um emprestador (Carteira)
    List<Investimento> findByEmprestadorId (Long emprestadorId);

    // Encontrar todos os investimentos num empréstimo específico
    List<Investimento> findByEmprestimoId (Long emprestimoId);
}
