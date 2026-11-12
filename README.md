# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi iniciado como um **MVP (Minimum Viable Product)** no **ZG-HERO / Acelera ZG** e posteriormente refatorado para o padrão **MVC (Model–View–Controller)**, utilizando **Groovy** no backend, **PostgreSQL** como banco de dados relacional, integração via **JDBC** e uma interface web em **TypeScript**. O projeto aplica conceitos de **Programação Orientada a Objetos (POO)**, **DAO**, **Modelagem de Dados Relacional**, **Estruturas de Dados**, organização em camadas e desenvolvimento de uma interface funcional. O backend também disponibiliza uma **API REST HTTP sem frameworks web** para cadastro de candidatos, empresas e vagas.

A proposta do Linketinder é aproximar **candidatos** e **empresas** com base em suas **Skills**, reduzindo a influência de popularidade e priorizando a compatibilidade entre as competências do candidato e as necessidades das vagas.

---

# 📌 Sobre o projeto

O Linketinder surgiu como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, que identificou uma dificuldade nos processos tradicionais de recrutamento: encontrar profissionais com competências relevantes sem depender exclusivamente de perfis com maior visibilidade.

A proposta combina:

- O conceito de **Skills** utilizado em plataformas profissionais;
- A relação entre **candidato, vaga e empresa**;
- O conceito de **Like e Match** inspirado em aplicativos de relacionamento;
- Uma experiência de interação que prioriza o **anonimato antes do Match**;
- Estruturação relacional em banco de dados com scripts de tabelas e consultas consolidadas de matches;
- Uma interface web para cadastro, visualização e gerenciamento de candidatos, empresas e vagas;
- Um **índice de afinidade** entre o candidato e cada vaga, calculado a partir das Skills em comum.

O projeto atualmente possui:

- domínio estruturado e modelagem relacional de dados;
- integração do backend com PostgreSQL através de JDBC;
- camada DAO responsável pelo acesso e persistência dos dados;
- CRUD de candidatos, empresas, competências e vagas;
- relacionamentos `Candidato N:N Competência`, `Vaga N:N Competência` e `Empresa 1:N Vaga`;
- scripts SQL para criação do banco, carga de dados iniciais e consultas de validação;
- serviços integrados à camada de persistência;
- API REST no backend com endpoints HTTP para criação de candidatos, empresas e vagas;
- interface de terminal no backend;
- interface web em TypeScript;
- cadastro de candidatos, empresas e vagas;
- edição e exclusão de registros;
- persistência local no frontend;
- controle de edição entre abas;
- visualização de perfis;
- gerenciamento das vagas das empresas;
- visualização anônima de candidatos e empresas;
- sistema de Skills;
- gráfico de Skills;
- cálculo de afinidade entre candidato e vaga com regras formais de interseção;
- persistência relacional completa de curtidas e matches no PostgreSQL (com triggers de reciprocidade e integridade); a integração JDBC dessas operações no `CurtidaService`/`MatchService` do backend e na interface web faz parte das próximas evoluções das camadas de aplicação.

---

# 🎯 Objetivo

O objetivo do projeto é construir uma plataforma de recrutamento capaz de:

- manter candidatos cadastrados;
- manter empresas cadastradas;
- associar Skills aos candidatos;
- associar Skills às vagas;
- associar vagas às empresas;
- estruturar e popular o armazenamento relacional via `database.sql`;
- executar consultas relacionais e de validação através de `queries.sql`;
- disponibilizar serviços para consulta dos dados;
- disponibilizar uma interface de terminal;
- disponibilizar uma interface web;
- permitir cadastro e gerenciamento de candidatos;
- permitir cadastro e gerenciamento de empresas;
- permitir cadastro e gerenciamento de vagas;
- permitir a visualização anônima dos candidatos;
- permitir a visualização das vagas;
- calcular o índice de afinidade percentual entre candidato e vaga;
- manter a estrutura relacional necessária para curtidas entre candidatos, vagas e empresas;
- persistir curtidas e registrar matches automaticamente no PostgreSQL via triggers e constraints relacionais;
- validar a integridade dos dados carregados;
- manter uma arquitetura preparada para futuras evoluções.

---

# 🛠️ Tecnologias utilizadas

## Banco de Dados

- **PostgreSQL / SQL ANSI**
- Scripts DDL/DML (`database.sql`)
- Consultas relacionais e relatórios (`queries.sql`)

## Backend

- **Groovy**
- **Java**
- **JDBC (Java Database Connectivity)**
- **Gradle**
- **IntelliJ IDEA**
- **Java HttpServer (`jdk.httpserver`)** para API REST sem framework web
- **Groovy JSON (`JsonSlurper` / `JsonOutput`)**
- **Spock e JaCoCo** para testes e cobertura

## Frontend

- **TypeScript**
- **HTML**
- **CSS**
- **JavaScript / DOM API**
- **Chart.js**
- **LocalStorage**

O backend utiliza Groovy como linguagem principal, conforme especificado no desafio.

O frontend foi desenvolvido separadamente utilizando TypeScript, com organização por **models**, **services**, **components**, **data** e controle de navegação.

---

# 🏗️ Arquitetura

O projeto está dividido em três partes principais:

```text
Linketinder
│
├── database
│   ├── database.sql
│   └── queries.sql
│
├── Backend
│   └── Groovy
│       ├── Main
│       ├── UI
│       ├── Controllers
│       ├── Services
│       ├── Repository / DAO
│       ├── Database
│       └── Model
│
└── Frontend
    └── TypeScript
        ├── Components (View)
        ├── Controllers
        ├── Services
        ├── Models
        ├── Data
        └── Main
```

No backend, o fluxo principal de acesso aos dados passou a seguir:

```text
Main (ConexaoFactory → ConexaoProvider)
  ↓
Menu
  ↓
Service
  ↓
Repository / DAO
  ↓
JDBC
  ↓
PostgreSQL
```

No backend, a interface de terminal e os controllers delegam operações aos services; o acesso ao banco ocorre através dos repositórios e DAOs. A camada de interface não acessa o banco diretamente. Os `Services` concentram as operações da aplicação e utilizam os `DAOs` para executar os comandos SQL via JDBC.

A intenção é evitar concentrar toda a lógica em um único arquivo, mantendo cada parte do sistema responsável por uma função específica.

---

# 📁 Estrutura do projeto

## Estrutura Geral e Banco de Dados

```text
linketinder/
│
├── database/
│   ├── database.sql
│   └── queries.sql
│
├── src/
│   └── main/
│       └── groovy/
│           └── com/
│               └── linketinder/
│                   ├── Main.groovy
│                   │
│                   ├── api/
│                   │   ├── ApiServer.groovy
│                   │   ├── CandidatoHttpHandler.groovy
│                   │   ├── EmpresaHttpHandler.groovy
│                   │   ├── VagaHttpHandler.groovy
│                   │   ├── HttpRequestUtil.groovy
│                   │   └── HttpResponseUtil.groovy
│                   │
│                   ├── dao/
│                   │   ├── CandidatoDAO.groovy
│                   │   ├── CandidatoCompetenciaDAO.groovy
│                   │   ├── CompetenciaDAO.groovy
│                   │   ├── EmpresaDAO.groovy
│                   │   ├── VagaDAO.groovy
│                   │   └── VagaCompetenciaDAO.groovy
│                   │
│                   ├── database/
│                   │   ├── ConexaoDB.groovy
│                   │   ├── ConexaoProvider.groovy
│                   │   ├── ConexaoPostgresProvider.groovy
│                   │   └── ConexaoFactory.groovy
│                   │
│                   ├── repository/
│                   │   ├── CandidatoRepository.groovy
│                   │   ├── CandidatoCompetenciaRepository.groovy
│                   │   ├── EmpresaRepository.groovy
│                   │   ├── VagaRepository.groovy
│                   │   ├── VagaCompetenciaRepository.groovy
│                   │   └── CompetenciaRepository.groovy
│                   │
│                   ├── model/
│                   │   ├── Pessoa.groovy
│                   │   ├── Candidato.groovy
│                   │   ├── Empresa.groovy
│                   │   ├── Informacoes.groovy
│                   │   ├── Skill.groovy
│                   │   ├── Vaga.groovy
│                   │   ├── Curtida.groovy
│                   │   ├── Match.groovy
│                   │   ├── TipoCurtida.groovy
│                   │   ├── CandidatoAnonimo.groovy
│                   │   └── EmpresaAnonima.groovy
│                   │
│                   ├── service/
│                   │   ├── CandidatoService.groovy
│                   │   ├── EmpresaService.groovy
│                   │   ├── VagaService.groovy
│                   │   ├── CurtidaService.groovy
│                   │   └── MatchService.groovy
│                   │
│                   └── ui/
│                       └── Menu.groovy
│
└── frontend/
    ├── index.html
    ├── package.json
    ├── package-lock.json
    ├── tsconfig.json
    ├── public/
    │
    └── src/
        ├── controllers/
        │   ├── candidatoController.ts
        │   ├── empresaController.ts
        │   └── vagaController.ts
        │
        ├── components/
        │   ├── candidatos/
        │   │   ├── renderizarCardsCandidatos.ts
        │   │   ├── renderizarPerfilCandidato.ts
        │   │   └── renderizarFormularioCandidato.ts
        │   │
        │   ├── empresas/
        │   │   ├── renderizarPerfilEmpresa.ts
        │   │   ├── renderizarGraficoSkills.ts
        │   │   └── renderizarFormularioEmpresa.ts
        │   │
        │   ├── vagas/
        │   │   ├── renderizarCardsVagas.ts
        │   │   ├── renderizarCardsVagasEmpresa.ts
        │   │   └── renderizarFormularioVaga.ts
        │   │
        │   └── layout/
        │       ├── renderizarSidebar.ts
        │       └── renderizarTela.ts
        │
        ├── data/
        │   └── dados.ts
        │
        ├── models/
        │   ├── Candidato.ts
        │   ├── Empresa.ts
        │   └── Vaga.ts
        │
        ├── services/
        │   ├── candidato/           # Cadastro e edição de candidatos
        │   ├── empresa/             # Cadastro e edição de empresas
        │   ├── vaga/                # Cadastro e edição de vagas
        │   └── utils/               # Storage, navegação, IDs, skills e afinidade
        │
        └── main.ts
```

