package com.Projeto.Financeiro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.CategoriaRequest;
import com.Projeto.Financeiro.dto.CategoriaResponse;
import com.Projeto.Financeiro.model.Categoria;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.CategoriaRepository;
import com.Projeto.Financeiro.repository.LancamentoRepository;

@Service
@Transactional
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    private final LancamentoRepository lancamentoRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            LancamentoRepository lancamentoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public CategoriaResponse cadastrar(CategoriaRequest dados, Usuario usuario) {

        Categoria categoria = new Categoria(
            dados.nome(),
            dados.tipo(),
            dados.descricao(),
            usuario
        );

        Categoria salva = categoriaRepository.save(categoria);

        return converterParaResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarPorUsuario(long usuarioId) {
        
        return categoriaRepository.findAllByUsuario_Id(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id, long usuarioId) {
        
        Categoria categoria = buscarEntidadePorId(id, usuarioId);

        return converterParaResponse(categoria);
    }

    public CategoriaResponse atualizar(
            Long id,
            CategoriaRequest dados,
            long usuarioId) {

        Categoria categoria = buscarEntidadePorId(id, usuarioId);

        categoria.setNome(dados.nome());
        categoria.setTipo(dados.tipo());
        categoria.setDescricao(dados.descricao());

        Categoria atualizada = categoriaRepository.save(categoria);

        return converterParaResponse(atualizada);
    }

    // Excluir categoria verificando integridade (RN07)
    public void excluir(Long id, long usuarioId) {

        Categoria categoria = buscarEntidadePorId(id, usuarioId);

        if (lancamentoRepository.existsByCategoria_IdAndUsuario_Id(id, usuarioId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível excluir esta categoria pois existem lançamentos vinculados a ela"
            );
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria buscarEntidadePorId(Long id, long usuarioId) {

        return categoriaRepository
                .findByIdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Categoria não encontrada"
                ));
    }

    private CategoriaResponse converterParaResponse(Categoria categoria) {

        return new CategoriaResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getTipo(),
            categoria.getDescricao()
        );
    }
}