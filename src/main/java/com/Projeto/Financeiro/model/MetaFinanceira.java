package com.Projeto.Financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "metas_financeiras")
public class MetaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O nome da meta é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotNull(message = "O valor objetivo é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor objetivo deve ser maior que zero")
    @Column(name = "valor_objetivo", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorObjetivo;

    @NotNull(message = "O valor atual é obrigatório")
    @DecimalMin(value = "0.00", message = "O valor atual não pode ser negativo")
    @Column(name = "valor_atual", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAtual;

    @NotNull(message = "A data limite é obrigatória")
    @Column(name = "data_limite", nullable = false)
    private LocalDate dataLimite;

    @NotNull(message = "O status da meta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMeta status;

    @NotNull(message = "O usuário proprietário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public MetaFinanceira() {
    }

    public MetaFinanceira(
            String nome,
            BigDecimal valorObjetivo,
            BigDecimal valorAtual,
            LocalDate dataLimite,
            StatusMeta status,
            Usuario usuario) {
        this.nome = nome;
        this.valorObjetivo = valorObjetivo;
        this.valorAtual = valorAtual;
        this.dataLimite = dataLimite;
        this.status = status;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorObjetivo() {
        return valorObjetivo;
    }

    public void setValorObjetivo(BigDecimal valorObjetivo) {
        this.valorObjetivo = valorObjetivo;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public StatusMeta getStatus() {
        return status;
    }

    public void setStatus(StatusMeta status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}