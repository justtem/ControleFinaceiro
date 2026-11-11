
package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;

import com.Projeto.Financeiro.model.TipoConta;

public record ContaResponse(
    Long id,
    String nome,
    TipoConta tipo,
    BigDecimal saldoInicial,
    boolean ativa
) {}