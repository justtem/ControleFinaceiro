package com.Projeto.Financeiro.dto;

import com.Projeto.Financeiro.model.TipoCategoria;

public record CategoriaResponse(
    Long id,
    String nome,
    TipoCategoria tipo,
    String descricao
) {    
}
