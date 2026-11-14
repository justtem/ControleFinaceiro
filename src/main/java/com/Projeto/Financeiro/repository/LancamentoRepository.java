package com.Projeto.Financeiro.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Projeto.Financeiro.dto.DespesaCategoriaResponse;
import com.Projeto.Financeiro.model.Lancamento;
import com.Projeto.Financeiro.model.StatusLancamento;
import com.Projeto.Financeiro.model.TipoLancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    
    // Lista todos os lançamentos do usuário ordenados da data mais recente para a mais antiga
    List<Lancamento> findAllByUsuario_IdOrderByDataDesc(long usuarioId);

    // Busca lançamentos com filtros dinâmicos opcionais 
    @Query("""
        SELECT l FROM Lancamento l
        WHERE l.usuario.id = :usuarioId
          AND (:dataInicio IS NULL OR l.data >= :dataInicio)
          AND (:dataFim IS NULL OR l.data <= :dataFim)
          AND (:categoriaId IS NULL OR l.categoria.id = :categoriaId)
          AND (:contaId IS NULL OR l.conta.id = :contaId)
          AND (:tipo IS NULL OR l.tipo = :tipo)
          AND (:status IS NULL OR l.status = :status)
        ORDER BY l.data DESC
    """)
    List<Lancamento> listarComFiltros(
            @Param("usuarioId") long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("categoriaId") Long categoriaId,
            @Param("contaId") Long contaId,
            @Param("tipo") TipoLancamento tipo,
            @Param("status") StatusLancamento status);

    // Busca um lançamento específico garantindo que pertence ao usuário autenticado
    Optional<Lancamento> findByIdAndUsuario_Id(Long id, long usuarioId);

    // Verifica se existem lançamentos vinculados a uma categoria (Proteger exclusão)
    boolean existsByCategoria_IdAndUsuario_Id(Long categoriaId, long usuarioId);

    // Verificar se existem lançamentos vinculados a uma conta (Proteger contra exclusão)
    boolean existsByConta_IdAndUsuario_Id(Long contaId, long usuarioId);

    // Soma os valores de uma conta específica por tipo e status
    @Query("""
        SELECT COALESCE(SUM(l.valor), 0)
        FROM Lancamento l
        WHERE l.conta.id = :contaId
          AND l.usuario.id = :usuarioId
          AND l.tipo = :tipo
          AND l.status = :status
    """)
    BigDecimal somarValoresPorConta(
            @Param("contaId") Long contaId,
            @Param("usuarioId") long usuarioId,
            @Param("tipo") TipoLancamento tipo,
            @Param("status") StatusLancamento status);

    // Soma valores do usuário dentro de um período por tipo e status (para Receitas/Despesas do período no Dashboard)
    @Query("""
        SELECT COALESCE(SUM(l.valor), 0)
        FROM Lancamento l
        WHERE l.usuario.id = :usuarioId
          AND l.tipo = :tipo
          AND l.status = :status
          AND l.data BETWEEN :dataInicio AND :dataFim
    """)
    BigDecimal somarValoresPorPeriodo(
            @Param("usuarioId") long usuarioId,
            @Param("tipo") TipoLancamento tipo,
            @Param("status") StatusLancamento status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    // Soma todos os valores do usuário por tipo e status sem filtro de data (ex: Total geral de Despesas Pendentes)
    @Query("""
        SELECT COALESCE(SUM(l.valor), 0)
        FROM Lancamento l
        WHERE l.usuario.id = :usuarioId
          AND l.tipo = :tipo
          AND l.status = :status
    """)
    BigDecimal somarValoresPorUsuario(
            @Param("usuarioId") long usuarioId,
            @Param("tipo") TipoLancamento tipo,
            @Param("status") StatusLancamento status);

    // Agrupa o total de despesas pagas por categoria dentro do período (Gráfico de Despesas por Categoria)
    @Query("""
        SELECT new com.Projeto.Financeiro.dto.DespesaCategoriaResponse(
            l.categoria.id,
            l.categoria.nome,
            COALESCE(SUM(l.valor), 0)
        )
        FROM Lancamento l
        WHERE l.usuario.id = :usuarioId
          AND l.tipo = com.Projeto.Financeiro.model.TipoLancamento.DESPESA
          AND l.status = com.Projeto.Financeiro.model.StatusLancamento.PAGO
          AND l.data BETWEEN :dataInicio AND :dataFim
        GROUP BY l.categoria.id, l.categoria.nome
        ORDER BY SUM(l.valor) DESC
    """)
    List<DespesaCategoriaResponse> somarDespesasPorCategoriaNoPeriodo(
            @Param("usuarioId") long usuarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    // Busca as 5 movimentações mais recentes do usuário (Histórico do Dashboard)
    List<Lancamento> findTop5ByUsuario_IdOrderByDataDescIdDesc(long usuarioId);
}