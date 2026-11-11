# Changelog

Todas as mudanças relevantes deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/). Cada tarefa de implementação deve atualizar este arquivo.

## [Não lançado]

### Adicionado
- `database/database.sql` e `database/queries.sql`: tabela `recrutadores` (relacionamento 1:N com `empresas`, `ON DELETE CASCADE`, `UNIQUE` em `email` e `cpf` e índice em `empresa_id`), dados iniciais com hashes PBKDF2 e consulta de validação sem exposição de senha ou CPF (Tarefa T-01).
- `docs/specs/recrutador/spec.md` e `docs/specs/recrutador/plano.md`: especificação funcional/técnica e plano de implementação incremental da feature Recrutador.
- `AGENTS.md`: instruções do repositório para agentes de IA (stack, estrutura, arquitetura, comandos, convenções, restrições de segurança, Princípio do Anonimato, scripts de banco e fluxo SDD).
- `skills/feature-com-spec/SKILL.md`: skill reutilizável de Spec-Driven Development.
- `CHANGELOG.md`: este arquivo.
- `PROMPTS.md`: registro de rastreabilidade dos prompts do desafio.

### Modificado
- Nada até o momento.

### Corrigido
- Nada até o momento.

### Removido
- Nada até o momento.
