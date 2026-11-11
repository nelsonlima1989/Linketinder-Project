# PROMPTS.md — Rastreabilidade dos prompts

## Objetivo

Registrar, em ordem, os prompts usados com agentes de IA no desafio da feature **Recrutador**, os resultados obtidos e as decisões tomadas, separando decisões aprovadas de pendências.

## Contexto do desafio

Preparar o Linketinder para desenvolvimento orientado por especificações (SDD) com agentes de IA e, depois, implementar o **Recrutador** (colaborador de uma empresa; uma empresa tem N recrutadores). Atributos solicitados: nome, área de recrutamento, descrição, e-mail, CPF, data de nascimento e senha. A implementação deve respeitar a arquitetura existente e o **Princípio do Anonimato**.

## Sequência de etapas

| # | Etapa | Estado |
|---|---|---|
| 1 | Auditoria técnica (somente leitura) | Concluída |
| 2 | Preparação do repositório para SDD (este registro) | Em revisão — aguardando commit |
| 3 | Revisão e commit dos artefatos | Pendente |
| 4 | Issue e branch `feature/recrutador` | Pendente |
| 5 | Levantamento e resolução de dúvidas de negócio | Pendente |
| 6 | `docs/specs/recrutador/spec.md` e validação com o Tutor | Pendente |
| 7 | `docs/specs/recrutador/plano.md` | Pendente |
| 8 | Implementação incremental (um commit por tarefa) | Pendente |
| 9 | Validação final e Pull Request | Pendente |

---

## Prompt 1 — Auditoria técnica (Etapa 01)

- **Propósito:** auditoria exclusivamente de leitura do repositório, sem alterações, para levantar backend, PostgreSQL, frontend, domínio, curtidas e matches, anonimato, segurança, testes e artefatos SDD, com relatório em 14 seções (resumo, estrutura, backend, banco, frontend, persistência, regras de domínio, curtidas/matches, anonimato, testes, impactos, riscos, recomendações, perguntas pendentes).
- **Restrições:** nenhum arquivo, banco ou estado do Git podia ser alterado; sem HTTP/REST/sincronização novos; sem expor credenciais.
- **Resultado:**
  - Nenhuma alteração foi feita; o Git permaneceu limpo (`main` @ `cff86b9`).
  - O PostgreSQL **não foi consultado**: o esquema foi levantado apenas dos scripts SQL.
  - Os testes **não foram executados**; as contagens foram estáticas.
  - Divergências encontradas em relação ao contexto informado:
    1. Existe API HTTP no backend (`POST /api/candidatos|empresas|vagas`), que o frontend não consome.
    2. Existem tabelas de curtidas nos scripts SQL.
    3. Existem curtidas e matches **em memória** no backend.
  - Defeitos prováveis no fluxo de curtida/match, **por leitura estática, não confirmados por execução**.
  - Anonimato **parcial** e riscos de segurança legados (senhas em texto puro, sem autenticação, dados pessoais em logs).

<details>
<summary>Transcrição integral — Prompt 1 (Etapa 01)</summary>

