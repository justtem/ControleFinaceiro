package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;

public record DespesaCategoriaResponse(
    Long categoriaId,
    String categoriaNome,
    BigDecimal total
) {}