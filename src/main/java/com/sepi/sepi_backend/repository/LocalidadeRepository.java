package com.sepi.sepi_backend.repository;

import com.sepi.sepi_backend.entity.Localidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de CRUD da entidade Localidade.
 */
@Repository
public interface LocalidadeRepository extends JpaRepository<Localidade, String>
{
	
	/**
	 * Encontra todas as localidades que são filhos (Municípios ou Comunas) de uma localidade pai (Província ou Município).
	 *
	 * @param pkLocalidadePai A chave primária da Localidade Pai.
	 * @return Lista de Localidades filhas.
	 */
	List<Localidade> findByLocalidadePai_PkLocalidade(String pkLocalidadePai);
	
	/**
	 * Encontra todas as localidades que não têm um pai (assumindo que são as Províncias).
	 *
	 * @return Lista de Localidades (Províncias).
	 */
	List<Localidade> findByLocalidadePaiIsNull();
	
	/**
	 * Encontra uma Localidade pela sua designação.
	 *
	 * @param designacao Nome da Localidade.
	 * @return Localidade.
	 */
	Optional<Localidade> findByDesignacao(String designacao);
}
