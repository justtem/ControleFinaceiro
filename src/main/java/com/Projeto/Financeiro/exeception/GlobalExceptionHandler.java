package com.Projeto.Financeiro.exeception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}