package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LancamentoRequest(

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres")
    String descricao,

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    BigDecimal valor,

    @NotNull(message = "O tipo do lançamento é obrigatório")
    TipoLancamento tipo,

    @NotNull(message = "A data é obrigatória")
    LocalDate data,

    LocalDate dataVencimento,

    @NotNull(message = "O status do lançamento é obrigatório")
    StatusLancamento status,

    @NotNull(message = "A categoria é obrigatória")
    Long categoriaId,

    @NotNull(message = "A conta é obrigatória")
    Long contaId

) {}