```markdown
# PROMPT — LINKETINDER | ETAPA 01: AUDITORIA TÉCNICA E LEVANTAMENTO PARA A FEATURE RECRUTADOR

## 1. Papel do agente

Atue como um **Engenheiro de Software Sênior**, especializado em:

- Groovy, Java e programação orientada a objetos.
- PostgreSQL e modelagem relacional.
- TypeScript e aplicações frontend.
- Arquitetura de software, SOLID e Clean Code.
- Testes automatizados com Spock.
- Spec-Driven Development (SDD).
- Desenvolvimento assistido por agentes de Inteligência Artificial.
- Análise de requisitos, rastreabilidade e preservação de funcionalidades existentes.

Sua responsabilidade nesta primeira etapa é realizar uma **auditoria técnica rigorosa do projeto Linketinder**, preparando informações confiáveis para o desenvolvimento da funcionalidade **Recrutador**.

**ATENÇÃO: ESTA ETAPA É EXCLUSIVAMENTE DE LEITURA, INSPEÇÃO E ANÁLISE. NÃO IMPLEMENTE NADA.**

---

## 2. Localização do projeto

O repositório está localizado em:

`/home/nelson/IdeaProjects/linketinder`

Antes de iniciar:

1. Confirme a existência do diretório.
2. Identifique a raiz do repositório Git.
3. Identifique os diretórios de backend e frontend.
4. Verifique o estado atual do Git, incluindo branch ativa e alterações não commitadas.
5. Inspecione arquivos de configuração, documentação, scripts e testes existentes.

Não acesse projetos externos ao diretório informado, exceto dependências e ferramentas necessárias para uma inspeção estritamente de leitura.

Não altere arquivos existentes.

---

## 3. Contexto arquitetural conhecido

O projeto Linketinder possui, inicialmente, a seguinte arquitetura:

### Backend

- Linguagem principal: Groovy.
- Persistência: PostgreSQL.
- Implementação de entidades, DAOs, serviços e regras de negócio.
- Testes automatizados com Spock, quando disponíveis.

### Frontend

- Linguagem: TypeScript.
- Persistência local utilizando `localStorage`.
- Funcionalidades implementadas independentemente do backend.

### Integração

Atualmente, frontend e backend **não possuem integração HTTP entre si**.

A decisão arquitetural preliminar é manter essa separação durante o desafio.

A funcionalidade Recrutador deverá ser desenvolvida posteriormente nas duas camadas, respeitando os mecanismos de persistência e a arquitetura já existentes.

**Não criar integração HTTP, API REST ou mecanismos de sincronização como parte desta tarefa.**

Todas essas informações devem ser verificadas no código. Se alguma estiver incorreta, apresente evidências e registre a divergência.

---

## 4. Objetivo do desafio

O desafio consiste em preparar o Linketinder para um fluxo de desenvolvimento orientado por especificações e agentes de IA e, posteriormente, implementar a funcionalidade **Recrutador**.

O Recrutador representa um colaborador associado a uma empresa.

Os atributos solicitados são:

- Nome.
- Área de recrutamento.
- Descrição.
- E-mail.
- CPF.
- Data de nascimento.
- Senha.

Uma empresa pode possuir múltiplos recrutadores.

A implementação deverá respeitar as regras de negócio, os padrões arquiteturais existentes e, principalmente, o **Princípio do Anonimato**.

O enunciado também menciona a possibilidade de o recrutador analisar vagas, visualizar perfis de candidatos e curtir candidatos quando a funcionalidade de curtidas já estiver implementada.

Não assuma regras de negócio que não estejam documentadas.

---

## 5. Decisões e restrições já estabelecidas

### 5.1 Separação entre frontend e backend

Não implementar comunicação HTTP entre as camadas.

O backend continuará utilizando PostgreSQL.

O frontend continuará utilizando `localStorage`.

As duas implementações deverão preservar regras de negócio equivalentes, dentro das possibilidades de cada camada.

Essa decisão deverá ser documentada posteriormente na especificação e no plano de implementação.

### 5.2 Curtidas e matches

Foi informado que as funcionalidades de **curtidas e matches ainda não foram implementadas no banco de dados nem no frontend**.

Sua responsabilidade é confirmar essa informação no repositório, incluindo possíveis implementações parciais ou exclusivamente em memória no backend.

Não implemente curtidas ou matches nesta etapa.

O enunciado condiciona a capacidade de curtir candidatos à existência prévia da funcionalidade de curtidas.

Portanto:

- Não trate curtidas como uma dependência obrigatória sem justificativa.
- Não trate matches como uma funcionalidade obrigatória independente.
- Identifique eventuais estruturas já existentes.
- Registre as lacunas encontradas.
- Aponte as decisões que precisam ser confirmadas com o Tutor antes da implementação.

Se houver alguma contradição entre o código, o enunciado e essas informações, apresente-a claramente.

### 5.3 Princípio do Anonimato

O anonimato é uma restrição fundamental do desafio.

Investigue como o projeto protege atualmente a identidade dos candidatos e empresas.

Identifique:

- Quais informações podem ser visualizadas antes de um eventual match.
- Quais informações são consideradas identificadoras.
- Onde existem verificações de acesso ou filtros.
- Se as regras estão implementadas, parcialmente implementadas ou ausentes.
- Quais riscos a introdução do Recrutador pode causar.

Não invente regras de liberação de identidade após o match.

Se não houver uma definição explícita, registre a questão como pendente de decisão.

### 5.4 Segurança de dados

A feature envolve CPF, e-mail, data de nascimento e senha.

Analise as práticas existentes de validação, armazenamento e proteção desses dados.

Identifique riscos relacionados a:

- Senhas armazenadas em texto puro.
- Exposição de CPF e data de nascimento.
- Dados sensíveis persistidos no `localStorage`.
- Exposição de informações em logs.
- Falta de validação de entradas.
- Ausência de controle de acesso.

Não implemente mecanismos de segurança nesta etapa. Apenas apresente os problemas e as recomendações técnicas para a futura especificação.

---

## 6. Auditoria do backend

Inspecione a implementação Groovy e identifique:

1. Organização dos pacotes e diretórios.
2. Modelos e entidades existentes.
3. Relacionamentos entre candidatos, empresas e vagas.
4. Implementações de DAO.
5. Serviços e regras de negócio.
6. Controllers, endpoints ou interfaces existentes, caso existam.
7. Padrões de validação e tratamento de erros.
8. Estratégias de persistência.
9. Dependências e configurações do Gradle.
10. Testes automatizados e sua organização.

Investigue especificamente como o Recrutador poderá ser associado à Empresa utilizando os padrões já adotados.

Não proponha uma nova arquitetura sem demonstrar a necessidade.

---

## 7. Auditoria do PostgreSQL

Inspecione scripts SQL, configurações e mecanismos de acesso ao banco.

Identifique:

- Tabelas existentes.
- Chaves primárias e estrangeiras.
- Relacionamentos.
- Restrições de integridade.
- Índices.
- Campos obrigatórios.
- Estratégias de criação e atualização do esquema.
- Scripts de inicialização e dados de exemplo.

Verifique se existe alguma estrutura relacionada a recrutadores, curtidas ou matches.

Caso o acesso ao banco esteja disponível e autorizado, utilize exclusivamente consultas de leitura.

**É proibido executar CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, TRUNCATE ou migrações.**

Não exiba credenciais ou informações pessoais reais no relatório.

Se o banco não estiver acessível, diferencie claramente a estrutura encontrada nos scripts da estrutura efetivamente confirmada no PostgreSQL.

---

## 8. Auditoria do frontend

Inspecione a aplicação TypeScript e identifique:

1. Estrutura de diretórios.
2. Modelos e interfaces.
3. Componentes e páginas.
4. Formulários existentes.
5. Cadastro e edição de candidatos.
6. Cadastro e edição de empresas.
7. Representação e visualização de vagas.
8. Validações existentes.
9. Uso do `localStorage`.
10. Estratégias de identificação e associação entre registros.
11. Navegação e organização da interface.
12. Testes automatizados, caso existam.

Avalie como a feature Recrutador poderá ser incorporada sem introduzir dependências desnecessárias.

Identifique especialmente quais funcionalidades existentes poderão ser reutilizadas.

Não crie componentes, páginas ou modelos nesta etapa.

---

## 9. Análise de domínio

Examine as regras de negócio existentes e determine:

- Como uma empresa é identificada.
- Como candidatos são identificados.
- Como vagas são associadas às empresas.
- Se há autenticação ou identificação de usuário.
- Se há controle de permissões.
- Se há distinção entre empresa e seus colaboradores.
- Se há regras de exclusão e atualização de registros relacionados.

Para a futura feature Recrutador, avalie os impactos de um relacionamento:

**Empresa (1) → (N) Recrutadores**

Investigue também quais decisões precisam ser tomadas sobre:

- Cadastro de recrutadores.
- Vínculo e desvinculação de empresas.
- Exclusão de recrutadores.
- Unicidade de e-mail e CPF.
- Validação de nascimento.
- Requisitos de senha.
- Escopo das vagas acessíveis.
- Visualização de candidatos.
- Visibilidade de informações identificadoras.

Não transforme recomendações em requisitos aprovados.

---

## 10. Análise de curtidas e matches

Verifique separadamente a existência das funcionalidades no:

- Backend Groovy.
- Banco PostgreSQL.
- Frontend TypeScript.

Considere implementações em memória, código incompleto, testes e estruturas não utilizadas.

Classifique cada funcionalidade como:

- IMPLEMENTADA.
- PARCIALMENTE IMPLEMENTADA.
- NÃO IMPLEMENTADA.
- NÃO VERIFICÁVEL.

Explique o impacto de cada situação sobre a feature Recrutador.

O objetivo é identificar dependências reais, não aumentar artificialmente o escopo do desafio.

---

## 11. Testes e qualidade

Investigue:

- Testes Spock existentes.
- Cobertura de serviços e DAOs.
- Testes de validação.
- Testes de persistência.
- Testes do frontend.
- Scripts de execução.
- Dependências necessárias.
- Possíveis testes de regressão.

Identifique quais testes deverão ser criados posteriormente para validar:

- Cadastro de recrutador.
- Associação com empresa.
- Relacionamento 1:N.
- Validações.
- Consulta e atualização de dados.
- Exclusão e integridade referencial.
- Anonimato.
- Preservação das funcionalidades existentes.

Não crie testes nesta etapa.

Não execute comandos que alterem arquivos, bancos de dados ou o estado do repositório.

---

## 12. Preparação para Spec-Driven Development

Verifique se o repositório já possui:

- `AGENTS.md`
- `CHANGELOG.md`
- `PROMPTS.md`
- `skills/feature-com-spec/SKILL.md`
- `docs/specs/`
- Documentação arquitetural.
- Convenções de commits.
- Instruções de execução.
- Scripts de banco documentados.

Identifique quais artefatos precisam ser criados ou atualizados para atender ao desafio.

O futuro fluxo deverá seguir esta ordem:

1. Preparar AGENTS.md, skill e CHANGELOG.md.
2. Revisar e commitar esses artefatos.
3. Criar a issue e a branch `feature/recrutador`.
4. Levantar e resolver dúvidas de negócio.
5. Elaborar `docs/specs/recrutador/spec.md`.
6. Validar a especificação com o Tutor.
7. Committar a especificação antes de implementar código.
8. Elaborar `docs/specs/recrutador/plano.md`.
9. Implementar uma tarefa pequena por vez.
10. Revisar o diff, executar testes, verificar anonimato e atualizar plano e changelog.
11. Criar um commit por tarefa.
12. Realizar validação final e preparar o Pull Request.

**Não execute nenhuma dessas ações agora. Apenas verifique o estado atual e os requisitos necessários.**

---

## 13. Regras obrigatórias durante a auditoria

É expressamente proibido:

- Criar, editar ou excluir arquivos.
- Implementar código.
- Criar tabelas ou alterar o banco.
- Instalar dependências.
- Executar migrações.
- Criar branches.
- Criar issues.
- Criar commits.
- Fazer push.
- Criar Pull Requests.
- Reestruturar diretórios.
- Alterar configurações.
- Gerar arquivos de documentação.
- Expor credenciais.
- Executar testes ou builds que produzam alterações persistentes.

Se uma verificação depender de uma operação que modifique o ambiente, informe a limitação e solicite autorização antes de prosseguir.

Não execute ações destrutivas.

---

## 14. Formato obrigatório do relatório

Ao finalizar a auditoria, apresente um relatório estruturado com as seguintes seções:

### 1. Resumo executivo
Estado geral do projeto e preparação para a feature Recrutador.

### 2. Estrutura do repositório
Diretórios principais, módulos e arquivos relevantes.

### 3. Arquitetura do backend
Modelos, DAOs, serviços, dependências e padrões.

### 4. Estrutura do PostgreSQL
Tabelas, relacionamentos, scripts e limitações de verificação.

### 5. Arquitetura do frontend
Componentes, modelos, formulários e organização.

### 6. Persistência
Como os dados são armazenados e manipulados em cada camada.

### 7. Regras de domínio existentes
Candidatos, empresas, vagas e relacionamentos.

### 8. Curtidas e matches
Estado de implementação por camada, com evidências.

### 9. Princípio do Anonimato
Regras encontradas, lacunas e riscos.

### 10. Testes automatizados
Frameworks, testes existentes e lacunas.

### 11. Impactos da feature Recrutador
Componentes, classes, serviços, tabelas e interfaces potencialmente afetados.

### 12. Riscos e inconsistências
Problemas arquiteturais, de segurança, integridade e compatibilidade.

### 13. Recomendações técnicas
Estratégia sugerida para implementação futura, sem executar alterações.

### 14. Perguntas pendentes
Questões que precisam ser respondidas pelo responsável pelo projeto ou pelo Tutor.

Para cada descoberta relevante, informe:

- **Status:** CONFIRMADO, PARCIAL, INEXISTENTE ou PENDENTE.
- **Evidência:** caminho do arquivo e, quando possível, classe, método ou intervalo de linhas.
- **Impacto:** consequência para a feature Recrutador.
- **Recomendação:** ação sugerida, quando aplicável.

Diferencie sempre fatos observados, hipóteses e recomendações.

---

## 15. Critérios de conclusão

A auditoria será considerada concluída somente quando:

- A arquitetura existente estiver documentada no relatório.
- A separação entre backend e frontend tiver sido verificada.
- O estado real das funcionalidades de curtidas e matches estiver identificado.
- As regras de anonimato tiverem sido investigadas.
- As dependências da feature Recrutador estiverem mapeadas.
- Os artefatos necessários ao SDD estiverem identificados.
- As dúvidas que dependem do Tutor estiverem explicitadas.
- Nenhuma alteração tiver sido realizada no repositório ou banco.

Ao final, apresente também o estado do Git para demonstrar que a auditoria não introduziu modificações, distinguindo alterações preexistentes de eventuais alterações novas.

**PARE APÓS ENTREGAR O RELATÓRIO.**

Não inicie a criação de AGENTS.md, CHANGELOG.md, skill, especificação ou implementação.

Aguarde minha revisão e autorização explícita para a próxima etapa.
```

