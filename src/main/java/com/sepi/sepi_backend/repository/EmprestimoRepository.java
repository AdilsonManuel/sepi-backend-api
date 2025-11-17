/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sepi.sepi_backend.repository;

import com.sepi.sepi_backend.entity.Emprestimo;
import com.sepi.sepi_backend.enums.EstadoEmprestimo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author azm
 */
@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>
{

    List<Emprestimo> findBySolicitanteId (Long solicitanteId);

    List<Emprestimo> findByEstado (EstadoEmprestimo estado);

    boolean existsBySolicitanteIdAndEstadoIn (Long solicitanteId, List<EstadoEmprestimo> estados);
}
