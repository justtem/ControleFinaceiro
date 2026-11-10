package com.Projeto.Financeiro.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.Financeiro.model.Usuario;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
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

    public record UsuarioResponse(
        long id,
        String nome,
        String email,
        LocalDateTime dataCadastro
    ) {
    }
}
