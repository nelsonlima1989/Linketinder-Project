# Plano de Implementação — Integração de Curtidas e Matches no Backend Groovy (PostgreSQL + REST)

- **Feature:** `match-backend`
- **Issue vinculada:** [#5 — [FEATURE] Integrar Curtidas e Matches do backend Groovy com PostgreSQL e REST](https://github.com/nelsonlima1989/Linketinder-Project/issues/5)
- **Especificação de referência:** [`docs/specs/match-backend/spec.md`](spec.md)
- **Branch:** `feature/match-backend`
- **Status geral do plano:** Em planejamento (aguardando aprovação para início da execução da T-01)

---

## 1. Diretrizes de Execução Incremental (Etapa E da Skill SDD)

1. **Uma tarefa por vez:** Nenhuma tarefa subsequente pode ser iniciada antes que a tarefa atual tenha sido completamente implementada, validada com testes, revisada, registrada no `CHANGELOG.md` e commitada com autorização humana explícita.
2. **Escopo restrito:** Não alterar arquivos do frontend TypeScript (`frontend/**`), a lógica do menu CLI (`Menu.groovy`) nem implementar sistemas de autenticação nesta feature.
3. **Ciclo obrigatório ao final de cada tarefa (`T-01` a `T-06`):**
   - Apresentar as alterações realizadas;
   - Revisar `git status`, `git diff` e `git diff --check`;
   - Executar os testes automatizados aplicáveis (Spock e/ou PostgreSQL);
   - Validar a **Privacidade e Anonimato** (ausência de contatos pessoais e credenciais em endpoints públicos);
   - Atualizar o `CHANGELOG.md`;
   - Atualizar o status da tarefa neste `plano.md`;
   - Solicitar revisão e autorização humana antes do commit;
   - Criar **um commit próprio da tarefa** seguindo Conventional Commits em português sem acentos no prefixo.
4. **Tratamento de lacunas:** Caso seja identificada qualquer dúvida de negócio ou regra técnica não contemplada na `spec.md`, interromper imediatamente a codificação e atualizar a especificação.

---

## 2. Visão Geral das Tarefas

| ID | Tarefa | Camada | Dependências | Status | Commit Esperado |
|---|---|---|---|---|---|
| **T-01** | Aprovação e formalização dos artefatos SDD (`spec.md` e `plano.md`) | Documentação / SDD | Nenhuma | Concluída | `docs(specs): adiciona especificacao e plano de integracao de matches no backend` |
| **T-02** | Criação de DTOs, Repositories e DAOs JDBC com suporte transacional (`CurtidaDAO`, `MatchDAO`) | Persistência (Groovy/JDBC) | T-01 | Concluída | `feat(backend): implementa DAOs JDBC de curtidas e matches com suporte transacional` |
| **T-03** | Refatoração de `CurtidaService` e `MatchService` com transação JDBC atômica e testes de rollback | Serviço (Groovy) | T-02 | Concluída | `feat(backend): refatora servicos de curtida e match para persistencia real e atomica` |
| **T-04** | Criação dos Handlers REST com privacidade estrita (`/api/curtidas/*`, `/api/matches`) em `ApiServer` | API HTTP (Groovy) | T-03 | Concluída | `feat(api): adiciona rotas REST de curtidas e matches com privacidade estrita` |
| **T-05** | Testes de integração ponta a ponta com PostgreSQL descartável (cenários reais, rollback e privacidade) | Testes (Spock/PostgreSQL) | T-04 | Planejada | `test(backend): adiciona testes integrados de curtidas e matches com PostgreSQL` |
| **T-06** | Atualização da documentação no `README.md`, `CHANGELOG.md` e auditoria final | Documentação / Qualidade | T-05 | Planejada | `docs: documenta endpoints de curtidas e matches no README e changelog` |

---

## 3. Detalhamento das Tarefas

---

### Tarefa T-01 — Aprovação e Formalização dos Artefatos SDD (`spec.md` e `plano.md`)

- **Status:** Concluída
- **Objetivo:** Submeter a especificação técnica e o plano de implementação para revisão do responsável, dirimindo decisões pendentes e commitando a base documental da feature antes de qualquer linha de código de produção.
- **Camada afetada:** Documentação (`docs/specs/match-backend/`).
- **Arquivos previstos:**
  - `docs/specs/match-backend/spec.md` (criado)
  - `docs/specs/match-backend/plano.md` (criado)
- **Dependências:** Validação técnica das decisões DP-01 a DP-05 com o usuário.
- **Critérios de conclusão e validação:**
  - `spec.md` e `plano.md` alinhados e aprovados.
  - `git diff --check` sem avisos de espaço em branco.
- **Commit esperado:** `docs(specs): adiciona especificacao e plano de integracao de matches no backend`

---

### Tarefa T-02 — Criação de DTOs, Repositories e DAOs JDBC com Suporte Transacional (`CurtidaDAO`, `MatchDAO`)

- **Status:** Concluída
- **Objetivo:** Implementar os contratos de persistência e DAOs JDBC parametrizados para operar sobre as tabelas `curtidas_candidato_vaga`, `curtidas_empresa_candidato` e `matches`:
  - `CurtidaRepository` e `CurtidaDAO`: métodos para inserir curtida de candidato em vaga, inserir curtida de empresa em candidato (com `recrutador_id`), aceitando opcionalmente uma `Connection` existente para permitir transações atômicas coordenadas pelo serviço.
  - `MatchRepository` e `MatchDAO`: métodos para buscar match por `(empresa_id, candidato_id)` (também aceitando `Connection` opcional) e listar matches com filtros opcionais (`candidato_id`, `empresa_id`), mapeando dados das empresas, candidatos, competências e recrutadores associados sob anonimato estrito (sem nomes e sem contatos pessoais).
  - Criação de DTOs seguros de transporte que omitam nomes, dados de contato pessoal e credenciais.
- **Camada afetada:** Persistência (`src/main/groovy/com/linketinder/repository/`, `dao/`, `model/`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/repository/CurtidaRepository.groovy` (criado)
  - `src/main/groovy/com/linketinder/repository/MatchRepository.groovy` (criado)
  - `src/main/groovy/com/linketinder/dao/CurtidaDAO.groovy` (criado)
  - `src/main/groovy/com/linketinder/dao/MatchDAO.groovy` (criado)
  - `src/test/groovy/daoTest/CurtidaDAOSpec.groovy` (criado)
  - `src/test/groovy/daoTest/MatchDAOSpec.groovy` (criado)
  - `CHANGELOG.md` e `plano.md` (atualizados)
- **Requisitos e Regras Atendidos:** `RF-01`, `RF-02`, `RF-03`, `RNF-02`, `RNF-03`, `CA-01`, `CA-02`, `CA-07`, `CA-09`.
- **Testes necessários:** Testes unitários com Spock utilizando mocks para `Connection`, `PreparedStatement` e `ResultSet`.
- **Commit esperado:** `feat(backend): implementa DAOs JDBC de curtidas e matches com suporte transacional`

---

### Tarefa T-03 — Refatoração de `CurtidaService` e `MatchService` com Transação JDBC Atômica e Testes de Rollback

- **Status:** Concluída
- **Objetivo:** Refatorar a camada de serviço:
  - Eliminar as coleções voláteis em memória RAM (`curtidas = []` e `matches = []`).
  - Implementar transação JDBC atômica: obter conexão única com `autoCommit = false`, executar o `INSERT` da curtida e, na mesma transação, consultar se o trigger PostgreSQL gerou o match, finalizando com `commit()`.
  - Tratamento de rollback: se qualquer etapa falhar (erro no INSERT, rejeição pelo trigger, erro na leitura do match ou falha de conexão), efetuar `rollback()` imediato.
  - Integrar `MatchService` com `MatchDAO` para listagem e busca por filtros.
  - Limpar defeitos legados: remover prints com dados pessoais no stdout e eliminar a leitura da propriedade inexistente `candidato.idade`.
- **Camada afetada:** Serviços (`src/main/groovy/com/linketinder/service/`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/service/CurtidaService.groovy` (modificado)
  - `src/main/groovy/com/linketinder/service/MatchService.groovy` (modificado)
  - `src/test/groovy/serviceTest/CurtidaServiceSpec.groovy` (criado)
  - `src/test/groovy/serviceTest/MatchServiceSpec.groovy` (criado)
  - `CHANGELOG.md` e `plano.md` (atualizados)
- **Requisitos e Regras Atendidos:** `RF-01`, `RF-02`, `RF-03`, `RF-07`, `RF-08`, `RNF-01`, `RNF-03`, `CA-03`, `CA-04`, `CA-05`, `CA-06`, `CA-11`, `CA-12`.
- **Testes necessários:** Testes unitários com Spock cobrindo cenários de sucesso, match recíproco, validações de recrutador, duplicidade, rollback em falhas e simulação de erros SQL.
- **Commit esperado:** `feat(backend): refatora servicos de curtida e match para persistencia real e atomica`

---

### Tarefa T-04 — Criação dos Handlers REST com Privacidade Estrita (`/api/curtidas/*`, `/api/matches`) em `ApiServer`

- **Status:** Concluída
- **Objetivo:** Criar os manipuladores HTTP REST no servidor nativo do JDK (`HttpServer`):
  - `CurtidaHttpHandler`: gerencia `POST /api/curtidas/candidato-vaga` e `POST /api/curtidas/empresa-candidato`, validando cabeçalhos `Content-Type`, parse JSON, campos obrigatórios, IDs válidos e mapeando códigos HTTP (201, 400, 404, 409, 500).
  - `MatchHttpHandler`: gerencia `GET /api/matches` com extração segura de parâmetros de busca (`candidatoId`, `empresaId`), retornando lista em JSON sob estrita política de anonimato (sem nomes, sem contatos pessoais como e-mail e WhatsApp, sem CPF, sem CNPJ e sem senhas).
  - Registrar os contextos no `ApiServer.groovy`.
- **Camada afetada:** API HTTP (`src/main/groovy/com/linketinder/api/`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/api/CurtidaHttpHandler.groovy` (criado)
  - `src/main/groovy/com/linketinder/api/MatchHttpHandler.groovy` (criado)
  - `src/main/groovy/com/linketinder/api/ApiServer.groovy` (modificado)
  - `src/test/groovy/apiTest/CurtidaHttpHandlerSpec.groovy` (criado)
  - `src/test/groovy/apiTest/MatchHttpHandlerSpec.groovy` (criado)
  - `CHANGELOG.md` e `plano.md` (atualizados)
- **Requisitos e Regras Atendidos:** `RF-04`, `RF-05`, `RF-06`, `RNF-01`, `RNF-05`, `CA-01` a `CA-09`, `CA-12`.
- **Testes necessários:** Testes unitários Spock simulando requisições `HttpExchange`, incluindo testes assertivos de privacidade (verificando que as chaves `email`, `whatsapp`, `telefone`, `cep`, `cpf`, `cnpj` e `senha` não constam na resposta JSON).
- **Commit esperado:** `feat(api): adiciona rotas REST de curtidas e matches com privacidade estrita`

---

### Tarefa T-05 — Testes de Integração Ponta a Ponta com PostgreSQL Descartável

- **Status:** Concluída
- **Objetivo:** Validar o ciclo completo da aplicação conectada ao PostgreSQL real:
  - Inicializar cluster temporário com `pg_virtualenv` executando `database.sql` e `queries.sql`.
  - Executar testes automatizados Spock de integração que gravam curtidas reais via `CurtidaService` e endpoints HTTP REST, verificando o disparo dos triggers e a criação dos matches na tabela `matches`.
  - Validar rollback transacional em falhas simuladas de persistência sem registros órfãos.
  - Validar privacidade e ausência de vazamento de contatos pessoais no retorno de `GET /api/matches`.
  - Validar preservação de match após exclusão de recrutador (`SET NULL`) e desfazimento após exclusão da última vaga.
  - Validar unicidade com múltiplas vagas e concorrência sem duplicação de matches.
  - Validar mapeamento de erros HTTP (409 para duplicidade, 404 para entidades inexistentes e 400 para recrutador inválido).
- **Camada afetada:** Testes de Integração (`src/test/groovy/integrationTest/`).
- **Arquivos implementados:**
  - `src/test/groovy/integrationTest/CurtidaMatchApiIntegrationSpec.groovy` (criado)
  - `CHANGELOG.md` e `plano.md` (atualizados)
- **Requisitos e Regras Atendidos:** `RF-01` a `RF-07`, `RNF-01` a `RNF-06`, `CA-01` a `CA-12`.
- **Testes executados:** Execução automatizada com Gradle e PostgreSQL descartável via `pg_virtualenv` (8 cenários integrados, 348 testes backend Spock e 388 testes frontend Vitest com 100% de sucesso).
- **Commit esperado:** `test(backend): adiciona testes integrados de curtidas e matches com PostgreSQL`

---

### Tarefa T-06 — Atualização da Documentação no `README.md`, `CHANGELOG.md` e Auditoria Final

- **Status:** Concluída
- **Objetivo:** Consolidar a documentação do projeto:
  - Documentar no `README.md` os novos endpoints REST de curtidas e matches, exemplos de curl, parâmetros aceitos, política de privacidade sem autenticação e modelo transacional.
  - Atualizar o `CHANGELOG.md` consolidando todas as entregas da feature.
  - Executar auditoria de não regressão (277 testes legados + 76 novos testes Spock totalizando 353 testes backend, e 388 testes Vitest no frontend).
- **Camada afetada:** Documentação (`README.md`, `CHANGELOG.md`).
- **Arquivos modificados:**
  - `README.md` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match-backend/plano.md` (modificado para Concluído)
- **Critérios de conclusão e validação:**
  - README claro e objetivo, com exemplos curl funcionais e especificação das regras de privacidade.
  - 100% dos testes verdes (353 backend + 388 frontend).
- **Commit esperado:** `docs: documenta endpoints de curtidas e matches no README e changelog`