---

# 🗄️ Camada de Banco de Dados

A pasta `database/` reúne os scripts SQL utilizados pelo projeto:

- **`database.sql`**: cria as tabelas, chaves primárias, chaves estrangeiras e constraints, além de inserir os dados iniciais necessários para executar o projeto;
- **`queries.sql`**: reúne consultas SQL utilizadas para validar e explorar os relacionamentos do banco.

As tabelas principais integradas ao backend são:

```text
candidatos
empresas
competencias
vagas
recrutadores
```

Também são utilizadas tabelas associativas e de interação:

```text
candidato_competencias
vaga_competencias
curtidas_candidato_vaga
curtidas_empresa_candidato
matches
```

A modelagem representa:

```text
Candidato   N:N Competência
Vaga        N:N Competência
Empresa     1:N Vaga
Empresa     1:N Recrutadores
Candidato   N:N Vaga            (curtidas_candidato_vaga)
Empresa     N:N Candidato       (curtidas_empresa_candidato, identificando recrutador_id)
Empresa     1:0..1 Candidato    (matches, único por par empresa/candidato)
```

O esquema completo, incluindo chaves primárias, chaves estrangeiras com `ON DELETE CASCADE` / `ON DELETE SET NULL`, índices e triggers está detalhado no diagrama MER disponível em [`docs/der-linketinder.png`](docs/der-linketinder.png).

> **Nota de escopo e arquitetura:** A persistência relacional de curtidas, recrutadores e matches está totalmente implementada e testada no PostgreSQL (`database/database.sql` e `database/queries.sql`). A integração dessas operações nas camadas de aplicação (DAOs JDBC em Groovy e frontend TypeScript) permanece independente e planejada para evoluções específicas de cada camada.

## Integração JDBC e DAO

A Parte 2 introduziu a integração entre o backend Groovy e o PostgreSQL através de **JDBC**, sem utilização de JPA ou Hibernate.

A configuração de acesso PostgreSQL permanece em `com.linketinder.database.ConexaoDB`, que lê credenciais do ambiente e utiliza JDBC. A escolha da implementação de `ConexaoProvider` agora é centralizada na `ConexaoFactory`, que retorna `ConexaoPostgresProvider`. O provedor chama `ConexaoDB` para abrir conexões, e cada DAO gerencia o fechamento dos recursos por `try-with-resources`.

O acesso às tabelas foi separado em classes DAO:

```text
CandidatoDAO
EmpresaDAO
CompetenciaDAO
VagaDAO
CandidatoCompetenciaDAO
VagaCompetenciaDAO
```

Os quatro CRUDs obrigatórios foram implementados:

- CRUD de candidatos;
- CRUD de empresas;
- CRUD de competências;
- CRUD de vagas.

Os DAOs utilizam `PreparedStatement` para executar operações de inserção, consulta, atualização e exclusão.

### Relacionamento Candidato x Competência

A associação N:N é persistida através de `candidato_competencias`, com chave primária composta:

```sql
PRIMARY KEY (candidato_id, competencia_id)
```

Não existe um `id` artificial nessa tabela.

### Relacionamento Empresa x Vaga

Cada vaga mantém uma FK para sua empresa:

```text
vagas.empresa_id → empresas.id
```

permitindo que uma empresa possua diversas vagas.

### Relacionamento Vaga x Competência

As competências exigidas por uma vaga são persistidas em `vaga_competencias`, também com chave primária composta.

---

# 🧩 Backend

## Model

A camada `model` representa as entidades principais do domínio.

### Pessoa

`Pessoa` representa atributos gerais compartilhados pelo candidato.

```text
Pessoa
├── id
├── estado
└── informacoes
```

A decisão arquitetural atual é que **Empresa não herda de Pessoa**, pois candidato e empresa representam entidades diferentes e possuem identificadores distintos.

---

## Candidato

`Candidato` representa uma pessoa física candidata a uma vaga.

```text
Candidato
├── Pessoa
├── cpf
├── dataNascimento
├── descricaoPessoal
├── senha
└── skills
```

O candidato possui uma lista de `Skill`. A idade, quando necessária, pode ser calculada a partir de `dataNascimento`.

---

## Empresa

`Empresa` representa uma empresa recrutadora.

```text
Empresa
├── id
├── cnpj
├── pais
├── estado
├── descricaoEmpresarial
├── informacoes
└── vagas
```

O CNPJ é mantido no modelo da empresa porque faz parte da identificação da entidade.

---

## Informacoes

A classe `Informacoes` concentra informações de identificação e contato utilizadas pelos perfis.

```text
Informacoes
├── nome
├── sobrenome
├── cep
├── email
├── whatsapp
└── pais
```

A concentração dessas informações permite maior controle sobre quais dados poderão ser apresentados em cada contexto da aplicação.

---

## Skill

Representa uma competência que pode ser associada a candidatos e vagas.

Skills utilizadas nos dados SQL atuais:

```text
Java
Groovy
JavaScript
TypeScript
React
C#
SQL
PostgreSQL
HTML
CSS
```

---

## Vaga

Representa uma oportunidade disponibilizada por uma empresa.

```text
Vaga
├── id
├── titulo
├── descricaoVaga
├── estado
├── cidade
├── empresa
└── skills
```

Cada vaga possui uma lista de Skills que representam as competências desejadas pela empresa.

---

# 💾 Persistência dos dados no backend

Nas etapas iniciais, o projeto utilizava `DataInicializador` para manter candidatos, empresas, vagas e Skills em memória.

Com a implementação da Parte 2, candidatos e empresas deixaram de depender do `DataInicializador` no fluxo principal. O `Main` passou a criar os DAOs e injetá-los nos Services.

```text
CandidatoDAO
      │
CandidatoCompetenciaDAO
      │
      ▼
CandidatoService
```

```text
EmpresaDAO
   │
VagaDAO
   │
VagaCompetenciaDAO
   │
   ▼
EmpresaService
```

As listagens exibidas pelo menu agora são carregadas do PostgreSQL.

---

# ⚙️ Services do backend

A camada `service` concentra as operações relacionadas ao domínio.

## CandidatoService

Responsável pelas operações relacionadas aos candidatos e pela composição das competências persistidas.

Principais operações atuais:

```groovy
listarTodosCandidatos()
cadastrarCandidato(Candidato candidato)
buscarPorId(Long id)
atualizarCandidato(Candidato candidato)
excluirCandidato(Long id)
listarCandidatosAnonimos()
```

O serviço utiliza `CandidatoDAO` e `CandidatoCompetenciaDAO`, mantendo a interface desacoplada do JDBC.

---

## EmpresaService

Responsável pelas operações relacionadas às empresas, suas vagas e as competências associadas às vagas.

Principais operações atuais:

```groovy
listarTodasEmpresas()
cadastrarEmpresa(Empresa empresa)
buscarPorId(Long id)
atualizarEmpresa(Empresa empresa)
excluirEmpresa(Long id)
listarEmpresasAnonimas()
```

O serviço utiliza `EmpresaDAO`, `VagaDAO` e `VagaCompetenciaDAO`.

Ao listar as empresas, as vagas correspondentes são associadas através de `empresa.id`, e as Skills de cada vaga são carregadas da tabela `vaga_competencias`.

---

## CurtidaService e MatchService no Backend

No backend Groovy (`src/main/groovy/com/linketinder/service/`), `CurtidaService` e `MatchService` foram construídos nas etapas iniciais mantendo o histórico de curtidas e matches em coleções em memória.

No banco de dados PostgreSQL (`database/database.sql`), a persistência relacional já está plenamente consolidada com:
- `curtidas_candidato_vaga`: chave composta `(candidato_id, vaga_id)` e `ON DELETE CASCADE`;
- `curtidas_empresa_candidato`: chave composta `(empresa_id, candidato_id)`, `recrutador_id` obrigatório na criação com validação de pertencimento à empresa e `ON DELETE SET NULL`;
- `matches`: tabela relacional com unicidade estrita por `(empresa_id, candidato_id)` e triggers de reciprocidade automática em ambas as ordens de curtida.

