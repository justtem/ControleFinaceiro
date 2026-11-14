package com.Projeto.Financeiro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.ContaRequest;
import com.Projeto.Financeiro.dto.ContaResponse;
import com.Projeto.Financeiro.model.Conta;
import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.ContaRepository;
import com.Projeto.Financeiro.repository.LancamentoRepository;

@Service
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

    public ContaService(
            ContaRepository contaRepository,
            LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    // Cadastrar conta
    public ContaResponse cadastrar(ContaRequest dados, Usuario usuario) {

        Conta conta = new Conta(
            dados.nome(),
            dados.tipo(),
            dados.saldoInicial(),
            usuario
        );

        Conta salva = contaRepository.save(conta);

        return converterParaResponse(salva);
    }

    // Listar contas de um usuário
    @Transactional(readOnly = true)
    public List<ContaResponse> listarPorUsuario(long usuarioId) {

        return contaRepository.findAllByUsuario_Id(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    // Buscar conta por ID
    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id, long usuarioId) {

        Conta conta = buscarEntidadePorId(id, usuarioId);

        return converterParaResponse(conta);
    }

    // Atualizar conta
    public ContaResponse atualizar(
            Long id,
            ContaRequest dados,
            long usuarioId) {

        Conta conta = buscarEntidadePorId(id, usuarioId);

        conta.setNome(dados.nome());
        conta.setTipo(dados.tipo());
        conta.setSaldoInicial(dados.saldoInicial());

        Conta atualizada = contaRepository.save(conta);

        return converterParaResponse(atualizada);
    }

    // Excluir conta verificando integridade (RN06)
    public void excluir(Long id, long usuarioId) {

        Conta conta = buscarEntidadePorId(id, usuarioId);

        if (lancamentoRepository.existsByConta_IdAndUsuario_Id(id, usuarioId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível excluir esta conta pois existem lançamentos vinculados a ela"
            );
        }

        contaRepository.delete(conta);
    }

    // ================= MÉTODOS AUXILIARES =================

    // Buscar entidade verificando o proprietário
    private Conta buscarEntidadePorId(Long id, long usuarioId) {

        return contaRepository
                .findByIdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada"
                ));
    }

    // Calcula o saldo atual (Saldo Inicial + Receitas Pagas - Despesas Pagas)
    public BigDecimal calcularSaldoConta(Conta conta) {
        long usuarioId = conta.getUsuario().getId();

        BigDecimal receitasPagas = lancamentoRepository.somarValoresPorConta(
            conta.getId(),
            usuarioId,
            TipoLancamento.RECEITA,
            StatusLancamento.PAGO
        );

        BigDecimal despesasPagas = lancamentoRepository.somarValoresPorConta(
            conta.getId(),
            usuarioId,
            TipoLancamento.DESPESA,
            StatusLancamento.PAGO
        );

        return conta.getSaldoInicial()
                .add(receitasPagas)
                .subtract(despesasPagas);
    }

    // Converter entidade para DTO já incluindo o saldo calculado
    private ContaResponse converterParaResponse(Conta conta) {

        BigDecimal saldoAtual = calcularSaldoConta(conta);

        return new ContaResponse(
            conta.getId(),
            conta.getNome(),
            conta.getTipo(),
            conta.getSaldoInicial(),
            saldoAtual,
            conta.isAtiva()
        );
    }
}