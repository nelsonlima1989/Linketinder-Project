---
name: feature-com-spec
description: Desenvolve uma feature do Linketinder com Spec-Driven Development (investigar, perguntar, especificar, planejar, implementar em tarefas pequenas, validar e entregar). Use ao iniciar qualquer feature nova ou mudança relevante de regra de negócio, em backend (Groovy/PostgreSQL), frontend (TypeScript/localStorage) ou ambos.
---

# feature-com-spec

Fluxo reutilizável: **Levantamento → Perguntas → Especificação → Aprovação → Plano → Implementação incremental → Testes → Revisão → Commit → PR**.

Leia antes de começar: [`AGENTS.md`](../../AGENTS.md) (arquitetura, comandos, segurança, **Princípio do Anonimato**).

## Quando usar

- Nova feature ou alteração relevante de regra de negócio.
- Mudança que afete mais de uma camada, o esquema do banco ou dados pessoais.

Não use para correções triviais e isoladas, nem para tarefas somente de leitura.

## Entradas

- Nome da feature em minúsculas, com hífen (`<feature>`), usado em `docs/specs/<feature>/`.
- Enunciado, issue ou descrição do pedido.
- Decisões já aprovadas, quando houver.

## Regras gerais

- **Nunca** escreva código de produção antes de a especificação ser aprovada e commitada.
- Não presuma regras de negócio. Marque como **pendente** o que não foi confirmado.
- Backend e frontend têm persistências independentes (PostgreSQL × `localStorage`) e **não são integrados**. Não crie integração sem decisão explícita.
- O **Princípio do Anonimato é inegociável**. Quando a regra não estiver definida, pergunte.
- Não faça commit, push, branch, issue ou PR sem autorização explícita.
- Não execute scripts SQL nem DDL/DML sem autorização. Não registre credenciais nem dados pessoais reais.
- Diferencie sempre **fato observado**, **hipótese** e **recomendação**.

---

## Etapa A — Investigar (somente leitura)

1. Inspecione a implementação existente relacionada (models, repositories, DAOs, services, controllers, view, API e frontend).
2. Identifique dependências e funcionalidades relacionadas, e confirme o estado real, como implementada, parcial ou inexistente.
3. Mapeie o impacto em **backend, banco e frontend**, incluindo testes existentes.
4. Levante riscos de **anonimato**, segurança e integridade referencial (cascata, unicidade).
5. Liste as **decisões pendentes**.
6. Valide no código as afirmações do enunciado e registre as divergências com evidência (arquivo e linha).

**Saída:** relatório de investigação com status (CONFIRMADO, PARCIAL, INEXISTENTE ou PENDENTE).

## Etapa B — Perguntar

Antes de escrever a especificação, apresente as perguntas de **negócio e arquitetura** (cadastro, vínculo, exclusão, unicidade, validações, permissões, visibilidade, anonimato, escopo por camada, dependências de outras features).

- Agrupe por tema e ofereça opções quando possível.
- **Não assuma** respostas. Aguarde as decisões e registre-as como aprovadas.

## Etapa C — Especificar

Crie `docs/specs/<feature>/spec.md` com:

1. Contexto
2. Objetivos
3. Escopo e exclusões
4. Requisitos funcionais (numerados, ex.: RF-01)
5. Regras de negócio (numeradas, ex.: RN-01)
6. Modelo de dados (por camada)
7. Relacionamentos
8. Fluxos
9. Validações
10. **Princípio do Anonimato** (o que é visível a quem, e o que não pode vazar)
11. Segurança e privacidade (senhas, dados pessoais, logs, `localStorage`)
12. Critérios de aceite (verificáveis)
13. Cenários de teste
14. Decisões aprovadas (com origem: quem aprovou)
15. Decisões pendentes

**Validação obrigatória:** peça a revisão e a aprovação da especificação (responsável e, quando aplicável, o Tutor). Depois, com autorização, **commit da especificação antes de qualquer código**.

## Etapa D — Planejar

Crie `docs/specs/<feature>/plano.md` com tarefas **pequenas e numeradas** (T-01, T-02…). Cada tarefa informa:

- Objetivo
- Camada afetada (backend, banco, frontend)
- Arquivos previstos
- Dependências (outras tarefas)
- Critérios de conclusão
- Testes necessários
- Verificação de anonimato
- Atualização do CHANGELOG
- Commit esperado (mensagem em Conventional Commits)

Inclua status por tarefa (pendente, em andamento, concluída). Submeta o plano para aprovação.

## Etapa E — Implementar (uma tarefa por vez)

Para a tarefa atual:

1. Implemente apenas o escopo da tarefa, seguindo as convenções do `AGENTS.md`.
2. Apresente as alterações.
3. Revise o diff (`git diff`, `git diff --check`).
4. Execute os testes aplicáveis (ver comandos no `AGENTS.md`).
5. Verifique regressões (suítes completas das camadas afetadas).
6. Verifique o **anonimato** (testes e inspeção de DTOs, respostas e logs).
7. Atualize o `CHANGELOG.md`.
8. Atualize o `plano.md` (status e observações).
9. Solicite revisão e autorização.
10. Após aprovação, faça **um commit específico da tarefa**.

**Se a implementação revelar uma lacuna na especificação: interrompa o código, atualize a especificação, obtenha nova aprovação e só então continue.**

> **Regra obrigatória:** cada tarefa de implementação atualiza o CHANGELOG e termina com um commit próprio, após revisão.

## Etapa F — Validar

1. Execute as suítes completas aplicáveis (Spock e/ou Vitest, mais `npx tsc --noEmit` no frontend).
2. Valide cada critério de aceite da especificação.
3. Confirme que as funcionalidades existentes foram preservadas.
4. Confirme o anonimato e a ausência de novos vazamentos de dados pessoais ou senhas.
5. Registre as evidências, como comandos executados e resultados reais.

## Etapa G — Entregar

Prepare o Pull Request contendo:

- Resumo
- Referência à issue (`Closes #N` quando houver)
- Link para especificação e plano
- Testes realizados
- Evidências de validação
- Impactos
- Riscos e limitações (incluindo riscos legados não corrigidos)

Abrir o PR só com autorização.

---

## Critérios de conclusão da skill

- [ ] Investigação concluída e perguntas respondidas.
- [ ] `spec.md` aprovada e commitada antes do código.
- [ ] `plano.md` aprovado, com todas as tarefas concluídas.
- [ ] Um commit por tarefa, cada um com o CHANGELOG atualizado.
- [ ] Testes passando e critérios de aceite validados.
- [ ] Anonimato verificado (com testes).
- [ ] Nenhuma funcionalidade fora do escopo alterada.
- [ ] PR preparado, com evidências.