A substituição das coleções em memória por DAOs JDBC dedicados (`CurtidaDAO`, `MatchDAO`) no backend Groovy segue o planejamento de evolução das regras de negócio do backend.

---

# 🌐 Frontend

O frontend foi desenvolvido em **TypeScript**, utilizando uma abordagem modular com separação entre modelos, componentes, serviços, dados e navegação.

A interface permite trabalhar com candidatos, empresas e vagas de forma independente.

A implementação do frontend atende aos fluxos de CRUD e gerenciamento local previstos para esta etapa do projeto.

---

# 👤 Funcionalidades do candidato

O frontend permite:

- cadastrar candidato;
- visualizar perfil;
- editar perfil;
- excluir conta;
- visualizar vagas disponíveis;
- visualizar detalhes das vagas;
- manter Skills;
- persistir os dados no `localStorage`;
- bloquear a edição do mesmo candidato em outra aba;
- liberar o bloqueio ao salvar ou cancelar;
- utilizar heartbeat para manter o bloqueio durante a edição;
- expirar automaticamente bloqueios abandonados.

Os candidatos podem ser apresentados para as empresas de forma anônima, exibindo informações como:

- formação;
- Skills;
- descrição;
- estado.

Dados de identificação permanecem ocultos nos contextos de visualização anônima.

---

# 🏢 Funcionalidades da empresa

O frontend permite:

- cadastrar empresa;
- visualizar perfil;
- editar perfil;
- excluir conta;
- cadastrar novas vagas;
- visualizar suas próprias vagas;
- editar vagas;
- excluir vagas;
- visualizar candidatos de forma anônima;
- visualizar Skills dos candidatos;
- visualizar formação dos candidatos;
- visualizar gráfico de Skills;
- bloquear a edição da mesma empresa em outra aba;
- liberar o bloqueio ao salvar ou cancelar;
- utilizar heartbeat para manter o bloqueio durante a edição;
- expirar automaticamente bloqueios abandonados.

Quando uma empresa é excluída, suas vagas também são removidas.

Esse comportamento mantém a relação entre empresas e vagas consistente no frontend.

---

# 💼 Funcionalidades das vagas

As empresas podem:

- cadastrar uma vaga;
- informar título;
- informar descrição;
- associar Skills;
- listar suas vagas;
- editar uma vaga;
- excluir uma vaga;
- cancelar o cadastro ou edição.

Cada vaga possui seu próprio controle de edição.

Isso permite que:

- a mesma vaga não seja editada simultaneamente em duas abas;
- vagas diferentes possam ser editadas simultaneamente;
- o bloqueio seja renovado enquanto a edição estiver aberta;
- o bloqueio seja liberado ao salvar ou cancelar;
- bloqueios abandonados expirem automaticamente.

---

# 📊 Índice de afinidade entre candidato e vaga

O **Índice de Afinidade** é uma métrica percentual de compatibilidade que mensura o quanto o perfil técnico de um candidato atende aos requisitos exigidos por uma determinada vaga.

Na arquitetura da plataforma, a empresa cadastra vagas com competências obrigatórias/desejáveis, e o candidato registra suas próprias competências. Portanto, a comparação ocorre na relação direta:

$$\text{Candidato} \longleftrightarrow \text{Vaga}$$

A empresa atua como provedora da vaga através do `empresaId`, mantendo a neutralidade do anonimato pré-match.

```text
Candidato (Perfil Ativo)
 └── Skills: [Java, SQL, Git]
         │
         │ (Interseção das coleções)
         ▼
       Vaga (Anúncio)
        ├── Skills Requeridas: [Java, SQL, Docker, React]
        └── empresaId ──► Empresa (oculta)
```

---

## 📐 Formulação Matemática

O cálculo baseia-se na cardinalidade da interseção entre o conjunto de competências do candidato ($S_{\text{candidato}}$) e o conjunto de competências requeridas pela vaga ($S_{\text{vaga}}$), ponderada pelo total de competências exigidas pela vaga:

$$\text{Afinidade (\%)} = \left( \frac{|S_{\text{candidato}} \cap S_{\text{vaga}}|}{|S_{\text{vaga}}|} \right) \times 100$$

Onde:
- $|S_{\text{candidato}} \cap S_{\text{vaga}}|$ representa a quantidade de competências em comum dominadas pelo candidato e demandadas pela vaga;
- $|S_{\text{vaga}}|$ representa o total de competências exigidas pela vaga anunciada.

---

## ⚙️ Regras de Negócio e Casos de Contorno (Edge Cases)

1. **Vaga sem Skills cadastradas ($|S_{\text{vaga}}| = 0$):**
    - Retorna impreterivelmente `0%`, prevenindo divisões por zero ou exceções em tempo de execução.
2. **Candidato sem Skills cadastradas ($|S_{\text{candidato}}| = 0$):**
    - Retorna `0%`, indicando ausência de compatibilidade técnica aferível.
3. **Competências Excedentes do Candidato:**
    - Habilidades que o candidato possui mas que a vaga não demanda (ex.: candidato domina *Git*, mas a vaga não exige) **não** aumentam a afinidade além de 100% e não alteram o denominador da vaga. O teto máximo é estritamente `100%`.
4. **Normalização de Dados:**
    - Antes de realizar o `Set intersection`, os termos são normalizados via `.map(s => s.trim().toLowerCase())`. Dessa forma, variações como `"Java "`, `"java"` e `"JAVA"` são reconhecidas como a mesma competência.

---

## 💡 Exemplos Práticos de Cálculo

### Cenário 1: Compatibilidade Parcial (50%)
- **Candidato:** `['Java', 'SQL', 'Git']`
- **Vaga:** `['Java', 'SQL', 'Docker', 'React']`
- **Skills em Comum ($S_{\text{candidato}} \cap S_{\text{vaga}}$):** `['Java', 'SQL']` (Total = 2)
- **Total da Vaga ($|S_{\text{vaga}}|$):** 4
- **Cálculo:**
  $$\text{Afinidade} = \left(\frac{2}{4}\right) \times 100 = 50\%$$

### Cenário 2: Compatibilidade Total com Excedente (100%)
- **Candidato:** `['Java', 'Spring Framework', 'Docker', 'AWS', 'PostgreSQL']`
- **Vaga:** `['Java', 'Spring Framework', 'Docker']`
- **Skills em Comum:** `['Java', 'Spring Framework', 'Docker']` (Total = 3)
- **Total da Vaga:** 3
- **Cálculo:**
  $$\text{Afinidade} = \left(\frac{3}{3}\right) \times 100 = 100\%$$

---

## 🛠️ Implementação Modular no Frontend

A lógica de afinidade é isolada e desacoplada da camada visual:

- **Serviço de Regra de Negócio (`src/services/afinidadeService.ts`):**  
  Recebe as entidades `Candidato` e `Vaga`, extrai as listas de competências, aplica a normalização e executa a fórmula matemática retornando o valor numérico inteiro/arredondado.
- **Renderizador de Interface (`src/components/vagas/renderizarCardsVagas.ts`):**  
  Identifica o candidato autenticado/selecionado na sessão, aciona o `afinidadeService` para cada vaga iterada na listagem e injeta no DOM o selo informativo (badge) de afinidade:
  ```text
  ┌────────────────────────────────────────────────────────┐
  │ [Pastelsoft] Desenvolvedor Java                        │
  │ Afinidade: 50% ★                                       │
  │ Requisitos: Java, SQL, Docker, React                   │
  └────────────────────────────────────────────────────────┘
  ```

---

# 📊 Gráfico de Skills

O frontend possui uma visualização gráfica das Skills cadastradas.

O gráfico permite visualizar a quantidade de ocorrências de cada Skill entre os candidatos.

Essa funcionalidade utiliza **Chart.js**.

A lógica de contagem das Skills fica no serviço:

```text
skillService.ts
```

A renderização do gráfico fica em:

```text
renderizarGraficoSkills.ts
```

---

# 🗄️ Persistência no frontend

O frontend utiliza o `localStorage` do navegador para persistir os dados.

Os serviços responsáveis pelos dados utilizam uma camada própria de armazenamento:

```text
services/
└── storage.ts
```

A aplicação carrega os dados salvos quando disponíveis e utiliza os dados iniciais como base quando ainda não existem registros no `localStorage`.

Essa abordagem permite testar o MVP sem a dependência imediata de uma API ativa para o funcionamento visual da interface.

Os principais conjuntos de dados persistidos são:

```text
candidatos
empresas
vagas
```

---

# 🔒 Controle de edição entre abas

O frontend possui mecanismos de bloqueio para evitar que o mesmo registro seja editado simultaneamente em diferentes abas do navegador.

Existem controles independentes para:

```text
Candidato
Empresa
Vaga
```

Cada bloqueio possui:

- identificação do registro;
- timestamp;
- heartbeat;
- tempo de expiração;
- liberação manual.

