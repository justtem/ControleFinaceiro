package com.Projeto.Financeiro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.MetaFinanceiraRequest;
import com.Projeto.Financeiro.dto.MetaFinanceiraResponse;
import com.Projeto.Financeiro.model.MetaFinanceira;
import com.Projeto.Financeiro.model.StatusMeta;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.MetaFinanceiraRepository;

@Service
@Transactional
public class MetaFinanceiraService {

    private final MetaFinanceiraRepository metaFinanceiraRepository;

    public MetaFinanceiraService(MetaFinanceiraRepository metaFinanceiraRepository) {
        this.metaFinanceiraRepository = metaFinanceiraRepository;
    }

    // Cadastrar nova meta financeira
    public MetaFinanceiraResponse cadastrar(MetaFinanceiraRequest dados, Usuario usuario) {

        StatusMeta statusFinal = definirStatusAutomatico(
            dados.valorAtual(),
            dados.valorObjetivo(),
            dados.status()
        );

        MetaFinanceira meta = new MetaFinanceira(
            dados.nome(),
            dados.valorObjetivo(),
            dados.valorAtual(),
            dados.dataLimite(),
            statusFinal,
            usuario
        );

        MetaFinanceira salva = metaFinanceiraRepository.save(meta);

        return converterParaResponse(salva);
    }

    // Listar todas as metas do usuário ordenadas pela data limite mais próxima
    @Transactional(readOnly = true)
    public List<MetaFinanceiraResponse> listarPorUsuario(long usuarioId) {

        return metaFinanceiraRepository.findAllByUsuario_IdOrderByDataLimiteAsc(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    // Buscar meta específica por ID garantindo que pertence ao usuário
    @Transactional(readOnly = true)
    public MetaFinanceiraResponse buscarPorId(Long id, long usuarioId) {

        MetaFinanceira meta = buscarEntidadePorId(id, usuarioId);

        return converterParaResponse(meta);
    }

    // Atualizar meta financeira
    public MetaFinanceiraResponse atualizar(
            Long id,
            MetaFinanceiraRequest dados,
            long usuarioId) {

        MetaFinanceira meta = buscarEntidadePorId(id, usuarioId);

        StatusMeta statusFinal = definirStatusAutomatico(
            dados.valorAtual(),
            dados.valorObjetivo(),
            dados.status()
        );

        meta.setNome(dados.nome());
        meta.setValorObjetivo(dados.valorObjetivo());
        meta.setValorAtual(dados.valorAtual());
        meta.setDataLimite(dados.dataLimite());
        meta.setStatus(statusFinal);

        MetaFinanceira atualizada = metaFinanceiraRepository.save(meta);

        return converterParaResponse(atualizada);
    }

    // Excluir meta financeira
    public void excluir(Long id, long usuarioId) {

        MetaFinanceira meta = buscarEntidadePorId(id, usuarioId);

        metaFinanceiraRepository.delete(meta);
    }

    // ================= MÉTODOS AUXILIARES =================

    private MetaFinanceira buscarEntidadePorId(Long id, long usuarioId) {
        return metaFinanceiraRepository
                .findByIdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Meta financeira não encontrada"
                ));
    }

    // Define automaticamente CONCLUIDA se valorAtual >= valorObjetivo (exceto se foi CANCELADA)
    private StatusMeta definirStatusAutomatico(
            BigDecimal valorAtual,
            BigDecimal valorObjetivo,
            StatusMeta statusInformado) {

        if (statusInformado == StatusMeta.CANCELADA) {
            return StatusMeta.CANCELADA;
        }

        if (valorAtual.compareTo(valorObjetivo) >= 0) {
            return StatusMeta.CONCLUIDA;
        }

        return StatusMeta.EM_ANDAMENTO;
    }

    private MetaFinanceiraResponse converterParaResponse(MetaFinanceira meta) {

        BigDecimal percentualConcluido = meta.getValorAtual()
                .multiply(new BigDecimal("100"))
                .divide(meta.getValorObjetivo(), 2, RoundingMode.HALF_UP);

        return new MetaFinanceiraResponse(
            meta.getId(),
            meta.getNome(),
            meta.getValorObjetivo(),
            meta.getValorAtual(),
            percentualConcluido,
            meta.getDataLimite(),
            meta.getStatus()
        );
    }
}