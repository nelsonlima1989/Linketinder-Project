# Plano de Implementação — Feature Recrutador

- **Feature:** `recrutador`
- **Issue vinculada:** [#1 — [FEATURE] Implementar funcionalidade Recrutador no Linketinder](https://github.com/nelsonlima1989/Linketinder-Project/issues/1)
- **Especificação de referência:** [`docs/specs/recrutador/spec.md`](spec.md) (commit `febaf69`)
- **Branch:** `feature/recrutador`
- **Status geral do plano:** Em revisão (aguardando aprovação do responsável antes de iniciar a Tarefa T-01)

---

## 1. Diretrizes de Execução Incremental (Etapa E da Skill SDD)

1. **Uma tarefa por vez:** Nenhuma tarefa seguinte pode ser iniciada antes que a anterior tenha sido implementada, testada, revisada, registrada no `CHANGELOG.md` e commitada com autorização explícita.
2. **Ciclo obrigatório ao final de cada tarefa (`T-01` a `T-08`):**
   - Apresentar alterações realizadas;
   - Revisar `git diff` e `git diff --check`;
   - Executar os testes automatizados da camada afetada (e verificar ausência de regressões);
   - Executar a verificação explícita do **Princípio do Anonimato** e de segurança;
   - Atualizar o `CHANGELOG.md`;
   - Atualizar o status da tarefa neste `plano.md`;
   - Solicitar revisão e autorização humana;
   - Realizar **um commit próprio da tarefa** seguindo Conventional Commits sem acentos no prefixo.
3. **Lacunas de especificação:** Se durante qualquer tarefa surgir dúvida ou lacuna não coberta pela `spec.md`, interromper imediatamente o código e atualizar a `spec.md` antes de prosseguir.

---

## 2. Visão Geral das Tarefas

| ID | Tarefa | Camada | Dependências | Status | Commit Esperado |
|---|---|---|---|---|---|
| **T-01** | Estrutura SQL e seeds/consultas de `recrutadores` | Banco (PostgreSQL) | `spec.md` aprovada | Concluída | `feat(database): adiciona tabela e consultas de recrutadores` |
| **T-02** | Models, utilitário PBKDF2 (`SenhaHashUtil`), `RecrutadorRepository` e `RecrutadorDAO` + `RecrutadorDAOSpec` | Backend (Groovy) | T-01 | Concluída | `feat: implementa model e DAO de recrutador com PBKDF2` |
| **T-03** | `RecrutadorService` (validações, unicidade, 1:N, cascata e consultas anônimas) + `RecrutadorServiceSpec` | Backend (Groovy) | T-02 | Pendente | `feat: implementa RecrutadorService com validacoes e anonimato` |
| **T-04** | `RecrutadorController`, `Menu` de terminal e `Main` + `RecrutadorControllerSpec` e `MenuSpec` | Backend (Groovy) | T-03 | Pendente | `feat: adiciona controller e opcoes de recrutador no menu` |
| **T-05** | Endpoint HTTP `POST /api/recrutadores` (`RecrutadorHttpHandler` e `ApiServer`) + `RecrutadorHttpHandlerSpec` | Backend (API HTTP) | T-03 | Pendente | `feat: adiciona endpoint POST /api/recrutadores na API HTTP` |
| **T-06** | Model `Recrutador.ts`, seeds, `recrutadorService.ts`, `edicaoRecrutadorService.ts` e cascata em `exclusaoEmpresaService.ts` + testes Vitest | Frontend (TypeScript) | `spec.md` aprovada | Pendente | `feat: implementa servicos de recrutador e cascata no frontend` |
| **T-07** | Componentes de UI (`renderizarCardsRecrutadoresEmpresa`, `renderizarFormularioRecrutador`) e integração no perfil da empresa + testes Vitest | Frontend (TypeScript) | T-06 | Pendente | `feat: adiciona componentes de listagem e formulario de recrutador` |
| **T-08** | `recrutadorController.ts`, navegação (`navegacaoService`, `renderizarTela`, `main.ts`) e validação final ponta a ponta + testes Vitest | Frontend (TypeScript) | T-07 | Pendente | `feat: integra controller e navegacao de recrutador no frontend` |

---

## 3. Detalhamento das Tarefas

---

### Tarefa T-01 — Estrutura SQL e Seeds/Consultas de `recrutadores`

- **Status:** Concluída
- **Objetivo:** Adicionar o DDL da tabela `recrutadores` (relacionamento `1:N` com `empresas`, constraints `NOT NULL`, `UNIQUE` para `email` e `cpf`, `ON DELETE CASCADE` e índice em `empresa_id`) e incluir seeds iniciais (com senhas em formato hash PBKDF2) e consultas SQL de validação.
- **Camada afetada:** Banco de Dados (`database/`).
- **Arquivos previstos:**
  - `database/database.sql` (modificado — adição de `CREATE TABLE recrutadores` e `CREATE INDEX idx_recrutadores_empresa_id`)
  - `database/queries.sql` (modificado — adição de `INSERT INTO recrutadores` com 2+ recrutadores na mesma empresa para comprovar `1:N` e consultas `SELECT` omitindo `senha`)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado — atualização de status)