O heartbeat atualiza periodicamente o timestamp do registro em edição.

Caso a aba seja fechada ou o bloqueio deixe de ser renovado, ele poderá expirar automaticamente.

Esse mecanismo garante que:

- o mesmo registro não seja editado simultaneamente;
- registros diferentes possam ser editados ao mesmo tempo;
- salvar libere o bloqueio;
- cancelar libere o bloqueio;
- bloqueios abandonados expirem.

---

# 🗑️ Exclusão em cascata

Quando uma empresa é excluída no frontend, suas vagas também são excluídas.

Fluxo:

```text
Excluir empresa
       ↓
Localizar vagas pelo empresaId
       ↓
Excluir vagas vinculadas
```

Isso evita que permaneçam vagas associadas a uma empresa inexistente.

---

# 🖥️ Interface de usuário do backend

O backend possui uma interface simples baseada em terminal.

O menu principal apresenta:

```text
================================

          LINKETINDER

================================

1 - Listar candidatos

2 - Listar vagas

3 - Curtir vaga

4 - Curtir candidato

5 - Listar matches

0 - Sair

================================
```

---

# 👥 Listagem anônima

Uma das regras de negócio do Linketinder é trabalhar com **anonimato antes do Match**.

## Candidato anônimo

Antes do Match, são apresentados:

```text
Candidato
├── ID
├── Estado
├── Descrição pessoal
└── Skills
```

Ficam ocultos:

```text
Nome
CPF
Idade
CEP
E-mail
WhatsApp
```

---

## Empresa e vaga anônimas

Na visualização das vagas, são apresentados:

```text
Vaga
├── ID
├── Título
├── Descrição
├── País da empresa
├── Estado da empresa
└── Skills
```

O nome e os dados de identificação e contato da empresa permanecem ocultos até o estabelecimento do match mútuo.

---

# ❤️ Sistema de Likes

O backend possui um sistema de curtidas que permite que os dois lados demonstrem interesse.

## Candidato curtindo vagas

Um candidato pode curtir uma ou várias vagas:

```text
Candidato
   │
   ├── Like ──► Vaga 1
   ├── Like ──► Vaga 2
   └── Like ──► Vaga 3
```

A curtida registra o candidato, a vaga, a empresa relacionada e o tipo de curtida.

---

## Empresa curtindo candidatos

Uma empresa pode curtir um ou vários candidatos:

```text
Empresa
   │
   ├── Like ──► Candidato 1
   ├── Like ──► Candidato 2
   └── Like ──► Candidato 3
```

Na versão atual, a estrutura relacional das curtidas está implementada e testada no PostgreSQL, registrando `(empresa_id, candidato_id, recrutador_id)` com validação obrigatória de que o recrutador pertence à empresa no momento da ação, e suporte a `ON DELETE SET NULL` para preservar o histórico caso o recrutador seja excluído.

---

# 🤝 Match

O Match acontece quando existe **interesse mútuo entre um candidato e uma empresa**.

### Regras de Negócio e Reciprocidade

1. **Atores das curtidas:** O candidato curte vagas de interesse (`curtidas_candidato_vaga`); o recrutador curte candidatos em nome da sua empresa (`curtidas_empresa_candidato`).
2. **Independência da ordem:** O match é formado automaticamente quer o candidato curta a vaga primeiro e a empresa curta o candidato depois, quer a empresa curta o candidato primeiro e o candidato curta a vaga depois.
3. **Unicidade de Match:** Há no máximo 1 match ativo por par `(empresa_id, candidato_id)`. Se o candidato curtir múltiplas vagas da mesma empresa, o match não é duplicado.
4. **Remoção seletiva e recriação:** Caso uma vaga curtida seja excluída (ou o candidato desfaça a curtida), o match permanece ativo se ainda restar outra vaga daquela empresa curtida pelo candidato. Se a última vaga for desfeita/excluída, o match é removido automaticamente, podendo ser recriado caso haja uma nova curtida mútua.
5. **Preservação de histórico (`ON DELETE SET NULL`):** A exclusão de um recrutador não desfaz a curtida nem o match da empresa, passando `recrutador_id` para `NULL`.
6. **Exclusão em cascata (`ON DELETE CASCADE`):** A exclusão de uma empresa ou de um candidato remove automaticamente suas respectivas curtidas e matches.

### Automação via Triggers no PostgreSQL

No PostgreSQL (`database/database.sql`), essa inteligência é garantida no nível do banco por triggers PL/pgSQL dedicados:

- `trg_validar_curtida_empresa_recrutador`: exige `recrutador_id` e valida pertencimento à empresa.
- `trg_validar_match`: impede inserção manual direta sem reciprocidade mútua ativa.
- `trg_registrar_match_apos_curtida_candidato`: detecta reciprocidade e cria match quando candidato curte vaga.
- `trg_registrar_match_apos_curtida_empresa`: detecta reciprocidade e cria match quando empresa curte candidato.
- `trg_remover_match_sem_reciprocidade`: remove o match quando desaparece a última vaga curtida daquela empresa.
- `trg_sincronizar_matches_apos_update_vaga`: reavalia matches se uma vaga for transferida de empresa via `UPDATE`.
- Bloqueios consultivos com `pg_advisory_xact_lock` e `FOR KEY SHARE` evitam *race conditions* e deadlocks em transações concorrentes.

O fluxo de negócio previsto é:

```text
Candidato (curte Vaga) ────┐
                           ├──► Triggers PL/pgSQL ──► Tabela MATCHES (1 por Empresa/Candidato)
Recrutador (curte Candidato)─┘
```

> **Nota de escopo:** A persistência e os triggers de Match estão 100% implementados no banco de dados. A camada de serviços em Groovy (`src/main/groovy/**`) e a interface web em TypeScript (`frontend/**`) mantêm suas regras locais e serão integradas a essas tabelas em etapas específicas de backend e frontend.

---

# ▶️ Como executar

## Banco de Dados

1. Crie um banco PostgreSQL para o projeto (ou utilize um container Docker/instância local).
2. Execute o script de estrutura DDL (tabelas, índices e triggers de curtidas/matches):

   ```bash
   psql -h localhost -U seu_usuario -d linketinder -f database/database.sql
   ```

3. Execute o script de carga inicial de seeds e suite de demonstração SQL:

   ```bash
   psql -h localhost -U seu_usuario -d linketinder -f database/queries.sql
   ```

   **O que esperar ao executar `queries.sql`:**
   - Carga inicial de 10 competências, 5 candidatos, 5 empresas, 5 vagas, 6 recrutadores (com senhas derivadas em PBKDF2), 8 curtidas de candidatos e 8 curtidas de empresas;
   - Os triggers registram automaticamente **7 matches únicos** na tabela `matches`, demonstrando reciprocidade nas duas ordens de curtida e integridade referencial com os recrutadores responsáveis;
   - Execução de testes automatizados com `ROLLBACK` cobrindo unicidade, exclusão parcial/total, `SET NULL`, `CASCADE` e idempotência de migração incremental (todos com status `[OK]`).

4. Para consultar diretamente os matches registrados na tabela física:

   ```sql
   SELECT
       m.id AS match_id,
       e.nome AS empresa,
       c.nome AS candidato,
       COALESCE(r.nome, '[RECRUTADOR EXCLUIDO]') AS recrutador,
       m.data_match
   FROM matches m
   INNER JOIN empresas e ON e.id = m.empresa_id
   INNER JOIN candidatos c ON c.id = m.candidato_id
   LEFT JOIN recrutadores r ON r.id = m.recrutador_id
   ORDER BY m.id;
   ```

5. O Diagrama Entidade-Relacionamento atualizado e consolidado com as 10 tabelas, colunas `estado`, relacionamentos e regras `ON DELETE` está disponível em [`docs/der-linketinder.png`](docs/der-linketinder.png).

6. Configure as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. A classe `ConexaoDB.groovy` lê essas variáveis com `System.getenv()`, sem credenciais fixas no código.

   Exemplo de arquivo `.env` na raiz do projeto (substitua os valores pelos de seu ambiente):

   ```dotenv
   DB_URL=jdbc:postgresql://localhost:5432/linketinder
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   ```

   Mantenha `.env` fora do controle de versão (`.gitignore`) e **não publique credenciais**. No Linux/macOS, carregue o arquivo em um terminal confiável antes de executar o Gradle:

   ```bash
   set -a
   source .env
   set +a
   ```

   No IntelliJ IDEA, configure as três variáveis de ambiente na configuração de execução ou execute pelo terminal que já as exportou.

O `database.sql` cria a estrutura e insere os dados iniciais utilizados pelo projeto.

## Backend

### Pré-requisitos

Para executar o backend, é necessário possuir:

- Java JDK instalado;
- Groovy;
- Gradle ou Gradle Wrapper;
- IntelliJ IDEA ou outra IDE compatível.

### Executando pela IDE

Abra o projeto no IntelliJ IDEA e execute:

```text
Main.groovy
```

O programa iniciará o menu do Linketinder no terminal.

### Executando pelo Gradle

