package com.Projeto.Financeiro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.ContaResponse;
import com.Projeto.Financeiro.dto.DashboardResponse;
import com.Projeto.Financeiro.dto.DespesaCategoriaResponse;
import com.Projeto.Financeiro.dto.LancamentoResponse;
import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;
import com.Projeto.Financeiro.repository.LancamentoRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final LancamentoRepository lancamentoRepository;
    private final ContaService contaService;

    public DashboardService(
            LancamentoRepository lancamentoRepository,
            ContaService contaService) {
        this.lancamentoRepository = lancamentoRepository;
        this.contaService = contaService;
    }

    // Resumo geral do Dashboard (se não informar datas, usa o mês atual)
    public DashboardResponse obterResumo(long usuarioId, LocalDate dataInicio, LocalDate dataFim) {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = (dataInicio != null) ? dataInicio : hoje.withDayOfMonth(1);
        LocalDate fim = (dataFim != null) ? dataFim : hoje.withDayOfMonth(hoje.lengthOfMonth());

        if (fim.isBefore(inicio)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A data final não pode ser anterior à data inicial"
            );
        }

        // 1. Saldo Total Consolidado (soma o saldoAtual de todas as contas ativas do usuário)
        BigDecimal saldoTotal = contaService.listarPorUsuario(usuarioId)
                .stream()
                .filter(ContaResponse::ativa)
                .map(ContaResponse::saldoAtual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Total de Receitas Pagas no período
        BigDecimal receitasPeriodo = lancamentoRepository.somarValoresPorPeriodo(
                usuarioId, TipoLancamento.RECEITA, StatusLancamento.PAGO, inicio, fim);

        // 3. Total de Despesas Pagas no período
        BigDecimal despesasPeriodo = lancamentoRepository.somarValoresPorPeriodo(
                usuarioId, TipoLancamento.DESPESA, StatusLancamento.PAGO, inicio, fim);

        // 4. Resultado Financeiro do período (Receitas - Despesas)
        BigDecimal resultadoFinanceiro = receitasPeriodo.subtract(despesasPeriodo);

        // 5. Total de Despesas Pendentes no período
        BigDecimal despesasPendentes = lancamentoRepository.somarValoresPorPeriodo(
                usuarioId, TipoLancamento.DESPESA, StatusLancamento.PENDENTE, inicio, fim);

        // 6. Gráfico de Despesas por Categoria no período
        List<DespesaCategoriaResponse> despesasPorCategoria =
                lancamentoRepository.somarDespesasPorCategoriaNoPeriodo(usuarioId, inicio, fim);

        // 7. Histórico das 5 movimentações mais recentes
        List<LancamentoResponse> ultimasMovimentacoes = lancamentoRepository
                .findTop5ByUsuario_IdOrderByDataDescIdDesc(usuarioId)
                .stream()
                .map(l -> new LancamentoResponse(
                    l.getId(),
                    l.getDescricao(),
                    l.getValor(),
                    l.getTipo(),
                    l.getData(),
                    l.getDataVencimento(),
                    l.getStatus(),
                    l.getCategoria().getId(),
                    l.getCategoria().getNome(),
                    l.getConta().getId(),
                    l.getConta().getNome()
                ))
                .toList();

        return new DashboardResponse(
            inicio,
            fim,
            saldoTotal,
            receitasPeriodo,
            despesasPeriodo,
            resultadoFinanceiro,
            despesasPendentes,
            despesasPorCategoria,
            ultimasMovimentacoes
        );
    }

    // Consultar apenas despesas por categoria no período (GET /dashboard/categorias)
    public List<DespesaCategoriaResponse> obterDespesasPorCategoria(
            long usuarioId, LocalDate dataInicio, LocalDate dataFim) {

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = (dataInicio != null) ? dataInicio : hoje.withDayOfMonth(1);
        LocalDate fim = (dataFim != null) ? dataFim : hoje.withDayOfMonth(hoje.lengthOfMonth());

        if (fim.isBefore(inicio)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A data final não pode ser anterior à data inicial"
            );
        }

        return lancamentoRepository.somarDespesasPorCategoriaNoPeriodo(usuarioId, inicio, fim);
    }

    // Consultar resumo de um mês e ano específicos (GET /dashboard/mensal?ano=2026&mes=11)
    public DashboardResponse obterResumoMensal(long usuarioId, Integer ano, Integer mes) {

        LocalDate hoje = LocalDate.now();
        int anoConsulta = (ano != null) ? ano : hoje.getYear();
        int mesConsulta = (mes != null) ? mes : hoje.getMonthValue();

        if (mesConsulta < 1 || mesConsulta > 12) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "O mês deve ser um valor entre 1 e 12"
            );
        }

        LocalDate inicio = LocalDate.of(anoConsulta, mesConsulta, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        return obterResumo(usuarioId, inicio, fim);
    }
}