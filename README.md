# 🏦 API RESTful de Controle Financeiro Pessoal

![Java](https://img.shields.io/badge/Java-26-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-Stateless_JWT-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Hibernate JPA](https://img.shields.io/badge/Hibernate_/_JPA-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-JJWT_0.12.6-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Swagger OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI_3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![JUnit 5](https://img.shields.io/badge/Tests-JUnit_5_%7C_MockMvc-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![LGPD](https://img.shields.io/badge/LGPD-Compliant_(Lei_13.709/18)-0052CC?style=for-the-badge&logo=shield&logoColor=white)
![OWASP](https://img.shields.io/badge/OWASP-API_Security_Top_10-B22222?style=for-the-badge&logo=owasp&logoColor=white)

> **Back-end corporativo moderno para gerenciamento de finanças individuais**, abrangendo o controle integrado de **Contas**, **Categorias**, **Lançamentos Financeiros**, **Metas Financeiras Automatizadas** e **Dashboard Analítico em Tempo Real**. Arquitetado desde a concepção sob os pilares de **Privacy by Design (LGPD — Lei nº 13.709/2018)** e **Segurança Defensiva de APIs (OWASP API Security Top 10)**.

---

## 📋 Sumário

1. [Visão Geral do Projeto](#-1-visão-geral-do-projeto)
2. [Stack Tecnológica e Arquitetura do Sistema](#-2-stack-tecnológica-e-arquitetura-do-sistema)
3. [Regras de Negócio e Funcionalidades Core](#-3-regras-de-negócio-e-funcionalidades-core)
4. [Conformidade Técnica e Jurídica com a LGPD (Lei nº 13.709/2018)](#-4-conformidade-técnica-e-jurídica-com-a-lgpd-lei-nº-137092018)
5. [Cibersegurança e Blindagem OWASP API Security](#-5-cibersegurança-e-blindagem-owasp-api-security)
6. [Catálogo Completo de Endpoints da API (Com Exemplos de JSON)](#-6-catálogo-completo-de-endpoints-da-api-com-exemplos-de-json)
7. [Documentação Interativa com Swagger (OpenAPI 3)](#-7-documentação-interativa-com-swagger-openapi-3)
8. [Suíte de Testes Automatizados End-to-End (`FinanceiroApplicationTests`)](#-8-suíte-de-testes-automatizados-end-to-end-financeiroapplicationtests)
9. [Guia de Instalação, Configuração (`.env`) e Execução Local](#-9-guia-de-instalação-configuração-env-e-execução-local)

---

## 🎯 1. Visão Geral do Projeto

A **API de Controle Financeiro Pessoal** (`com.Projeto.Financeiro`) é uma solução RESTful robusta desenvolvida em **Java 26** e **Spring Boot 4.1.2**, projetada para oferecer previsibilidade, rastreabilidade e segurança no gerenciamento do patrimônio pessoal.

Diferente de CRUDs financeiros convencionais, esta API implementa:
- **Cálculo Dinâmico e Determinístico de Saldos:** O saldo das contas jamais sofre inconsistências por concorrência ou desatualização, sendo apurado em tempo real a partir do histórico de movimentações efetivamente pagas.
- **Isolamento Multi-Tenant Estrito por Titular:** Cada operação no banco de dados é vinculada ao identificador extraído diretamente do contexto criptográfico do token JWT (`@AuthenticationPrincipal Usuario`), neutralizando vulnerabilidades de *Broken Object Level Authorization (BOLA / IDOR)*.
- **Governança Nativa de Dados Pessoais (LGPD):** Endpoints dedicados para transparência pública dos termos e contato do DPO (`GET /auth/termos-lgpd`), coleta de consentimento explícito e auditável (`@AssertTrue`), portabilidade integral de dados em formato estruturado (`GET /usuarios/me/exportar`) e Direito ao Esquecimento com exclusão física em cascata e invalidação imediata de sessão (`DELETE /usuarios/me`).
- **Blindagem Ativa contra Ameaças (OWASP):** Proteção em memória contra ataques de força bruta com bloqueio temporário (`429 Too Many Requests`), prevenção contra *Long Password DoS* no BCrypt, sanitização de mensagens de erro contra vazamento de *StackTrace* ou estrutura interna e cabeçalhos HTTP de segurança.

---

## 🏗️ 2. Stack Tecnológica e Arquitetura do Sistema

### 🛠️ Tecnologias e Dependências (`pom.xml`)

| Tecnologia / Biblioteca | Versão | Papel na Arquitetura |
| :--- | :---: | :--- |
| **Java (OpenJDK)** | **26** *(target 17+)* | Linguagem base utilizando recursos modernos como *Java Records*, *Text Blocks*, *Pattern Matching* e API de Datas imutáveis (`java.time`). |
| **Spring Boot** | **4.1.2** | Framework principal de inversão de controle (IoC), injeção de dependências e autoconfiguração produtiva (`spring-boot-starter-parent`). |
| **Spring Web MVC** | **4.1.2** | Construção dos endpoints RESTful, serialização JSON via Jackson e tratamento global de exceções (`@RestControllerAdvice`). |
| **Spring Data JPA / Hibernate** | **4.1.2** | Camada de persistência ORM, gerenciamento transacional (`@Transactional`), *Derived Queries* e consultas customizadas em **JPQL** com projeções diretas para DTO. Configurado com `spring.jpa.open-in-view=false` para evitar *Connection Starvation* e queries `N+1` fora da camada de serviço. |
| **Spring Security** | **4.1.2** | Pipeline de segurança HTTP *Stateless*, controle de acesso por rotas, configuração de CORS, proteção de cabeçalhos HTTP e criptografia de senhas com `BCryptPasswordEncoder`. |
| **Spring Validation** | **4.1.2** | Validação declarativa de contratos de entrada (Jakarta Bean Validation: `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@DecimalMin`, `@Pattern`, `@AssertTrue`). |
| **MySQL Connector/J & HikariCP** | **8.0+** | Driver JDBC para persistência relacional no **MySQL 8.0**, gerenciado pelo pool de conexões de alta performance **HikariCP**. |
| **JJWT (`io.jsonwebtoken`)** | **0.12.6** | Geração, assinatura e verificação criptográfica de tokens **JWT** utilizando algoritmo **HMAC-SHA** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`). |
| **SpringDoc OpenAPI UI** | **2.8.5** | Geração automática da especificação **OpenAPI 3** (`/v3/api-docs`) e interface interativa **Swagger UI** (`/swagger-ui/index.html`) com suporte global a `bearerAuth`. |
| **JUnit 5 & Spring MockMvc** | **4.1.2 (Test)** | Suíte de testes automatizados de integração *End-to-End* com simulação real da cadeia de filtros de segurança e rollback transacional automático no MySQL. |

---

### 📂 Arquitetura em Camadas e Estrutura de Pacotes

O projeto segue rigorosamente a arquitetura em camadas (*Layered Architecture*), garantindo baixo acoplamento, alta coesão e separação clara entre contratos de entrada/saída (DTOs) e o modelo de domínio persistente (Entidades JPA):

```text
Financeiro/
├── pom.xml                                                 # Gerenciamento de dependências Maven e plugins Spring Boot 4.1.2
├── .env                                                    # Variáveis de ambiente locais (Credenciais do MySQL e Chave Secreta JWT)
└── src/
    ├── main/
    │   ├── java/com/Projeto/Financeiro/
    │   │   ├── FinanceiroApplication.java                  # Ponto de entrada (@SpringBootApplication)
    │   │   │
    │   │   ├── config/                                     # Configurações de Infraestrutura, Segurança e Documentação
    │   │   │   ├── OpenApiConfig.java                      # Configuração do Swagger/OpenAPI 3, metadados DPO e esquema global bearerAuth
    │   │   │   ├── PasswordConfig.java                     # Bean do BCryptPasswordEncoder para hash irreversível de senhas
    │   │   │   └── SecurityConfig.java                     # SecurityFilterChain Stateless, CORS, Headers OWASP e rotas públicas/privadas
    │   │   │
    │   │   ├── controller/                                 # Camada de Apresentação REST (Recebe DTOs validados e injeta @AuthenticationPrincipal)
    │   │   │   ├── AuthController.java                     # Endpoints públicos: /auth/termos-lgpd, /auth/register e /auth/login
    │   │   │   ├── UsuarioController.java                  # Gestão do titular e direitos LGPD: /usuarios/me, /usuarios/me/exportar
    │   │   │   ├── CategoriaController.java                # CRUD de Categorias: /categorias
    │   │   │   ├── ContaController.java                    # CRUD de Contas e consulta de saldo dinâmico: /contas
    │   │   │   ├── LancamentoController.java               # CRUD de Lançamentos e busca com filtros combinados: /lancamentos
    │   │   │   ├── MetafinanceiraController.java           # CRUD de Metas Financeiras com progresso automático: /metas
    │   │   │   └── DashboardController.java                # Indicadores analíticos consolidados: /dashboard, /categorias, /mensal
    │   │   │
    │   │   ├── service/                                    # Camada de Negócio Transacional (@Service / @Transactional)
    │   │   │   ├── UsuarioService.java                     # Cadastro, autenticação com Rate Limiting (Força Bruta) e exclusão em cascata LGPD
    │   │   │   ├── CategoriaService.java                   # Regras de categorias e bloqueio de exclusão com vínculo (RN07)
    │   │   │   ├── ContaService.java                       # Regras de contas, cálculo dinâmico de saldoAtual e proteção RN06
    │   │   │   ├── LancamentoService.java                  # Validação de conta ativa, coerência RN03, datas e filtros dinâmicos
    │   │   │   ├── MetaFinanceiraService.java              # Cálculo de percentual (BigDecimal HALF_UP) e conclusão automática
    │   │   │   └── DashboardService.java                   # Consolidação financeira de saldos, receitas, despesas, pendências e rankings
    │   │   │
    │   │   ├── repository/                                 # Camada de Persistência de Dados (Spring Data JPA & Queries JPQL)
    │   │   │   ├── UsuarioRepository.java                  # Busca e verificação de unicidade por e-mail
    │   │   │   ├── CategoriaRepository.java                # Consultas e exclusão em lote isoladas por usuario_id
    │   │   │   ├── ContaRepository.java                    # Consultas e exclusão em lote isoladas por usuario_id
    │   │   │   ├── LancamentoRepository.java               # Queries JPQL dinâmicas, somatórios com COALESCE e projeções para DTO
    │   │   │   └── MetaFinanceiraRepository.java           # Consultas ordenadas por dataLimite ASC e exclusão por usuario_id
    │   │   │
    │   │   ├── model/                                      # Modelo de Domínio (Entidades JPA e Enumerações)
    │   │   │   ├── Usuario.java                            # Entidade raiz com auditoria @PrePersist de cadastro e consentimento LGPD
    │   │   │   ├── Categoria.java                          # Entidade de categorização financeira (RECEITA / DESPESA)
    │   │   │   ├── Conta.java                              # Entidade de conta financeira com saldoInicial e flag ativa
    │   │   │   ├── Lancamento.java                         # Entidade central de movimentação financeira (valor, datas, status, vínculos)
    │   │   │   ├── MetaFinanceira.java                     # Entidade de objetivos financeiros do usuário
    │   │   │   ├── Role.java                               # Enum: USER, ADMIN
    │   │   │   ├── TipoCategoria.java                      # Enum: RECEITA, DESPESA
    │   │   │   ├── TipoConta.java                          # Enum: CONTA_BANCARIA, CONTA_DIGITAL, POUPANCA, CARTEIRA
    │   │   │   ├── TipoLancamento.java                     # Enum: RECEITA, DESPESA
    │   │   │   ├── StatusLancamento.java                   # Enum: PENDENTE, PAGO, CANCELADO
    │   │   │   └── StatusMeta.java                         # Enum: EM_ANDAMENTO, CONCLUIDA, CANCELADA
    │   │   │
    │   │   ├── dto/                                        # Objetos de Transferência de Dados (Java Records & Classes Validadas)
    │   │   │   ├── UsuarioRequest.java                     # Payload de cadastro/atualização com política de senha forte e @AssertTrue LGPD
    │   │   │   ├── LoginRequest.java                       # Payload de credenciais de autenticação
    │   │   │   ├── LoginResponse.java                      # Resposta contendo token JWT e tipo "Bearer"
    │   │   │   ├── CategoriaRequest.java                   # Record de entrada de categoria
    │   │   │   ├── CategoriaResponse.java                  # Record de saída de categoria
    │   │   │   ├── ContaRequest.java                       # Record de entrada de conta
    │   │   │   ├── ContaResponse.java                      # Record de saída de conta (inclui saldoAtual calculado)
    │   │   │   ├── LancamentoRequest.java                  # Record de entrada de lançamento
    │   │   │   ├── LancamentoResponse.java                 # Record de saída de lançamento (enriquecido com nomes de conta e categoria)
    │   │   │   ├── MetaFinanceiraRequest.java              # Record de entrada de meta financeira
    │   │   │   ├── MetaFinanceiraResponse.java             # Record de saída de meta (inclui percentualConcluido)
    │   │   │   ├── DashboardResponse.java                  # Record consolidado com todos os indicadores do período
    │   │   │   ├── DespesaCategoriaResponse.java           # Record para gráfico/ranking de despesas agrupadas por categoria
    │   │   │   └── ExportacaoDadosLgpdResponse.java        # Record completo para portabilidade de dados (Art. 18, V da LGPD)
    │   │   │
    │   │   ├── security/                                   # Componentes de Autenticação e Filtro JWT
    │   │   │   ├── JwtService.java                         # Assinatura, extração de Claims e validação temporal de tokens JWT
    │   │   │   └── JwtAuthenticationFilter.java            # Interceptador OncePerRequestFilter que autentica a requisição no SecurityContext
    │   │   │
    │   │   └── exeception/                                 # Tratamento Centralizado de Erros e Blindagem de Respostas
    │   │       └── GlobalExceptionHandler.java             # @RestControllerAdvice contra vazamento de StackTrace e JSON inválido
    │   │
    │   └── resources/
    │       └── application.properties                      # Configurações de DataSource, JPA/Hibernate, JWT e importação do .env
    │
    └── test/
        └── java/com/Projeto/Financeiro/
            └── FinanceiroApplicationTests.java             # Suíte de testes de integração End-to-End (26 cenários validados)
```

---

### 🗄️ Diagrama de Entidade-Relacionamento (DER)

O modelo relacional foi projetado para garantir **isolamento absoluto por titular (`usuario_id`)** em todas as entidades de domínio. A entidade `Usuario` possui relacionamento `1:N` com as 4 tabelas filhas (`categorias`, `contas`, `lancamentos` e `metas_financeiras`), enquanto `lancamentos` também referencia obrigatoriamente uma `Categoria` (`categoria_id`) e uma `Conta` (`conta_id`).

```mermaid
erDiagram
    USUARIO ||--o{ CATEGORIA : "possui (1:N)"
    USUARIO ||--o{ CONTA : "possui (1:N)"
    USUARIO ||--o{ LANCAMENTO : "realiza (1:N)"
    USUARIO ||--o{ META_FINANCEIRA : "define (1:N)"
    CATEGORIA ||--o{ LANCAMENTO : "classifica (1:N)"
    CONTA ||--o{ LANCAMENTO : "movimenta (1:N)"

    USUARIO {
        bigint id PK "AUTO_INCREMENT"
        varchar(255) nome "NOT NULL"
        varchar(150) email UK "NOT NULL, UNIQUE"
        varchar(255) senha "NOT NULL (Hash BCrypt)"
        varchar(20) role "NOT NULL (USER | ADMIN)"
        datetime data_cadastro "NOT NULL, UPDATABLE=FALSE"
        datetime data_aceite_termos "UPDATABLE=FALSE (LGPD Art. 8)"
        varchar(20) versao_termos "UPDATABLE=FALSE (Default '1.0')"
    }

    CATEGORIA {
        bigint id PK "AUTO_INCREMENT"
        varchar(100) nome "NOT NULL"
        varchar(20) tipo "NOT NULL (RECEITA | DESPESA)"
        varchar(255) descricao "NULLABLE"
        bigint usuario_id FK "NOT NULL"
    }

    CONTA {
        bigint id PK "AUTO_INCREMENT"
        varchar(100) nome "NOT NULL"
        varchar(30) tipo "NOT NULL (CONTA_BANCARIA | CONTA_DIGITAL | POUPANCA | CARTEIRA)"
        decimal(15_2) saldo_inicial "NOT NULL"
        boolean ativa "NOT NULL (Default TRUE)"
        bigint usuario_id FK "NOT NULL"
    }

    LANCAMENTO {
        bigint id PK "AUTO_INCREMENT"
        varchar(150) descricao "NOT NULL"
        decimal(15_2) valor "NOT NULL (>= 0.01)"
        varchar(20) tipo "NOT NULL (RECEITA | DESPESA)"
        date data "NOT NULL"
        date data_vencimento "NULLABLE (>= data)"
        varchar(20) status "NOT NULL (PENDENTE | PAGO | CANCELADO)"
        bigint categoria_id FK "NOT NULL"
        bigint conta_id FK "NOT NULL"
        bigint usuario_id FK "NOT NULL"
    }

    META_FINANCEIRA {
        bigint id PK "AUTO_INCREMENT"
        varchar(100) nome "NOT NULL"
        decimal(15_2) valor_objetivo "NOT NULL (>= 0.01)"
        decimal(15_2) valor_atual "NOT NULL (>= 0.00)"
        date data_limite "NOT NULL"
        varchar(20) status "NOT NULL (EM_ANDAMENTO | CONCLUIDA | CANCELADA)"
        bigint usuario_id FK "NOT NULL"
    }
```

---

## ⚙️ 3. Regras de Negócio e Funcionalidades Core

### 3.1. Isolamento Multi-Tenant por Titular (Prevenção de BOLA / IDOR)
- Nenhum endpoint recebe `usuarioId` via parâmetros de URL ou corpo JSON.
- O titular autenticado é resolvido exclusivamente pelo filtro `JwtAuthenticationFilter` e injetado nos Controllers via `@AuthenticationPrincipal Usuario usuario`.
- Todas as queries de busca, atualização e deleção nos repositórios exigem a combinação `(id, usuarioId)` — como `findByIdAndUsuario_Id(id, usuarioId)` e `findAllByUsuario_Id(usuarioId)`. Caso um usuário autenticado tente acessar, editar ou excluir um recurso pertencente a outro usuário, o sistema retorna imediatamente **`404 Not Found`**, sem revelar sequer a existência do registro alheio.

### 3.2. Gestão de Categorias (`CategoriaService`)
- Cada categoria pertence a um único usuário e é classificada estritamente pelo enum `TipoCategoria` (`RECEITA` ou `DESPESA`).
- **Proteção de Integridade Referencial (RN07):** Antes de excluir uma categoria, o serviço consulta `lancamentoRepository.existsByCategoria_IdAndUsuario_Id(id, usuarioId)`. Se houver qualquer lançamento vinculado, a exclusão é abortada com status **`409 Conflict`** e mensagem explicativa, preservando o histórico contábil do usuário.

### 3.3. Gestão de Contas e Cálculo Dinâmico de Saldo (`ContaService`)
- Suporta quatro modalidades através do enum `TipoConta`: `CONTA_BANCARIA`, `CONTA_DIGITAL`, `POUPANCA` e `CARTEIRA`.
- **Cálculo Dinâmico em Tempo Real (`calcularSaldoConta`):** O campo `saldoAtual` retornado no `ContaResponse` nunca é armazenado de forma estática no banco para evitar anomalias de sincronização. Ele é calculado em tempo real via agregação JPQL (`COALESCE(SUM(l.valor), 0)`) seguindo a fórmula:

$$\text{Saldo Atual} = \text{Saldo Inicial} + \sum \text{Receitas}_{\text{PAGAS}} - \sum \text{Despesas}_{\text{PAGAS}}$$

- **Isolamento por Status:** Lançamentos com status `PENDENTE` ou `CANCELADO` são ignorados no cálculo do `saldoAtual`, garantindo que apenas movimentações efetivamente liquidadas impactem a disponibilidade financeira da conta.
- **Proteção de Integridade Referencial (RN06):** A exclusão de uma conta (`DELETE /contas/{id}`) verifica previamente `lancamentoRepository.existsByConta_IdAndUsuario_Id(id, usuarioId)`. Caso existam lançamentos vinculados, a operação é bloqueada com **`409 Conflict`**.

### 3.4. Gestão de Lançamentos (`LancamentoService`)
- Registra movimentações classificadas por `TipoLancamento` (`RECEITA`, `DESPESA`) e `StatusLancamento` (`PENDENTE`, `PAGO`, `CANCELADO`).
- **Validação de Propriedade Cruzada:** Garante que tanto a `Categoria` (`categoriaId`) quanto a `Conta` (`contaId`) informadas pertençam ao usuário autenticado (`404 Not Found` caso contrário).
- **Validação de Conta Ativa:** Bloqueia com **`400 Bad Request`** qualquer tentativa de registrar ou atualizar lançamentos em uma conta marcada como inativa (`!conta.isAtiva()`).
- **Coerência de Tipo entre Lançamento e Categoria (RN03):** Impede a criação ou atualização de um lançamento cujo `TipoLancamento` seja incompatível com o `TipoCategoria` da categoria associada (ex.: tentar lançar uma `DESPESA` usando a categoria "Salário" do tipo `RECEITA`), retornando **`400 Bad Request`**.
- **Coerência Cronológica:**
  - Se `dataVencimento` for informada, valida que ela não seja anterior à `data` do lançamento (`dataVencimento.isBefore(data)` -> **`400 Bad Request`**).
  - Na listagem com filtros, valida que `dataFim` não seja anterior a `dataInicio` (`dataFim.isBefore(dataInicio)` -> **`400 Bad Request`**).
- **Filtros Dinâmicos Combinados via JPQL (`listarComFiltros`):** O endpoint `GET /lancamentos` permite combinar livremente até 6 parâmetros opcionais (`dataInicio`, `dataFim`, `categoriaId`, `contaId`, `tipo`, `status`) em uma única query JPQL otimizada e ordenada por `l.data DESC`.

### 3.5. Metas Financeiras Automatizadas (`MetaFinanceiraService`)
- Permite planejar objetivos financeiros com `valorObjetivo`, `valorAtual` e `dataLimite`, listados por padrão na ordem das metas mais próximas do vencimento (`findAllByUsuario_IdOrderByDataLimiteAsc`).
- **Cálculo de Progresso com Precisão Monetária:** O campo `percentualConcluido` é calculado automaticamente em `BigDecimal` com escala de 2 casas decimais e arredondamento financeiro `RoundingMode.HALF_UP`:

$$\text{Percentual Concluído} = \left( \frac{\text{Valor Atual} \times 100}{\text{Valor Objetivo}} \right)$$

- **Transição Automática de Status (`definirStatusAutomatico`):** Sempre que uma meta é cadastrada ou atualizada e seu `valorAtual` atinge ou supera o `valorObjetivo` (`valorAtual.compareTo(valorObjetivo) >= 0`), o serviço promove automaticamente o status de `EM_ANDAMENTO` para **`CONCLUIDA`** (respeitando apenas a exceção caso o usuário tenha definido explicitamente o status como `CANCELADA`).

### 3.6. Dashboard Analítico (`DashboardService`)
- **Período Inteligente Padrão:** Caso `dataInicio` e/ou `dataFim` não sejam informados na requisição, o serviço assume automaticamente o primeiro dia (`hoje.withDayOfMonth(1)`) e o último dia (`hoje.withDayOfMonth(hoje.lengthOfMonth())`) do mês corrente.
- **Consolidação em Resposta Única (`DashboardResponse`):**
  1. `saldoTotal`: Soma do `saldoAtual` de todas as contas **ativas** (`ContaResponse::ativa`) do titular.
  2. `receitasPeriodo`: Soma de todas as receitas com status `PAGO` dentro do intervalo de datas.
  3. `despesasPeriodo`: Soma de todas as despesas com status `PAGO` dentro do intervalo de datas.
  4. `resultadoFinanceiro`: Balanço líquido do período (`receitasPeriodo.subtract(despesasPeriodo)`).
  5. `despesasPendentes`: Soma de todas as despesas com status `PENDENTE` no período (previsão de saída de caixa).
  6. `despesasPorCategoria`: Ranking analítico das despesas pagas no período, agrupadas por categoria (`GROUP BY l.categoria.id, l.categoria.nome`) e ordenadas da maior despesa para a menor (`ORDER BY SUM(l.valor) DESC`) com projeção direta para `DespesaCategoriaResponse`.
  7. `ultimasMovimentacoes`: Top 5 movimentações mais recentes do usuário (`findTop5ByUsuario_IdOrderByDataDescIdDesc`).
- **Visões Especializadas:** Disponibiliza também o detalhamento exclusivo por categorias (`GET /dashboard/categorias`) e o fechamento mensal parametrizado por ano e mês (`GET /dashboard/mensal?ano=YYYY&mes=MM`, com validação de mês entre `1` e `12`).

---

## 🛡️ 4. Conformidade Técnica e Jurídica com a LGPD (Lei nº 13.709/2018)

> [!IMPORTANT]
> Este sistema foi projetado sob a metodologia **Privacy by Design & Privacy by Default**, traduzindo as exigências jurídicas da **Lei Geral de Proteção de Dados Pessoais (Lei nº 13.709/2018)** diretamente em regras de validação, constraints de banco de dados e endpoints dedicados aos direitos do titular.

### 📊 Matriz de Rastreabilidade LGPD no Código-Fonte

| Dispositivo Legal (LGPD) | Princípio / Direito do Titular | Implementação Técnica no Projeto | Classes / Endpoints Responsáveis |
| :--- | :--- | :--- | :--- |
| **Art. 6º, I e III**<br>**Art. 46** | **Finalidade, Necessidade, Minimização de Dados e Segurança nos Logs** | Coleta restrita estritamente ao mínimo necessário para autenticação e identificação (`nome`, `email`, `senha`), sem solicitar CPF, RG, telefone, endereço ou dados sensíveis. Além disso, a exibição de queries SQL nos logs é desativada por padrão (`spring.jpa.show-sql=${SHOW_SQL:false}`) para impedir vazamento de dados pessoais em arquivos de log de produção. | `Usuario.java`<br>`UsuarioRequest.java`<br>`application.properties` |
| **Art. 7º, I**<br>**Art. 8º, § 2º e § 4º** | **Consentimento Livre, Informado, Inequívoco e Auditável (Ônus da Prova)** | **Proibição de *opt-in* automático:** No `UsuarioRequest`, o atributo `private boolean aceitouTermos;` inicia como `false` por padrão e exige `@AssertTrue`. Omitir o campo no JSON ou enviar `false` bloqueia o cadastro com `400 Bad Request`.<br>**Auditoria imutável:** No `@PrePersist` de `Usuario`, o sistema grava automaticamente o timestamp exato (`dataAceiteTermos`) e a versão da política aceita (`versaoTermos = "1.0"`), ambos mapeados com `updatable = false` no banco de dados. | `UsuarioRequest.java`<br>`Usuario.java`<br>`POST /auth/register` |
| **Art. 9º**<br>**Art. 41** | **Transparência Facilitada e Canal de Contato do Encarregado (DPO)** | Disponibilização de endpoint **100% público** (`permitAll()`) acessível antes mesmo de qualquer cadastro, retornando de forma clara a versão vigente dos termos (`1.0`), finalidade específica do tratamento, lista exata de dados coletados, política de não compartilhamento com terceiros, regras de retenção e o contato oficial do DPO (`dpo.privacidade@projetofinanceiro.com.br`). | `AuthController.java`<br>`OpenApiConfig.java`<br>`GET /auth/termos-lgpd` |
| **Art. 18, I, II e III** | **Confirmação da Existência de Tratamento, Acesso aos Dados e Correção** | Permite ao titular consultar a qualquer momento seus dados cadastrais e metadados de consentimento (`dataCadastro`, `dataAceiteTermos`, `versaoTermos`) e atualizar seus dados pessoais. O retorno utiliza o record `UsuarioResponse`, garantindo que o hash BCrypt da senha **jamais** seja exposto no JSON. | `UsuarioController.java`<br>`UsuarioService.java`<br>`GET /usuarios/me`<br>`PUT /usuarios/me` |
| **Art. 18, V** | **Direito à Portabilidade dos Dados** | Exporta em uma única requisição todo o acervo de dados pessoais e financeiros do titular em formato estruturado e interoperável (JSON via `ExportacaoDadosLgpdResponse`), contendo metadados da exportação, dados do titular, histórico de consentimento e todas as suas categorias, contas, lançamentos e metas financeiras. | `UsuarioController.java`<br>`ExportacaoDadosLgpdResponse.java`<br>`GET /usuarios/me/exportar` |
| **Art. 18, VI**<br>**Art. 16** | **Direito à Eliminação dos Dados Pessoais (Direito ao Esquecimento)** | Executa a exclusão física, definitiva e transacional em cascata de todos os registros vinculados ao titular na ordem estrita de integridade referencial: `MetaFinanceira` $\rightarrow$ `Lancamento` $\rightarrow$ `Conta` $\rightarrow$ `Categoria` $\rightarrow$ `Usuario`. Em seguida, força a sincronização imediata com o banco via `usuarioRepository.flush()` e limpa a sessão em memória (`SecurityContextHolder.clearContext()`), invalidando o token JWT instantaneamente (`401 Unauthorized`). | `UsuarioController.java`<br>`UsuarioService.java`<br>`DELETE /usuarios/me` |

---

## 🔒 5. Cibersegurança e Blindagem OWASP API Security

A arquitetura implementa defesas em profundidade alinhadas ao **OWASP API Security Top 10** e ao **Art. 46 da LGPD** (medidas técnicas e administrativas aptas a proteger os dados pessoais de acessos não autorizados):

```mermaid
flowchart LR
    Client["Cliente / Front-end"] -->|1. Requisicao HTTP + JWT| CORS["CORS & Security Headers<br/>(X-Frame-Options: DENY / nosniff)"]
    CORS -->|2. Interceptacao| Filter["JwtAuthenticationFilter<br/>(Clear Context + Valida HMAC & Usuario Ativo)"]
    Filter -->|3. Validacao Bean| Controller["RestControllers<br/>(@Valid + @AuthenticationPrincipal)"]
    Controller -->|4. Regras & Rate Limit| Service["Services (@Transactional)<br/>(Anti-Brute Force 429 + Isolamento Multi-Tenant)"]
    Service -->|5. Queries Seguras| Repo["Repositories (JPQL Parametrizado)<br/>(Zero SQL Injection + Rollback/Flush)"]
    Repo --> DB[("MySQL 8.0<br/>(Senhas em BCrypt)")]
    Controller -.->|Excecoes Capturadas| Handler["GlobalExceptionHandler<br/>(Oculta StackTrace e Internals)"]
```

### 5.1. Proteção contra Ataques de Força Bruta / Rate Limiting (`UsuarioService` — OWASP API2 / API4)
- Implementa controle concorrente e *thread-safe* em memória utilizando estruturas `ConcurrentHashMap<String, Integer> tentativasLogin` e `ConcurrentHashMap<String, LocalDateTime> bloqueiosLogin`.
- A chave de rastreamento do e-mail é normalizada (`email.toLowerCase().trim()`).
- Ao atingir **5 tentativas de login falhas consecutivas** (`MAX_TENTATIVAS_FALHAS = 5`), o identificador entra em quarentena por **15 minutos** (`MINUTOS_BLOQUEIO = 15`).
- Enquanto o bloqueio estiver ativo, qualquer nova tentativa de login para aquele e-mail é interceptada **antes** de consultar o banco de dados ou acionar o algoritmo `BCrypt`, lançando imediatamente **`429 Too Many Requests`**:
  ```json
  {
    "mensagem": "Muitas tentativas de login falhas. Conta temporariamente protegida por 15 minutos."
  }
  ```
- Caso o usuário autentique com sucesso antes da 5ª falha (ou após a expiração dos 15 minutos), os contadores são automaticamente limpos (`limparFalhasLogin`).

### 5.2. Política de Senha Forte e Prevenção de DoS no BCrypt (`UsuarioRequest` — OWASP API2 / API4)
- **Prevenção contra *Long Password Denial of Service*:** O algoritmo `BCrypt` possui custo computacional intensivo e processa blocos limitados de bytes; senhas gigantescas podem exaurir a CPU do servidor. Por isso, `UsuarioRequest` impõe `@Size(min = 8, max = 100)`.
- **Complexidade Alfanumérica Obrigatória:** Validação via expressão regular `@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$")`, exigindo obrigatoriamente a presença combinada de letras e números e rejeitando senhas fracas com `400 Bad Request`.

### 5.3. Blindagem contra Vazamento de StackTrace e JSON Malformado (`GlobalExceptionHandler` — OWASP API8 / LGPD Art. 46)
A classe `GlobalExceptionHandler` (`@RestControllerAdvice`) intercepta todas as falhas da aplicação para garantir que nenhuma informação sensível de infraestrutura, nomes de pacotes, classes internas ou estrutura de tabelas SQL vaze para o cliente:

| Exceção Interceptada | Status HTTP Retornado | Comportamento de Segurança |
| :--- | :---: | :--- |
| `MethodArgumentNotValidException` | `400 Bad Request` | Mapeia cada campo inválido (`field`) para sua respectiva mensagem amigável do Bean Validation. |
| `HttpMessageNotReadableException` | `400 Bad Request` | Oculta exceções internas do Jackson (que exporiam nomes de pacotes `com.Projeto.Financeiro.model.*` e valores de Enums quando um JSON quebrado é enviado) e retorna mensagem limpa e padronizada. |
| `IllegalArgumentException` | `409 Conflict` | Converte conflitos de regra de negócio (como e-mail duplicado no cadastro ou atualização) em `{"mensagem": "..."}`. |
| `ResponseStatusException` | Dinâmico (`400`, `404`, `409`, `429`) | Preserva o código HTTP exato lançado pelos serviços e formata o motivo em `{"mensagem": "..."}`. |
| `Exception` *(Fallback Geral)* | `500 Internal Server Error` | Captura qualquer falha inesperada e retorna apenas `{"mensagem": "Ocorreu um erro interno no servidor. Tente novamente mais tarde."}`, eliminando vazamento de *StackTrace*. |

### 5.4. Hardening do Spring Security, CORS e Security Headers (`SecurityConfig`)
- **Arquitetura 100% Stateless:** `SessionCreationPolicy.STATELESS`, com desativação de `formLogin`, `httpBasic` e `csrf` (adequado para APIs REST baseadas em tokens Bearer).
- **Validação Rigorosa por Requisição (`JwtAuthenticationFilter`):**
  - Executa `SecurityContextHolder.clearContext()` no início do processamento do token para eliminar qualquer risco de contaminação de thread.
  - Além de verificar a assinatura criptográfica HMAC-SHA e a data de expiração (`JwtService`), consulta o banco de dados (`usuarioService.buscarPorEmail(email)`) para confirmar que o usuário ainda existe — garantindo que contas recém-excluídas via Direito ao Esquecimento tenham seus tokens invalidados no mesmo milissegundo.
  - O `FilterRegistrationBean` desativa o registro duplicado do filtro no container Servlet (`registration.setEnabled(false)`), garantindo execução única dentro da cadeia do Spring Security.
- **Security Headers (OWASP):**
  - `X-Frame-Options: DENY` (`frame.deny()`): Impede que respostas da API sejam embutidas em `<frame>` ou `<iframe>`, mitigando ataques de *Clickjacking*.
  - `X-Content-Type-Options: nosniff` (`contentTypeOptions`): Impede que navegadores façam *MIME-Sniffing* do payload JSON.
- **EntryPoint Padronizado:** Configura `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)` para retornar `401 Unauthorized` limpo sempre que um cliente não autenticado tentar acessar rotas privadas.
- **Política de CORS Restrita (`corsConfigurationSource`):** Permite apenas origens homologadas de desenvolvimento front-end (`http://localhost:3000`, `http://localhost:4200`, `http://localhost:5173`, `http://localhost:5500`, `http://127.0.0.1:5500`), métodos explícitos (`GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`) e cabeçalhos controlados (`Authorization`, `Content-Type`, `Accept`).
- **Supressão de Credenciais Padrão:** Sobrescreve o `@Bean UserDetailsService` para desativar a geração da senha temporária padrão do Spring Security no console de inicialização.

---

## 📡 6. Catálogo Completo de Endpoints da API (Com Exemplos de JSON)

### Visão Geral das Rotas

| Módulo | Método | URI | Autenticação | Status HTTP Possíveis | Descrição Resumida |
| :--- | :---: | :--- | :---: | :---: | :--- |
| **Auth / LGPD** | `GET` | `/auth/termos-lgpd` | 🔓 Pública | `200` | Consulta pública dos Termos de Privacidade (LGPD) e contato do DPO. |
| **Auth** | `POST` | `/auth/register` | 🔓 Pública | `201`, `400`, `409` | Cadastra novo usuário mediante aceite explícito dos termos LGPD. |
| **Auth** | `POST` | `/auth/login` | 🔓 Pública | `200`, `400`, `401`, `429` | Autentica usuário, gera token JWT e aplica proteção contra força bruta. |
| **Usuários / LGPD** | `GET` | `/usuarios/me` | 🔒 Bearer JWT | `200`, `401` | Retorna perfil e metadados de consentimento LGPD do titular autenticado. |
| **Usuários / LGPD** | `PUT` | `/usuarios/me` | 🔒 Bearer JWT | `200`, `400`, `401`, `409` | Atualiza nome, e-mail e senha do titular autenticado. |
| **Usuários / LGPD** | `GET` | `/usuarios/me/exportar` | 🔒 Bearer JWT | `200`, `401` | **Portabilidade LGPD (Art. 18, V):** Exporta todos os dados do titular. |
| **Usuários / LGPD** | `DELETE` | `/usuarios/me` | 🔒 Bearer JWT | `204`, `401` | **Direito ao Esquecimento (Art. 18, VI):** Exclui conta e dados em cascata. |
| **Categorias** | `POST` | `/categorias` | 🔒 Bearer JWT | `201`, `400`, `401` | Cria uma nova categoria (`RECEITA` ou `DESPESA`). |
| **Categorias** | `GET` | `/categorias` | 🔒 Bearer JWT | `200`, `401` | Lista todas as categorias do usuário autenticado. |
| **Categorias** | `GET` | `/categorias/{id}` | 🔒 Bearer JWT | `200`, `401`, `404` | Busca uma categoria específica do titular por ID. |
| **Categorias** | `PUT` | `/categorias/{id}` | 🔒 Bearer JWT | `200`, `400`, `401`, `404` | Atualiza os dados de uma categoria do titular. |
| **Categorias** | `DELETE` | `/categorias/{id}` | 🔒 Bearer JWT | `204`, `401`, `404`, `409` | Exclui uma categoria (bloqueia com `409` se houver lançamentos — RN07). |
| **Contas** | `POST` | `/contas` | 🔒 Bearer JWT | `201`, `400`, `401` | Cria uma nova conta financeira com saldo inicial. |
| **Contas** | `GET` | `/contas` | 🔒 Bearer JWT | `200`, `401` | Lista todas as contas do usuário com `saldoAtual` calculado em tempo real. |
| **Contas** | `GET` | `/contas/{id}` | 🔒 Bearer JWT | `200`, `401`, `404` | Busca uma conta específica por ID com `saldoAtual` atualizado. |
| **Contas** | `PUT` | `/contas/{id}` | 🔒 Bearer JWT | `200`, `400`, `401`, `404` | Atualiza nome, tipo e saldo inicial de uma conta. |
| **Contas** | `DELETE` | `/contas/{id}` | 🔒 Bearer JWT | `204`, `401`, `404`, `409` | Exclui uma conta (bloqueia com `409` se houver lançamentos — RN06). |
| **Lançamentos** | `POST` | `/lancamentos` | 🔒 Bearer JWT | `201`, `400`, `401`, `404` | Registra um lançamento validando conta ativa, datas e coerência RN03. |
| **Lançamentos** | `GET` | `/lancamentos` | 🔒 Bearer JWT | `200`, `400`, `401` | Lista lançamentos com filtros opcionais (`dataInicio`, `dataFim`, `categoriaId`, `contaId`, `tipo`, `status`). |
| **Lançamentos** | `GET` | `/lancamentos/{id}` | 🔒 Bearer JWT | `200`, `401`, `404` | Busca um lançamento específico do titular por ID. |
| **Lançamentos** | `PUT` | `/lancamentos/{id}` | 🔒 Bearer JWT | `200`, `400`, `401`, `404` | Atualiza um lançamento existente validando todas as regras de negócio. |
| **Lançamentos** | `DELETE` | `/lancamentos/{id}` | 🔒 Bearer JWT | `204`, `401`, `404` | Exclui um lançamento e recalcula instantaneamente o saldo da conta. |
| **Metas** | `POST` | `/metas` | 🔒 Bearer JWT | `201`, `400`, `401` | Cria meta financeira com cálculo de percentual e status automático. |
| **Metas** | `GET` | `/metas` | 🔒 Bearer JWT | `200`, `401` | Lista metas do titular ordenadas pela data limite mais próxima. |
| **Metas** | `GET` | `/metas/{id}` | 🔒 Bearer JWT | `200`, `401`, `404` | Busca uma meta financeira específica por ID. |
| **Metas** | `PUT` | `/metas/{id}` | 🔒 Bearer JWT | `200`, `400`, `401`, `404` | Atualiza meta e promove automaticamente para `CONCLUIDA` se atingir 100%. |
| **Metas** | `DELETE` | `/metas/{id}` | 🔒 Bearer JWT | `204`, `401`, `404` | Exclui uma meta financeira do titular. |
| **Dashboard** | `GET` | `/dashboard` | 🔒 Bearer JWT | `200`, `400`, `401` | Retorna resumo executivo completo do período (ou mês atual por padrão). |
| **Dashboard** | `GET` | `/dashboard/categorias` | 🔒 Bearer JWT | `200`, `400`, `401` | Retorna ranking de despesas pagas agrupadas por categoria no período. |
| **Dashboard** | `GET` | `/dashboard/mensal` | 🔒 Bearer JWT | `200`, `400`, `401` | Retorna resumo executivo de um mês/ano específico (`?ano=2026&mes=11`). |

---

### 🔐 6.1. Módulo de Autenticação e Transparência LGPD (`/auth`)

#### `GET /auth/termos-lgpd` — Consultar Termos de Uso, Política de Privacidade e DPO
- **Autenticação:** Pública (`permitAll`)
- **Response `200 OK` (`AuthController.TermosLgpdResponse`):**
```json
{
  "versaoTermos": "1.0",
  "finalidadeTratamento": "Gestão e controle financeiro pessoal de contas, categorias, lançamentos e metas do próprio titular.",
  "dadosColetados": [
    "Nome",
    "E-mail",
    "Senha (armazenada exclusivamente sob hash criptográfico BCrypt)"
  ],
  "compartilhamentoDados": "Os dados não são compartilhados com terceiros.",
  "retencaoDados": "Os dados são mantidos enquanto a conta estiver ativa, podendo ser exportados (GET /usuarios/me/exportar) ou eliminados permanentemente (DELETE /usuarios/me) a qualquer momento pelo titular.",
  "contatoEncarregadoDpo": "dpo.privacidade@projetofinanceiro.com.br"
}
```

#### `POST /auth/register` — Cadastrar Novo Usuário
- **Autenticação:** Pública (`permitAll`)
- **Request Body (`UsuarioRequest`):**
```json
{
  "nome": "Ana Silva",
  "email": "ana.silva@email.com",
  "senha": "senhaSegura123",
  "aceitouTermos": true
}
```
- **Response `201 Created`:**
```text
Usuário cadastrado com sucesso!
```
- **Possíveis Erros:** `400 Bad Request` (se `aceitouTermos` for `false`/omitido ou se a senha não contiver letras e números entre 8 e 100 caracteres); `409 Conflict` (se o e-mail já estiver cadastrado).

#### `POST /auth/login` — Autenticar e Gerar Token JWT
- **Autenticação:** Pública (`permitAll`)
- **Request Body (`LoginRequest`):**
```json
{
  "email": "ana.silva@email.com",
  "senha": "senhaSegura123"
}
```
- **Response `200 OK` (`LoginResponse`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmEuc2lsdmFAZW1haWwuY29tIiwiaWF0IjoxNzYzMzI0NDAwLCJleHAiOjE3NjM0MTA4MDB9.AssinaturaHmacSha256",
  "tipo": "Bearer"
}
```
- **Possíveis Erros:** `401 Unauthorized` (credenciais inválidas); `429 Too Many Requests` (conta bloqueada por 15 minutos após 5 falhas seguidas).

---

### 👤 6.2. Módulo do Titular e Direitos LGPD (`/usuarios`)

#### `GET /usuarios/me` — Consultar Perfil e Auditoria de Consentimento
- **Autenticação:** `Authorization: Bearer <token>`
- **Response `200 OK` (`UsuarioController.UsuarioResponse`):**
```json
{
  "id": 1,
  "nome": "Ana Silva",
  "email": "ana.silva@email.com",
  "dataCadastro": "2026-11-16T19:30:00",
  "dataAceiteTermos": "2026-11-16T19:30:00",
  "versaoTermos": "1.0"
}
```

#### `PUT /usuarios/me` — Atualizar Dados do Titular
- **Autenticação:** `Authorization: Bearer <token>`
- **Request Body (`UsuarioRequest`):**
```json
{
  "nome": "Ana Silva Atualizada",
  "email": "ana.silva@email.com",
  "senha": "novaSenhaForte456",
  "aceitouTermos": true
}
```
- **Response `200 OK` (`UsuarioController.UsuarioResponse`):** Retorna o objeto atualizado sem expor a senha.

#### `GET /usuarios/me/exportar` — Portabilidade Completa de Dados (LGPD Art. 18, V)
- **Autenticação:** `Authorization: Bearer <token>`
- **Response `200 OK` (`ExportacaoDadosLgpdResponse`):**
```json
{
  "dataExportacao": "2026-11-16T20:15:30",
  "idTitular": 1,
  "nomeTitular": "Ana Silva Atualizada",
  "emailTitular": "ana.silva@email.com",
  "dataCadastro": "2026-11-16T19:30:00",
  "dataAceiteTermosLgpd": "2026-11-16T19:30:00",
  "versaoTermosLgpd": "1.0",
  "categorias": [
    {
      "id": 1,
      "nome": "Salário",
      "tipo": "RECEITA",
      "descricao": "Renda mensal"
    },
    {
      "id": 2,
      "nome": "Mercado",
      "tipo": "DESPESA",
      "descricao": "Compras do mês"
    }
  ],
  "contas": [
    {
      "id": 1,
      "nome": "Conta Principal",
      "tipo": "CONTA_DIGITAL",
      "saldoInicial": 1000.00,
      "saldoAtual": 3200.00,
      "ativa": true
    }
  ],
  "lancamentos": [
    {
      "id": 1,
      "descricao": "Salário Recebido",
      "valor": 2500.00,
      "tipo": "RECEITA",
      "data": "2026-11-10",
      "dataVencimento": null,
      "status": "PAGO",
      "categoriaId": 1,
      "categoriaNome": "Salário",
      "contaId": 1,
      "contaNome": "Conta Principal"
    }
  ],
  "metasFinanceiras": [
    {
      "id": 1,
      "nome": "Viagem de Férias",
      "valorObjetivo": 3000.00,
      "valorAtual": 1500.00,
      "percentualConcluido": 50.00,
      "dataLimite": "2027-06-30",
      "status": "EM_ANDAMENTO"
    }
  ]
}
```

#### `DELETE /usuarios/me` — Excluir Conta e Dados em Cascata (Direito ao Esquecimento — LGPD Art. 18, VI)
- **Autenticação:** `Authorization: Bearer <token>`
- **Response `204 No Content`** (Sem corpo; remove permanentemente metas, lançamentos, contas, categorias e o registro do usuário, invalidando o token JWT imediatamente).

---

### 🏷️ 6.3. Módulo de Categorias (`/categorias`)

#### `POST /categorias` — Criar Categoria (`PUT /categorias/{id}` utiliza o mesmo Request Body)
- **Autenticação:** `Authorization: Bearer <token>`
- **Valores aceitos em `tipo` (`TipoCategoria`):** `RECEITA`, `DESPESA`
- **Request Body (`CategoriaRequest`):**
```json
{
  "nome": "Mercado",
  "tipo": "DESPESA",
  "descricao": "Compras de supermercado e alimentação"
}
```
- **Response `201 Created` (`CategoriaResponse`):**
```json
{
  "id": 2,
  "nome": "Mercado",
  "tipo": "DESPESA",
  "descricao": "Compras de supermercado e alimentação"
}
```

#### `GET /categorias` | `GET /categorias/{id}` | `DELETE /categorias/{id}`
- `GET /categorias`: Retorna `200 OK` com `List<CategoriaResponse>`.
- `GET /categorias/{id}`: Retorna `200 OK` com `CategoriaResponse` ou `404 Not Found`.
- `DELETE /categorias/{id}`: Retorna `204 No Content` ou **`409 Conflict`** caso existam lançamentos vinculados (RN07).

---

### 💳 6.4. Módulo de Contas (`/contas`)

#### `POST /contas` — Criar Conta Financeira (`PUT /contas/{id}` utiliza o mesmo Request Body)
- **Autenticação:** `Authorization: Bearer <token>`
- **Valores aceitos em `tipo` (`TipoConta`):** `CONTA_BANCARIA`, `CONTA_DIGITAL`, `POUPANCA`, `CARTEIRA`
- **Request Body (`ContaRequest`):**
```json
{
  "nome": "Conta Principal",
  "tipo": "CONTA_DIGITAL",
  "saldoInicial": 1000.00
}
```
- **Response `201 Created` (`ContaResponse`):**
```json
{
  "id": 1,
  "nome": "Conta Principal",
  "tipo": "CONTA_DIGITAL",
  "saldoInicial": 1000.00,
  "saldoAtual": 1000.00,
  "ativa": true
}
```

#### `GET /contas` | `GET /contas/{id}` | `DELETE /contas/{id}`
- `GET /contas` e `GET /contas/{id}`: Retornam `ContaResponse` com o `saldoAtual` calculado dinamicamente (`saldoInicial + receitasPagas - despesasPagas`).
- `DELETE /contas/{id}`: Retorna `204 No Content` ou **`409 Conflict`** caso existam lançamentos vinculados (RN06).

---

### 💸 6.5. Módulo de Lançamentos Financeiros (`/lancamentos`)

#### `POST /lancamentos` — Cadastrar Lançamento (`PUT /lancamentos/{id}` utiliza o mesmo Request Body)
- **Autenticação:** `Authorization: Bearer <token>`
- **Valores aceitos em `tipo` (`TipoLancamento`):** `RECEITA`, `DESPESA`
- **Valores aceitos em `status` (`StatusLancamento`):** `PENDENTE`, `PAGO`, `CANCELADO`
- **Request Body (`LancamentoRequest`):**
```json
{
  "descricao": "Conta de Internet Fibra",
  "valor": 150.00,
  "tipo": "DESPESA",
  "data": "2026-11-14",
  "dataVencimento": "2026-11-25",
  "status": "PENDENTE",
  "categoriaId": 2,
  "contaId": 1
}
```
- **Response `201 Created` (`LancamentoResponse`):**
```json
{
  "id": 3,
  "descricao": "Conta de Internet Fibra",
  "valor": 150.00,
  "tipo": "DESPESA",
  "data": "2026-11-14",
  "dataVencimento": "2026-11-25",
  "status": "PENDENTE",
  "categoriaId": 2,
  "categoriaNome": "Mercado",
  "contaId": 1,
  "contaNome": "Conta Principal"
}
```

#### `GET /lancamentos` — Listar com Filtros Dinâmicos Opcionais
- **Query Params Suportados (todos opcionais):**
  `GET /lancamentos?dataInicio=2026-11-01&dataFim=2026-11-30&categoriaId=2&contaId=1&tipo=DESPESA&status=PAGO`
- **Response `200 OK`:** Lista de `LancamentoResponse` ordenada por `data DESC`.

---

### 🎯 6.6. Módulo de Metas Financeiras (`/metas`)

#### `POST /metas` — Criar Meta Financeira (`PUT /metas/{id}` utiliza o mesmo Request Body)
- **Autenticação:** `Authorization: Bearer <token>`
- **Valores aceitos em `status` (`StatusMeta` — opcional no envio):** `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`
- **Request Body (`MetaFinanceiraRequest`):**
```json
{
  "nome": "Reserva de Emergência",
  "valorObjetivo": 5000.00,
  "valorAtual": 2000.00,
  "dataLimite": "2027-12-31"
}
```
- **Response `201 Created` (`MetaFinanceiraResponse`):**
```json
{
  "id": 1,
  "nome": "Reserva de Emergência",
  "valorObjetivo": 5000.00,
  "valorAtual": 2000.00,
  "percentualConcluido": 40.00,
  "dataLimite": "2027-12-31",
  "status": "EM_ANDAMENTO"
}
```
> [!TIP]
> Ao atualizar `valorAtual` para `5000.00` (ou superior) via `PUT /metas/{id}`, o campo `percentualConcluido` passa a ser `100.00` e o `status` transita automaticamente para `"CONCLUIDA"`.

---

### 📊 6.7. Módulo de Dashboard Analítico (`/dashboard`)

#### `GET /dashboard` e `GET /dashboard/mensal`
- **Exemplos de Chamada:**
  - `GET /dashboard` *(assume automaticamente o primeiro e o último dia do mês corrente)*
  - `GET /dashboard?dataInicio=2026-11-01&dataFim=2026-11-30`
  - `GET /dashboard/mensal?ano=2026&mes=11`
- **Response `200 OK` (`DashboardResponse`):**
```json
{
  "dataInicio": "2026-11-01",
  "dataFim": "2026-11-30",
  "saldoTotal": 3200.00,
  "receitasPeriodo": 2500.00,
  "despesasPeriodo": 300.00,
  "resultadoFinanceiro": 2200.00,
  "despesasPendentes": 150.00,
  "despesasPorCategoria": [
    {
      "categoriaId": 2,
      "categoriaNome": "Mercado",
      "total": 300.00
    }
  ],
  "ultimasMovimentacoes": [
    {
      "id": 3,
      "descricao": "Conta de Internet",
      "valor": 150.00,
      "tipo": "DESPESA",
      "data": "2026-11-14",
      "dataVencimento": "2026-11-25",
      "status": "PENDENTE",
      "categoriaId": 2,
      "categoriaNome": "Mercado",
      "contaId": 1,
      "contaNome": "Conta Principal"
    },
    {
      "id": 2,
      "descricao": "Supermercado",
      "valor": 300.00,
      "tipo": "DESPESA",
      "data": "2026-11-12",
      "dataVencimento": null,
      "status": "PAGO",
      "categoriaId": 2,
      "categoriaNome": "Mercado",
      "contaId": 1,
      "contaNome": "Conta Principal"
    },
    {
      "id": 1,
      "descricao": "Salário Recebido",
      "valor": 2500.00,
      "tipo": "RECEITA",
      "data": "2026-11-10",
      "dataVencimento": null,
      "status": "PAGO",
      "categoriaId": 1,
      "categoriaNome": "Salário",
      "contaId": 1,
      "contaNome": "Conta Principal"
    }
  ]
}
```

#### `GET /dashboard/categorias` — Ranking de Despesas por Categoria
- **Exemplo de Chamada:** `GET /dashboard/categorias?dataInicio=2026-11-01&dataFim=2026-11-30`
- **Response `200 OK` (`List<DespesaCategoriaResponse>`):**
```json
[
  {
    "categoriaId": 2,
    "categoriaNome": "Mercado",
    "total": 300.00
  }
]
```

---

## 📖 7. Documentação Interativa com Swagger (OpenAPI 3)

A aplicação conta com documentação interativa gerada dinamicamente pelo **SpringDoc OpenAPI 3** (`OpenApiConfig.java`), já configurada com esquema global de segurança JWT (`bearerAuth`) e metadados de governança LGPD/DPO.

### 🔗 URLs de Acesso (Com o servidor em execução)
- **Interface Visual (Swagger UI):** `http://localhost:8080/swagger-ui/index.html`
- **Especificação OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

### 🔑 Passo a Passo para Autenticar e Testar no Swagger UI
1. Acesse `http://localhost:8080/swagger-ui/index.html` no navegador.
2. Expanda o `auth-controller` e execute `POST /auth/register` para criar seu usuário (lembre-se de enviar `"aceitouTermos": true`).
3. Em seguida, execute `POST /auth/login` informando seu `email` e `senha`.
4. Copie o valor do campo `"token"` retornado no JSON de resposta (apenas o código hash JWT, **sem** a palavra `Bearer`).
5. No topo direito da página do Swagger, clique no botão **Authorize** (ícone de cadeado 🔒).
6. No campo **Value** do esquema `bearerAuth (http, Bearer)`, cole o token JWT copiado e clique em **Authorize** $\rightarrow$ **Close**.
7. Pronto! O Swagger anexará automaticamente o cabeçalho `Authorization: Bearer <seu_token>` em todas as suas requisições para `/usuarios`, `/categorias`, `/contas`, `/lancamentos`, `/metas` e `/dashboard`.

---

## 🧪 8. Suíte de Testes Automatizados End-to-End (`FinanceiroApplicationTests`)

A confiabilidade da API é assegurada pela classe de teste de integração `FinanceiroApplicationTests`, construída com **`@SpringBootTest`**, **`MockMvc`** acoplado ao filtro real **`JwtAuthenticationFilter`** e anotada com **`@Transactional`** (garantindo *rollback* automático no banco MySQL ao término da execução para manter o ambiente sempre limpo e idempotente).

O teste `deveTestarFluxoCompletoDoSistema()` valida sequencialmente **26 passos críticos de negócio, LGPD e cibersegurança OWASP**:

| Passo | Categoria | Endpoint Acionado | Cenário Validado | Status Esperado |
| :---: | :--- | :--- | :--- | :---: |
| **0.1** | 🛡️ LGPD (Art. 9º e 41) | `GET /auth/termos-lgpd` | Acesso público aos Termos de Uso (`versaoTermos = "1.0"`) e presença do contato do DPO. | `200 OK` |
| **0.2** | 🛡️ LGPD (Art. 8º, § 4º) | `POST /auth/register` | Bloqueio de cadastro quando o campo `aceitouTermos` é omitido (proibição de *opt-in* tácito). | `400 Bad Request` |
| **1** | 🔐 Autenticação / LGPD | `POST /auth/register` | Cadastro de usuário com senha forte e consentimento explícito (`"aceitouTermos": true`). | `201 Created` |
| **2** | 🔐 Autenticação JWT | `POST /auth/login` | Login com credenciais válidas e extração do token JWT assinado. | `200 OK` |
| **3** | 🏷️ Categorias | `POST /categorias` | Criação de duas categorias: `"Salário"` (`RECEITA`) e `"Mercado"` (`DESPESA`). | `201 Created` |
| **4** | 💳 Contas | `POST /contas` | Criação da `"Conta Principal"` (`CONTA_DIGITAL`) com `saldoInicial` e `saldoAtual` de `R$ 1000.00`. | `201 Created` |
| **5** | 💸 Lançamentos | `POST /lancamentos` | Registro de receita `PAGO` no valor de `R$ 2500.00` vinculada à categoria `"Salário"`. | `201 Created` |
| **6** | 💸 Lançamentos | `POST /lancamentos` | Registro de despesa `PAGO` no valor de `R$ 300.00` vinculada à categoria `"Mercado"`. | `201 Created` |
| **6.1** | 💸 Lançamentos | `POST /lancamentos` | Registro de despesa `PENDENTE` de `R$ 150.00` com `dataVencimento` futura (não deve alterar saldo atual). | `201 Created` |
| **7** | ⚙️ Regra RN03 | `POST /lancamentos` | Bloqueio ao tentar registrar uma `DESPESA` utilizando uma categoria do tipo `RECEITA`. | `400 Bad Request` |
| **8** | 🧮 Cálculo de Saldo | `GET /contas/{id}` | Conferência matemática exata do `saldoAtual`: `1000 + 2500 - 300 = R$ 3200.00` (ignorando os `150` pendentes). | `200 OK` |
| **9** | ⚙️ Regra RN06 | `DELETE /contas/{id}` | Bloqueio de integridade referencial ao tentar excluir conta que possui lançamentos vinculados. | `409 Conflict` |
| **10** | 📊 Dashboard Geral | `GET /dashboard` | Validação de `saldoTotal` (`3200.00`), `receitasPeriodo` (`2500.00`), `despesasPeriodo` (`300.00`), `resultadoFinanceiro` (`2200.00`), `despesasPendentes` (`150.00`), categoria `"Mercado"` e Top 3 movimentações. | `200 OK` |
| **11** | 📊 Dashboard Categorias | `GET /dashboard/categorias` | Conferência do agrupamento JPQL de despesas pagas por categoria (`"Mercado": 300.00`). | `200 OK` |
| **12** | 📊 Dashboard Mensal | `GET /dashboard/mensal` | Consulta parametrizada por `?ano=2026&mes=11` confirmando consistência dos saldos e resultados. | `200 OK` |
| **13** | 🎯 Metas Financeiras | `POST /metas` | Criação de meta (`objetivo: 5000.00`, `atual: 2000.00`), validando `percentualConcluido = 40.00` e status `EM_ANDAMENTO`. | `201 Created` |
| **14** | 🎯 Metas Financeiras | `GET /metas` & `GET /metas/{id}` | Listagem ordenada de metas do titular e busca individual por ID. | `200 OK` |
| **15** | 🎯 Conclusão Automática | `PUT /metas/{id}` | Atualização de `valorAtual` para `5000.00`, validando `percentualConcluido = 100.00` e promoção automática para `CONCLUIDA`. | `200 OK` |
| **16** | ⚙️ Validação de Metas | `POST /metas` | Bloqueio de cadastro de meta com `valorObjetivo` igual a `0.00` (`@DecimalMin("0.01")`). | `400 Bad Request` |
| **17** | 🎯 Exclusão de Meta | `DELETE /metas/{id}` & `GET /metas/{id}` | Exclusão de meta (`204 No Content`) seguida de confirmação de inexistência (`404 Not Found`). | `204` / `404` |
| **18** | 🛡️ LGPD (Art. 18, I-III) | `GET /usuarios/me` & `PUT /usuarios/me` | Consulta e atualização de perfil verificando timestamps LGPD e garantindo que `$.senha` **não existe** no JSON. | `200 OK` |
| **19** | 🛡️ LGPD (Art. 8º) | `POST /auth/register` | Bloqueio de cadastro quando o usuário recusa explicitamente os termos (`"aceitouTermos": false`). | `400 Bad Request` |
| **20** | 🎯 Preparação Cascata | `POST /metas` | Criação de nova meta ativa (`"Viagem de Férias"`) para popular simultaneamente todas as 4 tabelas filhas. | `201 Created` |
| **21** | 🛡️ LGPD (Portabilidade) | `GET /usuarios/me/exportar` | Exportação completa validando dados do titular e contagem exata: 2 categorias, 1 conta, 3 lançamentos e 1 meta. | `200 OK` |
| **22** | 🛡️ LGPD (Esquecimento) | `DELETE /usuarios/me` | Exclusão física em cascata das 4 tabelas filhas e do usuário (`flush` + limpeza do `SecurityContext`). | `204 No Content` |
| **22.1** | 🔒 Revogação Imediata | `GET /usuarios/me` | Tentativa de usar o token JWT antigo imediatamente após a exclusão da conta. | `401 Unauthorized` |
| **22.2** | 🔒 Confirmação no Banco | `POST /auth/login` | Tentativa de login com as credenciais da conta eliminada. | `401 Unauthorized` |
| **23** | 🔒 Anti-Força Bruta | `POST /auth/login` | Disparo de 5 logins inválidos (`401`) seguidos do bloqueio imediato na **6ª tentativa** por Rate Limiting. | `429 Too Many Requests` |
| **24** | 🔒 Blindagem JSON | `POST /auth/login` | Envio de payload JSON corrompido (`"{ json_quebrado_ataque: "`) retornando erro limpo sem vazar classes internas. | `400 Bad Request` |
| **25** | 🔒 Senha Forte (OWASP) | `POST /auth/register` | Bloqueio de cadastro com senha contendo apenas letras (`"apenasletras"`), exigindo combinação alfanumérica. | `400 Bad Request` |
| **26** | 📖 Swagger / OpenAPI | `GET /v3/api-docs` & `/swagger-ui/index.html` | Verificação de disponibilidade da especificação JSON e da interface gráfica do Swagger. | `200 OK` |

---

## 🚀 9. Guia de Instalação, Configuração (`.env`) e Execução Local

### 9.1. Pré-requisitos
- **Java JDK 26** instalado e configurado no `PATH` (`java -version`).
- **MySQL Server 8.0+** em execução local ou em container Docker.
- **Maven:** Não é necessário instalar manualmente, pois o projeto já inclui o **Maven Wrapper** (`mvnw` e `mvnw.cmd`).

### 9.2. Criação do Banco de Dados no MySQL
Conecte-se ao seu servidor MySQL e execute o comando abaixo para criar o schema (o Hibernate criará e atualizará automaticamente todas as tabelas e constraints via `spring.jpa.hibernate.ddl-auto=update`):

```sql
CREATE DATABASE IF NOT EXISTS controle_financeiro
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 9.3. Configuração das Variáveis de Ambiente (`.env`)
O arquivo `src/main/resources/application.properties` importa automaticamente variáveis de um arquivo `.env` localizado na raiz do projeto (`spring.config.import=optional:file:.env[.properties]`).

Crie um arquivo chamado **`.env`** na raiz do diretório `Financeiro/` com a seguinte estrutura:

```properties
# Configuração de Conexão com o Banco de Dados MySQL 8.0
DB_URL=jdbc:mysql://localhost:3306/controle_financeiro
DB_USERNAME=seu_usuario_mysql
DB_PASSWORD=sua_senha_mysql

# Chave Secreta JWT (Deve estar codificada em Base64 e possuir no mínimo 256 bits / 32 bytes)
JWT_SECRET=sua_chave_secreta_em_base64_com_no_minimo_256_bits

# LGPD Art. 46: Oculta queries SQL nos logs por padrão (Altere para true apenas em debug local)
SHOW_SQL=false
```

> [!WARNING]
> **Segurança de Credenciais:** Nunca versione o arquivo `.env` com senhas reais ou chaves de produção em repositórios públicos. Para gerar uma chave `JWT_SECRET` segura em Base64 (256 bits), você pode executar no terminal:
> - **Linux/macOS:** `openssl rand -base64 32`
> - **PowerShell (Windows):** `[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))`

### 9.4. Executando a Suíte de Testes Automatizados
Para validar os 26 passos da suíte de testes *End-to-End* (`FinanceiroApplicationTests`):

**No Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd clean test
```

**No Linux / macOS / Git Bash:**
```bash
./mvnw clean test
```

### 9.5. Inicializando o Servidor da API
Para compilar e subir a aplicação na porta `8080`:

**No Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**No Linux / macOS / Git Bash:**
```bash
./mvnw spring-boot:run
```

Assim que o servidor iniciar:
- **Base URL da API:** `http://localhost:8080`
- **Documentação Interativa (Swagger UI):** `http://localhost:8080/swagger-ui/index.html`
- **Termos de Privacidade e Contato DPO (LGPD):** `http://localhost:8080/auth/termos-lgpd`

---

<div align="center">
  <sub>Desenvolvido com excelência em Engenharia de Software, Segurança Defensiva (OWASP) e Privacidade por Design (LGPD).</sub>
</div>