Na raiz do projeto, com as variáveis do banco carregadas:

```bash
./gradlew run --console=plain
```

No Windows:

```bash
gradlew.bat run
```

---

# 💻 Executando o frontend

Entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Para verificar se o TypeScript está compilando corretamente:

```bash
npx tsc --noEmit
```

Para executar o frontend em desenvolvimento:

```bash
npm run dev
```

---

# 🧪 Validação

O projeto foi testado em diferentes níveis.

## Banco de Dados

Foram validados:
- integridade referencial das chaves primárias e estrangeiras;
- inserção de dados de candidatos, empresas, vagas e competências;
- consultas de correspondência cruzando candidatos, vagas e empresas com `INNER JOIN`.

## Backend

Foram validados:

- conexão do backend com PostgreSQL através de JDBC;
- CRUD completo de candidatos;
- CRUD completo de empresas;
- CRUD completo de competências;
- CRUD completo de vagas;
- associação N:N entre candidatos e competências;
- associação N:N entre vagas e competências;
- associação 1:N entre empresas e vagas;
- carregamento das Skills dos candidatos a partir do banco;
- carregamento das vagas e Skills das empresas a partir do banco;
- listagem anônima de candidatos;
- listagem anônima de empresas/vagas;
- execução do fluxo `Main → Menu → Service → DAO → PostgreSQL`;
- navegação pelo menu e encerramento da aplicação.

A persistência JDBC das curtidas e a integração do Match serão validadas no desafio seguinte.

## Frontend

Foram testados:

- cadastro, edição e exclusão de candidato;
- cadastro, edição e exclusão de empresa;
- cadastro, edição e exclusão de vaga;
- cancelamento de ações de formulário;
- visualização de perfis e detalhes de vagas;
- visualização anônima de candidatos;
- visualização das vagas da empresa;
- persistência através de `localStorage`;
- gráfico de Skills com Chart.js;
- cálculo de afinidade e exibição no card de cada vaga;
- bloqueio de edição entre abas, heartbeat e expiração de bloqueios;
- exclusão em cascata das vagas ao excluir uma empresa;
- edição simultânea de registros diferentes.

---

# 🧹 Refatoração do backend — Clean Code e SOLID

Na etapa anterior de Clean Code e SOLID, o escopo foi delimitado **ao backend Groovy**. A refatoração posterior para MVC também reorganizou o frontend TypeScript. A persistência das curtidas e dos matches via JDBC também permanece reservada para uma etapa posterior.

## Melhorias de Clean Code

- **Nomes explicativos e funções menores:** extração de métodos com finalidade específica no `Menu`, como apresentação das opções, apresentação das vagas/candidatos e preparação da lista de vagas.
- **DRY:** remoção de repetições relevantes na preparação dos mocks e na captura da saída do console no `MenuSpec`.
- **Tratamento de erros:** DAOs preservam as causas SQL ao lançar `IllegalStateException`; o menu apresenta mensagens compreensíveis quando as consultas falham.
- **Gerenciamento de recursos:** uso de `try-with-resources` nos DAOs para fechar conexões, statements e resultados.
- **Separação entre persistência e apresentação:** remoção de mensagens `println` de operações do `CandidatoCompetenciaDAO`.
- **Entrada de dados validada:** o `Menu` utiliza `Scanner` injetado e leitura consistente por `lerOpcao()`, evitando a mistura de `nextInt()` com `nextLine()`.
- **Comentários apenas quando úteis:** remoção de comentários que apenas repetiam nomes de métodos ou de blocos.

## Aplicação dos princípios SOLID

| Princípio | Aplicação no backend |
|---|---|
| **S — Responsabilidade única (SRP)** | `Menu` concentra a interação com o usuário; `Services` coordenam operações; `DAOs` acessam os dados; `Models` representam entidades. |
| **O — Aberto/Fechado (OCP)** | Os contratos Repository e `ConexaoProvider` permitem acrescentar implementações alternativas com poucas mudanças nos consumidores. |
| **L — Substituição de Liskov (LSP)** | As implementações analisadas respeitam as assinaturas de seus contratos; não foram identificadas violações evidentes na revisão. |
| **I — Segregação de Interfaces (ISP)** | Interfaces específicas para entidades e relacionamentos, sem obrigar os consumidores a receber um único contrato genérico. |
| **D — Inversão de Dependências (DIP)** | `Services` recebem interfaces Repository; os DAOs recebem `ConexaoProvider`; o `Main.groovy` conecta as implementações. |

A revisão dos princípios é uma **avaliação arquitetural**, não uma garantia formal de todas as propriedades comportamentais de SOLID.

## Organização e injeção de dependências

```text
Main.groovy (composition root)
    ├── Menu (Scanner injetado)
    │    └── Services
    │         └── interfaces Repository
    │              └── DAOs JDBC
    │                   └── ConexaoProvider (selecionado pela ConexaoFactory)
    │                        └── ConexaoPostgresProvider
    │                             └── ConexaoDB / DriverManager / PostgreSQL
    └── Montagem dos objetos e suas dependências
```

Os DAOs utilizam `PreparedStatement` com parâmetros para executar consultas e alterações. Em `VagaDAO.listarTodos()`, a recuperação de empresas por `EmpresaRepository.buscarPorIds(...)` evita consultas individuais para cada vaga.

## Testes unitários — Spock

Na validação do desafio de Clean Code e SOLID, foram **122 testes unitários aprovados**, dos quais **17 são do `MenuSpec`**. Após a implementação da Factory, foram adicionados quatro testes, elevando o total validado para **126 testes**. Os testes do menu usam `Mock()` para simular serviços e `Scanner` com entradas controladas, sem exigir acesso ao PostgreSQL.

Foram exercitados, entre outros, encerramento do menu, opções inválidas, entradas não numéricas, listas vazias, falhas simuladas de consulta e encaminhamento de seleção válida ao serviço de curtidas. Esse último cenário **verifica a chamada ao mock**, não a persistência de uma curtida.

Para executar a suíte completa:

```bash
./gradlew clean test
```

Para executar apenas os testes do menu:

```bash
./gradlew test --tests "uiTest.MenuSpec"
```

Para gerar o relatório JaCoCo, quando configurado no Gradle:

```bash
./gradlew clean test jacocoTestReport
```

O total de **126 testes aprovados** foi confirmado na execução local de `./gradlew clean test` após a implementação da Factory; futuras alterações podem modificar esse total.

## Limites e evoluções futuras

- A lógica de curtidas e matches existente foi mantida; **não foi implementada persistência JDBC dessas operações nesta refatoração**.
- Os cadastros que envolvem múltiplas tabelas poderão exigir **transações** para evitar gravações parciais em caso de falha.
- O carregamento individual de competências por candidato ou vaga poderá ser otimizado para evitar consultas do tipo **N+1**.
- A proteção de senhas, autenticação e autorização merecem atenção antes de um uso em produção.

---

# 🏭 Refatoração do backend — Design Patterns

A etapa anterior de Design Patterns deu continuidade ao Clean Code e SOLID, **sem refatorar o frontend TypeScript naquele momento**. O objetivo principal foi centralizar a escolha da implementação responsável por fornecer conexões JDBC, preservando a arquitetura e as regras de negócio existentes.

## Padrões utilizados e benefícios práticos

| Padrão | Onde é aplicado | Por que melhora o código |
|---|---|---|
| **Simple Factory** (padrão de criação) | `database/ConexaoFactory.groovy` | Concentra a criação e a escolha do `ConexaoProvider` em um único ponto, retirando do `Main` a instanciação direta do provedor PostgreSQL. |
| **Repository** (padrão de acesso a dados) | Interfaces em `repository/`, como `CandidatoRepository` e `EmpresaRepository` | Define contratos utilizados pelos Services, permitindo substituir ou simular implementações de persistência sem reescrever as regras dos serviços. |
| **DAO — Data Access Object** (padrão de persistência) | Classes em `dao/`, como `CandidatoDAO` e `VagaDAO` | Encapsula operações JDBC, instruções SQL e mapeamento de resultados, evitando SQL na interface ou nos Services. |

**Injeção de dependências por construtor** complementa esses padrões: DAOs recebem `ConexaoProvider`, Services recebem contratos Repository e o `Main` monta as dependências. Trata-se de uma técnica de projeto, não de um padrão GoF adicional implementado nesta etapa.

### 1. Simple Factory — implementação adicionada

**Arquivo:** `src/main/groovy/com/linketinder/database/ConexaoFactory.groovy`

```groovy
package com.linketinder.database

class ConexaoFactory {

    static ConexaoProvider criarProvider(String tipoBanco) {
        if ("postgresql".equalsIgnoreCase(tipoBanco?.trim())) {
            return new ConexaoPostgresProvider()
        }

        throw new IllegalArgumentException(
                "Banco de dados não suportado: ${tipoBanco}"
        )
    }
}
```

A criação do provedor no `Main.groovy` passou de:

```groovy
ConexaoProvider conexaoProvider = new ConexaoPostgresProvider()
```

para:

```groovy
ConexaoProvider conexaoProvider = ConexaoFactory.criarProvider("postgresql")
```

