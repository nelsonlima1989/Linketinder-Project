# AGENTS.md — Linketinder

Instruções para agentes de IA e colaboradores. Os fatos aqui foram verificados nos arquivos do repositório (commit base `cff86b9`). Quando algo não foi verificado, está marcado como **não verificado**.

---

## 1. Visão geral

O Linketinder é um sistema de contratação inspirado em LinkedIn + Tinder, desenvolvido como MVP educacional (Acelera ZG / ZG-HERO 2026). Aproxima **candidatos** e **empresas** por **competências (skills)**, com **anonimato antes do match**.

**Domínio:** candidatos, empresas, vagas (pertencem a uma empresa), competências (skills), curtidas e matches.

### Estado real de implementação

| Funcionalidade | Backend (Groovy/PostgreSQL) | Frontend (TS/localStorage) |
|---|---|---|
| Candidatos, empresas, vagas, skills | Implementado (CRUD via DAOs) | Implementado |
| API HTTP de cadastro | Implementada (somente `POST`) | Não consome |
| Anonimato | **Parcial** (ver seção 8) | Parcial (cards sem dados identificadores) |
| Curtidas | **Parcial**: tabelas SQL existem; `CurtidaService` guarda em memória, sem DAO | Não implementado |
| Matches | **Parcial**: `MatchService` em memória; sem tabela, só consulta derivada em `database/queries.sql` | Não implementado |
| Recrutador | **Inexistente** em todas as camadas | Inexistente |

O fluxo de curtida/match do backend apresenta **possíveis defeitos** identificados por leitura estática (comparação de objetos por identidade, uso de `candidato.idade`, que não existe no model). Ainda não foram corrigidos nem confirmados por execução.

### Feature futura: Recrutador

Colaborador associado a uma empresa (Empresa 1 → N Recrutadores), com nome, área de recrutamento, descrição, e-mail, CPF, data de nascimento e senha. Será implementada **nas duas camadas, de forma independente**. **Ainda não há especificação aprovada.** Regras de negócio não documentadas não devem ser presumidas (ver seção 10).

---

## 2. Stack

| Item | Versão / ferramenta | Fonte |
|---|---|---|
| Groovy | 5.0.8 (`groovy`, `groovy-json`) | `build.gradle` |
| Gradle Wrapper | 9.6.0 | `gradle/wrapper/gradle-wrapper.properties` |
| Plugins Gradle | `groovy`, `jacoco`, `application` | `build.gradle` |
| Driver JDBC | `org.postgresql:postgresql:42.7.13` | `build.gradle` |
| Testes backend | Spock 2.4-groovy-5.0 (BOM), JUnit Platform, byte-buddy 1.15.11, objenesis 3.4 | `build.gradle` |
| Cobertura | JaCoCo (relatórios HTML e XML) | `build.gradle` |
| Banco | PostgreSQL (versão **não verificada**) | `database/database.sql` |
| Frontend | TypeScript ~6.0.2, Vite ^8.3.0, Vitest ^5.0.5, Chart.js ^4.5.1 | `frontend/package.json` |
| tsconfig | `target es2023`, `noUnusedLocals`, `noUnusedParameters`, `erasableSyntaxOnly` | `frontend/tsconfig.json` |
| Ambiente local observado | Java 26.0.2, Node v24.18.1 (versão mínima exigida **não verificada**) | `java -version`, `node -v` |

---

## 3. Estrutura

```text
.
├── build.gradle / settings.gradle / gradlew      # build do backend
├── database/
│   ├── database.sql        # DDL (8 tabelas)
│   └── queries.sql         # seeds (INSERTs) + consultas de teste
├── docs/der-linketinder.png                      # DER (não reflete a coluna `estado`)
├── src/main/groovy/com/linketinder/
│   ├── Main.groovy         # menu de terminal (script; monta as dependências)
│   ├── api/                # HttpServer do JDK (ApiServer, *HttpHandler, HttpRequestUtil, HttpResponseUtil)
│   ├── controller/         # Candidato, Empresa, Curtida, Match
│   ├── dao/                # 6 DAOs JDBC
│   ├── data/               # DataInicializador (código morto, não referenciado)
│   ├── database/           # ConexaoProvider, ConexaoFactory, ConexaoPostgresProvider, ConexaoDB
│   ├── model/              # Pessoa, Candidato, Empresa, Vaga, Skill, Informacoes, *Anonim*, Curtida, TipoCurtida, Match
│   ├── repository/         # interfaces dos DAOs
│   ├── service/            # Candidato, Empresa, Vaga, Curtida, Match
│   └── view/Menu.groovy    # interface de terminal
├── src/test/groovy/        # apiTest, controllerTest, daoTest, databaseTest, serviceTest, uiTest (Spock)
├── frontend/
│   ├── package.json / tsconfig.json / index.html
│   └── src/ {components, controllers, data, models, services, styles, main.ts} (+ *.test.ts ao lado do código)
├── .env.example            # variáveis do banco (placeholders)
└── README.md               # documentação principal (extensa)
```

