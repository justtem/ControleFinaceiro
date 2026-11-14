package com.Projeto.Financeiro.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.Financeiro.dto.UsuarioRequest;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarUsuarioAutenticado(
            @AuthenticationPrincipal Usuario usuario) {

        UsuarioResponse resposta = new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getDataCadastro()
        );

        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarUsuarioAutenticado(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody UsuarioRequest dados) {

        Usuario atualizado = usuarioService.atualizar(usuario, dados);

        UsuarioResponse resposta = new UsuarioResponse(
            atualizado.getId(),
            atualizado.getNome(),
            atualizado.getEmail(),
            atualizado.getDataCadastro()
        );

        return ResponseEntity.ok(resposta);
    }

    public record UsuarioResponse(
        long id,
        String nome,
        String email,
        LocalDateTime dataCadastro
    ) {
    }
}