</details>

---

## Prompt 2 — Preparação SDD (Etapa 02)

- **Propósito:** criar somente `AGENTS.md`, `skills/feature-com-spec/SKILL.md`, `CHANGELOG.md` e `PROMPTS.md`, sem alterar código, testes, SQL ou comportamento, sem commit/branch/issue/PR e sem criar spec, plano ou qualquer parte do Recrutador.
- **Resultado:** os quatro arquivos foram criados e estão **sem commit, aguardando revisão**. Antes de documentar, as afirmações da auditoria foram validadas nos arquivos reais.

<details>
<summary>Transcrição integral — Prompt 2 (Etapa 02)</summary>

```markdown
# LINKETINDER — ETAPA 02: PREPARAÇÃO DO REPOSITÓRIO PARA SDD

## 1. Papel e responsabilidade

Atue como Engenheiro de Software Sênior especializado em Groovy, PostgreSQL, TypeScript, arquitetura de software, testes automatizados e Spec-Driven Development (SDD).

Estamos desenvolvendo o desafio Linketinder com auxílio de agentes de Inteligência Artificial.

A auditoria técnica inicial já foi realizada e aprovada como levantamento preliminar.

Sua responsabilidade nesta etapa é **preparar os artefatos de orientação, documentação e rastreabilidade do desenvolvimento**, sem modificar o comportamento da aplicação.

Trabalhe exclusivamente no repositório:

`/home/nelson/IdeaProjects/linketinder`

Antes de executar qualquer alteração, confirme a raiz Git, a branch atual e o estado do working tree.

Se houver alterações preexistentes, não as sobrescreva. Informe a situação antes de continuar.

## 2. Contexto técnico identificado na auditoria

### Backend
- Groovy 5 com Gradle.
- PostgreSQL com JDBC.
- Organização em Model, Repository, DAO, Service, Controller e View.
- API HTTP utilizando `HttpServer` do JDK.
- Testes automatizados com Spock.
- JaCoCo para cobertura.

### Frontend
- TypeScript com Vite.
- Persistência utilizando `localStorage`.
- Componentes, controllers, models e services.
- Testes automatizados com Vitest.
- Não consome a API HTTP do backend.

### Arquitetura
- Backend e frontend possuem persistências independentes.
- Não há integração HTTP entre as duas camadas.
- A feature Recrutador será implementada posteriormente em ambas, respeitando a arquitetura existente.
- Não está prevista a criação de integração frontend/backend para este desafio.

### Curtidas e matches
- Existem tabelas de curtidas nos scripts PostgreSQL.
- Existem serviços de curtidas e matches em memória no backend.
- O frontend não possui essas funcionalidades.
- A implementação atual é parcial e apresenta possíveis defeitos.
- A inclusão ou correção dessas funcionalidades no escopo do Recrutador ainda depende de decisão.

### Segurança e anonimato
A auditoria identificou:
- Implementação parcial do anonimato.
- Possível exposição do nome de empresas antes do match.
- Referências a entidades completas em objetos destinados à apresentação anônima.
- Senhas armazenadas em texto puro.
- Ausência de autenticação e autorização.

Essas situações deverão ser registradas como riscos existentes, sem corrigi-las nesta etapa.

**Valide as informações acima nos arquivos reais antes de documentá-las.**

Não afirme que o banco real foi inspecionado: a auditoria anterior examinou scripts SQL, mas não confirmou o esquema diretamente no PostgreSQL.

---

## 3. Objetivo desta etapa

Criar ou atualizar exclusivamente os seguintes artefatos:

1. `AGENTS.md`
2. `skills/feature-com-spec/SKILL.md`
3. `CHANGELOG.md`
4. `PROMPTS.md`

Não criar ainda:

- `docs/specs/recrutador/spec.md`
- `docs/specs/recrutador/plano.md`
- Models, DAOs, Services ou Controllers de Recrutador.
- Componentes frontend.
- Tabelas SQL.
- Endpoints HTTP.
- Testes da feature.

Não criar issue, branch ou Pull Request.

---

## 4. AGENTS.md — Instruções do repositório

Crie um `AGENTS.md` na raiz do projeto.

O documento deverá conter:

### 4.1 Visão geral
- Objetivo do Linketinder.
- Domínio de candidatos, empresas, vagas, competências, curtidas e matches.
- Estado real de implementação das funcionalidades.
- Papel da futura feature Recrutador.

### 4.2 Stack
Documentar versões e ferramentas verificadas em `build.gradle`, `package.json` e demais configurações.

### 4.3 Estrutura
Apresentar os principais diretórios do backend, frontend, banco, testes e documentação.

### 4.4 Arquitetura
Explicar o fluxo entre Model, Repository, DAO, Service, Controller e View.

Explicar a API HTTP existente.

Explicar que o frontend não consome essa API e utiliza persistência independente.

Não afirmar que frontend e backend estão integrados.

### 4.5 Comandos
Documentar comandos existentes e verificados para:

- Executar o backend.
- Executar a API HTTP.
- Executar os testes Spock.
- Executar o frontend.
- Executar os testes Vitest.
- Executar builds e relatórios de cobertura, quando disponíveis.

Não inventar scripts.

Diferenciar comandos encontrados nos arquivos daqueles efetivamente executados.

### 4.6 Convenções
- Respeitar padrões arquiteturais existentes.
- Utilizar tipos explícitos em Groovy quando apropriado.
- Preservar interfaces e contratos existentes.
- Evitar dependências desnecessárias.
- Utilizar consultas parametrizadas.
- Criar testes automatizados para novas regras.
- Manter alterações pequenas e revisáveis.
- Não modificar funcionalidades fora do escopo sem autorização.
- Seguir as convenções de commits verificadas no histórico.

### 4.7 Restrições de segurança
Incluir instruções explícitas para:

- Não registrar senhas em texto puro.
- Não expor CPF, e-mail, nascimento ou outros dados pessoais em logs.
- Não retornar senhas em DTOs de apresentação.
- Não armazenar senhas reais no `localStorage`.
- Não versionar credenciais.
- Não executar scripts destrutivos sem autorização.
- Não introduzir novas exposições de dados pessoais.

Registrar que existem riscos legados ainda não corrigidos.

### 4.8 Princípio do Anonimato

Criar uma seção de destaque:

**O Princípio do Anonimato é uma restrição obrigatória e inegociável do Linketinder.**

Documentar as regras existentes e verificadas para apresentação anônima de candidatos, empresas e vagas.

Toda nova funcionalidade deverá:

- Preservar a identidade dos participantes antes da condição de liberação prevista pelo domínio.
- Utilizar representações anônimas quando aplicável.
- Evitar referências a entidades completas em objetos de apresentação.
- Impedir exposição acidental por interfaces, respostas HTTP e logs.
- Possuir testes de anonimato.
- Não presumir quais informações são liberadas após o match.

Quando a regra de negócio não estiver definida, exigir esclarecimento antes da implementação.

### 4.9 Scripts de banco
Documentar a localização dos scripts SQL, como são utilizados e quais cuidados devem ser tomados.

Não executar scripts nesta etapa.

### 4.10 Fluxo SDD
Definir que toda feature deverá seguir:

Levantamento → Perguntas → Especificação → Aprovação → Plano → Implementação incremental → Testes → Revisão → Commit → PR.

---

## 5. Skill — feature-com-spec

Crie:

`skills/feature-com-spec/SKILL.md`

A skill deve ser reutilizável para outras features do projeto, não apenas para Recrutador.

Utilize uma estrutura clara, com nome, descrição, condições de uso, entradas, etapas e critérios de conclusão.

A skill deverá orientar o agente a:

### Etapa A — Investigar
- Inspecionar a implementação existente.
- Identificar dependências.
- Identificar impactos no backend, banco e frontend.
- Verificar funcionalidades relacionadas.
- Levantar riscos de anonimato.
- Identificar decisões pendentes.

### Etapa B — Perguntar
Antes de escrever a especificação, apresentar perguntas de negócio e arquitetura.

Não assumir decisões não confirmadas.

### Etapa C — Especificar
Criar:

`docs/specs/<feature>/spec.md`

A especificação deverá incluir:

- Contexto.
- Objetivos.
- Escopo e exclusões.
- Requisitos funcionais.
- Regras de negócio.
- Modelo de dados.
- Relacionamentos.
- Fluxos.
- Validações.
- Princípio do Anonimato.
- Segurança e privacidade.
- Critérios de aceite.
- Cenários de teste.
- Decisões aprovadas.
- Decisões pendentes.

Exigir validação da especificação antes de implementar código.

### Etapa D — Planejar
Criar:

`docs/specs/<feature>/plano.md`

O plano deverá conter tarefas pequenas e numeradas.

Cada tarefa deve indicar:

- Objetivo.
- Camada afetada.
- Arquivos previstos.
- Dependências.
- Critérios de conclusão.
- Testes necessários.
- Verificação de anonimato.
- Atualização do CHANGELOG.
- Commit esperado.

### Etapa E — Implementar
Executar apenas uma tarefa por vez.

Após cada tarefa:

1. Apresentar alterações.
2. Revisar o diff.
3. Executar os testes aplicáveis.
4. Verificar regressões.
5. Verificar anonimato.
6. Atualizar CHANGELOG.
7. Atualizar o plano.
8. Solicitar revisão e autorização.
9. Realizar um commit específico da tarefa após aprovação.

Se a implementação revelar uma lacuna na especificação, interromper o código e atualizar a especificação antes de continuar.

### Etapa F — Validar
Executar os testes automatizados e validar os critérios de aceite.

Verificar a preservação das funcionalidades existentes.

### Etapa G — Entregar
Preparar Pull Request contendo:

- Resumo.
- Referência à issue.
- Especificação.
- Plano.
- Testes realizados.
- Evidências de validação.
- Impactos.
- Riscos e limitações.

Usar `Closes #N` quando houver issue correspondente.

