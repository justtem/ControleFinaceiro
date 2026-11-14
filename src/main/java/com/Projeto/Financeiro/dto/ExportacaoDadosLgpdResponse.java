package com.Projeto.Financeiro.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ExportacaoDadosLgpdResponse(
    LocalDateTime dataExportacao,
    long idTitular,
    String nomeTitular,
    String emailTitular,
    LocalDateTime dataCadastro,
    LocalDateTime dataAceiteTermosLgpd,
    List<CategoriaResponse> categorias,
    List<ContaResponse> contas,
    List<LancamentoResponse> lancamentos,
    List<MetaFinanceiraResponse> metasFinanceiras
) {}