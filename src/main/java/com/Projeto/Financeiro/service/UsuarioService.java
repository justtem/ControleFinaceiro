package com.Projeto.Financeiro.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Projeto.Financeiro.dto.UsuarioRequest;
import com.Projeto.Financeiro.model.Role;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.CategoriaRepository;
import com.Projeto.Financeiro.repository.ContaRepository;
import com.Projeto.Financeiro.repository.LancamentoRepository;
import com.Projeto.Financeiro.repository.MetaFinanceiraRepository;
import com.Projeto.Financeiro.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetaFinanceiraRepository metaFinanceiraRepository;
    private final LancamentoRepository lancamentoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            MetaFinanceiraRepository metaFinanceiraRepository,
            LancamentoRepository lancamentoRepository,
            ContaRepository contaRepository,
            CategoriaRepository categoriaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.metaFinanceiraRepository = metaFinanceiraRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
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

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
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

    public Usuario atualizar(Usuario usuarioAutenticado, UsuarioRequest dados) {

        if (!usuarioAutenticado.getEmail().equalsIgnoreCase(dados.getEmail())
                && usuarioRepository.existsByEmail(dados.getEmail())) {

            throw new IllegalArgumentException(
                "Este e-mail já esta cadastrado por outro usuário."
            );
        }

        usuarioAutenticado.setNome(dados.getNome());
        usuarioAutenticado.setEmail(dados.getEmail());
        usuarioAutenticado.setSenha(passwordEncoder.encode(dados.getSenha()));

        return usuarioRepository.save(usuarioAutenticado);
    }

    // LGPD (Art. 18, VI - Direito à Eliminação): Remove todos os dados vinculados e a conta do titular
    public void excluirContaCompleta(long usuarioId) {
        metaFinanceiraRepository.deleteAllByUsuario_Id(usuarioId);
        lancamentoRepository.deleteAllByUsuario_Id(usuarioId);
        contaRepository.deleteAllByUsuario_Id(usuarioId);
        categoriaRepository.deleteAllByUsuario_Id(usuarioId);
        usuarioRepository.deleteById(usuarioId);
        
        // Garante a execução imediata dos DELETEs no banco e encerra o contexto de autenticação atual
        usuarioRepository.flush();
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
}