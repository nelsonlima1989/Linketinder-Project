# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi desenvolvido como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, utilizando **Groovy** no backend e uma interface web em **TypeScript**, com foco na aplicação prática de conceitos de **Programação Orientada a Objetos (POO)**, **Estruturas de Dados**, organização em camadas e desenvolvimento de uma interface funcional.

A proposta do Linketinder é aproximar **candidatos** e **empresas** com base em suas **Skills**, reduzindo a influência de popularidade e priorizando a compatibilidade entre as competências do candidato e as necessidades da vaga.

---

# 📌 Sobre o projeto

O Linketinder surgiu como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, que identificou uma dificuldade nos processos tradicionais de recrutamento: encontrar profissionais com competências relevantes sem depender exclusivamente de perfis com maior visibilidade.

A proposta combina:

- O conceito de **Skills** utilizado em plataformas profissionais;
- A relação entre **candidato e empresa**;
- O conceito de **Like e Match** inspirado em aplicativos de relacionamento;
- Uma experiência de interação que prioriza o **anonimato antes do Match**;
- Uma interface web para cadastro, visualização e gerenciamento de candidatos, empresas e vagas.

O projeto atualmente possui:

- domínio estruturado;
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
- visualização anônima de candidatos;
- sistema de Skills;
- gráfico de Skills;
- sistema de curtidas e Matches no backend.

---

# 🎯 Objetivo

O objetivo do projeto é construir uma plataforma de recrutamento capaz de:

- manter candidatos cadastrados;
- manter empresas cadastradas;
- associar Skills aos candidatos;
- associar Skills às vagas;
- associar vagas às empresas;
- disponibilizar serviços para consulta dos dados;
- disponibilizar uma interface de terminal;
- disponibilizar uma interface web;
- permitir cadastro e gerenciamento de candidatos;
- permitir cadastro e gerenciamento de empresas;
- permitir cadastro e gerenciamento de vagas;
- permitir a visualização anônima dos candidatos;
- permitir a visualização das vagas;
- permitir curtidas de candidatos em vagas;
- permitir curtidas de empresas em candidatos;
- identificar curtidas mútuas;
- criar e listar Matches;
- liberar os dados completos após o Match;
- validar a integridade dos dados carregados;
- manter uma arquitetura preparada para futuras evoluções.

---

# 🛠️ Tecnologias utilizadas

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

O projeto está dividido em duas partes principais:

```text
Linketinder
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

## Backend

```text
src/
└── main/
    └── groovy/
        └── com/
            └── linketinder/
                ├── Main.groovy
                │
                ├── data/
                │   └── DataInicializador.groovy
                │
                ├── model/
                │   ├── Pessoa.groovy
                │   ├── Candidato.groovy
                │   ├── Empresa.groovy
                │   ├── Informacoes.groovy
                │   ├── Skill.groovy
                │   ├── Vaga.groovy
                │   ├── Curtida.groovy
                │   ├── Match.groovy
                │   ├── TipoCurtida.groovy
                │   ├── CandidatoAnonimo.groovy
                │   └── EmpresaAnonima.groovy
                │
                ├── service/
                │   ├── CandidatoService.groovy
                │   ├── EmpresaService.groovy
                │   ├── CurtidaService.groovy
                │   └── MatchService.groovy
                │
                └── ui/
                    └── Menu.groovy
```

## Frontend

```text
frontend/
├── index.html
├── package.json
├── package-lock.json
├── tsconfig.json
│
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
    │   └── vagaService.ts
    │
    └── main.ts
```

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

Skills utilizadas inicialmente:

```text
Java
Groovy
Python
Spring Framework
Angular
JavaScript
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

- **6 Skills**
- **5 candidatos**
- **5 empresas**
- **5 vagas**

Cada empresa possui inicialmente uma vaga.

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

O Match é estabelecido entre **candidato e empresa**, e não diretamente entre candidato e vaga.

Isso permite que uma empresa possua várias vagas e que, após o Match, candidato e empresa possam discutir qual oportunidade faz mais sentido.

---

# 🌐 Frontend

O frontend foi desenvolvido em **TypeScript**, utilizando uma abordagem modular com separação entre modelos, componentes, serviços, dados e navegação.

A interface permite trabalhar com candidatos, empresas e vagas de forma independente.

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

# 🗄️ Persistência no frontend

O frontend utiliza o `localStorage` do navegador para persistir os dados.

