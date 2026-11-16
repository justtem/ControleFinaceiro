package com.Projeto.Financeiro.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.Financeiro.dto.ExportacaoDadosLgpdResponse;
import com.Projeto.Financeiro.dto.UsuarioRequest;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.CategoriaService;
import com.Projeto.Financeiro.service.ContaService;
import com.Projeto.Financeiro.service.LancamentoService;
import com.Projeto.Financeiro.service.MetaFinanceiraService;
import com.Projeto.Financeiro.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final ContaService contaService;
    private final LancamentoService lancamentoService;
    private final MetaFinanceiraService metaFinanceiraService;

    public UsuarioController(
            UsuarioService usuarioService,
            CategoriaService categoriaService,
            ContaService contaService,
            LancamentoService lancamentoService,
            MetaFinanceiraService metaFinanceiraService) {
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.contaService = contaService;
        this.lancamentoService = lancamentoService;
        this.metaFinanceiraService = metaFinanceiraService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarUsuarioAutenticado(
            @AuthenticationPrincipal Usuario usuario) {

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UsuarioResponse resposta = new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getDataCadastro(),
            usuario.getDataAceiteTermos(),
            usuario.getVersaoTermos()
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
            atualizado.getDataCadastro(),
            atualizado.getDataAceiteTermos(),
            atualizado.getVersaoTermos()
        );

        return ResponseEntity.ok(resposta);
    }

    // LGPD (Art. 18, V - Direito à Portabilidade): Exporta todos os dados do titular em formato estruturado
    @GetMapping("/me/exportar")
    public ResponseEntity<ExportacaoDadosLgpdResponse> exportarDadosLgpd(
            @AuthenticationPrincipal Usuario usuario) {

        long usuarioId = usuario.getId();

        ExportacaoDadosLgpdResponse pacoteDados = new ExportacaoDadosLgpdResponse(
            LocalDateTime.now(),
            usuarioId,
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getDataCadastro(),
            usuario.getDataAceiteTermos(),
            usuario.getVersaoTermos(),
            categoriaService.listarPorUsuario(usuarioId),
            contaService.listarPorUsuario(usuarioId),
            lancamentoService.listarPorUsuario(usuarioId, null, null, null, null, null, null),
            metaFinanceiraService.listarPorUsuario(usuarioId)
        );

        return ResponseEntity.ok(pacoteDados);
    }

    // LGPD (Art. 18, VI - Direito ao Esquecimento): Exclui a conta e todos os dados vinculados ao titular
    @DeleteMapping("/me")
    public ResponseEntity<Void> excluirMinhaConta(
            @AuthenticationPrincipal Usuario usuario) {

        usuarioService.excluirContaCompleta(usuario.getId());

        return ResponseEntity.noContent().build();
    }

    public record UsuarioResponse(
        long id,
        String nome,
        String email,
        LocalDateTime dataCadastro,
        LocalDateTime dataAceiteTermos,
        String versaoTermos
    ) {
    }
}