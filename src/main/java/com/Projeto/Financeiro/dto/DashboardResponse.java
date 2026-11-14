package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
    LocalDate dataInicio,
    LocalDate dataFim,
    BigDecimal saldoTotal,
    BigDecimal receitasPeriodo,
    BigDecimal despesasPeriodo,
    BigDecimal resultadoFinanceiro,
    BigDecimal despesasPendentes,
    List<DespesaCategoriaResponse> despesasPorCategoria,
    List<LancamentoResponse> ultimasMovimentacoes
) {}