Os serviços responsáveis pelos dados utilizam uma camada própria de armazenamento:

```text
services/
└── storage.ts
```

A aplicação carrega os dados salvos quando disponíveis e utiliza os dados iniciais como base quando ainda não existem registros no `localStorage`.

Essa abordagem permite testar o MVP sem a necessidade de uma API ou banco de dados para o funcionamento da interface.

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

Esse mecanismo foi testado para garantir que:

- o mesmo registro não seja editado simultaneamente;
- registros diferentes possam ser editados ao mesmo tempo;
- salvar libere o bloqueio;
- cancelar libere o bloqueio;
- bloqueios abandonados expirem.

---

# 📊 Gráfico de Skills

O frontend possui uma visualização gráfica das Skills cadastradas.

O gráfico permite visualizar a quantidade de ocorrências de cada Skill entre os candidatos.

Essa funcionalidade utiliza **Chart.js**.

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

O nome e os dados de identificação e contato da empresa permanecem ocultos.

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
  MATCH
```

Quando o `MatchService` identifica as duas curtidas:

1. verifica a curtida do candidato;
2. verifica a curtida da empresa;
3. verifica se o Match já existe;
4. cria o Match caso ainda não exista;
5. armazena o Match para consulta posterior.

### Regra importante

O Match é estabelecido entre:

```text
Candidato ↔ Empresa
```

e não entre:

```text
Candidato ↔ Vaga
```

A vaga é utilizada para registrar o interesse inicial do candidato e identificar a empresa relacionada.

Isso permite que uma empresa possua várias vagas.

---

# ▶️ Como executar

## Backend

### Pré-requisitos

Para executar o backend, é necessário possuir:

- Java instalado;
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

Para executar o frontend em desenvolvimento, utilize o servidor configurado no projeto.

A interface pode então ser acessada pelo navegador.

---

# 🧪 Validação

O projeto foi testado em diferentes níveis.

## Backend

Foram realizados testes envolvendo:

- carregamento dos cinco candidatos;
- carregamento das cinco empresas;
- carregamento das cinco vagas;
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

- cadastro de candidato;
- edição de candidato;
- exclusão de candidato;
- cadastro de empresa;
- edição de empresa;
- exclusão de empresa;
- cadastro de vaga;
- edição de vaga;
- exclusão de vaga;
- cancelamento de cadastro;
- cancelamento de edição;
- visualização de perfis;
- visualização de vagas;
- visualização anônima de candidatos;
- visualização das vagas da empresa;
- persistência através de `localStorage`;
- gráfico de Skills;
- bloqueio de edição entre abas;
- heartbeat dos bloqueios;
- expiração dos bloqueios;
- exclusão em cascata das vagas ao excluir uma empresa;
- possibilidade de editar registros diferentes simultaneamente.

---

# 📋 Status atual do projeto

| Funcionalidade | Status |
|---|---|
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
| 5 candidatos pré-cadastrados | ✅ Implementado |
| 5 empresas pré-cadastradas | ✅ Implementado |
| 5 vagas pré-cadastradas | ✅ Implementado |
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
| Cadastro de candidatos | ✅ Implementado |
| Edição de candidatos | ✅ Implementado |
| Exclusão de candidatos | ✅ Implementado |
| Cadastro de empresas | ✅ Implementado |
| Edição de empresas | ✅ Implementado |
| Exclusão de empresas | ✅ Implementado |
| Cadastro de vagas | ✅ Implementado |
| Edição de vagas | ✅ Implementado |
| Exclusão de vagas | ✅ Implementado |
| Cancelamento de cadastro/edição | ✅ Implementado |
| Persistência com LocalStorage | ✅ Implementado |
| Controle de edição entre abas | ✅ Implementado |
| Heartbeat dos bloqueios | ✅ Implementado |
| Expiração dos bloqueios | ✅ Implementado |
| Exclusão em cascata das vagas | ✅ Implementado |
| Visualização anônima de candidatos | ✅ Implementado |
| Visualização das vagas | ✅ Implementado |
| Gráfico de Skills | ✅ Implementado |
| Sistema de Likes | ✅ Implementado |
| Match | ✅ Implementado |

---

# 🧠 Conceitos aplicados

O projeto tem como objetivo principal praticar conceitos de **Programação Orientada a Objetos**, **Estruturas de Dados**, organização de responsabilidades e desenvolvimento frontend.

Entre os conceitos utilizados estão:

- Classes;
- Objetos;
- Herança;
- Composição;
- Encapsulamento;
- Interfaces TypeScript;
- Tipagem estática;
- Listas;
- Arrays;
- Relacionamento entre objetos;
- Métodos;
- Construtores;
- `Optional`;
- Closures;
- Busca em coleções;
- Separação de responsabilidades;
- Organização em camadas;
- Services;
- Components;
- Manipulação do DOM;
- Eventos;
- LocalStorage;
- Controle de estado;
- Controle de edição entre abas.

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

`Empresa` não herda de `Pessoa`.

Essa decisão foi tomada porque candidato e empresa representam entidades diferentes e possuem identificadores próprios:

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

Essa organização facilita o controle futuro sobre quais informações serão disponibilizadas durante a experiência anônima.

## Vaga pertence à Empresa

Uma empresa possui uma lista de vagas:

```text
Empresa
└── List<Vaga>
```

No MVP inicial, cada empresa possui uma vaga, mas o modelo permite que uma empresa possua várias vagas.

## Skills compartilhadas

As Skills são carregadas pelo `DataInicializador` e associadas tanto aos candidatos quanto às vagas.

```text
Skill
 ▲
 │
 ├──── Candidato
 │
 └──── Vaga
