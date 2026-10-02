# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi desenvolvido como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, utilizando **Groovy** no backend, **PostgreSQL** como banco de dados relacional, integração via **JDBC** e uma interface web em **TypeScript**. O projeto aplica conceitos de **Programação Orientada a Objetos (POO)**, **DAO**, **Modelagem de Dados Relacional**, **Estruturas de Dados**, organização em camadas e desenvolvimento de uma interface funcional.

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
- estrutura de banco preparada para curtidas; a integração JDBC das curtidas e a lógica de Match fazem parte da próxima evolução do backend.

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
- evoluir a integração JDBC para a persistência de curtidas e detecção de Match em uma etapa posterior;
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
│       ├── Services
│       ├── DAO
│       ├── Database
│       └── Model
│
└── Frontend
    └── TypeScript
        ├── Components
        ├── Services
        ├── Models
        ├── Data
        └── Main
```

No backend, o fluxo principal de acesso aos dados passou a seguir:

```text
Main
  ↓
Menu
  ↓
Service
  ↓
DAO
  ↓
JDBC
  ↓
PostgreSQL
```

A camada de interface não acessa o banco diretamente. Os `Services` concentram as operações da aplicação e utilizam os `DAOs` para executar os comandos SQL via JDBC.

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
│                   ├── dao/
│                   │   ├── CandidatoDAO.groovy
│                   │   ├── CandidatoCompetenciaDAO.groovy
│                   │   ├── CompetenciaDAO.groovy
│                   │   ├── EmpresaDAO.groovy
│                   │   ├── VagaDAO.groovy
│                   │   └── VagaCompetenciaDAO.groovy
│                   │
│                   ├── database/
│                   │   └── ConexaoDB.groovy
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
        │   ├── candidatoService.ts
        │   ├── empresaService.ts
        │   ├── edicaoCandidatoService.ts
        │   ├── edicaoEmpresaService.ts
        │   ├── edicaoVagaService.ts
        │   ├── idService.ts
        │   ├── navegacaoService.ts
        │   ├── skillService.ts
        │   ├── storage.ts
        │   ├── vagaService.ts
        │   └── afinidadeService.ts
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
```

Também são utilizadas tabelas associativas:

```text
candidato_competencias
vaga_competencias
```

A modelagem representa:

```text
Candidato N:N Competência
Vaga      N:N Competência
Empresa   1:N Vaga
```

O banco também possui as estruturas:

```text
curtidas_candidato_vaga
curtidas_empresa_candidato
```

que serão utilizadas na próxima etapa para persistir curtidas e implementar a lógica de Match via JDBC.

## Integração JDBC e DAO

A Parte 2 introduziu a integração entre o backend Groovy e o PostgreSQL através de **JDBC**, sem utilização de JPA ou Hibernate.

A conexão é centralizada em:

```text
ConexaoDB.groovy
```

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

## CurtidaService

O projeto já possui a lógica de curtidas desenvolvida nas etapas anteriores em memória.

O banco de dados também já contém as tabelas necessárias para representar:

- candidato curtindo uma vaga;
- empresa curtindo um candidato.

A integração dessas operações com JDBC será realizada no próximo desafio.

---

## MatchService

A lógica de Match já existe como conceito no domínio da aplicação e será evoluída na próxima etapa para trabalhar com as curtidas persistidas via JDBC.

Um Match ocorre conceitualmente quando:

1. o candidato curte uma vaga;
2. a vaga pertence a uma determinada empresa;
3. essa mesma empresa curte o candidato.

A persistência e integração completa dessa regra com o banco fazem parte do próximo desafio.

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

Na versão atual do primeiro desafio, a estrutura relacional das curtidas já existe no PostgreSQL. A persistência das ações do `CurtidaService` via JDBC será implementada na etapa seguinte.

---

# 🤝 Match

O Match acontece quando existe **interesse mútuo entre um candidato e uma empresa**.

O fluxo de negócio previsto é:

```text
Candidato
    │
    │ curte uma vaga
    ▼
  Vaga
    │
    │ pertence à
    ▼
  Empresa
    │
    │ curte o candidato
    ▼
 Candidato
    ↓
  MATCH (Candidato ↔ Vaga ↔ Empresa)
```

Na próxima etapa, o `MatchService` será integrado às curtidas persistidas para:

1. verificar a curtida do candidato;
2. verificar a curtida da empresa;
3. identificar a reciprocidade;
4. registrar ou disponibilizar o Match;
5. liberar os dados previstos pela regra de negócio.

---

# ▶️ Como executar

## Banco de Dados

1. Crie um banco PostgreSQL para o projeto.
2. Execute o script de estrutura e dados iniciais:

   ```bash
   psql -h localhost -U seu_usuario -d linketinder -f database/database.sql
   ```

3. Para executar as consultas de validação:

   ```bash
   psql -h localhost -U seu_usuario -d linketinder -f database/queries.sql
   ```

4. Configure em `ConexaoDB.groovy` a URL JDBC, o usuário e a senha correspondentes ao seu ambiente PostgreSQL.

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

Caso o projeto possua uma configuração de execução pelo Gradle, utilize:

```bash
./gradlew run
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
| Estrutura SQL para curtidas | ✅ Implementado |
| Integração JDBC de curtidas e Match | ⏳ Próximo desafio |
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
| Likes/Matches em memória (legado da etapa anterior) | ✅ Implementado |
| Match persistido via JDBC | ⏳ Próximo desafio |

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

Consumo direto dos endpoints ou serviços em substituição à manipulação exclusiva em `localStorage`.

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

# 👨‍💻 Autor

**Nelson Lima da Costa Junior**

Projeto desenvolvido durante o **Acelera ZG / ZG-HERO 2026**.

---

# 📄 Licença

Este projeto foi desenvolvido por **Nelson Lima** para fins educacionais no contexto do **Acelera ZG / ZG-HERO 2026**.