- **Dependências:** Especificação `docs/specs/recrutador/spec.md` aprovada e commitada.
- **Requisitos e Regras Atendidos:** `RF-01`, `RF-02`, `RF-06`, `RN-01`, `RN-02`, `RN-03`, `RN-04`, `RN-06`, `RN-12`.
- **Critérios de conclusão:**
  - `database/database.sql` contém a definição completa de `recrutadores` sem alterar as 8 tabelas existentes.
  - `database/queries.sql` contém seeds de recrutadores vinculados a empresas existentes (incluindo cardinalidade `1:N`) com hashes `pbkdf2_sha256$65536$...` e consultas `SELECT` de listagem por empresa sem retornar a coluna `senha`.
- **Testes necessários:**
  - Revisão sintática do DDL e DML em `database.sql` e `queries.sql` e execução de `./gradlew test --console=plain` para garantir que nada quebrou.
- **Verificação de anonimato e segurança:**
  - Confirmar que nenhum seed contém senha em texto puro ou dados pessoais reais.
  - Confirmar que as consultas de listagem em `queries.sql` não projetam a coluna `senha` nem cruzam recrutadores na consulta pública de vagas para candidatos.
- **Atualização do CHANGELOG:** Registrar em `[Não lançado] -> Adicionado` a criação da tabela `recrutadores`, índice e consultas em `database/`.
- **Commit esperado:** `feat(database): adiciona tabela e consultas de recrutadores`

---

### Tarefa T-02 — Models, Utilitário PBKDF2 (`SenhaHashUtil`), `RecrutadorRepository` e `RecrutadorDAO`

