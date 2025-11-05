package com.sepi.sepi_backend.repository;

import com.sepi.sepi_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de CRUD da entidade base Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
	
	/**
	 * Verifica se já existe um usuário com o email fornecido.
	 *
	 * @param email Email a ser verificado.
	 * @return True se existir, False caso contrário.
	 */
	boolean existsByEmail(String email);
	
	/**
	 * Encontra um usuário pelo email.
	 *
	 * @param email Email do usuário.
	 * @return Optional contendo o Usuário ou vazio.
	 */
	Optional<Usuario> findByEmail(String email);
}
