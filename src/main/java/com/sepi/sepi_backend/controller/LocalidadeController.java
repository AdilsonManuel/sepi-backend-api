package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.entity.Localidade;
import com.sepi.sepi_backend.service.LocalidadeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerir e consultar a hierarquia de Localidades (Províncias,
 * Municípios, Comunas). Endpoint essencial para o formulário de cadastro de
 * usuário.
 */
@RestController
@RequestMapping("/api/localidades")
@RequiredArgsConstructor
public class LocalidadeController
{

    private final LocalidadeService localidadeService;

//	public LocalidadeController(LocalidadeService localidadeService)
//	{
//		this.localidadeService = localidadeService;
//	}
    /**
     * Endpoint para listar todas as Províncias. GET /api/localidades/provincias
     */
    @GetMapping("/provincias")
    public ResponseEntity<List<Localidade>> listarProvincias ()
    {
        List<Localidade> provincias = localidadeService.listarProvincias();
        return ResponseEntity.ok(provincias);
    }

    /**
     * Endpoint para listar as localidades filhas de uma localidade pai (e.g.,
     * Municípios de uma Província). GET
     * /api/localidades/{pkLocalidadePai}/filhas
     */
    @GetMapping("/{pkLocalidadePai}/filhas")
    public ResponseEntity<List<Localidade>> listarLocalidadesFilhas (@PathVariable String pkLocalidadePai)
    {
        List<Localidade> filhas = localidadeService.listarLocalidadesFilhas(pkLocalidadePai);
        if (filhas.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filhas);
    }

    /**
     * Endpoint para adicionar uma nova localidade (Uso Admin - Exemplo de
     * CRUD). POST /api/localidades
     */
    @PostMapping
    public ResponseEntity<Localidade> adicionarLocalidade (@RequestBody Localidade localidade)
    {
        Localidade novaLocalidade = localidadeService.salvarLocalidade(localidade);
        return ResponseEntity.ok(novaLocalidade);
    }
}
