# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi desenvolvido como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, utilizando **Groovy** no backend, **PostgreSQL/SQL** na camada de persistência relacional e uma interface web em **TypeScript**, com foco na aplicação prática de conceitos de **Programação Orientada a Objetos (POO)**, **Modelagem de Dados Relacional**, **Estruturas de Dados**, organização em camadas e desenvolvimento de uma interface funcional.

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
- scripts SQL para criação de schema e execução de consultas analíticas;
- dados iniciais;
- serviços;
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
- sistema de curtidas e Matches consolidando candidato, vaga e empresa.

---

# 🎯 Objetivo

O objetivo do projeto é construir uma plataforma de recrutamento capaz de:

- manter candidatos cadastrados;
- manter empresas cadastradas;
- associar Skills aos candidatos;
- associar Skills às vagas;
- associar vagas às empresas;
- estruturar o armazenamento relacional via scripts SQL (`database.sql`);
- executar consultas especializadas de matches e compatibilidade (`queries.sql`);
- disponibilizar serviços para consulta dos dados;
- disponibilizar uma interface de terminal;
- disponibilizar uma interface web;
- permitir cadastro e gerenciamento de candidatos;
- permitir cadastro e gerenciamento de empresas;
- permitir cadastro e gerenciamento de vagas;
- permitir a visualização anônima dos candidatos;
- permitir a visualização das vagas;
- calcular o índice de afinidade percentual entre candidato e vaga;
- permitir curtidas de candidatos em vagas;
- permitir curtidas de empresas em candidatos;
- identificar curtidas mútuas;
- criar e listar Matches com o vínculo completo entre candidato, vaga e empresa;
- liberar os dados completos após o Match;
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
│       ├── Data
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
│                   ├── data/
│                   │   └── DataInicializador.groovy
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

A pasta `database/` reúne os ficheiros de definição de esquema e consultas SQL:

- **`database.sql`**: Script DDL com a criação de tabelas para candidatos, empresas, vagas, competências e relacionamentos (curtidas e matches), contendo chaves primárias, estrangeiras e constraints de integridade.
- **`queries.sql`**: Script DML com consultas para relatórios, agregações de competências mais procuradas e listagem de correspondências confirmadas entre candidatos, vagas e empresas.

### Consulta e Consolidação de Matches

Exemplo de consulta executada em `queries.sql` para recuperar as correspondências estabelecidas:

```sql
SELECT 
    c.nome AS candidato,
    v.titulo AS vaga,
    e.nome AS empresa
FROM matches m
INNER JOIN candidatos c ON m.candidato_id = c.id
INNER JOIN vagas v ON m.vaga_id = v.id
INNER JOIN empresas e ON v.empresa_id = e.id;
```

#### Resultado das correspondências:

| candidato | vaga | empresa |
| :--- | :--- | :--- |
| Carlos | Desenvolvedor Java | Pastelsoft |
| João | Desenvolvedor Backend | TechBrasil |
| João | Desenvolvedor Java | Pastelsoft |
| Maria | Desenvolvedor Full Stack | CodeMais |
| Maria | Desenvolvedor Frontend | SanduTech |
| Pedro | Desenvolvedor C# | DevHouse |

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
├── idade
├── descricaoPessoal
└── skills
```

O candidato possui uma lista de `Skill`.

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
├── cep
├── email
└── whatsapp
```

A concentração dessas informações permite maior controle sobre quais dados poderão ser apresentados em cada contexto da aplicação.

---

## Skill

Representa uma competência que pode ser associada a candidatos e vagas.

Skills utilizadas:

```text
Java
Groovy
Python
Spring Framework
Angular
JavaScript
C#
SQL
```

---

## Vaga

Representa uma oportunidade disponibilizada por uma empresa.

```text
Vaga
├── id
├── titulo
├── descricaoVaga
└── skills
```

Cada vaga possui uma lista de Skills que representam as competências desejadas pela empresa.

---

# 💾 DataInicializador

A classe `DataInicializador` é responsável pelo carregamento dos dados iniciais utilizados pelo backend.

Atualmente são criados:

- **Skills no domínio**
- **5 candidatos**
- **5 empresas**
- **Vagas associadas**

As principais relações do modelo são:

```text
Empresa
└── List<Vaga>
    └── List<Skill>
```

E:

```text
Candidato
└── List<Skill>
```

O `DataInicializador` também disponibiliza uma operação para localizar uma Skill pelo nome.

---

# ⚙️ Services do backend

A camada `service` concentra as operações relacionadas ao domínio.

## CandidatoService

Responsável pelas operações relacionadas aos candidatos.

