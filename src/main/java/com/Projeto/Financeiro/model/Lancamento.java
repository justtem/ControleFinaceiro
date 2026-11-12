
package com.Projeto.Financeiro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lancamentos")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "A descrição do lançamento é obrigatória")
    @Size(max = 150, message = "A descrição deve ter no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "O tipo do lançamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoLancamento tipo;

    @NotNull(message = "A data do lançamento é obrigatória")
    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @NotNull(message = "O status do lançamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusLancamento status;

    @NotNull(message = "A categoria é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull(message = "A conta é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @NotNull(message = "O usuário proprietário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Lancamento() {
    }

    public Lancamento(
            String descricao,
            BigDecimal valor,
            TipoLancamento tipo,
            LocalDate data,
            LocalDate dataVencimento,
            StatusLancamento status,
            Categoria categoria,
            Conta conta,
            Usuario usuario) {

        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.categoria = categoria;
        this.conta = conta;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public StatusLancamento getStatus() {
        return status;
    }

    public void setStatus(StatusLancamento status) {
        this.status = status;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
