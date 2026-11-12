# Plano de Implementação — Feature Match (Persistência Relacional e Demonstração SQL)

- **Feature:** `match`
- **Issue vinculada:** [#3 — [FEATURE] Implementar persistência de Curtidas e Matches no PostgreSQL](https://github.com/nelsonlima1989/Linketinder-Project/issues/3)
- **Especificação de referência:** [`docs/specs/match/spec.md`](spec.md)
- **Branch:** `feature/match-bd`
- **Status geral do plano:** Concluído (tarefas T-01 a T-06 implementadas, validadas e auditadas com sucesso)

---

## 1. Diretrizes de Execução Incremental (Etapa E da Skill SDD)

1. **Uma tarefa por vez:** Nenhuma tarefa seguinte pode ser iniciada antes que a anterior tenha sido implementada, validada, revisada, registrada no `CHANGELOG.md` e commitada com autorização explícita.
2. **Escopo restrito:** Nenhuma tarefa deve alterar código Groovy (`src/main/groovy/**`, `src/test/groovy/**`), API HTTP ou frontend TypeScript (`frontend/**`).
3. **Ciclo obrigatório ao final de cada tarefa (`T-01` a `T-06`):**
   - Apresentar alterações realizadas;
   - Revisar `git diff` e `git diff --check`;
   - Executar as validações SQL e testes de não regressão aplicáveis;
   - Executar a verificação explícita do **Princípio do Anonimato** e de segurança;
   - Atualizar o `CHANGELOG.md`;
   - Atualizar o status da tarefa neste `plano.md`;
   - Solicitar revisão e autorização humana;
   - Realizar **um commit próprio da tarefa** seguindo Conventional Commits sem acentos no prefixo.
4. **Lacunas de especificação:** Se durante qualquer tarefa surgir dúvida ou lacuna não coberta pela `spec.md`, interromper imediatamente a implementação e atualizar a `spec.md` antes de prosseguir.

---

## 2. Visão Geral das Tarefas

| ID | Tarefa | Camada | Dependências | Status | Commit Esperado |
|---|---|---|---|---|---|
| **T-01** | Evolução do DDL (`curtidas_candidato_vaga`, `curtidas_empresa_candidato`, `matches`, FKs `ON DELETE` e índices) | Banco (`database/database.sql`) | `spec.md` aprovada | Concluída | `feat(database): estrutura tabelas de curtidas e matches com integridade referencial` |
| **T-02** | Funções PL/pgSQL e Triggers (validação de recrutador, registro nas 2 ordens, exclusão condicional e concorrência) | Banco (`database/database.sql`) | T-01 | Concluída | `feat(database): adiciona triggers de reciprocidade e consistencia de matches` |
| **T-03** | Migração e adequação dos seeds (`recrutadores` antes de `curtidas_empresa_candidato` + empresas 4 e 5) | Banco (`database/queries.sql`) | T-02 | Concluída | `feat(database): migra seeds de recrutadores e curtidas de empresa` |
| **T-04** | Consultas de demonstração e suite de verificação SQL (reciprocidade, exclusões, `SET NULL`, recriação e restrições) | Banco (`database/queries.sql`) | T-03 | Concluída | `test(database): adiciona consultas e cenarios de validacao SQL de matches` |
| **T-05** | Atualização do Diagrama Entidade-Relacionamento (MER) | Documentação (`docs/der-linketinder.png`) | T-01, T-02 | Concluída | `docs: atualiza diagrama MER com recrutadores, curtidas e matches` |
| **T-06** | Atualização da documentação técnica e instruções SQL no `README.md` | Documentação (`README.md`) | T-04, T-05 | Concluída | `docs: documenta persistencia de curtidas e matches no README` |

---

## 3. Detalhamento das Tarefas

---

### Tarefa T-01 — Evolução do DDL (`curtidas_candidato_vaga`, `curtidas_empresa_candidato` e `matches`)

- **Status:** Concluída
- **Objetivo:** Atualizar o DDL em `database/database.sql` para:
  1. Configurar `ON DELETE CASCADE` nas FKs de `vagas`, `candidato_competencias`, `vaga_competencias` e `curtidas_candidato_vaga`;
  2. Adicionar `recrutador_id INTEGER NULL` em `curtidas_empresa_candidato` com `FOREIGN KEY (recrutador_id) REFERENCES recrutadores(id) ON DELETE SET NULL` e `ON DELETE CASCADE` para `empresa_id` e `candidato_id`;
  3. Criar a tabela `matches` (sem `vaga_id`, com `UNIQUE (empresa_id, candidato_id)`, FK composta para `curtidas_empresa_candidato (empresa_id, candidato_id) ON DELETE CASCADE` e FK para `recrutadores(id) ON DELETE SET NULL`);
  4. Criar os índices de suporte (`idx_curtidas_cand_vaga_vaga_id`, `idx_curtidas_emp_cand_candidato_id`, `idx_curtidas_emp_cand_recrutador_id`, `idx_matches_candidato_id`, `idx_matches_recrutador_id`).
- **Camada afetada:** Banco de Dados (`database/`).
- **Arquivos previstos:**
  - `database/database.sql` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** `spec.md` e `plano.md` aprovados e commitados.
- **Requisitos e Regras Atendidos:** `RF-01`, `RF-02`, `RF-04`, `RF-07`, `RN-01`, `RN-05`, `VAL-01`, `VAL-02`, `VAL-05`, `CA-01`, `CA-04`.
- **Critérios de conclusão e validação:**
  - `database/database.sql` define as 10 tabelas com sintaxe PostgreSQL válida, chaves primárias, chaves únicas, FKs (`ON DELETE CASCADE` / `ON DELETE SET NULL`) e índices especificados na Seção 6 da `spec.md`.
  - `git diff --check` sem erros de formatação.
- **Testes necessários:**
  - Validação do DDL no PostgreSQL (quando autorizado) e execução de `./gradlew test --console=plain` para confirmar ausência de regressões.
- **Verificação de anonimato e segurança:**
  - Confirmar ausência de credenciais ou dados reais no DDL.
- **Atualização do CHANGELOG:** Registrar em `[Não lançado] -> Adicionado / Modificado` a criação da tabela `matches` e evolução das tabelas de curtidas em `database/database.sql`.
- **Commit esperado:** `feat(database): estrutura tabelas de curtidas e matches com integridade referencial`

---

### Tarefa T-02 — Funções PL/pgSQL e Triggers de Validação, Reciprocidade e Concorrência

- **Status:** Concluída (auditada e reforçada com serialização em `BEFORE` triggers, `FOR KEY SHARE`, trigger em `UPDATE OF empresa_id ON vagas` e bloqueio de alteração de `recrutadores.empresa_id`)
- **Objetivo:** Implementar em `database/database.sql` as 5 funções PL/pgSQL e respectivos triggers especificados na Seção 6.4 da `spec.md`:
  1. `fn_validar_curtida_empresa_recrutador` (`BEFORE INSERT OR UPDATE ON curtidas_empresa_candidato`): exige `recrutador_id NOT NULL` no `INSERT`, valida pertencimento do recrutador à `empresa_id` e só permite `recrutador_id IS NULL` em `UPDATE` quando o recrutador foi excluído (`ON DELETE SET NULL`).
  2. `fn_validar_match` (`BEFORE INSERT OR UPDATE ON matches`): impede inserção manual sem reciprocidade ativa e protege `UPDATE` durante `ON DELETE SET NULL` sem conflito de ordem de triggers.
  3. `fn_registrar_match_apos_curtida_candidato` (`AFTER INSERT ON curtidas_candidato_vaga`): sincroniza via `pg_advisory_xact_lock` e registra match quando já há curtida da empresa.
  4. `fn_registrar_match_apos_curtida_empresa` (`AFTER INSERT ON curtidas_empresa_candidato`): sincroniza via `pg_advisory_xact_lock` e registra match quando já há curtida do candidato em vaga da empresa.
  5. `fn_remover_match_sem_reciprocidade` (`AFTER DELETE OR UPDATE OF candidato_id, vaga_id ON curtidas_candidato_vaga`): sincroniza via `pg_advisory_xact_lock` e remove o match somente quando não restarem outras curtidas válidas do candidato em vagas daquela empresa.
- **Camada afetada:** Banco de Dados (`database/`).
- **Arquivos previstos:**
  - `database/database.sql` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** T-01.
- **Requisitos e Regras Atendidos:** `RF-02` a `RF-07`, `RN-02` a `RN-05`, `VAL-03` a `VAL-07`, `CA-02` a `CA-10`.
- **Critérios de conclusão e validação:**
  - Todas as funções e triggers declaradas em `database/database.sql` sem conflitos entre `ON DELETE CASCADE` e `ON DELETE SET NULL`.
  - Uso consistente de `pg_advisory_xact_lock(empresa_id, candidato_id)` e `ON CONFLICT (empresa_id, candidato_id) DO NOTHING`.
- **Testes necessários:**
  - Validação sintática e de execução das funções/triggers no PostgreSQL (quando autorizado) + `git diff --check`.
- **Verificação de anonimato e segurança:**
  - Confirmar que nenhuma mensagem de `RAISE EXCEPTION` expõe senhas ou dados sensíveis.
- **Atualização do CHANGELOG:** Registrar a adição das funções PL/pgSQL e triggers de curtidas/matches em `database/database.sql`.
- **Commit esperado:** `feat(database): adiciona triggers de reciprocidade e consistencia de matches`

---

### Tarefa T-03 — Migração e Adequação dos Seeds em `database/queries.sql`

- **Status:** Concluída
- **Objetivo:** Atualizar os seeds iniciais em `database/queries.sql` para:
  1. Posicionar a inserção de `recrutadores` **antes** da seção de `curtidas_empresa_candidato`;
  2. Adicionar recrutadores fictícios (com senhas derivadas em formato `pbkdf2_sha256$65536$<salt>$<hash>`) para as empresas `4` (ArrozSoft) e `5` (FeijãoTech);
  3. Atualizar todas as inserções em `curtidas_empresa_candidato` informando explicitamente o `recrutador_id` válido de cada empresa;
  4. Organizar a ordem de inserção das curtidas para demonstrar tanto matches formados quando o candidato curte a vaga primeiro quanto quando o recrutador curte o candidato primeiro, além de candidato curtindo mais de uma vaga da mesma empresa;
  5. Incluir em comentário/bloco documentado o script de migração incremental para bancos preexistentes (Seção 7.2 da `spec.md`).
- **Camada afetada:** Banco de Dados (`database/`).
- **Arquivos previstos:**
  - `database/queries.sql` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** T-02.
- **Requisitos e Regras Atendidos:** `RF-02`, `RF-03`, `RF-04`, `RF-08`, `RN-01` a `RN-04`, `CA-01` a `CA-04`, `CA-11`.
- **Critérios de conclusão e validação:**
  - Todos os registros de `curtidas_empresa_candidato` possuem `recrutador_id` pertencente à respectiva `empresa_id`.
  - Nenhuma senha em texto puro nos novos seeds de recrutadores.
- **Testes necessários:**
  - Execução sequencial de `database/database.sql` e `database/queries.sql` (quando autorizado) + `git diff --check`.
- **Verificação de anonimato e segurança:**
  - Confirmar que todos os dados são sintéticos e que todas as senhas de recrutadores seguem o formato `pbkdf2_sha256$65536$...`.
- **Atualização do CHANGELOG:** Registrar a migração e reordenação dos seeds de recrutadores e curtidas em `database/queries.sql`.
- **Commit esperado:** `feat(database): migra seeds de recrutadores e curtidas de empresa`

---

### Tarefa T-04 — Consultas de Demonstração e Verificação SQL em `database/queries.sql`

- **Status:** Concluída
- **Objetivo:** Atualizar e expandir as consultas e blocos de verificação ao final de `database/queries.sql` para demonstrar e comprovar todos os critérios de aceite (`CA-01` a `CA-11`):
  1. Listagem de curtidas de candidatos em vagas e de empresas em candidatos (exibindo o recrutador responsável);
  2. Listagem dos matches registrados automaticamente na tabela `matches` (cobrindo as duas ordens de curtida e a unicidade quando há múltiplas vagas curtidas na mesma empresa);
  3. Demonstração transacional/controlada dos cenários de:
     - Exclusão de uma vaga quando há outra vaga curtida na mesma empresa (**match preservado** — `CA-05`);
     - Exclusão da última vaga curtida da empresa (**match removido** — `CA-06`);
     - Nova curtida em vaga restabelecendo a reciprocidade (**match recriado** — `CA-07`);
     - Exclusão de recrutador preservando a curtida e o match com `recrutador_id = NULL` (`CA-09`);
     - Exclusão em cascata de candidato e empresa (`CA-08`);
     - Rejeição de curtida sem recrutador, com recrutador de outra empresa e de inserção manual de match sem reciprocidade (`CA-02`, `CA-10`).
- **Camada afetada:** Banco de Dados (`database/`).
- **Arquivos previstos:**
  - `database/queries.sql` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** T-03.
- **Requisitos e Regras Atendidos:** `RF-03` a `RF-08`, `CA-01` a `CA-11`.
- **Critérios de conclusão e validação:**
  - Consultas e blocos de teste SQL executam do início ao fim sem erro inesperado e deixam os seeds principais íntegros para consulta.
  - Nenhuma consulta projeta `senha`, `cpf` ou `cnpj`.
- **Testes necessários:**
  - Execução completa de `database/database.sql` + `database/queries.sql` (quando autorizado) e verificação dos resultados retornados por cada consulta + `git diff --check`.
- **Verificação de anonimato e segurança:**
  - Verificar que as consultas pré-match preservam o anonimato e que as consultas de auditoria de match não expõem `senha`, `cpf` ou `cnpj`.
- **Atualização do CHANGELOG:** Registrar a adição das consultas de demonstração e cenários de verificação SQL em `database/queries.sql`.
- **Commit esperado:** `test(database): adiciona consultas e cenarios de validacao SQL de matches`

---

### Tarefa T-05 — Atualização do Diagrama Entidade-Relacionamento (`docs/der-linketinder.png`)

- **Status:** Concluída
- **Objetivo:** Atualizar a imagem do diagrama MER em `docs/der-linketinder.png` para refletir fielmente as 10 tabelas do esquema atual (`candidatos` e `empresas` com coluna `estado`, `competencias`, `vagas`, `recrutadores`, `candidato_competencias`, `vaga_competencias`, `curtidas_candidato_vaga`, `curtidas_empresa_candidato` com `recrutador_id` e `matches`), incluindo chaves primárias, chaves estrangeiras e cardinalidades.
- **Camada afetada:** Documentação (`docs/`).
- **Arquivos previstos:**
  - `docs/der-linketinder.png` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** T-01, T-02.
- **Requisitos e Regras Atendidos:** `RF-09`, `CA-12`.
- **Critérios de conclusão e validação:**
  - Inspeção visual de `docs/der-linketinder.png` confirmando legibilidade, presença das 10 tabelas, todas as colunas e relacionamentos (`1:N`, `N:N`, `0..1`) em conformidade com `database/database.sql`.
- **Testes necessários:**
  - Verificação visual do arquivo PNG gerado e `git status` / `git diff --check`.
- **Verificação de anonimato e segurança:**
  - Diagrama contém apenas metadados de esquema (nomes de tabelas, colunas e tipos).
- **Atualização do CHANGELOG:** Registrar a atualização do diagrama `docs/der-linketinder.png`.
- **Commit esperado:** `docs: atualiza diagrama MER com recrutadores, curtidas e matches`

---

### Tarefa T-06 — Atualização da Documentação no `README.md`

- **Status:** Concluída
- **Objetivo:** Atualizar o `README.md` para documentar:
  1. O esquema relacional completo com as tabelas `recrutadores`, `curtidas_candidato_vaga`, `curtidas_empresa_candidato` (com `recrutador_id`) e `matches`;
  2. As regras de negócio de curtidas e matches (reciprocidade nas duas ordens via triggers, unicidade por empresa/candidato, exclusão seletiva de vagas, `ON DELETE CASCADE` e preservação histórica `ON DELETE SET NULL` para recrutadores);
  3. O guia de execução e migração dos scripts SQL (`database/database.sql` e `database/queries.sql`) e os exemplos de consultas SQL de demonstração de matches;
  4. A distinção clara entre o que está implementado no banco PostgreSQL e o estado atual das camadas de aplicação.
- **Camada afetada:** Documentação (`README.md`).
- **Arquivos previstos:**
  - `README.md` (modificado)
  - `CHANGELOG.md` (modificado)
  - `docs/specs/match/plano.md` (modificado)
- **Dependências:** T-04, T-05.
- **Requisitos e Regras Atendidos:** `RF-08`, `RF-09`, `CA-11`, `CA-12`.
- **Critérios de conclusão e validação:**
  - `README.md` atualizado, consistente com `database.sql`, `queries.sql` e `docs/der-linketinder.png`, sem trechos obsoletos indicando que as tabelas/triggers de match no banco ainda não existem.
  - Suíte completa de testes existente (`./gradlew test --console=plain` e `cd frontend && npm test`) executada com 100% de aprovação para confirmar zero regressão no repositório.
- **Testes necessários:**
  - `./gradlew test --console=plain`
  - `cd frontend && npx tsc --noEmit && npm test`
  - `git diff --check`
- **Verificação de anonimato e segurança:**
  - Confirmar que nenhum exemplo no `README.md` expõe senhas em texto puro ou dados pessoais reais.
- **Atualização do CHANGELOG:** Registrar a documentação da persistência de curtidas e matches no `README.md`.
- **Commit esperado:** `docs: documenta persistencia de curtidas e matches no README`
