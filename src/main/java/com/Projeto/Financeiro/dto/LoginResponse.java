package com.Projeto.Financeiro.dto;

public class LoginResponse {
    
    private String token;
    private String tipo;

    public LoginResponse(String token) {
        this.token = token;
        this.tipo = "Bearer";
    }

    public String getToken() {
        return token;
    }
}
