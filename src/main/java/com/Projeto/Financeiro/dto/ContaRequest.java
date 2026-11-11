package com.Projeto.Financeiro.dto;

import java.math.BigDecimal;

import com.Projeto.Financeiro.model.TipoConta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContaRequest(
    
    @NotBlank(message = "O nome da conta é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    @NotNull(message = "O tipo da conta é obrigatório")
    TipoConta tipo,

    @NotNull(message = "O saldo inicial é obrigatório")
    BigDecimal saldoInicial

) {}
