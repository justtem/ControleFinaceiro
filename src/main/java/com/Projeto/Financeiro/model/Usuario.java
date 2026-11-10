package com.Projeto.Financeiro.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity 
@Table(name = "Usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O nome do Usuario é obrigatório")
    private String nome;

    @NotBlank(message = "O email do aluno é obrigatório")
    @Email(message = "Informa um e-mail Válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;


    @NotBlank(message = "A senha é obrigatórioa")
    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void prePersist() {
        this.dataCadastro = LocalDateTime.now();

        if (this.role == null) {
            this.role = Role.USER;
        }
    }


    public Usuario() {
    }


    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSenha() {
    return senha;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }



}