Dessa forma, o `Main` conhece a Factory e a interface `ConexaoProvider`, mas deixa de depender diretamente da classe concreta `ConexaoPostgresProvider`. A criação e a seleção da implementação ficam concentradas em uma classe de infraestrutura.

**Fluxo da conexão:**

```text
Main
  └── ConexaoFactory.criarProvider("postgresql")
        └── ConexaoProvider → ConexaoPostgresProvider
              └── ConexaoDB.conectar()
                    └── JDBC / DriverManager → PostgreSQL

DAO → ConexaoProvider.obterConexao() → Connection
```

A implementação aceita `postgresql`, variações entre maiúsculas/minúsculas e espaços externos; tipos não suportados provocam `IllegalArgumentException`. A fábrica **não abre uma conexão ao ser chamada**: ela cria o objeto provedor, que abre conexões quando os DAOs solicitam.

### 2. Repository — abstração da persistência

Os Services dependem das interfaces em `repository/` em vez de acoplarem suas regras de negócio a classes JDBC específicas. Por exemplo, `CandidatoDAO implements CandidatoRepository`. Isso permite criar mocks para testes unitários e considerar implementações alternativas no futuro.

### 3. DAO — encapsulamento de JDBC e SQL

Os DAOs implementam as interfaces Repository e realizam consultas com `PreparedStatement`, gerenciam `Connection`, `PreparedStatement` e `ResultSet` usando `try-with-resources` e transformam os resultados em objetos de domínio. A Factory não substitui esses DAOs: os dois padrões atuam em responsabilidades distintas.

## Sobre Singleton e portabilidade de bancos

**Singleton não foi implementado**, pois o enunciado o apresenta como opcional e manter uma única instância global de `Connection` JDBC não oferece vantagem neste projeto. Compartilhar a mesma conexão pode dificultar transações, fechamento correto de recursos e acessos concorrentes. Os DAOs continuam obtendo e fechando suas próprias conexões.

**Limitação de portabilidade:** atualmente, a Factory seleciona apenas PostgreSQL. Para suportar outro SGBD, será necessário implementar outro provedor, configurar o driver e revisar SQL específico do PostgreSQL utilizado nos DAOs, como `RETURNING id`. A Factory desacopla a **seleção do provedor**; ela não traduz SQL automaticamente.

## Validação desta etapa

O novo `ConexaoFactorySpec` possui **4 testes unitários com Spock**, cobrindo seleção do PostgreSQL, normalização de maiúsculas/espaços, rejeição de banco não suportado e entrada nula. Os testes não acessam o banco, pois apenas instanciam o provider.

```bash
# Testes isolados da Factory
./gradlew test --tests "databaseTest.ConexaoFactorySpec"

# Suíte completa
./gradlew clean test
```

**Resultado informado na execução local: 126 testes unitários aprovados**, incluindo os 4 novos testes da Factory. O menu também foi executado com sucesso após a integração, confirmando o funcionamento da aplicação no ambiente configurado.

**Escopo preservado:** a lógica e a persistência JDBC de curtidas e matches não foram alteradas nesta etapa; o frontend e os scripts de banco também não receberam novas refatorações.

---

# 📋 Status atual do projeto

| Funcionalidade | Status |
|---|---|
| Estrutura de banco de dados (`database.sql`, `queries.sql`) | ✅ Implementado |
| Conexão JDBC com PostgreSQL | ✅ Implementado |
| Camada DAO | ✅ Implementado |
| CRUD de candidatos, empresas, competências e vagas | ✅ Implementado |
| Modelagem de candidatos | ✅ Implementado |
| Modelagem de empresas | ✅ Implementado |
| Modelagem de Skills | ✅ Implementado |
| Modelagem de vagas | ✅ Implementado |
| Classe Informacoes | ✅ Implementado |
| CPF | ✅ Implementado |
| CNPJ | ✅ Implementado |
| País da empresa | ✅ Implementado |
| Estado | ✅ Implementado |
| Persistência PostgreSQL via JDBC | ✅ Implementado |
| Dados iniciais pré-cadastrados | ✅ Implementado |
| CandidatoService | ✅ Implementado |
| EmpresaService | ✅ Implementado |
| Estrutura SQL para curtidas e matches | ✅ Implementado com triggers e constraints relacionais no PostgreSQL |
| Integração JDBC de curtidas e Match | ⏳ Evolução futura do backend Groovy |
| Refatoração Clean Code e SOLID do backend | ✅ Concluída nesta etapa |
| Testes automatizados do backend (Spock) | ✅ Suíte executada com sucesso após a adição dos testes REST; total atual deve ser consultado no relatório |
| API REST sem framework web — candidatos, empresas e vagas | ✅ Três POSTs testados com `201 Created` |
| Simple Factory para seleção de provedor JDBC | ✅ Implementada e integrada ao Main |
| Busca de candidato | ✅ Implementado |
| Menu de terminal | ✅ Implementado |
| Listagem de candidatos | ✅ Implementado |
| Listagem de empresas | ✅ Implementado |
| Validação do MVP | ✅ Implementado |
| Frontend TypeScript | ✅ Implementado |
| Cadastro, edição e exclusão de candidatos | ✅ Implementado |
| Cadastro, edição e exclusão de empresas | ✅ Implementado |
| Cadastro, edição e exclusão de vagas | ✅ Implementado |
| Cancelamento de cadastro/edição | ✅ Implementado |
| Persistência com LocalStorage | ✅ Implementado |
| Controle de edição entre abas | ✅ Implementado |
| Heartbeat e expiração dos bloqueios | ✅ Implementado |
| Exclusão em cascata das vagas | ✅ Implementado |
| Visualização anônima de candidatos e empresas | ✅ Implementado |
| Visualização das vagas | ✅ Implementado |
| Gráfico de Skills | ✅ Implementado |
| Índice de afinidade candidato/vaga | ✅ Implementado |
| Exibição da afinidade no card da vaga | ✅ Implementado |
| Likes/Matches em memória (legado da etapa anterior no backend) | ✅ Implementado |
| Persistência e triggers de Match no PostgreSQL | ✅ Implementado (`database.sql`, `queries.sql` e MER atualizado) |
| Match persistido via JDBC no backend | ⏳ Evolução futura do backend Groovy |

---

# 🧠 Conceitos aplicados

O projeto tem como objetivo principal praticar conceitos de **Programação Orientada a Objetos**, **Estruturas de Dados**, modelagem de dados relacionais e desenvolvimento frontend.

Entre os conceitos utilizados estão:

- Classes e Objetos;
- Herança e Composição;
- Encapsulamento;
- Modelagem Relacional (DDL/DML);
- JDBC (`Connection`, `PreparedStatement`, `ResultSet`);
- Padrão DAO;
- Padrão Repository;
- Padrão Simple Factory para seleção do provedor JDBC;
- CRUD persistente;
- Chaves estrangeiras e relacionamentos N:N / 1:N;
- Junções SQL (`INNER JOIN`);
- Interfaces TypeScript;
- Tipagem estática;
- Listas e Coleções;
- Métodos e Construtores;
- `Optional`;
- Closures;
- Separação de responsabilidades e camadas;
- Clean Code e princípios SOLID aplicados ao backend;
- Injeção de dependências por construtor e contratos Repository/ConexaoProvider;
- Testes unitários com Spock e mocks;
- Tratamento consistente de exceções e `try-with-resources`;
- Services e Components;
- Manipulação da API DOM;
- Persistência e sincronização local;
- Controle de concorrência entre abas;
- Cálculo de métricas e afinidade.

---

# 🧱 Decisões de arquitetura

## Candidato e Pessoa

`Candidato` herda de `Pessoa`, permitindo compartilhar informações gerais.

```text
Pessoa
   ▲
   │
Candidato
```

## Empresa independente

`Empresa` não herda de `Pessoa`. Essa decisão foi tomada porque candidato e empresa representam entidades diferentes e possuem identificadores próprios:

```text
Candidato → CPF
Empresa   → CNPJ
```

## Informacoes

Os dados de identificação e contato foram concentrados em `Informacoes`.

```text
Informacoes
├── nome
├── sobrenome
├── cep
├── email
├── whatsapp
└── pais
```

Essa organização facilita o controle sobre quais informações serão disponibilizadas durante a experiência anônima.

## Vaga pertence à Empresa

Uma empresa possui uma lista de vagas:

```text
Empresa
└── List<Vaga>
```

O modelo permite que uma empresa possua várias vagas simultâneas.

## Skills compartilhadas no domínio

No backend, as Skills são associadas tanto aos candidatos quanto às vagas.

```text
Skill
 ▲
 │
 ├──── Candidato
 │
 └──── Vaga
```

Isso permite comparar as competências dos candidatos com as competências exigidas pelas vagas.

## Afinidade no frontend

No frontend, as competências pertencem à vaga e são confrontadas com as competências do candidato para calcular a compatibilidade percentual.

---

# 🚀 Evolução planejada

A arquitetura estabelecida permite evoluções futuras como:

## 1. Integração entre frontend e backend