**Regra obrigatória da skill:** cada tarefa de implementação deve atualizar o CHANGELOG e ser concluída com um commit próprio, após revisão.

---

## 6. CHANGELOG.md

Criar o arquivo seguindo o padrão Keep a Changelog.

Utilizar:

- Título `Changelog`.
- Seção `[Não lançado]`.
- Categorias `Adicionado`, `Modificado`, `Corrigido` e `Removido`.

Registrar apenas alterações realmente realizadas ou fatos históricos verificáveis.

Nesta etapa, registrar a introdução dos artefatos de preparação SDD.

Não declarar a feature Recrutador como implementada.

Não registrar correções de bugs que não foram feitas.

Manter a estrutura preparada para atualizações futuras a cada tarefa.

---

## 7. PROMPTS.md

Criar um registro de rastreabilidade dos prompts utilizados no desafio.

Incluir:

- Objetivo do documento.
- Contexto do desafio.
- Sequência de etapas.
- Registro do prompt de auditoria inicial, quando seu texto integral estiver disponível.
- Registro do prompt de preparação SDD.
- Resultados obtidos.
- Decisões aprovadas.
- Decisões ainda pendentes.

**Não invente o conteúdo literal de prompts anteriores.**

Se o texto completo de algum prompt não estiver disponível no contexto do agente, registre apenas seu propósito e identifique que a transcrição integral está pendente.

