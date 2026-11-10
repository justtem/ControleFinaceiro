package com.Projeto.Financeiro.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Projeto.Financeiro.model.Role;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder) {
            
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
        }

        public Usuario cadastrar(Usuario usuario) {

            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException(
                    "Este e-mail ja está cadastrado."
                );
            }

            usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
            );

            usuario.setRole(Role.USER);

            return usuarioRepository.save(usuario);
        }

        public Optional<Usuario> buscarPorEmail(String email) {
            return usuarioRepository.findByEmail(email);
        }


        public Optional<Usuario> autenticar(String email, String senha) {

            Optional<Usuario> usuarioEncontrado = 
            usuarioRepository.findByEmail(email);

            if (usuarioEncontrado.isEmpty()) {
                return Optional.empty();
            }

            Usuario usuario = usuarioEncontrado.get();

            if (!passwordEncoder.matches(senha, usuario.getSenha())) {
                return Optional.empty();
            }

            return Optional.of(usuario);
        }
}