Artefatos SDD: `AGENTS.md`, `CHANGELOG.md`, `PROMPTS.md`, `skills/feature-com-spec/SKILL.md`. Especificações futuras ficam em `docs/specs/<feature>/` (ainda não existe).

---

## 4. Arquitetura

### Backend

```text
View/Controller → Service → Repository (interface) ← DAO → ConexaoProvider → PostgreSQL
```

- **Model:** POGOs Groovy (sem validação, sem `equals`/`hashCode`).
- **Repository:** interfaces com `inserir`, `listarTodos`, `buscarPorId`, `atualizar`, `excluir`.
- **DAO:** JDBC com `PreparedStatement` e try-with-resources; `SQLException` é convertida em `IllegalStateException`. Sem transações.
- **Service:** regras de negócio. Hoje só `VagaService` valida entrada (empresa existente e `empresaId` positivo).
- **Controller:** delegam aos services.
- **View:** `Menu` (terminal).
- **Injeção de dependências manual** em `Main.groovy` e `api/ApiServer.groovy` (duplicadas).
- **Banco:** `ConexaoFactory.criarProvider("postgresql")`; `ConexaoDB` lê `DB_URL`, `DB_USER`, `DB_PASSWORD` via `System.getenv()`.

### API HTTP existente

`api/ApiServer.groovy` usa `com.sun.net.httpserver.HttpServer` em `127.0.0.1:8080`:

- `GET /api/health`
- `POST /api/candidatos`, `POST /api/empresas`, `POST /api/vagas`

Valida `Content-Type: application/json`, corpo de até 16 KiB e campos string obrigatórios. Não há autenticação, rotas `GET` por recurso, `PUT` nem `DELETE`. Violações de UNIQUE do banco resultam em HTTP 500.

### Frontend

`main.ts` → `controllers` → `services` → `storage.ts` (`localStorage`); `components` geram HTML por template strings (com `escaparHtml`). Chaves: `candidatos`, `empresas`, `vagas` e bloqueios `candidato-em-edicao-{id}`, `empresa-em-edicao-{id}`, `vaga-em-edicao-{id}`. O usuário atual é fixo em `main.ts` (`empresaAtualId = 3`, `candidatoAtualId = 3`); não há login.

### Separação frontend/backend

**O frontend NÃO consome a API HTTP e NÃO se comunica com o backend.** Cada camada tem persistência, IDs, seeds e models próprios e divergentes (ex.: o candidato do frontend tem `idade` e `formacao`, e não tem e-mail, senha nem data de nascimento). Não existe integração nem sincronização, e **não está prevista** para este desafio. Não afirme nem implemente integração sem decisão explícita.

---

## 5. Comandos

**Legenda:** *Encontrado* = consta em `build.gradle`, `package.json` ou `README.md`. *Executado* = rodado nesta preparação. **Nenhum comando abaixo foi executado nesta etapa** (apenas leitura de arquivos).

| Objetivo | Comando | Origem |
|---|---|---|
| Backend (menu) | `./gradlew run --console=plain` | Encontrado (README; `application.mainClass = com.linketinder.Main`) |
| API HTTP | `./gradlew runApi --console=plain` | Encontrado (`build.gradle`, tarefa `runApi`) |
| Testes Spock (todos) | `./gradlew test --console=plain` | Encontrado (`test { useJUnitPlatform() }`) |
| Testes Spock (filtro) | `./gradlew test --tests 'apiTest.*' --console=plain` | Encontrado (README) |
| Testes + cobertura | `./gradlew clean test jacocoTestReport --console=plain` | Encontrado (README) |
| Relatórios | `build/reports/tests/test/index.html`, `build/reports/jacoco/test/html/index.html` | Encontrado (README) |
| Frontend: dependências | `cd frontend && npm install` | Encontrado (README) |
| Frontend: dev | `cd frontend && npm run dev` | Encontrado (`package.json`) |
| Frontend: tipos | `cd frontend && npx tsc --noEmit` | Encontrado (README) |
| Frontend: build | `cd frontend && npm run build` (`tsc && vite build`) | Encontrado (`package.json`) |
| Testes Vitest | `cd frontend && npm test` (`vitest run`) | Encontrado (`package.json`) |
| Vitest em modo watch | `cd frontend && npm run test:watch` | Encontrado (`package.json`) |