Documente a decisão de manter frontend e backend independentes.

Registre a situação parcial de curtidas e matches e a necessidade de validação de escopo.

Não registre hipóteses como decisões aprovadas.

---

## 8. Limites de execução

Você está autorizado a criar ou editar somente os quatro arquivos listados nesta etapa.

Não está autorizado a:

- Modificar código de produção.
- Modificar testes.
- Modificar scripts SQL.
- Executar migrações.
- Alterar dados no PostgreSQL.
- Instalar dependências.
- Criar endpoints.
- Implementar Recrutador.
- Corrigir bugs legados.
- Criar branch ou issue.
- Fazer commit ou push.
- Criar Pull Request.

Não execute comandos destrutivos.

Se identificar a necessidade de alterar outro arquivo, apresente a justificativa e aguarde autorização.

---

## 9. Verificações obrigatórias

Após preparar os documentos:

1. Verifique a existência dos quatro arquivos.
2. Confira se os caminhos e comandos documentados existem.
3. Verifique a consistência entre AGENTS.md e a skill.
4. Confirme que o anonimato está explicitamente protegido.
5. Confirme que a separação frontend/backend está corretamente descrita.
6. Confirme que curtidas e matches estão documentados como parciais, quando aplicável.
7. Confirme que o CHANGELOG não declara implementações inexistentes.
8. Confirme que PROMPTS.md não contém transcrições inventadas.
9. Execute `git status --short`.
10. Execute `git diff --check` e revise as alterações, incluindo os arquivos novos.
11. Confirme que nenhum arquivo de código ou banco foi modificado.

