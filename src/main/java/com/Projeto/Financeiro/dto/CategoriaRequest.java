package com.Projeto.Financeiro.dto;

import com.Projeto.Financeiro.model.TipoCategoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaRequest (
    
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String nome,

    @NotBlank(message = "O tipo da categoria é obrigatório")
    TipoCategoria tipo,

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    String descricao
    
) {
}