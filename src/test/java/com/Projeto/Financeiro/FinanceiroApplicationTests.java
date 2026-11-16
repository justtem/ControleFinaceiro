package com.Projeto.Financeiro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.Projeto.Financeiro.security.JwtAuthenticationFilter;

@SpringBootTest
@Transactional // Faz rollback automático ao final de cada teste para não sujar o MySQL!
class FinanceiroApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(jwtAuthenticationFilter)
                .build();
    }

    @Test
    @DisplayName("Deve testar o fluxo completo: Auth -> Categoria -> Conta -> Lançamento -> Saldo -> Dashboard")
    void deveTestarFluxoCompletoDoSistema() throws Exception {

      // 0.1. TESTAR LGPD (Art. 9º e 41): Rota pública de transparência dos Termos e DPO (GET /auth/termos-lgpd)
        mockMvc.perform(get("/auth/termos-lgpd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versaoTermos").value("1.0"))
                .andExpect(jsonPath("$.contatoEncarregadoDpo").exists());

        // 0.2. TESTAR LGPD (Art. 8º, § 4º): Omitir aceitouTermos não pode assumir true automático (Deve dar 400 Bad Request)
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Usuario Sem Campo Termos",
                        "email": "omisso@teste.com",
                        "senha": "senhaSegura123"
                    }
                """))
                .andExpect(status().isBadRequest());

        // 1. CADASTRAR USUÁRIO COM CONSENTIMENTO EXPLÍCITO LGPD (POST /auth/register)
        String jsonCadastro = """
            {
                "nome": "Ana Teste Automatizado",
                "email": "ana.auto@teste.com",
                "senha": "senhaSegura123",
                "aceitouTermos": true
            }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCadastro))
                .andExpect(status().isCreated());

        // 2. FAZER LOGIN E CAPTURAR O TOKEN JWT (POST /auth/login)
        String jsonLogin = """
            {
                "email": "ana.auto@teste.com",
                "senha": "senhaSegura123"
            }
        """;

        MvcResult resultadoLogin = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = extrairCampoJson(resultadoLogin, "token");
        String bearerToken = "Bearer " + token;

        // 3. CRIAR CATEGORIA DE RECEITA E DE DESPESA (POST /categorias)
        MvcResult resCatReceita = mockMvc.perform(post("/categorias")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Salário",
                        "tipo": "RECEITA",
                        "descricao": "Renda mensal"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();
        long categoriaReceitaId = Long.parseLong(extrairCampoJson(resCatReceita, "id"));

        MvcResult resCatDespesa = mockMvc.perform(post("/categorias")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Mercado",
                        "tipo": "DESPESA",
                        "descricao": "Compras do mês"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();
        long categoriaDespesaId = Long.parseLong(extrairCampoJson(resCatDespesa, "id"));

        // 4. CRIAR CONTA COM SALDO INICIAL DE R$ 1000.00 (POST /contas)
        MvcResult resConta = mockMvc.perform(post("/contas")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Conta Principal",
                        "tipo": "CONTA_DIGITAL",
                        "saldoInicial": 1000.00
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldoAtual").value(1000.00))
                .andReturn();
        long contaId = Long.parseLong(extrairCampoJson(resConta, "id"));

        // 5. CADASTRAR RECEITA PAGA DE R$ 2500.00 (POST /lancamentos)
        mockMvc.perform(post("/lancamentos")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "descricao": "Salário Recebido",
                        "valor": 2500.00,
                        "tipo": "RECEITA",
                        "data": "2026-11-10",
                        "status": "PAGO",
                        "categoriaId": %d,
                        "contaId": %d
                    }
                """.formatted(categoriaReceitaId, contaId)))
                .andExpect(status().isCreated());

        // 6. CADASTRAR DESPESA PAGA DE R$ 300.00 (POST /lancamentos)
        mockMvc.perform(post("/lancamentos")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "descricao": "Supermercado",
                        "valor": 300.00,
                        "tipo": "DESPESA",
                        "data": "2026-11-12",
                        "status": "PAGO",
                        "categoriaId": %d,
                        "contaId": %d
                    }
                """.formatted(categoriaDespesaId, contaId)))
                .andExpect(status().isCreated());

        // 6.1. CADASTRAR DESPESA PENDENTE DE R$ 150.00 (Não deve afetar saldoTotal, mas deve aparecer em despesasPendentes)
        mockMvc.perform(post("/lancamentos")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "descricao": "Conta de Internet",
                        "valor": 150.00,
                        "tipo": "DESPESA",
                        "data": "2026-11-14",
                        "dataVencimento": "2026-11-25",
                        "status": "PENDENTE",
                        "categoriaId": %d,
                        "contaId": %d
                    }
                """.formatted(categoriaDespesaId, contaId)))
                .andExpect(status().isCreated());

        // 7. TESTAR REGRA DE NEGÓCIO RN03: Tentar criar DESPESA usando categoria de RECEITA (Deve dar 400 Bad Request!)
        mockMvc.perform(post("/lancamentos")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "descricao": "Erro Proposital",
                        "valor": 100.00,
                        "tipo": "DESPESA",
                        "data": "2026-11-12",
                        "status": "PAGO",
                        "categoriaId": %d,
                        "contaId": %d
                    }
                """.formatted(categoriaReceitaId, contaId)))
                .andExpect(status().isBadRequest());

        // 8. VERIFICAR SE O SALDO DA CONTA ATUALIZOU PARA R$ 3200.00 (1000 + 2500 - 300, ignorando os 150 pendentes)
        mockMvc.perform(get("/contas/" + contaId)
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoInicial").value(1000.00))
                .andExpect(jsonPath("$.saldoAtual").value(3200.00));

        // 9. TESTAR PROTEÇÃO DE EXCLUSÃO RN06: Tentar deletar conta com lançamentos (Deve dar 409 Conflict!)
        mockMvc.perform(delete("/contas/" + contaId)
                .header("Authorization", bearerToken))
                .andExpect(status().isConflict());

        // 10. TESTAR DASHBOARD GERAL (GET /dashboard)
        mockMvc.perform(get("/dashboard")
                .param("dataInicio", "2026-11-01")
                .param("dataFim", "2026-11-30")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoTotal").value(3200.00))
                .andExpect(jsonPath("$.receitasPeriodo").value(2500.00))
                .andExpect(jsonPath("$.despesasPeriodo").value(300.00))
                .andExpect(jsonPath("$.resultadoFinanceiro").value(2200.00))
                .andExpect(jsonPath("$.despesasPendentes").value(150.00))
                .andExpect(jsonPath("$.despesasPorCategoria[0].categoriaNome").value("Mercado"))
                .andExpect(jsonPath("$.despesasPorCategoria[0].total").value(300.00))
                .andExpect(jsonPath("$.ultimasMovimentacoes.length()").value(3));

        // 11. TESTAR DASHBOARD POR CATEGORIAS (GET /dashboard/categorias)
        mockMvc.perform(get("/dashboard/categorias")
                .param("dataInicio", "2026-11-01")
                .param("dataFim", "2026-11-30")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoriaNome").value("Mercado"))
                .andExpect(jsonPath("$[0].total").value(300.00));

        // 12. TESTAR DASHBOARD MENSAL (GET /dashboard/mensal?ano=2026&mes=11)
        mockMvc.perform(get("/dashboard/mensal")
                .param("ano", "2026")
                .param("mes", "11")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoTotal").value(3200.00))
                .andExpect(jsonPath("$.resultadoFinanceiro").value(2200.00));

		// 13. CRIAR META FINANCEIRA COM 40% DE PROGRESSO (POST /metas)
        MvcResult resMeta = mockMvc.perform(post("/metas")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Reserva de Emergência",
                        "valorObjetivo": 5000.00,
                        "valorAtual": 2000.00,
                        "dataLimite": "2027-12-31"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.percentualConcluido").value(40.00))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andReturn();
        long metaId = Long.parseLong(extrairCampoJson(resMeta, "id"));

        // 14. LISTAR METAS E BUSCAR POR ID (GET /metas e GET /metas/{id})
        mockMvc.perform(get("/metas")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Reserva de Emergência"));

        mockMvc.perform(get("/metas/" + metaId)
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(metaId))
                .andExpect(jsonPath("$.valorAtual").value(2000.00));

        // 15. ATUALIZAR META ATINGINDO 100% E VERIFICAR CONCLUSÃO AUTOMÁTICA (PUT /metas/{id})
        mockMvc.perform(put("/metas/" + metaId)
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Reserva de Emergência",
                        "valorObjetivo": 5000.00,
                        "valorAtual": 5000.00,
                        "dataLimite": "2027-12-31"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentualConcluido").value(100.00))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // 16. TESTAR VALIDAÇÃO DE ERRO EM METAS: Valor objetivo zero deve dar 400 Bad Request
        mockMvc.perform(post("/metas")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Meta Inválida",
                        "valorObjetivo": 0.00,
                        "valorAtual": 0.00,
                        "dataLimite": "2027-12-31"
                    }
                """))
                .andExpect(status().isBadRequest());

        // 17. EXCLUIR META (DELETE /metas/{id}) E CONFIRMAR QUE NÃO EXISTE MAIS (404 Not Found)
        mockMvc.perform(delete("/metas/" + metaId)
                .header("Authorization", bearerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/metas/" + metaId)
                .header("Authorization", bearerToken))
                .andExpect(status().isNotFound());

		// 18. TESTAR PERFIL DO USUÁRIO (GET /usuarios/me e PUT /usuarios/me) + PROTEÇÃO DE SENHA LGPD
        mockMvc.perform(get("/usuarios/me")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana.auto@teste.com"))
                .andExpect(jsonPath("$.dataCadastro").exists())
                .andExpect(jsonPath("$.dataAceiteTermos").exists())
                .andExpect(jsonPath("$.senha").doesNotExist()); // Garante que o hash da senha jamais vaza no JSON!

        mockMvc.perform(put("/usuarios/me")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Ana Atualizada LGPD",
                        "email": "ana.auto@teste.com",
                        "senha": "senhaSegura123",
                        "aceitouTermos": true
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Atualizada LGPD"))
                .andExpect(jsonPath("$.dataAceiteTermos").exists());

        // 19. TESTAR LGPD (Art. 8º): Recusar os termos de uso no cadastro deve bloquear com 400 Bad Request
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Usuario Sem Consentimento",
                        "email": "sem.lgpd@teste.com",
                        "senha": "senhaSegura123",
                        "aceitouTermos": false
                    }
                """))
                .andExpect(status().isBadRequest());

        // 20. CRIAR UMA NOVA META ATIVA PARA TESTAR PORTABILIDADE E EXCLUSÃO EM CASCATA COM DADOS EM TODAS AS 4 TABELAS
        mockMvc.perform(post("/metas")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "Viagem de Férias",
                        "valorObjetivo": 3000.00,
                        "valorAtual": 1500.00,
                        "dataLimite": "2027-06-30"
                    }
                """))
                .andExpect(status().isCreated());

        // 21. TESTAR LGPD (Art. 18, V - Portabilidade): Exportar todos os dados do titular (GET /usuarios/me/exportar)
        mockMvc.perform(get("/usuarios/me/exportar")
                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTitular").value("Ana Atualizada LGPD"))
                .andExpect(jsonPath("$.emailTitular").value("ana.auto@teste.com"))
                .andExpect(jsonPath("$.dataAceiteTermosLgpd").exists())
                .andExpect(jsonPath("$.categorias.length()").value(2))
                .andExpect(jsonPath("$.contas.length()").value(1))
                .andExpect(jsonPath("$.lancamentos.length()").value(3))
                .andExpect(jsonPath("$.metasFinanceiras.length()").value(1));

       // 22. TESTAR LGPD (Art. 18, VI - Direito ao Esquecimento): Excluir conta com Metas, Lançamentos, Contas e Categorias ativas!
        mockMvc.perform(delete("/usuarios/me")
                .header("Authorization", bearerToken))
                .andExpect(status().isNoContent());

        // 22.1. Confirmar que o token antigo é recusado imediatamente (401 Unauthorized)
        mockMvc.perform(get("/usuarios/me")
                .header("Authorization", bearerToken))
                .andExpect(status().isUnauthorized());

        // 22.2. Confirmar que o usuário foi realmente eliminado do bancSo tentando fazer login (Deve dar 401 Unauthorized)
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLogin))
                .andExpect(status().isUnauthorized());
    }

    // Extrai qualquer campo (texto ou número) do JSON retornado usando Java puro
    private String extrairCampoJson(MvcResult resultado, String campo) throws Exception {
        String json = resultado.getResponse().getContentAsString();
        Pattern padrao = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher matcher = padrao.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Campo '" + campo + "' não encontrado no JSON: " + json);
    }

	
}