- **Status:** Concluída
- **Objetivo:** Implementar as classes de modelo (`Recrutador` e `RecrutadorResumo`), o utilitário de derivação de senha `SenhaHashUtil` (`PBKDF2WithHmacSHA256` com salt aleatório individual de 16 bytes e 65.536 iterações), a interface `RecrutadorRepository` e o `RecrutadorDAO` (JDBC com `PreparedStatement`), acompanhados de testes unitários em Spock.
- **Camada afetada:** Backend (`model`, `repository`, `dao`, `service`, `src/test/groovy/daoTest`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/model/Recrutador.groovy` (novo)
  - `src/main/groovy/com/linketinder/model/RecrutadorResumo.groovy` (novo)
  - `src/main/groovy/com/linketinder/service/SenhaHashUtil.groovy` (novo)
  - `src/main/groovy/com/linketinder/repository/RecrutadorRepository.groovy` (novo)
  - `src/main/groovy/com/linketinder/dao/RecrutadorDAO.groovy` (novo)
  - `src/test/groovy/daoTest/RecrutadorDAOSpec.groovy` (novo)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-01.
- **Requisitos e Regras Atendidos:** `RF-01` a `RF-06`, `RNF-01`, `RNF-03`, `RNF-05`, `RNF-06`, `RN-01`, `RN-02`, `RN-03`, `RN-06`.
- **Critérios de conclusão:**
  - `RecrutadorDAO` implementa `inserir`, `listarTodos`, `listarPorEmpresaId`, `buscarPorId`, `buscarPorCpf`, `buscarPorEmail`, `atualizar` (sem alterar `empresa_id`), `excluir` e `excluirPorEmpresaId` usando exclusivamente `PreparedStatement` e convertendo `SQLException` em `IllegalStateException`.
  - `SenhaHashUtil` gera hashes no formato `pbkdf2_sha256$65536$<salt_hex>$<hash_hex>` com `SecureRandom` e permite verificar senhas em tempo constante (`MessageDigest.isEqual`).
  - `RecrutadorResumo` contém apenas `id`, `empresaId`, `nome`, `areaRecrutamento`, `descricao` e `email`.
- **Testes necessários:**
  - `./gradlew test --tests 'daoTest.RecrutadorDAOSpec' --console=plain`
  - `./gradlew test --console=plain` (suíte completa sem regressões).
- **Verificação de anonimato e segurança:**
  - Assegurar em teste que `RecrutadorResumo` não possui propriedades `senha`, `cpf`, `dataNascimento` nem `empresa`.
  - Assegurar que `RecrutadorDAO` não imprime `cpf`, `email` ou `senha` em logs/exceções.
- **Atualização do CHANGELOG:** Registrar a adição de `Recrutador`, `RecrutadorResumo`, `SenhaHashUtil`, `RecrutadorRepository`, `RecrutadorDAO` e `RecrutadorDAOSpec`.
- **Commit esperado:** `feat: implementa model e DAO de recrutador com PBKDF2`

---

### Tarefa T-03 — `RecrutadorService` (Validações, Unicidade, 1:N, Cascata e Consultas Anônimas)

- **Status:** Pendente
- **Objetivo:** Implementar o `RecrutadorService` contendo todas as regras de validação (`VAL-01` a `VAL-09`), verificação de empresa existente, unicidade de CPF e e-mail, derivação PBKDF2 da senha antes da persistência, listagem segura via `RecrutadorResumo`, consulta de vagas da empresa do recrutador, consulta de candidatos via `CandidatoAnonimo` e exclusão em cascata ao excluir empresa.
- **Camada afetada:** Backend (`service`, `src/test/groovy/serviceTest`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/service/RecrutadorService.groovy` (novo)
  - `src/main/groovy/com/linketinder/service/ConflitoDadosException.groovy` (novo — ou tratamento específico para duplicidade de CPF/e-mail)
  - `src/main/groovy/com/linketinder/service/EmpresaService.groovy` (modificado — suporte opcional a `RecrutadorRepository` para exclusão em cascata mantendo construtor existente compatível)
  - `src/test/groovy/serviceTest/RecrutadorServiceSpec.groovy` (novo)
  - `src/test/groovy/serviceTest/EmpresaServiceSpec.groovy` (modificado — teste de exclusão em cascata de recrutadores)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-02.
- **Requisitos e Regras Atendidos:** `RF-01` a `RF-06`, `RF-08`, `RF-09`, `RNF-01`, `RNF-02`, `RN-01` a `RN-11`, `CA-01` a `CA-14`.
- **Critérios de conclusão:**
  - Todas as validações (`empresaId > 0`, empresa existente, CPF 11 dígitos numéricos, e-mail válido, idade >= 18 anos, campos obrigatórios e limites de tamanho, senha >= 8 chars, unicidade de CPF e e-mail) implementadas e testadas.
  - Senha sempre transformada via `SenhaHashUtil.gerarHash` antes de chamar o repository.
  - `listarVagasDaEmpresaDoRecrutador` retorna apenas vagas da empresa do recrutador.
  - `listarCandidatosAnonimosParaRecrutador` retorna exclusivamente `List<CandidatoAnonimo>`.
- **Testes necessários:**
  - `./gradlew test --tests 'serviceTest.RecrutadorServiceSpec' --console=plain`
  - `./gradlew test --console=plain`
- **Verificação de anonimato e segurança:**
  - Testes explícitos em `RecrutadorServiceSpec` comprovando que: (1) a senha enviada ao DAO começa com `pbkdf2_sha256$65536$` e nunca é texto puro; (2) `listarRecrutadoresPorEmpresa` retorna `RecrutadorResumo` sem `senha`, `cpf` ou `Empresa` completa; (3) `listarCandidatosAnonimosParaRecrutador` não expõe nome, CPF, e-mail, CEP, WhatsApp ou data de nascimento dos candidatos.
- **Atualização do CHANGELOG:** Registrar a implementação de `RecrutadorService` e `RecrutadorServiceSpec`.
- **Commit esperado:** `feat: implementa RecrutadorService com validacoes e anonimato`

---

### Tarefa T-04 — `RecrutadorController`, `Menu` de Terminal e `Main`

- **Status:** Pendente
- **Objetivo:** Implementar `RecrutadorController`, integrar as opções de Recrutador (listagem de recrutadores por empresa e análise de vagas da empresa / candidatos anônimos pelo recrutador) no `Menu.groovy` e realizar a injeção manual de dependências em `Main.groovy`.
- **Camada afetada:** Backend (`controller`, `view`, `Main`, `src/test/groovy/controllerTest`, `src/test/groovy/uiTest`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/controller/RecrutadorController.groovy` (novo)
  - `src/main/groovy/com/linketinder/view/Menu.groovy` (modificado — construtor retrocompatível + opções de recrutador)
  - `src/main/groovy/com/linketinder/Main.groovy` (modificado — instanciação de `RecrutadorDAO`, `RecrutadorService` e `RecrutadorController`)
  - `src/test/groovy/controllerTest/RecrutadorControllerSpec.groovy` (novo)
  - `src/test/groovy/uiTest/MenuSpec.groovy` (modificado — testes das novas opções do menu)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-03.
- **Requisitos e Regras Atendidos:** `RF-02`, `RF-08`, `RF-09`, `RN-07`, `RN-08`, `RN-09`.
- **Critérios de conclusão:**
  - `RecrutadorController` delega operações para `RecrutadorService`.
  - `Menu` permite listar os recrutadores de uma empresa (`RecrutadorResumo`) e consultar as vagas da empresa e candidatos anônimos sob a perspectiva do recrutador, mantendo todos os fluxos existentes do `Menu` intactos.
- **Testes necessários:**
  - `./gradlew test --tests 'controllerTest.RecrutadorControllerSpec' --tests 'uiTest.MenuSpec' --console=plain`
  - `./gradlew test --console=plain`
- **Verificação de anonimato e segurança:**
  - Verificar em `MenuSpec` que a saída no console não exibe `senha`, `cpf` nem `dataNascimento` do recrutador, não expõe dados de recrutadores na listagem de vagas para candidatos e exibe apenas dados de `CandidatoAnonimo` para o recrutador.
- **Atualização do CHANGELOG:** Registrar a adição de `RecrutadorController` e a integração no `Menu` e `Main`.
- **Commit esperado:** `feat: adiciona controller e opcoes de recrutador no menu`

---

### Tarefa T-05 — Endpoint HTTP `POST /api/recrutadores` (`RecrutadorHttpHandler` e `ApiServer`)

- **Status:** Pendente
- **Objetivo:** Implementar `RecrutadorHttpHandler` para atender `POST /api/recrutadores` e registrá-lo em `ApiServer.groovy`, com testes de integração HTTP real em `RecrutadorHttpHandlerSpec`.
- **Camada afetada:** Backend (`api`, `src/test/groovy/apiTest`).
- **Arquivos previstos:**
  - `src/main/groovy/com/linketinder/api/RecrutadorHttpHandler.groovy` (novo)
  - `src/main/groovy/com/linketinder/api/ApiServer.groovy` (modificado — registro de `/api/recrutadores`)
  - `src/test/groovy/apiTest/RecrutadorHttpHandlerSpec.groovy` (novo)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-03.
- **Requisitos e Regras Atendidos:** `RF-07`, `RNF-01`, `RNF-02`, `RN-01`, `RN-04`, `RN-05`, `RN-06`, `RN-12`, `CA-01`, `CA-03` a `CA-07`, `CA-14`.
- **Critérios de conclusão:**
  - `POST /api/recrutadores` retorna `201 Created` com header `Location: /api/recrutadores/{id}` e JSON `{"id", "empresaId", "mensagem"}`.
  - Trata corretamente `400` (campos ausentes/inválidos, CPF inválido, e-mail inválido, menor de 18 anos, senha curta, `empresaId` inválido), `404` (empresa não encontrada ou subcaminho inválido), `405` (`Allow: POST`), `409` (CPF ou e-mail duplicado), `413` (> 16 KiB), `415` (`Content-Type` inválido) e `500` (falha de persistência).
- **Testes necessários:**
  - `./gradlew test --tests 'apiTest.RecrutadorHttpHandlerSpec' --console=plain`
  - `./gradlew clean test jacocoTestReport --console=plain`
- **Verificação de anonimato e segurança:**
  - Confirmar em `RecrutadorHttpHandlerSpec` que o corpo da resposta HTTP nunca inclui `senha` ou `cpf`, e que `RecrutadorHttpHandler` não imprime stack traces contendo dados sensíveis (CPF, e-mail, senha) no `stderr`.
- **Atualização do CHANGELOG:** Registrar a criação de `RecrutadorHttpHandler`, atualização de `ApiServer` e `RecrutadorHttpHandlerSpec`.
- **Commit esperado:** `feat: adiciona endpoint POST /api/recrutadores na API HTTP`

---

### Tarefa T-06 — Frontend: Model `Recrutador.ts`, Seeds, `recrutadorService.ts`, `edicaoRecrutadorService.ts` e Cascata

- **Status:** Pendente
- **Objetivo:** Implementar no frontend o modelo `Recrutador`, os dados iniciais em `dados.ts`, o serviço de persistência e validação `recrutadorService.ts` (chave `"recrutadores"` no `localStorage`, sem persistir senha/hash), o serviço de bloqueio entre abas `edicaoRecrutadorService.ts` (`"recrutador-em-edicao"`) e a exclusão em cascata de recrutadores em `exclusaoEmpresaService.ts`.
- **Camada afetada:** Frontend (`models`, `data`, `services`).
- **Arquivos previstos:**
  - `frontend/src/models/Recrutador.ts` (novo)
  - `frontend/src/data/dados.ts` (modificado — adição do seed `recrutadores: Recrutador[]`)
  - `frontend/src/services/recrutador/recrutadorService.ts` (novo)
  - `frontend/src/services/recrutador/recrutadorService.test.ts` (novo)
  - `frontend/src/services/recrutador/edicaoRecrutadorService.ts` (novo)
  - `frontend/src/services/recrutador/edicaoRecrutadorService.test.ts` (novo)
  - `frontend/src/services/empresa/exclusaoEmpresaService.ts` (modificado — inclusão de `excluirRecrutadoresPorEmpresa`)
  - `frontend/src/services/empresa/exclusaoEmpresaService.test.ts` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** `spec.md` aprovada (independente do backend, executada na sequência após T-05).
- **Requisitos e Regras Atendidos:** `RF-01` a `RF-06`, `RF-08`, `RF-10`, `RNF-01`, `RNF-05`, `RN-01` a `RN-08`, `CA-01` a `CA-06`, `CA-08` a `CA-12`.
- **Critérios de conclusão:**
  - `recrutadorService.ts` implementa `listarRecrutadores`, `obterRecrutadoresPorEmpresa`, `obterRecrutadorPorId`, `validarDadosRecrutador`, `criarRecrutador`, `editarRecrutador`, `excluirRecrutador` e `excluirRecrutadoresPorEmpresa`.
  - Validações de CPF (`^\d{11}$`), e-mail válido, idade >= 18 anos, unicidade de CPF/e-mail entre recrutadores e senha >= 8 caracteres no cadastro funcionando e testadas.
  - O objeto persistido em `localStorage` **não contém** propriedade `senha`.
- **Testes necessários:**
  - `cd frontend && npx tsc --noEmit && npm test`
- **Verificação de anonimato e segurança:**
  - Asserção explícita em `recrutadorService.test.ts` lendo `localStorage.getItem("recrutadores")` e comprovando que a string JSON não contém `"senha"` nem o valor da senha submetida.
- **Atualização do CHANGELOG:** Registrar a criação do model e dos serviços de recrutador no frontend.
- **Commit esperado:** `feat: implementa servicos de recrutador e cascata no frontend`

---

### Tarefa T-07 — Frontend: Componentes de UI de Recrutador e Integração no Perfil da Empresa

- **Status:** Pendente
- **Objetivo:** Criar os componentes de apresentação `renderizarCardsRecrutadoresEmpresa.ts` e `renderizarFormularioRecrutador.ts`, e atualizar `renderizarPerfilEmpresa.ts` para exibir o botão `"Cadastrar novo recrutador"` (`#botao-novo-recrutador`) e a seção `"Meus recrutadores"` (`#lista-recrutadores-empresa`), mantendo intactos os componentes existentes.
- **Camada afetada:** Frontend (`components`).
- **Arquivos previstos:**
  - `frontend/src/components/recrutadores/renderizarCardsRecrutadoresEmpresa.ts` (novo)
  - `frontend/src/components/recrutadores/renderizarCardsRecrutadoresEmpresa.test.ts` (novo)
  - `frontend/src/components/recrutadores/renderizarFormularioRecrutador.ts` (novo)
  - `frontend/src/components/recrutadores/renderizarFormularioRecrutador.test.ts` (novo)
  - `frontend/src/components/empresas/renderizarPerfilEmpresa.ts` (modificado)
  - `frontend/src/components/empresas/renderizarPerfilEmpresa.test.ts` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-06.
- **Requisitos e Regras Atendidos:** `RF-01`, `RF-02`, `RF-04`, `RF-05`, `RNF-02`, `RNF-04`, `RN-03`, `RN-07`, `RN-09`, `CA-13`, `CA-14`.
- **Critérios de conclusão:**
  - `renderizarCardsRecrutadoresEmpresa` renderiza estado vazio (`"Nenhum recrutador cadastrado."`) ou cards com `nome`, `areaRecrutamento`, `descricao`, `email` e botões `.botao-editar-recrutador` / `.botao-excluir-recrutador` com `data-recrutador-id`, sanitizando tudo com `escaparHtml()`.
  - `renderizarFormularioRecrutador` renderiza campos `nome`, `areaRecrutamento`, `descricao`, `email`, `cpf`, `dataNascimento` e `senha` (sem nunca preencher valor prévio no input `senha`).
- **Testes necessários:**
  - `cd frontend && npx tsc --noEmit && npm test`
- **Verificação de anonimato e segurança:**
  - Asserções explícitas em `renderizarCardsRecrutadoresEmpresa.test.ts` garantindo que `cpf`, `dataNascimento` e `senha` não aparecem no HTML dos cards.
  - Verificar que `renderizarCardsVagas.test.ts` e `renderizarCardsCandidatos.test.ts` permanecem íntegros e sem expor dados identificadores.
- **Atualização do CHANGELOG:** Registrar a adição dos componentes de UI de recrutador e atualização do perfil da empresa.
- **Commit esperado:** `feat: adiciona componentes de listagem e formulario de recrutador`

---

### Tarefa T-08 — Frontend: `recrutadorController.ts`, Navegação, `main.ts` e Validação Final

- **Status:** Pendente
- **Objetivo:** Implementar `recrutadorController.ts`, atualizar `empresaController.ts` (para acionar `excluirRecrutadoresPorEmpresa` na exclusão da empresa), adicionar a tela `"cadastro-recrutador"` em `navegacaoService.ts`, `contextoTelaService.ts` e `renderizarTela.ts`, conectar os fluxos em `main.ts` e realizar a validação completa da suíte (backend + frontend).
- **Camada afetada:** Frontend (`controllers`, `services/utils`, `components/layout`, `main.ts`).
- **Arquivos previstos:**
  - `frontend/src/controllers/recrutadorController.ts` (novo)
  - `frontend/src/controllers/recrutadorController.test.ts` (novo)
  - `frontend/src/controllers/empresaController.ts` (modificado — chamada a `excluirRecrutadoresPorEmpresa` na exclusão da empresa)
  - `frontend/src/controllers/empresaController.test.ts` (modificado)
  - `frontend/src/services/utils/navegacaoService.ts` (modificado — união `Tela` inclui `"cadastro-recrutador"`)
  - `frontend/src/services/utils/navegacaoService.test.ts` (modificado)
  - `frontend/src/components/layout/renderizarTela.ts` (modificado — suporte a `recrutadorId` e tela `"cadastro-recrutador"`)
  - `frontend/src/components/layout/renderizarTela.test.ts` (modificado)
  - `frontend/src/main.ts` (modificado — conexão dos handlers de recrutador e liberação de bloqueio ao navegar)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/recrutador/plano.md` (modificado)
- **Dependências:** T-07.
- **Requisitos e Regras Atendidos:** `RF-01` a `RF-10`, `RNF-01` a `RNF-07`, `CA-01` a `CA-15`.
- **Critérios de conclusão:**
  - Cadastro, listagem no perfil da empresa, edição (com bloqueio entre abas), cancelamento e exclusão (individual e em cascata com a empresa) funcionando ponta a ponta no frontend.
  - Suíte completa do frontend (`npx tsc --noEmit`, `npm test`, `npm run build`) e suíte completa do backend (`./gradlew clean test jacocoTestReport --console=plain`) executadas com 100% de aprovação.
- **Testes necessários:**
  - `cd frontend && npx tsc --noEmit && npm test && npm run build`
  - `./gradlew clean test jacocoTestReport --console=plain`
- **Verificação de anonimato e segurança:**
  - Verificação final de todos os critérios `CA-01` a `CA-15`, confirmando ausência de senha/hash no `localStorage`, uso de PBKDF2+salt no backend e preservação total do anonimato bilateral.
- **Atualização do CHANGELOG:** Registrar a implementação do controller, navegação e conclusão da feature Recrutador no frontend.
- **Commit esperado:** `feat: integra controller e navegacao de recrutador no frontend`
