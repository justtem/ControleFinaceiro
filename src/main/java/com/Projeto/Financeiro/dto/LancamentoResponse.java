package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;

public record LancamentoResponse(
    Long id,
    String descricao,
    BigDecimal valor,
    TipoLancamento tipo,
    LocalDate data,
    LocalDate dataVencimento,
    StatusLancamento status,
    Long categoriaId,
    String categoriaNome,
    Long contaId,
    String contaNome
) {}