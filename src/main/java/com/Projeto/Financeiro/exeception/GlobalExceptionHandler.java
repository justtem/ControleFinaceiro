package com.Projeto.Financeiro.exeception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> tratarValidacoes(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
            erros.put(erro.getField(), erro.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erros);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> tratarArgumentos(
            IllegalArgumentException ex) {

        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> tratarResponseStatus(
            ResponseStatusException ex) {

        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", ex.getReason());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(erro);
    }

    // Segurança (OWASP / LGPD Art. 46): Evita expor nomes de classes/Enums internos quando o JSON enviado é inválido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> tratarJsonInvalido(
            HttpMessageNotReadableException ex) {

        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", "Corpo da requisição inválido ou malformado. Verifique o formato dos campos enviados.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }

    // Segurança (OWASP / LGPD Art. 46): Captura falhas inesperadas sem vazar StackTrace ou estrutura do banco
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> tratarErroInternoGenerico(
            Exception ex) {

        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", "Ocorreu um erro interno no servidor. Tente novamente mais tarde.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }
}