package com.Projeto.Financeiro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.Financeiro.dto.DashboardResponse;
import com.Projeto.Financeiro.dto.DespesaCategoriaResponse;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Consultar resumo financeiro geral (GET /dashboard)
    @GetMapping
    public ResponseEntity<DashboardResponse> consultarResumo(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @AuthenticationPrincipal Usuario usuario) {

        DashboardResponse resumo = dashboardService.obterResumo(
            usuario.getId(), dataInicio, dataFim
        );

        return ResponseEntity.ok(resumo);
    }

    // Consultar despesas agrupadas por categoria (GET /dashboard/categorias)
    @GetMapping("/categorias")
    public ResponseEntity<List<DespesaCategoriaResponse>> consultarDespesasPorCategoria(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @AuthenticationPrincipal Usuario usuario) {

        List<DespesaCategoriaResponse> categorias = dashboardService.obterDespesasPorCategoria(
            usuario.getId(), dataInicio, dataFim
        );

        return ResponseEntity.ok(categorias);
    }

    // Consultar resumo mensal específico (GET /dashboard/mensal?ano=2026&mes=11)
    @GetMapping("/mensal")
    public ResponseEntity<DashboardResponse> consultarResumoMensal(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @AuthenticationPrincipal Usuario usuario) {

        DashboardResponse resumoMensal = dashboardService.obterResumoMensal(
            usuario.getId(), ano, mes
        );

        return ResponseEntity.ok(resumoMensal);
    }
}