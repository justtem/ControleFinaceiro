package com.Projeto.Financeiro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.Projeto.Financeiro.dto.ContaRequest;
import com.Projeto.Financeiro.dto.ContaResponse;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.ContaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contas")
public class ContaController {
    
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Cadastrar conta
    @PostMapping
    public ResponseEntity<ContaResponse> cadastrar(
        @Valid @RequestBody ContaRequest dados,
        @AuthenticationPrincipal Usuario usuario) {

            ContaResponse conta = contaService.cadastrar(dados, usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(conta);
        }

    // Listar contas
    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar(
            @AuthenticationPrincipal Usuario usuario) {

        List<ContaResponse> contas =
                contaService.listarPorUsuario(usuario.getId());

        return ResponseEntity.ok(contas);
    }

    // Buscar conta por ID
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ContaResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        ContaResponse conta =
                contaService.buscarPorId(id, usuario.getId());

        return ResponseEntity.ok(conta);
    }

    // Atualizar conta
    @PutMapping("/{id}")
    public ResponseEntity<ContaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContaRequest dados,
            @AuthenticationPrincipal Usuario usuario) {

        ContaResponse conta =
                contaService.atualizar(id, dados, usuario.getId());

        return ResponseEntity.ok(conta);
    }
}
