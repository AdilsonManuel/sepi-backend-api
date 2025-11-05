package com.sepi.sepi_backend.service;

import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.repository.LocalidadeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalidadeService
{

    private final LocalidadeRepository localidadeRepository;

//	public LocalidadeService(LocalidadeRepository localidadeRepository)
//	{
//		this.localidadeRepository = localidadeRepository;
//	}
    /**
     * Encontra uma localidade pelo seu PK (ID).
     *
     * @param pkLocalidade ID da localidade.
     * @return Localidade encontrada.
     */
    public Optional<Localidade> findByPk (String pkLocalidade)
    {
        return localidadeRepository.findById(pkLocalidade);
    }

    /**
     * Lista todas as Províncias (Localidades sem pai).
     *
     * @return Lista de Localidades que são Províncias.
     */
    public List<Localidade> listarProvincias ()
    {
        return localidadeRepository.findByLocalidadePaiIsNull();
    }

    /**
     * Lista as localidades filhas (Municípios/Comunas) de uma localidade pai
     * (Província/Município).
     *
     * @param pkLocalidadePai ID da localidade pai.
     * @return Lista de localidades filhas.
     */
    public List<Localidade> listarLocalidadesFilhas (String pkLocalidadePai)
    {
        return localidadeRepository.findByLocalidadePai_PkLocalidade(pkLocalidadePai);
    }

    /**
     * Salva uma nova localidade (para uso administrativo ou inicialização).
     *
     * @param localidade Objeto Localidade a ser salvo.
     * @return Localidade salva.
     */
    public Localidade salvarLocalidade (Localidade localidade)
    {

        localidadeRepository.findByDesignacao(localidade.getDesignacao())
                .ifPresent(l ->
                {
                    throw new RuntimeException("Localidade com designação '" + localidade.getDesignacao() + "' já existe.");
                });
        return localidadeRepository.save(localidade);
    }
}
