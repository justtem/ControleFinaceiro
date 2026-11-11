package com.Projeto.Financeiro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.Projeto.Financeiro.dto.CategoriaRequest;
import com.Projeto.Financeiro.dto.CategoriaResponse;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> cadastrar(
        @Valid @RequestBody CategoriaRequest dados,
        @AuthenticationPrincipal Usuario usuario) {

            CategoriaResponse categoria = categoriaService.cadastrar(dados, usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
        }

    @GetMapping 
    public ResponseEntity<List<CategoriaResponse>> listar(
        @AuthenticationPrincipal  Usuario usuario) {

            List<CategoriaResponse> categorias =
             categoriaService.listarPorUsuario(usuario.getId());

            return ResponseEntity.ok(categorias);
        }
    
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CategoriaResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

            CategoriaResponse categoria =
             categoriaService.buscarPorId(id, usuario.getId());

            return ResponseEntity.ok(categoria);
            }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody CategoriaRequest dados,
        @AuthenticationPrincipal Usuario usuario) {

            CategoriaResponse categoria =
            categoriaService.atualizar(id, dados, usuario.getId());

            return ResponseEntity.ok(categoria);
        }
        

    
        
}