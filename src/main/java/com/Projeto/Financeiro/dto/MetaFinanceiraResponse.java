package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.Projeto.Financeiro.model.StatusMeta;

public record MetaFinanceiraResponse(
    Long id,
    String nome,
    BigDecimal valorObjetivo,
    BigDecimal valorAtual,
    BigDecimal percentualConcluido,
    LocalDate dataLimite,
    StatusMeta status
) {}