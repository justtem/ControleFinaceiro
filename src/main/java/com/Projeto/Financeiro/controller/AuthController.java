package com.Projeto.Financeiro.controller;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // CADASTRO
    @PostMapping("/register")
    public ResponseEntity<String> cadastrar(
        @Valid @RequestBody UsuarioRequest request) {
            
            Usuario usuario = new Usuario();

            usuario.setNome(request.getNome());
            usuario.setEmail(request.getEmail());
            usuario.setSenha(request.getSenha());

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

           
    
}
