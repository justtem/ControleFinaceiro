package com.Projeto.Financeiro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.Projeto.Financeiro.dto.MetaFinanceiraRequest;
import com.Projeto.Financeiro.dto.MetaFinanceiraResponse;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.MetaFinanceiraService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/metas")
public class MetafinanceiraController {

    private final MetaFinanceiraService metaFinanceiraService;

    public MetafinanceiraController(MetaFinanceiraService metaFinanceiraService) {
        this.metaFinanceiraService = metaFinanceiraService;
    }

    // Cadastrar meta financeira (POST /metas)
    @PostMapping
    public ResponseEntity<MetaFinanceiraResponse> cadastrar(
            @Valid @RequestBody MetaFinanceiraRequest dados,
            @AuthenticationPrincipal Usuario usuario) {

        MetaFinanceiraResponse meta = metaFinanceiraService.cadastrar(dados, usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(meta);
    }

    // Listar metas do usuário (GET /metas)
    @GetMapping
    public ResponseEntity<List<MetaFinanceiraResponse>> listar(
            @AuthenticationPrincipal Usuario usuario) {

        List<MetaFinanceiraResponse> metas =
                metaFinanceiraService.listarPorUsuario(usuario.getId());

        return ResponseEntity.ok(metas);
    }

    // Buscar meta por ID (GET /metas/{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MetaFinanceiraResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        MetaFinanceiraResponse meta =
                metaFinanceiraService.buscarPorId(id, usuario.getId());

        return ResponseEntity.ok(meta);
    }

    // Atualizar meta financeira (PUT /metas/{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<MetaFinanceiraResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MetaFinanceiraRequest dados,
            @AuthenticationPrincipal Usuario usuario) {

        MetaFinanceiraResponse meta =
                metaFinanceiraService.atualizar(id, dados, usuario.getId());

        return ResponseEntity.ok(meta);
    }

    // Excluir meta financeira (DELETE /metas/{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        metaFinanceiraService.excluir(id, usuario.getId());

        return ResponseEntity.noContent().build();
    }
}