**Variáveis do banco:** o `.env` **não é carregado automaticamente**. Em Linux/macOS: `set -a; source .env; set +a` antes de `run`/`runApi`. Os testes Spock usam mocks e **não precisam do PostgreSQL** (README).

Não existem scripts de lint nem de migração.

---

## 6. Convenções

- Respeitar os padrões existentes (Model → Repository → DAO → Service → Controller → View; no frontend, components/controllers/services/models). **Não propor arquitetura nova sem demonstrar a necessidade.**
- Em Groovy, usar **tipos explícitos** em declarações e assinaturas (como nos DAOs e services atuais); evitar `def` quando o tipo é conhecido.
- Preservar interfaces e contratos existentes (assinaturas de repositories, services, endpoints, chaves do `localStorage`).
- Evitar dependências novas sem justificativa.
- **Consultas SQL sempre parametrizadas** (`PreparedStatement`); nunca concatenar entrada do usuário.
- Toda regra nova exige testes automatizados (Spock no backend, Vitest no frontend).
- Alterações pequenas e revisáveis. Não modificar funcionalidades fora do escopo sem autorização.
- Idioma do domínio e dos identificadores: **português** (ex.: `cadastrarVaga`, `Candidato`). Mensagens ao usuário em português.
- **Commits:** Conventional Commits em português, sem acentos nos prefixos, como no histórico: `feat:`, `fix:`, `refactor:`, `docs:`, `feat(database):`. Um commit por tarefa.
- Não fazer commit, push, branch, issue ou PR sem autorização explícita do responsável.

---

## 7. Restrições de segurança

**Proibido** ao implementar ou revisar código:

- Registrar senhas em texto puro (código, logs, testes, documentação, seeds reais).
- Expor CPF, e-mail, data de nascimento ou outros dados pessoais em logs, mensagens de erro ou stack traces.
- Retornar `senha` em DTOs de apresentação, respostas HTTP ou listagens.
- Armazenar senhas reais no `localStorage`.
- Versionar credenciais (`.env` é ignorado pelo Git; manter assim; usar apenas dados fictícios).
- Executar scripts destrutivos (DROP, TRUNCATE, DELETE, ALTER, migrações) sem autorização explícita.
- Introduzir **novas** exposições de dados pessoais.

### Riscos legados conhecidos (NÃO corrigidos)

Registrados pela auditoria da Etapa 01; **não os corrija sem tarefa e autorização próprias**, mas não os replique:

- Senhas em texto puro no banco e nos models; os DAOs devolvem `senha` em todas as consultas.
- Sem autenticação nem autorização; o menu permite escolher qualquer candidato ou empresa; o frontend usa IDs fixos.
- `CurtidaService` imprime CPF, e-mail, WhatsApp e CNPJ no stdout ao ocorrer match.
- `CandidatoHttpHandler` e `EmpresaHttpHandler` imprimem stack traces no stderr.
- CPF e CNPJ atualmente armazenados em texto puro no `localStorage` (presentes no seed do frontend). Os modelos atuais de `Candidato` e `Empresa` no frontend ainda não possuem campo `senha`, mas há risco futuro de armazenamento indevido de senha no `localStorage` na implementação de `Recrutador`.
- Sem validação de CPF, CNPJ ou e-mail; duplicidade vira HTTP 500.
- Sem transações em cadastros que envolvem várias tabelas.

---

## 8. Princípio do Anonimato

> **O Princípio do Anonimato é uma restrição obrigatória e inegociável do Linketinder.**

### Regras existentes e verificadas (README, "Listagem anônima")

- **Candidato, antes do match:** visíveis ID, estado, descrição pessoal e skills. **Ocultos:** nome, CPF, idade, CEP, e-mail e WhatsApp.
- **Empresa/vaga, antes do match:** visíveis ID, título, descrição, país e estado da empresa e skills. **Ocultos:** nome e dados de identificação e contato da empresa.
- **O que é liberado após o match: NÃO ESTÁ DEFINIDO** (o README apenas cita "os dados previstos pela regra de negócio").

