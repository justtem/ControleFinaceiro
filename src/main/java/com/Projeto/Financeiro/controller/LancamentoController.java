package com.Projeto.Financeiro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.Projeto.Financeiro.dto.LancamentoRequest;
import com.Projeto.Financeiro.dto.LancamentoResponse;
import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.LancamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {
    
    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    // Cadastrar lançamento (POST)
    @PostMapping
    public ResponseEntity<LancamentoResponse> cadastrar(
            @Valid @RequestBody LancamentoRequest dados,
            @AuthenticationPrincipal Usuario usuario) {

        LancamentoResponse lancamento = lancamentoService.cadastrar(dados, usuario);
      
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamento);
    }

    // Listar lançamentos do Usuário com filtros opcionais (GET)
    @GetMapping 
    public ResponseEntity<List<LancamentoResponse>> listar(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long contaId,
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) StatusLancamento status,
            @AuthenticationPrincipal Usuario usuario) {

        List<LancamentoResponse> lancamentos = lancamentoService.listarPorUsuario(
            usuario.getId(),
            dataInicio,
            dataFim,
            categoriaId,
            contaId,
            tipo,
            status
        );

        return ResponseEntity.ok(lancamentos);
    }

    // Buscar lançamento por ID (GET/{id})
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<LancamentoResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        
        LancamentoResponse lancamento =
                lancamentoService.buscarPorId(id, usuario.getId());
        
        return ResponseEntity.ok(lancamento);
    }

    // Atualizar lançamento (PUT/{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<LancamentoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LancamentoRequest dados,
            @AuthenticationPrincipal Usuario usuario) {
            
        LancamentoResponse lancamento = 
                lancamentoService.atualizar(id, dados, usuario.getId());
        
        return ResponseEntity.ok(lancamento);
    }

    // Excluir lançamento (DELETE/{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
            
        lancamentoService.excluir(id, usuario.getId());

        return ResponseEntity.noContent().build();
    }
}