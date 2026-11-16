package com.Projeto.Financeiro.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.Financeiro.dto.LoginRequest;
import com.Projeto.Financeiro.dto.LoginResponse;
import com.Projeto.Financeiro.dto.UsuarioRequest;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.security.JwtService;
import com.Projeto.Financeiro.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // LGPD (Art. 9º e Art. 41): Acesso público e facilitado às informações sobre o tratamento de dados e DPO
    @GetMapping("/termos-lgpd")
    public ResponseEntity<TermosLgpdResponse> consultarTermosLgpd() {
        TermosLgpdResponse termos = new TermosLgpdResponse(
            "1.0",
            "Gestão e controle financeiro pessoal de contas, categorias, lançamentos e metas do próprio titular.",
            List.of("Nome", "E-mail", "Senha (armazenada exclusivamente sob hash criptográfico BCrypt)"),
            "Os dados não são compartilhados com terceiros.",
            "Os dados são mantidos enquanto a conta estiver ativa, podendo ser exportados (GET /usuarios/me/exportar) ou eliminados permanentemente (DELETE /usuarios/me) a qualquer momento pelo titular.",
            "dpo.privacidade@projetofinanceiro.com.br"
        );

        return ResponseEntity.ok(termos);
    }

    // CADASTRO
    @PostMapping("/register")
    public ResponseEntity<String> cadastrar(
            @Valid @RequestBody UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setVersaoTermos("1.0");

        usuarioService.cadastrar(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuário cadastrado com sucesso!");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Optional<Usuario> usuarioAutenticado =
                usuarioService.autenticar(
                    request.getEmail(),
                    request.getSenha()
                );

        if (usuarioAutenticado.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarTokens(
                usuarioAutenticado.get()
        );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }

    public record TermosLgpdResponse(
        String versaoTermos,
        String finalidadeTratamento,
        List<String> dadosColetados,
        String compartilhamentoDados,
        String retencaoDados,
        String contatoEncarregadoDpo
    ) {
    }
}