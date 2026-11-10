package com.Projeto.Financeiro.security;

import java.io.IOException;
import java.util.Optional;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioService usuarioService) {

        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // Verifica se existe um token Bearer
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            // Extrai o e-mail armazenado no JWT
            String email = jwtService.extrairEmail(token);

            // Evita substituir uma autenticação já existente
            if (email != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                Optional<Usuario> usuarioEncontrado =
                        usuarioService.buscarPorEmail(email);

                if (usuarioEncontrado.isPresent()) {

                    Usuario usuario = usuarioEncontrado.get();

                    if (jwtService.validarToken(token, usuario)) {

                        Collection<GrantedAuthority> autoridades =
                             new java.util.ArrayList<>();

                        autoridades.add(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + usuario.getRole().name()
                                )
                        );

                        UsernamePasswordAuthenticationToken autenticacao =
                                new UsernamePasswordAuthenticationToken(
                                        usuario,
                                        null,
                                        autoridades
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(autenticacao);
                    }
                }
            }

        } catch (Exception ex) {
            // Token inválido ou expirado: a requisição
            // continua sem autenticação.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