### Implementação atual (parcial)

- Backend: `CandidatoAnonimo` e `EmpresaAnonima`, gerados em `CandidatoService.listarCandidatosAnonimos()` e `EmpresaService.listarEmpresasAnonimas()`.
- **Lacuna:** `EmpresaAnonima.vagas` guarda objetos `Vaga`, e cada `Vaga.empresa` referencia a `Empresa` completa (nome, CNPJ, e-mail, senha).
- **Violação existente:** `Menu.exibirVagasParaSelecao` mostra o nome da empresa ao candidato na opção "Curtir vaga".
- `listarTodosCandidatos` e `listarTodasEmpresas` devolvem entidades completas; os filtros existem só na apresentação, não há controle de acesso.
- Frontend: `renderizarCardsCandidatos` e `renderizarCardsVagas` não exibem dados identificadores. O rótulo `#1001+índice` do candidato não é estável.

### Obrigações para toda nova funcionalidade

1. Preservar a identidade dos participantes antes da condição de liberação definida pelo domínio.
2. Usar representações anônimas (DTOs específicos) para apresentação.
3. **Não referenciar entidades completas** em objetos de apresentação (nem indiretamente, como `Vaga.empresa`).
4. Impedir exposição acidental por interfaces, respostas HTTP, serialização JSON e logs.
5. Incluir **testes de anonimato** (ex.: assegurar que o DTO não contém campos identificadores e que a saída renderizada/serializada não contém nome, e-mail, CPF nem nascimento).
6. **Não presumir** o que é liberado após o match.

Quando a regra não estiver definida (inclusive o que o recrutador, o candidato e a empresa podem ver uns dos outros), **exija esclarecimento antes de implementar**.

---

## 9. Scripts de banco

| Arquivo | Conteúdo |
|---|---|
| `database/database.sql` | **Somente DDL** (`CREATE TABLE` de `candidatos`, `empresas`, `competencias`, `vagas`, `candidato_competencias`, `vaga_competencias`, `curtidas_candidato_vaga`, `curtidas_empresa_candidato`). Não é idempotente (sem `IF NOT EXISTS`) |
| `database/queries.sql` | `INSERT`s de dados de exemplo + consultas `SELECT` de teste (inclui consulta derivada de matches). Não é idempotente: reexecutar viola UNIQUE |

Uso documentado no README: `psql -h localhost -U <usuario> -d linketinder -f database/database.sql` e depois `-f database/queries.sql`.

> O README afirma que o `database.sql` "insere os dados iniciais", mas os `INSERT`s estão em `queries.sql`.

**Cuidados:**
- Não há migrações nem versionamento de esquema. Mudanças de esquema devem ser propostas como script incremental e aprovadas na especificação.
- **Não execute os scripts** (nem qualquer DDL/DML) sem autorização explícita; só consultas de leitura quando autorizadas.
- O esquema **efetivamente existente no PostgreSQL não foi verificado** diretamente, só pelos scripts.
- Nenhuma FK define `ON DELETE`, e `vagas.empresa_id` não tem índice.
- Não registrar credenciais nem dados pessoais reais em documentos.

---

## 10. Fluxo SDD (Spec-Driven Development)

Toda feature segue:

**Levantamento → Perguntas → Especificação → Aprovação → Plano → Implementação incremental → Testes → Revisão → Commit → PR**

Procedimento detalhado: [`skills/feature-com-spec/SKILL.md`](skills/feature-com-spec/SKILL.md).

Regras:

1. **Nenhum código antes da especificação aprovada e commitada** (`docs/specs/<feature>/spec.md`).
2. Plano em `docs/specs/<feature>/plano.md` com tarefas pequenas e numeradas.
3. **Uma tarefa por vez**; cada tarefa atualiza o `CHANGELOG.md` e termina em **um commit próprio**, após revisão e autorização.
4. Lacuna na especificação durante a implementação: **parar o código** e atualizar a especificação.
5. Decisões não confirmadas são **pendentes**, nunca requisitos.
6. Prompts relevantes são registrados em `PROMPTS.md`, sem transcrições inventadas.
7. Curtidas/matches são **parciais** e seu escopo no Recrutador **depende de decisão**; não os trate como dependência obrigatória nem corrija defeitos legados sem tarefa autorizada.
