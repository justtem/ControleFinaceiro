package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.Projeto.Financeiro.model.StatusMeta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MetaFinanceiraRequest(

    @NotBlank(message = "O nome da meta é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    @NotNull(message = "O valor objetivo é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor objetivo deve ser maior que zero")
    BigDecimal valorObjetivo,

    @NotNull(message = "O valor atual é obrigatório")
    @DecimalMin(value = "0.00", message = "O valor atual não pode ser negativo")
    BigDecimal valorAtual,

    @NotNull(message = "A data limite é obrigatória")
    LocalDate dataLimite,

    StatusMeta status

) {}