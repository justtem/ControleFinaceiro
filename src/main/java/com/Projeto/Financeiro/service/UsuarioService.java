package com.Projeto.Financeiro.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    private static final int MAX_TENTATIVAS_FALHAS = 5;
    private static final int MINUTOS_BLOQUEIO = 15;

    private final Map<String, Integer> tentativasLogin = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> bloqueiosLogin = new ConcurrentHashMap<>();

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
        String chaveEmail = email.toLowerCase().trim();

        // 1. Verifica se o e-mail está temporariamente bloqueado por força bruta
        verificarBloqueioForcaBruta(chaveEmail);

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findByEmail(email);

        if (usuarioEncontrado.isEmpty()) {
            registrarFalhaLogin(chaveEmail);
            return Optional.empty();
        }

        Usuario usuario = usuarioEncontrado.get();

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            registrarFalhaLogin(chaveEmail);
            return Optional.empty();
        }

        // Login com sucesso: limpa o histórico de falhas
        limparFalhasLogin(chaveEmail);

        return Optional.of(usuario);
    }

    private void verificarBloqueioForcaBruta(String chaveEmail) {
        LocalDateTime desbloqueio = bloqueiosLogin.get(chaveEmail);
        if (desbloqueio != null) {
            if (LocalDateTime.now().isBefore(desbloqueio)) {
                throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas de login falhas. Conta temporariamente protegida por 15 minutos."
                );
            } else {
                // Tempo de bloqueio já expirou: libera a conta
                bloqueiosLogin.remove(chaveEmail);
                tentativasLogin.remove(chaveEmail);
            }
        }
    }

    private void registrarFalhaLogin(String chaveEmail) {
        int falhas = tentativasLogin.getOrDefault(chaveEmail, 0) + 1;
        tentativasLogin.put(chaveEmail, falhas);

        if (falhas >= MAX_TENTATIVAS_FALHAS) {
            bloqueiosLogin.put(chaveEmail, LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEIO));
        }
    }

    private void limparFalhasLogin(String chaveEmail) {
        tentativasLogin.remove(chaveEmail);
        bloqueiosLogin.remove(chaveEmail);
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