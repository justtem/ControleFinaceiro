package com.Projeto.Financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.Financeiro.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    
    List<Conta> findAllByUsuario_Id(long usuarioId);

    Optional<Conta> findByIdAndUsuario_Id(Long id, long usuarioId);

    boolean existsByIdAndUsuario_Id(Long id, long usuarioId);
}
