# Changelog

Todas as mudanças relevantes deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/). Cada tarefa de implementação deve atualizar este arquivo.

## [Não lançado]

### Adicionado
- `renderizarCardsRecrutadoresEmpresa` e `renderizarFormularioRecrutador` (senha nunca preenchida, opcional na edição, mensagem de erro escapada), botão `#botao-novo-recrutador` e seção `#lista-recrutadores-empresa` em `renderizarPerfilEmpresa`, com testes Vitest de renderização, escape de HTML e anonimato (Tarefa T-07).
- `Recrutador.ts`, dados iniciais `recrutadores` em `dados.ts`, `recrutadorService.ts` (CRUD em `localStorage` sem persistir senha ou hash e validações `VAL-01` a `VAL-09`), `edicaoRecrutadorService.ts` (bloqueio entre abas `"recrutador-em-edicao"`), exclusão em cascata de recrutadores em `exclusaoEmpresaService.ts` e suítes Vitest `recrutadorService.test.ts`, `edicaoRecrutadorService.test.ts` e `exclusaoEmpresaService.test.ts` no frontend (Tarefa T-06).
- `RecrutadorHttpHandler` (`POST /api/recrutadores`), registro do contexto em `ApiServer.groovy` e suíte de testes de integração HTTP real `RecrutadorHttpHandlerSpec` (Tarefa T-05).
- `RecrutadorController`, integração de opções de recrutador no `Menu` de terminal (listagem por empresa, painel de análise de vagas da empresa e candidatos anônimos e submenu de gerenciamento), injeção manual de dependências em `Main.groovy` e testes Spock `RecrutadorControllerSpec` e `MenuSpec` (Tarefa T-04).
- `RecrutadorService` e `ConflitoDadosException` (validações `VAL-01` a `VAL-09`, unicidade de CPF/e-mail, derivação PBKDF2, consultas de vagas por empresa e candidatos anônimos), suporte a exclusão em cascata em `EmpresaService` e `RecrutadorServiceSpec` (Tarefa T-03).
- `Recrutador`, `RecrutadorResumo`, `SenhaHashUtil` (PBKDF2WithHmacSHA256 com salt individual), `RecrutadorRepository`, `RecrutadorDAO` e `RecrutadorDAOSpec` no backend Groovy (Tarefa T-02).
- `database/database.sql` e `database/queries.sql`: tabela `recrutadores` (relacionamento 1:N com `empresas`, `ON DELETE CASCADE`, `UNIQUE` em `email` e `cpf` e índice em `empresa_id`), dados iniciais com hashes PBKDF2 e consulta de validação sem exposição de senha ou CPF (Tarefa T-01).
- `docs/specs/recrutador/spec.md` e `docs/specs/recrutador/plano.md`: especificação funcional/técnica e plano de implementação incremental da feature Recrutador.
- `AGENTS.md`: instruções do repositório para agentes de IA (stack, estrutura, arquitetura, comandos, convenções, restrições de segurança, Princípio do Anonimato, scripts de banco e fluxo SDD).
- `skills/feature-com-spec/SKILL.md`: skill reutilizável de Spec-Driven Development.
- `CHANGELOG.md`: este arquivo.
- `PROMPTS.md`: registro de rastreabilidade dos prompts do desafio.

### Modificado
- Nada até o momento.

### Corrigido
- `RecrutadorService`, `EmpresaService`, `ConflitoDadosException`, `RecrutadorServiceSpec` e `EmpresaServiceSpec`: correção dos achados `A-01` a `A-08` da auditoria técnica da Tarefa T-03 (derivação PBKDF2 obrigatória para qualquer senha não vazia na atualização sem aceitar hashes externos pré-processados, delegação da exclusão em cascata de recrutadores ao `ON DELETE CASCADE` do PostgreSQL evitando exclusão parcial sem transação, adição de `buscarResumoPorId` e sanitização de `Vaga.empresa` na listagem de vagas do recrutador, lançamento de `IllegalStateException` para dependências ausentes e ampliação dos testes Spock alinhados a `CA-01`..`CA-14`).

### Removido
- Nada até o momento.
