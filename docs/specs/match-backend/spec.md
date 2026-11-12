# Especificação Funcional e Técnica — Integração de Curtidas e Matches no Backend Groovy (PostgreSQL + REST)

- **Feature:** `match-backend`
- **Issue vinculada:** [#5 — [FEATURE] Integrar Curtidas e Matches do backend Groovy com PostgreSQL e REST](https://github.com/nelsonlima1989/Linketinder-Project/issues/5)
- **Branch:** `feature/match-backend`
- **Commit base:** `a3e740a` (`Merge pull request #4 from nelsonlima1989/feature/match-bd`)
- **Camada afetada:** Backend Groovy (`src/main/groovy/com/linketinder/`), DAOs JDBC, Services, API HTTP REST (`ApiServer.groovy`) e Testes Spock (`src/test/groovy/`)
- **Status:** Proposta revisada e pronta para aprovação (T-01)

---

## 1. Visão Geral e Contexto

O Linketinder é um sistema de contratação baseado em afinidade de competências (skills) sob o **Princípio do Anonimato**. Na entrega anterior ([PR #4](https://github.com/nelsonlima1989/Linketinder-Project/pull/4), issue [#3](https://github.com/nelsonlima1989/Linketinder-Project/issues/3)), o banco de dados PostgreSQL foi completamente estruturado com tabelas relacionais (`curtidas_candidato_vaga`, `curtidas_empresa_candidato` e `matches`), constraints de integridade referencial, e **triggers PL/pgSQL** que gerenciam atomicamente a criação e remoção de matches com concorrência segura (`pg_advisory_xact_lock`).

Contudo, a camada de aplicação backend em Groovy permaneceu operando em memória:
- `CurtidaService` e `MatchService` armazenam dados em coleções voláteis (`List<Curtida>`, `List<Match>`).
- Não existem DAOs nem Repositories para curtidas e matches.
- A curtida de empresa não exige nem recebe o `recrutador_id` obrigatório pelo esquema do banco.
- Existem defeitos legados conhecidos em `CurtidaService` (tentativa de leitura de `candidato.idade` inexistente e impressão de CPF/e-mail no stdout).
- A API HTTP (`ApiServer.groovy`) não possui rotas para registrar curtidas nem para consultar matches.

Esta especificação define a integração real do backend Groovy com o PostgreSQL, utilizando os triggers do banco como **fonte única de verdade** para reciprocidade e disponibilizando endpoints REST padronizados sob estrito respeito ao anonimato e segurança de dados pessoais.

---

## 2. Objetivos da Feature

1. **Persistência Real via JDBC com Transacionalidade Consistente:** Substituir o armazenamento volátil em memória de `CurtidaService` e `MatchService` por DAOs parametrizados (`PreparedStatement`), garantindo que a inserção da curtida e a leitura do match gerado ocorram sob o mesmo contexto transacional atômico (com commit conjunto ou rollback integral).
2. **Delegação aos Triggers do Banco:** Aproveitar a inteligência transacional dos triggers PostgreSQL para criação, deduplicação e remoção de matches, eliminando lógica redundante no Groovy.
3. **Identificação do Recrutador Responsável:** Garantir que toda curtida de empresa seja associada a um recrutador pertencente à empresa, em conformidade com a tabela `curtidas_empresa_candidato`.
4. **Endpoints REST Padronizados:** Implementar rotas HTTP em `ApiServer.groovy` para cadastro de curtidas e consulta de matches consolidados com tratamento consistente de erros (400, 404, 409, 500).
5. **Privacidade Rigorosa e Anonimato sem Autenticação:** Como o sistema ainda não implementa autenticação (login/sessão), `GET /api/matches` retornará exclusivamente **IDs e metadados profissionais não sensíveis**, suprimindo terminantemente dados de contato pessoal (e-mail, WhatsApp/telefone, CEP) além de credenciais (`senha`) e documentos fiscais (`cpf`, `cnpj`).
6. **Cobertura Automatizada de Testes:** Desenvolver testes unitários e de integração com Spock cobrindo DAOs, Services, Handlers HTTP, cenários de privacidade, rollback transacional e erros SQL, garantindo 100% de aprovação e zero regressão.

---

## 3. Escopo e Exclusões

### 3.1 Escopo Incluído
- **Modelos e DTOs:**
  - Criação de DTOs seguros de transferência para requests e responses de curtidas e matches (`CurtidaCandidatoVagaRequest`, `CurtidaEmpresaCandidatoRequest`, `CurtidaResponse`, `MatchResponse`, `MatchCandidatoResponse`, `MatchEmpresaResponse`, `MatchRecrutadorResponse`).
  - DTOs de apresentação estritamente desprovidos de contatos pessoais (`email`, `whatsapp`), documentos (`cpf`, `cnpj`, `cep`) e credenciais (`senha`).
  - Evolução dos modelos `Curtida` e `Match` para refletir as tabelas do banco e o vínculo com `recrutador_id`.
- **Repositories e DAOs JDBC:**
  - Criação de `CurtidaRepository` e `CurtidaDAO` com suporte a execução transacional (`Connection`).
  - Criação de `MatchRepository` e `MatchDAO`.
- **Camada de Serviço:**
  - Refatoração de `CurtidaService` para registrar curtidas no banco com transação consistente, verificando de forma atômica se a operação resultou em match.
  - Refatoração de `MatchService` para consultar matches a partir do banco de dados, com suporte a filtros por candidato ou empresa.
  - Remoção de logs sensíveis e correção da propriedade inexistente `candidato.idade`.
- **API HTTP REST (`ApiServer.groovy`):**
  - `POST /api/curtidas/candidato-vaga`: registro de curtida de candidato.
  - `POST /api/curtidas/empresa-candidato`: registro de curtida de empresa informando o recrutador.
  - `GET /api/matches`: consulta de matches consolidados (com query parameters opcionais `candidatoId` e `empresaId`).
- **Testes Automatizados:**
  - Testes unitários com Spock e mocks JDBC (incluindo testes de privacidade, rollback e tratamento de erros SQL).
  - Testes de API HTTP com Spock para validação de contratos, status codes e payloads JSON.
  - Testes de integração em PostgreSQL descartável (`pg_virtualenv`).

### 3.2 Exclusões de Escopo (Estritas)
- **Frontend TypeScript (`frontend/**`):** Não será alterado nesta feature. O frontend permanece desacoplado e operando em `localStorage`.
- **Interface de Terminal (`Menu.groovy`):** Não será alterada nesta feature. A compatibilidade do código existente que invoca `CurtidaService` e `MatchService` será mantida por sobrecarga/default parameters para não quebrar `Main.groovy`.
- **Estrutura do Banco de Dados (`database/database.sql`):** O DDL e os triggers já foram consolidados na issue #3; nenhuma alteração estrutural no banco é necessária.
- **Sistema de Autenticação / Login:** Autenticação baseada em JWT, OAuth ou sessão de login **não faz parte do escopo** desta entrega.

---

## 4. Decisões Técnicas de Projeto (DP)

### DP-01 — Triggers PostgreSQL como Fonte Única de Verdade
- **Decisão:** O backend Groovy **não duplicará** o algoritmo de cálculo de reciprocidade ou verificação de matches concorrentes.
- **Mecanismo:** Ao registrar uma curtida, o `CurtidaDAO` executa o `INSERT`. O trigger do PostgreSQL cria atomicamente o registro na tabela `matches` se houver reciprocidade. Em seguida, na mesma conexão/transação, o service consulta se existe match ativo para o par `(empresa_id, candidato_id)` e retorna essa informação no payload de resposta (`match: true`, `matchId: X`).

### DP-02 — Vínculo Obrigatório de Recrutador na Curtida da Empresa
- **Decisão:** O payload de `POST /api/curtidas/empresa-candidato` exige obrigatoriamente `empresaId`, `candidatoId` e `recrutadorId`.
- **Validação:** A integridade é garantida em dois níveis: validação básica de campos obrigatórios no HTTP Handler e validação estrita no banco pelo trigger `trg_validar_curtida_empresa_recrutador`. Violação de pertencimento do recrutador resulta em HTTP 400 Bad Request.

### DP-03 — Anonimato Estrito e Segurança de Dados em Respostas Públicas (Sem Autenticação)
- **Decisão:** Enquanto não houver autenticação implementada no sistema, o endpoint `GET /api/matches` retornará exclusivamente **identificadores e metadados profissionais não sensíveis**, preservando o anonimato de candidatos, empresas e recrutadores.
- **Omissão terminante de nomes e contatos pessoais:** É expressamente proibido retornar campos como `nome`, `sobrenome`, `email`, `whatsapp`, `telefone`, `cep`, `cpf`, `cnpj`, `dataNascimento` ou `senha`.
- **Estrutura segura do retorno:**
  - **Match:** `id`, `dataMatch`.
  - **Empresa:** `id`, `pais`, `estado`, `descricaoEmpresarial` (sem `nome`, sem contatos e sem credenciais).
  - **Candidato:** `id`, `estado`, `descricaoPessoal`, `skills` (lista de nomes de competências, sem `nome` e sem contatos).
  - **Recrutador:** `id`, `areaRecrutamento` (sem `nome` e sem contatos). Caso `recrutador_id` seja `NULL` (devido a `ON DELETE SET NULL`), o campo `recrutador` é retornado como `null`.
- **Curtidas isoladas:** Não haverá endpoint público para listar curtidas unilaterais, evitando que qualquer usuário descubra quem curtiu quem antes do match mútuo.

### DP-04 — Tratamento Consistente de Erros HTTP
- **Decisão:** Mapear exceções de persistência e domínio para os códigos HTTP canônicos:
  - `400 Bad Request`: JSON malformado, campos ausentes ou com tipos incompatíveis, IDs menores ou iguais a zero, ou erro de trigger de negócio (ex.: recrutador não pertence à empresa).
  - `404 Not Found`: Candidato, empresa, vaga ou recrutador inexistente na tentativa de curtida.
  - `409 Conflict`: Curtida já existente para aquele par candidato-vaga ou empresa-candidato (violação de chave primária).
  - `413 Payload Too Large` e `415 Unsupported Media Type`: mantidos os padrões de `HttpRequestUtil`.
  - `500 Internal Server Error`: Falha inesperada de conexão ou execução de banco sem vazamento de stacktrace ou SQL interno no corpo da resposta.

### DP-05 — Transação JDBC Consistente e Atômica (INSERT + Leitura de Match)
- **Decisão:** A inserção da curtida e a verificação imediata de formação de match serão executadas sob o mesmo controle transacional explícito (`Connection` com `autoCommit = false`).
- **Garantia:**
  1. A conexão executa o `INSERT` da curtida (acionando os triggers PL/pgSQL).
  2. Na mesma transação, consulta se o par `(empresa_id, candidato_id)` gerou linha na tabela `matches`.
  3. Caso ambas as operações tenham êxito, executa `commit()`.
  4. Caso ocorra qualquer `SQLException` ou erro de integridade, executa `rollback()` imediato, garantindo ausência de inconsistência ou estado parcial no banco.

---

## 5. Requisitos Funcionais (RF)

- **RF-01 — Persistir Curtida de Candidato em Vaga (JDBC Transacional):** Inserir registro na tabela `curtidas_candidato_vaga (candidato_id, vaga_id, data_curtida)`. Rejeitar duplicidade via chave primária.
- **RF-02 — Persistir Curtida de Empresa em Candidato (JDBC Transacional):** Inserir registro na tabela `curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id, data_curtida)`. Rejeitar duplicidade via chave primária e validar que o recrutador pertence à empresa.
- **RF-03 — Consultar Matches Persistidos sob Anonimato Estrito (JDBC):** Consultar registros na tabela `matches` com `JOIN` nas tabelas `candidatos`, `empresas` e opcionalmente `recrutadores` (left join, suportando `recrutador_id NULL`), além de carregar as competências do candidato, retornando estritamente IDs e metadados profissionais não sensíveis (sem nomes, sem contatos pessoais, sem CPF, sem CNPJ, sem senhas).
- **RF-04 — Endpoint REST `POST /api/curtidas/candidato-vaga`:** Receber `{ "candidatoId": Long, "vagaId": Long }`, validar campos, persistir atomicamente e retornar `201 Created` contendo os dados da curtida e a indicação se gerou match (`match: boolean`, `matchId: Long?`).
- **RF-05 — Endpoint REST `POST /api/curtidas/empresa-candidato`:** Receber `{ "empresaId": Long, "candidatoId": Long, "recrutadorId": Long }`, validar campos, persistir atomicamente e retornar `201 Created` contendo os dados da curtida e o status de match.
- **RF-06 — Endpoint REST `GET /api/matches`:** Retornar lista JSON de matches consolidados com suporte a filtros opcionais por query parameters (`?candidatoId=X` e `?empresaId=Y`), respeitando estritamente a política de privacidade da DP-03.
- **RF-07 — Retorno Imediato de Match Recíproco:** Após a inserção bem-sucedida de qualquer curtida, o retorno deve informar imediatamente ao cliente se houve formação de match (`match: true` com ID do match ou `match: false`).
- **RF-08 — Limpeza de Defeitos Legados no Serviço:** Eliminar prints com dados sensíveis e acessos a atributos inexistentes (`candidato.idade`) em `CurtidaService`.

---

## 6. Requisitos Não Funcionais (RNF)

- **RNF-01 — Segurança de Credenciais e PII (Privacidade por Padrão):** Omissão absoluta de contatos pessoais (`email`, `whatsapp`, `telefone`, `cep`), documentos fiscais (`cpf`, `cnpj`) e credenciais (`senha`, hashes) nos corpos de resposta de matches e curtidas enquanto não houver camada de autenticação.
- **RNF-02 — Prevenção de SQL Injection:** Utilização estrita de `PreparedStatement` parametrizado em todas as instruções SQL de DAOs.
- **RNF-03 — Atomicidade e Resiliência Transacional:** Controle de transação explícito (`autoCommit = false`, `commit()`, `rollback()` em bloco `try/catch/finally`).
- **RNF-04 — Tipagem Estática e Convenções Groovy:** Emprego de tipos explícitos em classes, métodos e assinaturas do backend, evitando o uso de `def` quando o tipo é determinado.
- **RNF-05 — Zero Novas Dependências Externas:** Não adicionar novos frameworks ou bibliotecas no `build.gradle`, utilizando os recursos existentes do JDK, Groovy e Spock.
- **RNF-06 — Cobertura Rigorosa de Testes Automatizados:** Testes unitários com Spock mockando JDBC e conexões (incluindo testes de privacidade, rollback e erros SQL), e testes integrados validando o ciclo completo sobre o PostgreSQL descartável.

---

## 7. Critérios de Aceitação (CA)

- **CA-01 (Curtida Candidato Vaga com Sucesso):** Envio de `POST /api/curtidas/candidato-vaga` com candidato e vaga válidos persiste em `curtidas_candidato_vaga` e retorna HTTP 201 com payload `{ "candidatoId": ..., "vagaId": ..., "match": false }` (ou `true` se a empresa já havia curtido).
- **CA-02 (Curtida Empresa Candidato com Sucesso):** Envio de `POST /api/curtidas/empresa-candidato` com empresa, candidato e recrutador válidos da mesma empresa persiste em `curtidas_empresa_candidato` e retorna HTTP 201.
- **CA-03 (Rejeição de Recrutador de Outra Empresa):** Envio de `POST /api/curtidas/empresa-candidato` com recrutador pertencente a outra empresa retorna HTTP 400 com mensagem explicativa originada do trigger PostgreSQL.
- **CA-04 (Rejeição de Curtida Duplicada):** Envio repetido de curtida idêntica retorna HTTP 409 Conflict.
- **CA-05 (Rejeição de Recursos Inexistentes):** Curtida referenciando ID inexistente de candidato, vaga, empresa ou recrutador retorna HTTP 404 Not Found.
- **CA-06 (Detecção e Notificação Automática de Match):** Quando a segunda curtida for inserida estabelecendo reciprocidade, a resposta HTTP 201 contém `"match": true` e o `"matchId"` gerado pelo trigger.
- **CA-07 (Consulta de Matches Segura sob Anonimato Estrito):** `GET /api/matches` retorna HTTP 200 com array de matches contendo apenas IDs, estados, descrições e competências. Os campos `nome`, `sobrenome`, `email`, `whatsapp`, `telefone`, `cep`, `cpf`, `cnpj` e `senha` estão estritamente ausentes do JSON retornado.
- **CA-08 (Filtros de Consulta de Matches):** `GET /api/matches?candidatoId=1` retorna apenas matches do candidato 1; `GET /api/matches?empresaId=2` retorna apenas matches da empresa 2.
- **CA-09 (Match com Recrutador Nulo):** Matches cujo recrutador tenha sido excluído do banco (`recrutador_id = NULL`) são serializados com campo `recrutador: null` sem falhas.
- **CA-10 (Regressão e Cobertura):** Todos os 277 testes de backend existentes continuam passando sem falhas após a introdução dos novos DAOs e refatoração dos services.
- **CA-11 (Atomicidade Transacional e Rollback):** Em caso de falha no fluxo de curtida (seja no `INSERT`, no disparo de trigger ou na consulta subsequente do match), a transação JDBC sofre rollback completo, garantindo que nenhuma curtida ou estado parcial seja gravado no banco.
- **CA-12 (Tratamento de Erros e Resiliência SQL):** Simulações de indisponibilidade de conexão, violações de integridade referencial ou falhas SQL resultam em códigos HTTP apropriados (400, 404, 409, 500) com respostas JSON padronizadas e sem vazamento de stacktrace ou detalhes de infraestrutura.

---

## 8. Contratos de API REST

### 8.1 `POST /api/curtidas/candidato-vaga`
**Request:**
```json
{
  "candidatoId": 1,
  "vagaId": 2
}
```
**Response (201 Created):**
```json
{
  "candidatoId": 1,
  "vagaId": 2,
  "dataCurtida": "2026-11-12T18:00:00",
  "match": true,
  "matchId": 4,
  "mensagem": "Curtida registrada com sucesso."
}
```

### 8.2 `POST /api/curtidas/empresa-candidato`
**Request:**
```json
{
  "empresaId": 1,
  "candidatoId": 2,
  "recrutadorId": 1
}
```
**Response (201 Created):**
```json
{
  "empresaId": 1,
  "candidatoId": 2,
  "recrutadorId": 1,
  "dataCurtida": "2026-11-12T18:00:00",
  "match": true,
  "matchId": 5,
  "mensagem": "Curtida registrada com sucesso."
}
```

### 8.3 `GET /api/matches`
**Query Parameters (opcionais):** `candidatoId` (número), `empresaId` (número).  
**Regra de Anonimato:** Retorna somente IDs e metadados profissionais; zero nomes e zero contatos pessoais (`email`, `whatsapp`, etc.) enquanto não houver autenticação.  
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "dataMatch": "2026-11-12T18:00:00",
    "candidato": {
      "id": 1,
      "estado": "SP",
      "descricaoPessoal": "Desenvolvedor Backend",
      "skills": ["Java", "Groovy", "PostgreSQL"]
    },
    "empresa": {
      "id": 1,
      "pais": "Brasil",
      "estado": "SP",
      "descricaoEmpresarial": "Empresa de tecnologia e inovação"
    },
    "recrutador": {
      "id": 1,
      "areaRecrutamento": "Tecnologia"
    }
  }
]
```
*(Nota: se `recrutador_id` for `NULL`, o campo `recrutador` é serializado como `null`)*