O backend já disponibiliza endpoints REST para cadastro; permanece planejado o consumo desses endpoints pelo frontend em substituição à manipulação exclusiva em `localStorage`.

## 2. Persistência de curtidas e Match

Integrar `CurtidaService` e `MatchService` ao PostgreSQL via JDBC, utilizando as tabelas de curtidas já existentes.

## 3. Autenticação e autorização

Mecanismos de login para restringir acessos e painéis por tipo de usuário (candidato ou empresa).

## 4. Filtro por Skills e comunicação pós-match

Ferramentas avançadas de busca e módulo de chat/contato direto desbloqueado após o match.

---

# ✅ Parte 2 — Integração entre BD e Backend

Nesta etapa, o backend deixou de depender exclusivamente de dados em memória e passou a acessar o PostgreSQL através de JDBC.

Foram implementados os quatro CRUDs obrigatórios:

```text
Candidato
Empresa
Competência
Vaga
```

Também foram integrados os relacionamentos:

```text
Candidato N:N Competência
Vaga      N:N Competência
Empresa   1:N Vaga
```

A arquitetura adotada para o acesso aos dados é:

```text
UI → Service → DAO → JDBC → PostgreSQL
```

O projeto utiliza apenas JDBC nessa etapa, sem JPA ou Hibernate.

O arquivo `database.sql` permite recriar a estrutura e os dados iniciais do projeto, incluindo cinco candidatos, cinco empresas, competências, vagas e seus relacionamentos.

O arquivo `queries.sql` contém consultas SQL utilizadas para validar a modelagem e os relacionamentos.

---

# 🎓 Requisitos das etapas do projeto

O projeto foi desenvolvido considerando os requisitos apresentados no desafio.

## Candidatos

- [x] Array/lista com pelo menos 5 candidatos;
- [x] Nome;
- [x] E-mail;
- [x] CPF;
- [x] Idade;
- [x] Estado;
- [x] CEP;
- [x] Descrição pessoal;
- [x] Skills.

## Empresas

- [x] Array/lista com pelo menos 5 empresas;
- [x] Nome;
- [x] E-mail corporativo;
- [x] CNPJ;
- [x] País;
- [x] Estado;
- [x] CEP;
- [x] Descrição da empresa;
- [x] Vagas com Skills.

## Interface

- [x] Menu no terminal;
- [x] Listagem de candidatos;
- [x] Listagem de empresas;
- [x] Interface web;
- [x] Cadastro de candidatos;
- [x] Cadastro de empresas;
- [x] Cadastro de vagas;
- [x] Índice de afinidade no anúncio de cada vaga.

## Parte 2 — JDBC

- [x] Integração do backend com PostgreSQL;
- [x] Uso de JDBC sem JPA/Hibernate;
- [x] CRUD de candidatos;
- [x] CRUD de empresas;
- [x] CRUD de competências;
- [x] CRUD de vagas;
- [x] Relação N:N candidato/competência;
- [x] Relação 1:N empresa/vaga;
- [x] Arquivo `database.sql` com dados iniciais;
- [x] Consultas de validação em `queries.sql`;
- [x] Main e Services consumindo os dados persistidos.

## Requisito opcional

- [x] Cadastro de novos candidatos;
- [x] Cadastro de novas empresas;
- [x] Cadastro de novas vagas.

---

# 🧭 Refatoração MVC — desafio de Padrões de Projeto

## Objetivo desta entrega

Reorganizar o Linketinder para explicitar a divisão entre **Model, View e Controller**, preservando as regras de negócio, o acesso a dados e as funcionalidades já existentes. A estrutura em camadas complementares (`Service`, `Repository` e `DAO`) foi mantida para reduzir o acoplamento e facilitar futuras evoluções com frameworks.

## Responsabilidades das camadas

| Camada | Responsabilidade | Exemplos do projeto |
|---|---|---|
| **Model** | Representar os dados e entidades do domínio | `Candidato`, `Empresa`, `Vaga`, `Skill` |
| **View** | Apresentar dados e obter ações do usuário | Componentes TypeScript, formulários e telas; menu de terminal no backend |
| **Controller** | Receber eventos/ações da View e coordenar a chamada aos serviços | `candidatoController.ts`, `empresaController.ts`, `vagaController.ts` e controladores do backend |
| **Service** | Concentrar operações e regras de negócio | Serviços de candidatos, empresas e vagas |
| **Repository / DAO** | Abstrair e executar persistência no PostgreSQL | Interfaces Repository e DAOs JDBC no backend |

**Importante (escopo da etapa MVC):** o frontend TypeScript e o backend Groovy são aplicações executadas separadamente. O frontend usa `localStorage`, enquanto o backend acessa PostgreSQL via JDBC. Na etapa posterior **K2-T6**, foi adicionada uma API HTTP ao backend, **sem integrar o frontend a ela**. O diagrama abaixo descreve os fluxos internos da etapa MVC, não uma comunicação direta entre as duas aplicações.

### Fluxo no frontend

```text
Interação do usuário
        ↓
View (components / layout)
        ↓
Controller (eventos de formulário, edição e exclusão)
        ↓
Service (operações e regras)
        ↓
Storage (localStorage)
        ↓
Controller → atualização da View
```

### Fluxo no backend

```text
View (menu de terminal)
        ↓
Controller (coordenação da ação)
        ↓
Service (regras de negócio)
        ↓
Repository (contratos) / DAO (JDBC)
        ↓
PostgreSQL
```

## Principais correções realizadas na refatoração

- **Responsabilidades de interface isoladas:** eventos de formulário, edição e exclusão foram organizados em controladores de candidatos, empresas e vagas, reduzindo a concentração de código no `main.ts` do frontend.
- **Separação da apresentação:** renderização de perfis, formulários, cartões de vagas e navegação permaneceu nos componentes de View, sem incorporar diretamente a persistência.
- **Encaminhamento de operações aos Services:** controladores invocam serviços que realizam operações de cadastro, consulta, edição e exclusão, evitando duplicação de regras.
- **Modelos explícitos:** entidades e contratos permanecem na camada Model, desacoplados das rotinas de apresentação.
- **Persistência encapsulada no backend:** Repository/DAO concentram acesso JDBC e SQL, preservando os Services independentes da interface.
- **Estado de navegação corrigido:** após cadastrar candidato ou empresa, o ID criado passa a ser selecionado. Após excluir empresa, a interface seleciona outra existente ou apresenta o estado sem empresa selecionada.
- **Testabilidade ampliada:** eventos e decisões dos controladores podem ser verificados com mocks e testes unitários, sem exigir banco ou navegador completo em cada caso.

**Benefícios:** maior coesão, menor acoplamento, facilidade de manutenção, testes isolados e organização mais adequada à futura adoção de frameworks.

## Execução e testes para entrega

Na raiz do projeto, para validar o backend:

```bash
./gradlew clean test
./gradlew run --console=plain
```

Na pasta `frontend/`, para validar a interface:

```bash
npm install
npm test
npm run build
npm run dev
```

**Últimos resultados informados durante a refatoração:** 302 testes unitários aprovados no frontend e 141 no backend. Esses números são registros das execuções informadas, **não substituem uma execução final** após eventuais modificações na árvore do projeto.

## Limitações conhecidas e próximos passos

- O backend agora expõe uma API REST HTTP; o frontend TypeScript ainda não a consome e mantém `localStorage`.
- A exclusão de empresa e vagas no `localStorage` envolve gravações separadas, sem atomicidade transacional; o tratamento de falhas parciais é uma melhoria futura.
- Persistência JDBC de curtidas e matches e autenticação completa permanecem para etapas futuras.

---


# 🌐 API REST/RESTful sem frameworks web

## Objetivo e escopo

Nesta etapa, o backend do Linketinder passou a oferecer **três endpoints HTTP de cadastro** com persistência no PostgreSQL, sem Spring, Grails ou Micronaut. A API usa `HttpServer` do JDK, Groovy para processamento e JSON para troca de dados. **Não há integração com o frontend TypeScript nesta etapa**: o frontend segue independente, utilizando `localStorage`.

### Recursos e responsabilidades

| Recurso | Responsabilidade |
|---|---|
| `ApiServer.groovy` | Inicializa o `HttpServer` em `127.0.0.1:8080`, monta as dependências e registra as rotas |
| `CandidatoHttpHandler.groovy` | Recebe e valida o cadastro de candidato, delegando ao `CandidatoService` |
| `EmpresaHttpHandler.groovy` | Recebe e valida o cadastro de empresa, delegando ao `EmpresaService` |
| `VagaHttpHandler.groovy` | Recebe e valida o cadastro de vaga, delegando ao `VagaService` |
| `HttpRequestUtil.groovy` | Valida rota, método, `Content-Type`, tamanho do corpo, JSON e campos obrigatórios |
| `HttpResponseUtil.groovy` | Serializa mapas em JSON e configura status, `Content-Type` e corpo da resposta |
| `Services` | Coordenam os cadastros, verificam a associação da vaga à empresa e usam os Repositories |
| `DAOs` + JDBC | Realizam consultas e `INSERT ... RETURNING id` no PostgreSQL |