```

Isso permite futuramente comparar as competências dos candidatos com as competências exigidas pelas vagas.

## Separação do frontend

O frontend foi organizado para evitar que toda a lógica fique concentrada no `main.ts`.

A responsabilidade foi distribuída entre:

```text
models/
    ↓
services/
    ↓
components/
    ↓
main.ts
```

Os `models` representam os dados, os `services` concentram regras e operações, os `components` cuidam da renderização e o `main.ts` coordena os eventos e a navegação.

---

# 🚀 Evolução planejada

O MVP atual representa uma primeira etapa do Linketinder.

A arquitetura permite evoluções futuras como:

## 1. Integração entre frontend e backend

Substituir gradualmente a persistência local por uma API, permitindo que o frontend TypeScript consuma os serviços do backend.

## 2. Persistência em banco de dados

Substituir os dados em memória por uma camada de persistência permanente.

## 3. Autenticação

Adicionar autenticação para candidatos e empresas.

## 4. Controle de acesso

Restringir funcionalidades de acordo com o tipo de usuário autenticado.

## 5. Filtro por Skills

Permitir que candidatos filtrem vagas e que empresas filtrem candidatos de acordo com competências.

## 6. Evolução do Match

Expandir a experiência de Match e permitir comunicação entre as partes após o interesse mútuo.

## 7. Novos recursos de recrutamento

Adicionar funcionalidades relacionadas a:

- filtros;
- gerenciamento de vagas;
- candidaturas;
- notificações;
- comunicação;
- histórico de Matches;
- recomendações baseadas em Skills.

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
- [x] Cadastro de vagas.

## Requisito opcional

- [x] Cadastro de novos candidatos;
- [x] Cadastro de novas empresas;
- [x] Cadastro de novas vagas.

---

# 👨‍💻 Autor

**Nelson Lima da Costa Júnior**

Projeto desenvolvido durante o **Acelera ZG / ZG-HERO 2026**.

---

# 📌 Status

## MVP funcional

O Linketinder possui atualmente:

- estrutura de domínio;
- candidatos;
- empresas;
- vagas;
- Skills;
- informações de contato;
- carregamento dos dados;
- serviços;
- interface de terminal;
- interface web;
- cadastro;
- edição;
- exclusão;
- persistência local;
- anonimização nas listagens;
- controle de edição entre abas;
- sistema de curtidas;
- verificação de curtidas mútuas;
- criação de Matches;
- listagem de Matches;
- liberação das informações completas após o Match;
- gráfico de Skills;
- validações;
- organização modular do frontend.

A evolução planejada do projeto segue a ideia:

```text
Skills
   ↓
Filtros
   ↓
Anonimato
   ↓
Likes
   ↓
Match
   ↓
Comunicação
```

O objetivo é transformar gradualmente o MVP em um sistema de recrutamento inspirado no conceito do LinkedIn e na mecânica de interação do Tinder.

---

# 📄 Licença

Este projeto foi desenvolvido por **Nelson Lima** para fins educacionais no contexto do **Acelera ZG / ZG-HERO 2026**.
