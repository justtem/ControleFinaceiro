package com.Projeto.Financeiro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.LancamentoRequest;
import com.Projeto.Financeiro.dto.LancamentoResponse;
import com.Projeto.Financeiro.model.Categoria;
import com.Projeto.Financeiro.model.Conta;
import com.Projeto.Financeiro.model.Lancamento;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.CategoriaRepository;
import com.Projeto.Financeiro.repository.ContaRepository;
import com.Projeto.Financeiro.repository.LancamentoRepository;

@Service 
@Transactional 
public class LancamentoService {
    
    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ContaRepository contaRepository;


    public LancamentoService(
          LancamentoRepository lancamentoRepository
        , CategoriaRepository categoriaRepository
        , ContaRepository contaRepository) {

        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.contaRepository = contaRepository;
    }

    // Cadastrar novo lançamento
    public LancamentoResponse cadastrar(LancamentoRequest dados, Usuario usuario) {
        
        Categoria categoria = buscarCategoriaDoUsuario(dados.categoriaId(), usuario.getId());
        Conta conta = buscarContaDoUsuario(dados.contaId(), usuario.getId());

        validarRegrasDeNegocio(dados, categoria, conta);

        Lancamento lancamento = new Lancamento(
            dados.descricao(),
            dados.valor(),
            dados.tipo(),
            dados.data(),
            dados.dataVencimento(),
            dados.status(),
            categoria,
            conta,
            usuario
        );

        Lancamento salvo = lancamentoRepository.save(lancamento);

        return converterParaResponse(salvo);

    }

    // Listar todos os lançamentos do usuário autenticado
    @Transactional(readOnly = true)
    public List<LancamentoResponse> listarPorUsuario(long usuarioId) {

        return lancamentoRepository.findAllByUsuario_IdOrderByDataDesc(usuarioId)
                                   .stream()
                                   .map(this::converterParaResponse)
                                   .toList();
    }

    // Buscar lançamento específico por ID
    @Transactional(readOnly = true)
    public LancamentoResponse buscarPorId(Long id, long usuarioId) {
        
        Lancamento lancamento = buscarEntidadePorId(id, usuarioId); {

        return converterParaResponse(lancamento);
        }
    }

    // Atualizar lançamento existente 
    public LancamentoResponse atualizar(
            Long id,
            LancamentoRequest dados,
            long usuarioId) {
        
        Lancamento lancamento = buscarEntidadePorId(id, usuarioId);
        Categoria categoria = buscarCategoriaDoUsuario(dados.categoriaId(), usuarioId);
        Conta conta = buscarContaDoUsuario(dados.contaId(), usuarioId);

        validarRegrasDeNegocio(dados, categoria, conta);

        lancamento.setDescricao(dados.descricao());
        lancamento.setValor(dados.valor());
        lancamento.setTipo(dados.tipo());
        lancamento.setData(dados.data());
        lancamento.setDataVencimento(dados.dataVencimento());
        lancamento.setStatus(dados.status());
        lancamento.setCategoria(categoria);
        lancamento.setConta(conta);

        Lancamento atualizado = lancamentoRepository.save(lancamento);

        return converterParaResponse(atualizado);
        
        }
    
    // Excluir lançamento
    public void excluir(Long id, long usuarioId) {

        Lancamento lancamento = buscarEntidadePorId(id, usuarioId);

        lancamentoRepository.delete(lancamento);
    }

    // ================= MÉTODOS AUXILIARES E VALIDAÇÕES =================

    private Lancamento buscarEntidadePorId(Long id, long usuarioId) {
        return lancamentoRepository
               .findByIdAndUsuario_Id(id, usuarioId)
               .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Lançamento não encontrado"
               ));
    }

    private Categoria buscarCategoriaDoUsuario(Long categoriaId, long usuarioId) {
        return categoriaRepository
                .findByIdAndUsuario_Id(categoriaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Categoria não encontrada para este usuário"
                ));
    }

    private Conta buscarContaDoUsuario(Long contaId, long usuarioId) {
        return contaRepository
                .findByIdAndUsuario_Id(contaId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada para este usuário"
                ));
    }

    private void validarRegrasDeNegocio(LancamentoRequest dados, Categoria categoria, Conta conta) {
        // Verifica se a conta está ativa
        if (!conta.isAtiva()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não é possível registrar lançamentos em uma conta inativa"
            );
        }

        // RN03: Receita exige categoria RECEITA; Despesa exige categoria DESPESA
        if (!dados.tipo().name().equals(categoria.getTipo().name())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O tipo do lançamento (" + dados.tipo() + ") é incompatível com o tipo da categoria (" + categoria.getTipo() + ")"
            );
        }

        // RN09: Se houver data de vencimento, não pode ser anterior à data do lançamento
        if (dados.dataVencimento() != null && dados.dataVencimento().isBefore(dados.data())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A data de vencimento não pode ser anterior à data do lançamento"
            );
        }
    }

    private LancamentoResponse converterParaResponse(Lancamento lancamento) {
        return new LancamentoResponse(
            lancamento.getId(),
            lancamento.getDescricao(),
            lancamento.getValor(),
            lancamento.getTipo(),
            lancamento.getData(),
            lancamento.getDataVencimento(),
            lancamento.getStatus(),
            lancamento.getCategoria().getId(),
            lancamento.getCategoria().getNome(),
            lancamento.getConta().getId(),
            lancamento.getConta().getNome()
        );
    }
}
