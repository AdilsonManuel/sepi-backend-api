package com.sepi.sepi_backend.controller;

import com.sepi.sepi_backend.dto.AtualizacaoUsuarioRequest;
import com.sepi.sepi_backend.dto.RegistroUsuarioRequest;
import com.sepi.sepi_backend.dto.UsuarioResponse;
import com.sepi.sepi_backend.entity.Usuario;
import com.sepi.sepi_backend.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
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

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController
{

    private final UsuarioService usuarioService;

    public UsuarioController (UsuarioService usuarioService)
    {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponse> registrarUsuario (@Valid @RequestBody RegistroUsuarioRequest request)
    {
        Usuario novoUsuario = usuarioService.registrarNovoUsuario(request);
        return new ResponseEntity<>(new UsuarioResponse(novoUsuario), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obterPorId (@PathVariable Long id)
    {
        Usuario usuario = usuarioService.obterPorId(id);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarPerfil (@PathVariable Long id, @Valid @RequestBody AtualizacaoUsuarioRequest request)
    {
        Usuario usuarioAtualizado = usuarioService.actualizarPerfil(id, request);
        return ResponseEntity.ok(new UsuarioResponse(usuarioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> desativarConta (@PathVariable Long id)
    {
        Usuario usuarioDesativado = usuarioService.desativarConta(id);
        return ResponseEntity.ok(new UsuarioResponse(usuarioDesativado));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos ()
    {
        List<UsuarioResponse> responseList = usuarioService.listarTodos().stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{id}/verificado")
    public ResponseEntity<UsuarioResponse> atualizarStatusVerificacao (@PathVariable Long id, @RequestParam boolean status)
    {
        Usuario usuarioAtualizado = usuarioService.atualizarStatusVerificacao(id, status);
        return ResponseEntity.ok(new UsuarioResponse(usuarioAtualizado));
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<UsuarioResponse> reativarConta (@PathVariable Long id)
    {
        Usuario usuarioReativado = usuarioService.reactivarConta(id);
        return ResponseEntity.ok(new UsuarioResponse(usuarioReativado));
    }
}
