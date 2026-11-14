package com.Projeto.Financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.Financeiro.model.MetaFinanceira;

@Repository
public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, Long> {

    List<MetaFinanceira> findAllByUsuario_IdOrderByDataLimiteAsc(long usuarioId);

    Optional<MetaFinanceira> findByIdAndUsuario_Id(Long id, long usuarioId);

    void deleteAllByUsuario_Id(long usuarioId);
}