Não é necessário executar a suíte de testes nesta etapa exclusivamente documental.

---

## 10. Relatório final obrigatório

Apresente:

### A. Arquivos criados
Caminho e finalidade de cada arquivo.

### B. Conteúdo implementado
Resumo das principais seções e regras documentadas.

### C. Validações
Quais verificações foram realizadas e seus resultados.

### D. Decisões pendentes
Questões que deverão ser resolvidas na futura especificação.

### E. Estado do Git
Branch, arquivos alterados e confirmação de que nenhum commit foi realizado.

### F. Próximo passo
Explicar o que estará pronto para revisão e commit.

**IMPORTANTE: NÃO REALIZE O COMMIT.**

Pare após criar, inspecionar e apresentar os quatro documentos.

Aguarde minha aprovação explícita antes de qualquer commit ou avanço para a feature Recrutador.
```

</details>

---

## Decisões aprovadas

Fonte: instruções do responsável nos prompts acima.

1. **Frontend e backend permanecem independentes**: PostgreSQL no backend e `localStorage` no frontend, sem integração HTTP nem sincronização neste desafio. A API HTTP existente não passa a ser consumida pelo frontend.
2. O Recrutador será implementado **posteriormente**, nas duas camadas, respeitando a arquitetura existente.
3. Curtidas e matches **não serão implementados nem corrigidos** na preparação.
4. O Princípio do Anonimato é restrição obrigatória.
5. O fluxo de trabalho SDD e a regra de um commit por tarefa, com CHANGELOG atualizado.
6. Nesta etapa, riscos legados são **apenas registrados**, não corrigidos.

## Decisões pendentes

Nada abaixo é requisito aprovado.

1. **Escopo de curtidas e matches:** o estado é **parcial** (tabelas SQL e serviços em memória, com defeitos prováveis). É preciso validar com o Tutor se "recrutador curte candidato" entra no escopo e se os defeitos legados devem ser corrigidos.
2. **Endpoint HTTP:** `POST /api/recrutadores` por consistência com a API existente, ou fora do escopo?
3. Quem cadastra o recrutador e como representar a empresa logada, já que não há autenticação.
4. Vínculo e desvinculação com a empresa; exclusão do recrutador e da empresa (cascata, bloqueio ou órfão).
5. Unicidade de e-mail e CPF (somente entre recrutadores ou entre todas as entidades).
6. Validação de nascimento (idade mínima) e requisitos de senha; hash; senha no `localStorage`.
7. Área de recrutamento: texto livre ou lista fechada, e se restringe as vagas visíveis.
8. Escopo das vagas e dos candidatos acessíveis ao recrutador.
9. Visibilidade dos dados do recrutador ao candidato antes do match.
10. **O que é liberado após o match** (não definido em nenhum lugar do repositório).
11. Autorização para consulta somente leitura ao PostgreSQL e para executar `./gradlew test` e `npm test`.

## Registro de resultados futuros

Esta seção será atualizada a cada etapa (ID/link da issue, spec aprovada, plano, PR). Nada foi registrado ainda.