Fluxo de comunicação:

```text
curl / cliente HTTP
       ↓
HttpServer → HttpHandler → HttpRequestUtil
       ↓
Service → Repository / DAO → JDBC → PostgreSQL
       ↓
HttpResponseUtil → JSON + código HTTP + Location
```

A API utiliza **injeção manual de dependências por construtor**: o servidor monta os DAOs com `ConexaoProvider`, os Services com Repositories/DAOs e os Handlers com Services. As regras de persistência existentes foram reaproveitadas.

## Contrato dos endpoints

| Método | Rota | Função | Resposta de sucesso |
|---|---|---|---|
| `GET` | `/api/health` | Verificação simples de disponibilidade do processo HTTP | `200 OK`, `{"status":"UP"}` |
| `POST` | `/api/candidatos` | Cadastra candidato sem Skills na requisição inicial | `201 Created` |
| `POST` | `/api/empresas` | Cadastra empresa sem vagas na requisição inicial | `201 Created` |
| `POST` | `/api/vagas` | Cadastra vaga associada a uma empresa existente | `201 Created` |

O `201 Created` inclui `Location: /api/<recurso>/<id>` e um JSON com o ID criado. **Os caminhos individuais informados por `Location` ainda não oferecem operações GET**; a implementação desta etapa é focada nos três cadastros exigidos.

### Exemplo 1 — Cadastrar candidato

```bash
curl -i -X POST http://localhost:8080/api/candidatos \
  -H 'Content-Type: application/json' \
  -d '{
    "nome": "Sandubinha",
    "sobrenome": "Silva",
    "dataNascimento": "2000-05-15",
    "email": "sandubinha@example.com",
    "cpf": "12345678909",
    "pais": "Brasil",
    "estado": "RJ",
    "cep": "20000000",
    "descricaoPessoal": "Desenvolvedor backend",
    "senha": "SenhaDeTeste123!"
  }'
```

Resposta de referência (ID ilustrativo):

```http
HTTP/1.1 201 Created
Location: /api/candidatos/6
Content-Type: application/json; charset=UTF-8

{"id":6,"mensagem":"Candidato cadastrado com sucesso."}
```

### Exemplo 2 — Cadastrar empresa

```bash
curl -i -X POST http://localhost:8080/api/empresas \
  -H 'Content-Type: application/json' \
  -d '{
    "nome": "ZG Tecnologia",
    "cnpj": "11222333000181",
    "email": "contato@example.com",
    "descricaoEmpresarial": "Empresa de tecnologia",
    "pais": "Brasil",
    "estado": "RJ",
    "cep": "20000000",
    "senha": "SenhaTeste123!"
  }'
```

Resposta de referência (ID ilustrativo):

```http
HTTP/1.1 201 Created
Location: /api/empresas/6
Content-Type: application/json; charset=UTF-8

{"id":6,"mensagem":"Empresa cadastrada com sucesso."}
```

### Exemplo 3 — Cadastrar vaga

O campo `empresaId` deve informar o identificador de uma **empresa já cadastrada**. Substitua `6` pelo ID obtido no cadastro anterior, caso seja diferente.

```bash
curl -i -X POST http://localhost:8080/api/vagas \
  -H 'Content-Type: application/json' \
  -d '{
    "empresaId": 6,
    "titulo": "Desenvolvedor Backend Júnior",
    "descricaoVaga": "Desenvolvimento de APIs REST com Groovy e Java.",
    "estado": "RJ",
    "cidade": "Rio de Janeiro"
  }'
```

Resposta de referência (IDs ilustrativos):

```http
HTTP/1.1 201 Created
Location: /api/vagas/6
Content-Type: application/json; charset=UTF-8

{"id":6,"empresaId":6,"mensagem":"Vaga cadastrada com sucesso."}
```

### Códigos HTTP tratados

| Status | Contexto |
|---|---|
| `200 OK` | Health check |
| `201 Created` | Cadastro realizado |
| `400 Bad Request` | JSON inválido, campos obrigatórios incorretos ou ID de empresa inválido |
| `404 Not Found` | Caminho inexistente nos handlers ou empresa da vaga não encontrada |
| `405 Method Not Allowed` | Método diferente do permitido (`Allow` é informado) |
| `413 Content Too Large` | Corpo superior a 16 KiB |
| `415 Unsupported Media Type` | `Content-Type` diferente de `application/json` |
| `500 Internal Server Error` | Falha na execução do cadastro |

## Como configurar e executar a API

### Pré-requisitos

- JDK compatível com o projeto, Gradle Wrapper (`gradlew`) e PostgreSQL em execução;
- banco de dados criado e estruturado a partir de `database/database.sql` (consulte a seção **Como executar → Banco de Dados**);
- variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD` disponíveis ao processo Java;
- porta TCP `8080` disponível em `127.0.0.1`.

O `build.gradle` utiliza os plugins `groovy`, `application` e `jacoco`, além das dependências `org.apache.groovy:groovy:5.0.8`, `org.apache.groovy:groovy-json:5.0.8` e driver JDBC PostgreSQL. O task `runApi` inicia `com.linketinder.api.ApiServer`, enquanto `run` continua executando o menu de terminal.

O arquivo `.env` **não é carregado automaticamente por `System.getenv()`**. Em Linux/macOS, a partir da raiz do projeto, carregue um `.env` confiável, de sintaxe compatível com shell, antes de executar:

```bash
set -a
source .env
set +a
./gradlew runApi --console=plain
```

Em outro terminal, faça o health check:

```bash
curl -i http://localhost:8080/api/health
```

Resposta esperada: `200 OK` com `{"status":"UP"}`. A execução de `runApi` permanece ativa enquanto o servidor HTTP estiver atendendo requisições. Encerre com `Ctrl+C`. Para o menu tradicional, utilize `./gradlew run --console=plain`.

> **Atenção:** não comite o `.env` ou credenciais. O servidor usa `127.0.0.1` para testes locais. Utilize exclusivamente dados fictícios nos exemplos. A implementação educacional atual não deve ser exposta publicamente como serviço de produção; autenticação, autorização, proteção de senhas e transações de gravações relacionadas são evoluções futuras, fora do escopo K2-T6.

## Testes automatizados e cobertura

Os testes utilizam **Spock** com Services/Repositories simulados, sem conexão com o PostgreSQL. Nos testes dos handlers, um `HttpServer` temporário com porta escolhida pelo sistema recebe requisições de um `HttpClient`, permitindo verificar respostas HTTP reais.

| Classe de teste | O que verifica |
|---|---|
| `apiTest.CandidatoHttpHandlerSpec` | `201`, `400`, `405`, `415`, `500` e `Location` |
| `apiTest.EmpresaHttpHandlerSpec` | `201`, `400`, `405`, `415`, `500` e `Location` |
| `apiTest.VagaHttpHandlerSpec` | `201`, `400`, `404`, `405`, `413`, `415`, `500`, validações de `empresaId` e `Location` |
| `serviceTest.VagaServiceSpec` | Associação com empresa existente, rejeição de ID inválido/empresa inexistente e falha simulada no Repository |

```bash
# Executar apenas os testes REST
./gradlew test --tests 'apiTest.*' --console=plain

# Testar especificamente o serviço de vagas
./gradlew test --tests 'serviceTest.VagaServiceSpec' --console=plain

# Executar toda a suíte e gerar o JaCoCo
./gradlew clean test jacocoTestReport --console=plain
```

Relatórios: `build/reports/tests/test/index.html` e `build/reports/jacoco/test/html/index.html`.

Os três endpoints tiveram **cadastros manuais com `201 Created`** e os testes automatizados foram executados com sucesso durante o desenvolvimento. O número total de testes e os percentuais de cobertura devem ser consultados no relatório mais recente: não são valores fixos do projeto. Os testes com mocks não substituem a verificação de persistência com o banco real.

## Decisões e limitações da etapa REST

- **Sem framework web:** `HttpServer` e `HttpHandler` do próprio Java, não Tomcat/Spring/Grails.
- **REST sobre HTTP/JSON:** métodos e códigos HTTP adequados, separação cliente/servidor e comunicação stateless nas operações de cadastro.
- **Sem integração com frontend:** a interface TypeScript continua usando `localStorage`.
- **Reutilização do backend:** Services, Repositories, DAOs e JDBC existentes foram mantidos.
- **Sem cadastro de Skills neste contrato:** os cadastros iniciais usam listas vazias de Skills/vagas, apesar de o domínio já oferecer associações por outros fluxos.
- **Limitações conhecidas:** rotas individuais GET não implementadas, senhas ainda armazenadas conforme o fluxo legado, ausência de autenticação/autorização e falta de transação única em cadastros que envolvam múltiplas tabelas.

---

# 👨‍💻 Autor

**Nelson Lima da Costa Junior**

Projeto desenvolvido durante o **Acelera ZG / ZG-HERO 2026**.

---

# 📄 Licença

Este projeto foi desenvolvido por **Nelson Lima** para fins educacionais no contexto do **Acelera ZG / ZG-HERO 2026**.