Exemplos:

```groovy
listarTodosCandidatos()

buscarCandidatoPorNome(String nome)
```

A busca por nome retorna um `Optional<Candidato>`, permitindo representar o cenário em que um candidato não é encontrado.

Exemplo:

```groovy
def resultado = candidatoService.buscarCandidatoPorNome("Nelson")
```

---

## EmpresaService

Responsável pelas operações relacionadas às empresas.

O serviço atua como camada intermediária entre os dados e a interface da aplicação.

Também disponibiliza a listagem de empresas em formato anônimo, ocultando informações de identificação e contato.

---

## CurtidaService

Responsável pelo registro das curtidas realizadas por candidatos e empresas.

O serviço permite:

- candidato curtir uma vaga;
- empresa curtir um candidato;
- armazenar as curtidas realizadas;
- solicitar ao `MatchService` a verificação de uma possível curtida mútua.

A curtida do candidato é relacionada à vaga, enquanto a curtida da empresa é relacionada ao candidato.

---

## MatchService

Responsável por verificar e registrar Matches.

Um Match é criado quando:

1. o candidato curtiu uma vaga pertencente à empresa;
2. a empresa curtiu o candidato;
3. ainda não existe um Match entre aquele candidato e aquela empresa.

O Match conecta o **candidato, a vaga de interesse e a empresa contratante**, permitindo identificar com precisão a oportunidade correspondida.

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

As curtidas são armazenadas pelo `CurtidaService`.

---

# 🤝 Match

O Match acontece quando existe **interesse mútuo entre um candidato e uma empresa**.

O fluxo implementado é:

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

Quando o `MatchService` identifica as duas curtidas:

1. verifica a curtida do candidato;
2. verifica a curtida da empresa;
3. verifica se o Match já existe;
4. cria o Match caso ainda não exista;
5. armazena o Match para consulta posterior.

---

# ▶️ Como executar

## Banco de Dados

1. Conecte-se ao seu SGBD relacional (PostgreSQL).
2. Execute o script de estrutura e dados iniciais:
   ```bash
   psql -U postgres -d linketinder -f database/database.sql
   ```
3. Execute o script de consultas para validar os matches e afinidades:
   ```bash
   psql -U postgres -d linketinder -f database/queries.sql
   ```

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

Foram realizados testes envolvendo:

- carregamento dos candidatos, empresas e vagas iniciais;
- busca de candidato por nome;
- tratamento de candidato não encontrado através de `Optional`;
- listagem anônima de candidatos;
- listagem anônima de vagas;
- curtida de candidato em vaga;
- curtida de empresa em candidato;
- identificação de curtida mútua;
- criação de Match;
- listagem de Matches;
- liberação das informações após o Match;
- navegação pelo menu;
- encerramento da aplicação.

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
| Modelagem de candidatos | ✅ Implementado |
| Modelagem de empresas | ✅ Implementado |
| Modelagem de Skills | ✅ Implementado |
| Modelagem de vagas | ✅ Implementado |
| Classe Informacoes | ✅ Implementado |
| CPF | ✅ Implementado |
| CNPJ | ✅ Implementado |
| País da empresa | ✅ Implementado |
| Estado | ✅ Implementado |
| DataInicializador | ✅ Implementado |
| Dados iniciais pré-cadastrados | ✅ Implementado |
| CandidatoService | ✅ Implementado |
| EmpresaService | ✅ Implementado |
| CurtidaService | ✅ Implementado |
| MatchService | ✅ Implementado |
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
| Sistema de Likes e Matches | ✅ Implementado |
| Tabela de Matches (candidato ↔ vaga ↔ empresa) | ✅ Implementado |

---

# 🧠 Conceitos aplicados

O projeto tem como objetivo principal praticar conceitos de **Programação Orientada a Objetos**, **Estruturas de Dados**, modelagem de dados relacionais e desenvolvimento frontend.

Entre os conceitos utilizados estão:

- Classes e Objetos;
- Herança e Composição;
- Encapsulamento;
- Modelagem Relacional (DDL/DML);
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
├── cep
├── email
└── whatsapp
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

## 2. Persistência permanente conectada

Integração dos serviços backend diretamente com o PostgreSQL através dos scripts da pasta `database/`.

## 3. Autenticação e autorização

Mecanismos de login para restringir acessos e painéis por tipo de usuário (candidato ou empresa).

## 4. Filtro por Skills e comunicação pós-match

Ferramentas avançadas de busca e módulo de chat/contato direto desbloqueado após o match.

---

# 🎓 Requisitos do desafio

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