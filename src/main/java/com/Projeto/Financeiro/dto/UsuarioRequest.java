package com.Projeto.Financeiro.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "A senha deve conter pelo menos uma letra e um número"
    )
    private String senha;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    private String email;

    // LGPD (Art. 8º, § 4º): O consentimento deve ser livre, informado e inequívoco (não pode vir pré-marcado como true)
    @AssertTrue(message = "É obrigatório aceitar explicitamente os Termos de Uso e a Política de Privacidade (LGPD)")
    private boolean aceitouTermos;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAceitouTermos() {
        return this.aceitouTermos;
    }

    public void setAceitouTermos(boolean aceitouTermos) {
        this.aceitouTermos = aceitouTermos;
    }
}