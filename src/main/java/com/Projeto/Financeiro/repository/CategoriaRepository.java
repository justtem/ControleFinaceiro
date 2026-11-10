package com.Projeto.Financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Projeto.Financeiro.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByUsuario_Id(long usuarioId);

    Optional<Categoria> findByIdAndUsuario_Id(Long id, long usuarioId);

    boolean existsByIdAndUsuario_Id(Long id, long usuarioId);
}