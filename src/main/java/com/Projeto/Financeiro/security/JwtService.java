package com.Projeto.Financeiro.security;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.print.DocFlavor.STRING;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Projeto.Financeiro.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarTokens(Usuario usuario) {

        Date agora = new Date();

        Date expiracao = new Date(
            agora.getTime() + expiration
        );

        return Jwts.builder()
               .subject(usuario.getEmail())
               .issuedAt(agora)
               .expiration(expiracao)
               .signWith(getSigninKey())
               .compact();

    }
    
    public String extrairEmail(String token) {
        
        return extrairClaims(token).getSubject();
    }
    
    public boolean validarToken(String token, Usuario usuario) {
        
        try {
            String email = extrairEmail(token);
            
            return email.equals(usuario.getEmail())
            && extrairClaims(token)
            .getExpiration()
            .after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }
    
    private Claims extrairClaims(String token) {

        return Jwts.parser()
               .verifyWith(getSigninKey())
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }
}
