package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.AtualizacaoUsuarioRequest;
import com.sepi.sepi_backend.dto.RegistroUsuarioRequest;
import com.sepi.sepi_backend.dto.UsuarioResponse;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para endpoints públicos relacionados a Usuários (ex: Cadastro).
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController
{

    private final UsuarioService usuarioService;

//	public UsuarioController(UsuarioService usuarioService)
//	{
//		this.usuarioService = usuarioService;
//	}
    /**
     * RF01/RF02: Endpoint para registrar um novo usuário (Solicitante ou
     * Emprestador). POST /api/usuarios/registrar
     *
     * @param request DTO com os dados do usuário.
     * @return Resposta de sucesso com os dados do usuário.
     */
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponse> registrarUsuario (@Valid @RequestBody RegistroUsuarioRequest request)
    {
        Usuario novoUsuario = usuarioService.registrarNovoUsuario(request);
        return new ResponseEntity<>(new UsuarioResponse(novoUsuario), HttpStatus.CREATED);
    }

    // =========================================================================
    // 2. CRUD Admin Básico / Gestão de Perfil
    // =========================================================================
    /**
     * Obtém um usuário pelo ID.(Uso Perfil Próprio / Admin) GET
     * /api/usuarios/{id}
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obterPorId (@PathVariable Long id)
    {
        Usuario usuario = usuarioService.obterPorId(id);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    /**
     * Atualiza os dados básicos do perfil (Nome, Email, Telefone,
     * Localidade).(Uso Perfil Próprio) PUT /api/usuarios/{id}
     *
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarPerfil (@PathVariable Long id, @Valid @RequestBody AtualizacaoUsuarioRequest request)
    {
        Usuario usuarioAtualizado = usuarioService.atualizarPerfil(id, request);
        return ResponseEntity.ok(new UsuarioResponse(usuarioAtualizado));
    }

    /**
     * Desativa a conta do usuário (Remoção Lógica).DELETE /api/usuarios/{id}
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> desativarConta (@PathVariable Long id)
    {
        Usuario usuarioDesativado = usuarioService.desativarConta(id);
        return ResponseEntity.ok(new UsuarioResponse(usuarioDesativado));
    }

    // =========================================================================
    // 3. Métodos Admin / Monitorização
    // =========================================================================
    /**
     * Lista todos os usuários.(Uso Admin) GET /api/usuarios
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos ()
    {
        List<UsuarioResponse> responseList = usuarioService.listarTodos().stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * RF20/RF03: Aprova/Recusa a verificação do documento.(Uso Admin) PATCH
     * /api/usuarios/{id}/verificado
     *
     * @param id ID do usuário
     * @param status true para aprovar, false para recursar.
     * @return
     */
    @PatchMapping("/{id}/verificado")
    public ResponseEntity<UsuarioResponse> atualizarStatusVerificacao (@PathVariable Long id, @RequestParam boolean status)
    {
        Usuario usuarioAtualizado = usuarioService.atualizarStatusVerificacao(id, status);
        return ResponseEntity.ok(new UsuarioResponse(usuarioAtualizado));
    }

    /**
     * Reativa a conta de um usuário (Uso Admin).PATCH
     * /api/usuarios/{id}/reativar
     *
     * @param id
     * @return
     */
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<UsuarioResponse> reativarConta (@PathVariable Long id)
    {
        Usuario usuarioReativado = usuarioService.reativarConta(id);
        return ResponseEntity.ok(new UsuarioResponse(usuarioReativado));
    }
}
