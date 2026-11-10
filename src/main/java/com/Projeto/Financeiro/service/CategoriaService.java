package com.Projeto.Financeiro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.model.Categoria;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaService {
    
    
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria cadastrar(Categoria categoria, Usuario usuario) {
        categoria.setUsuario(usuario);

        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarPorUsuario(long usuarioId) {
        return categoriaRepository.findAllByUsuario_Id(usuarioId);
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id, long usuarioId) {
        return categoriaRepository
                .findByIdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada"
                ));
    }

    public Categoria atualizar(Long id, Categoria dados, long usuarioId) {
        Categoria categoria = buscarPorId(id, usuarioId);

        categoria.setNome(dados.getNome());
        categoria.setTipo(dados.getTipo());
        categoria.setDescricao(dados.getDescricao());

        return categoriaRepository.save(categoria);
    }
}
