package com.Projeto.Financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.Financeiro.model.Lancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    
    // Lista todos os lançamentos do usuário ordenados da data mais recente para a mais antiga
    List<Lancamento> findAllByUsuario_IdOrderByDataDesc(long usuarioId);

    // Busca um lançamento específico garantindo que pertence ao usuário autenticado
    Optional<Lancamento> findByIdAndUsuario_Id(Long id, long usuarioId);

    // Verifica se existem lançamentos vinculados a uma categoria (Proteger exclusão)
    boolean existsByCategoria_IdAndUsuario_Id(Long catageriaId, long usuarioId);

    // Verificar se existem lançamentos vinculados a uma conta (Proteger contra exclusão)
    boolean existsByConta_IdAndUsuario_Id(Long contaId, long usuarioId);
}
