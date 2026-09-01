# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi desenvolvido como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, utilizando **Groovy**, com foco na aplicação prática de conceitos de **Programação Orientada a Objetos (POO)** e **Estruturas de Dados**.

A proposta do Linketinder é aproximar **candidatos** e **empresas** com base em suas **Skills**, reduzindo a influência de popularidade e priorizando a compatibilidade entre as competências do candidato e as necessidades da vaga.

---

# 📌 Sobre o projeto

O Linketinder surgiu como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, que identificou uma dificuldade nos processos tradicionais de recrutamento: encontrar profissionais com competências relevantes sem depender exclusivamente de perfis com maior visibilidade.

A proposta combina:

* O conceito de **Skills** utilizado em plataformas profissionais;
* A relação entre **candidato e empresa**;
* O conceito de **Like e Match** inspirado em aplicativos de relacionamento;
* Uma experiência de interação que deverá priorizar o **anonimato antes do Match**.

Neste momento, o projeto possui um MVP funcional com estruturação do domínio, carregamento dos dados, serviços, menu de terminal, sistema de curtidas e geração de Matches.

---

# 🎯 Objetivo

O objetivo atual é construir um MVP funcional capaz de:

* manter candidatos pré-cadastrados;
* manter empresas pré-cadastradas;
* associar Skills aos candidatos;
* associar Skills às vagas;
* associar vagas às empresas;
* disponibilizar serviços para consulta dos dados;
* disponibilizar um menu no terminal;
* permitir a visualização anônima dos candidatos;
* permitir a visualização anônima das vagas;
* permitir curtidas de candidatos em vagas;
* permitir curtidas de empresas em candidatos;
* identificar curtidas mútuas;
* criar e listar Matches;
* liberar os dados completos após o Match;
* validar a integridade dos dados carregados.

A arquitetura foi construída pensando também na evolução futura do projeto.

---

# 🛠️ Tecnologias utilizadas

* **Groovy**
* **Java**
* **Gradle**
* **IntelliJ IDEA**

O projeto utiliza Groovy como linguagem principal, conforme especificado no desafio.

---

# 🏗️ Arquitetura

O projeto utiliza uma organização baseada na separação de responsabilidades:

```text
                    ┌───────────────┐
                    │     Main      │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │      UI       │
                    │     Menu      │
                    └───────┬───────┘
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
        ┌───────────────┐       ┌───────────────┐
        │ Candidato     │       │    Empresa    │
        │   Service     │       │    Service    │
        └───────┬───────┘       └───────┬───────┘
                │                       │
                └───────────┬───────────┘
                            ▼
                    ┌───────────────┐
                    │     Data      │
                    │Inicializador  │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │     Model     │
                    └───────────────┘
```

A intenção é evitar concentrar toda a lógica no `Main`, mantendo cada parte do sistema responsável por uma função específica.

---

# 📁 Estrutura do projeto

A estrutura atual do projeto está organizada da seguinte maneira:

```text
src/
└── main/
    └── groovy/
        └── com/
            └── linketinder/
                │
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

A estrutura poderá receber novos componentes conforme novas funcionalidades forem implementadas.

---

# 🧩 Model

A camada `model` representa as entidades principais do domínio.

## Pessoa

`Pessoa` representa atributos gerais compartilhados pelo candidato.

Atualmente:

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

Entretanto, o CNPJ **não é exposto na experiência de listagem atual**, considerando a futura regra de anonimato do Linketinder.

---

## Informacoes

A classe `Informacoes` concentra informações de identificação e contato utilizadas pelos perfis.

Atualmente:

```text
Informacoes
├── nome
├── cep
├── email
└── whatsapp
```

A concentração dessas informações permite maior controle sobre quais dados poderão ser apresentados em cada contexto da aplicação.

O `nome` foi colocado em `Informacoes` para que a arquitetura possa evoluir futuramente para uma experiência em que determinadas informações sejam ocultadas antes do Match.

---

## Skill

Representa uma competência que pode ser associada a candidatos e vagas.

Skills utilizadas atualmente:

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

A classe `DataInicializador` é responsável pelo carregamento dos dados iniciais utilizados pelo MVP.

Atualmente são criados:

* **6 Skills**
* **5 candidatos**
* **5 empresas**
* **5 vagas**

Cada empresa possui atualmente uma vaga.

As principais relações do modelo são:

```text
Empresa
   │
   └── List<Vaga>
            │
            └── List<Skill>
```

E:

```text
Candidato
   │
   └── List<Skill>
```

O `DataInicializador` também disponibiliza uma operação para localizar uma Skill pelo nome.

---

# ⚙️ Services

A camada `service` concentra as operações relacionadas ao domínio.

## CandidatoService

Responsável pelas operações relacionadas aos candidatos.

Atualmente disponibiliza operações como:

```groovy
listarTodosCandidatos()
```

e:

```groovy
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

Assim como o `CandidatoService`, o serviço atua como camada intermediária entre os dados e a interface da aplicação.

Também disponibiliza a listagem de empresas em formato anônimo, ocultando informações de identificação e contato.

---

## CurtidaService

Responsável pelo registro das curtidas realizadas por candidatos e empresas.

O serviço permite:

* candidato curtir uma vaga;
* empresa curtir um candidato;
* armazenar as curtidas realizadas;
* solicitar ao `MatchService` a verificação de uma possível curtida mútua.

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

O serviço também disponibiliza a listagem dos Matches registrados.

---

# 🖥️ Interface de usuário

O projeto atualmente utiliza uma interface simples baseada em terminal.

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

## Listar candidatos

A opção `1` apresenta os candidatos de forma anônima.

São exibidos:

* ID;
* Estado;
* Descrição pessoal;
* Skills.

Informações de identificação e contato, como nome, CPF, idade, CEP, e-mail e WhatsApp, permanecem ocultas.

## Listar vagas

A opção `2` apresenta as vagas disponíveis de forma anônima.

São exibidos:

* ID da vaga;
* Título;
* Descrição;
* País da empresa;
* Estado da empresa;
* Skills.

O nome e os dados de contato da empresa permanecem ocultos.

## Curtir vaga

A opção `3` permite que um candidato escolha uma vaga e demonstre interesse.

O fluxo é:

```text
Candidato
    │
    │ curte
    ▼
  Vaga
    │
    ▼
 Empresa
```

A vaga é selecionada pela posição apresentada na lista.

## Curtir candidato

A opção `4` permite que uma empresa escolha um candidato e demonstre interesse.

O candidato é apresentado de forma anônima durante a seleção.

## Listar matches

A opção `5` apresenta os Matches registrados.

Após o Match, são exibidas as informações completas do candidato e da empresa, incluindo dados de identificação e contato.

## Sair

A opção `0` encerra a aplicação.

# ▶️ Como executar

## Pré-requisitos

Para executar o projeto, é necessário possuir:

* Java instalado;
* Groovy;
* Gradle ou Gradle Wrapper;
* IntelliJ IDEA ou outra IDE compatível.

## Executando pela IDE

Abra o projeto no IntelliJ IDEA e execute o arquivo:

```text
Main.groovy
```

O programa iniciará o menu do Linketinder no terminal.

## Executando pelo Gradle

Caso o projeto possua uma configuração de execução pelo Gradle, utilize o comando correspondente definido no `build.gradle`.

Por exemplo:

```bash
./gradlew run
```

No Windows:

```bash
gradlew.bat run
```

---

# 🧪 Validação do MVP

O projeto possui validações para verificar a integridade dos dados iniciais.

Durante a validação foram confirmados:

```text
Candidatos cadastrados: 5
Empresas cadastradas: 5

Todos os candidatos possuem Skills.
Todas as empresas possuem CNPJ.
Todas as empresas possuem uma vaga.
Todas as vagas possuem Skills.

MVP validado com sucesso!
```

Também foram realizados testes dos serviços e do menu, incluindo:

* carregamento dos cinco candidatos;
* carregamento das cinco empresas;
* busca de candidato por nome;
* tratamento de candidato não encontrado através de `Optional`;
* listagem anônima de candidatos;
* listagem anônima de vagas;
* curtida de candidato em vaga;
* curtida de empresa em candidato;
* identificação de curtida mútua;
* criação de Match;
* listagem de Matches;
* liberação das informações completas após o Match;
* navegação pelo menu;
* encerramento da aplicação.

---

# 🔐 Anonimato

Uma das principais regras de negócio do Linketinder é trabalhar com **anonimato antes do Match**.

O MVP atual já aplica essa regra nas principais listagens.

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

## Após o Match

Quando candidato e empresa demonstram interesse mútuo, o sistema cria um `Match`.

Nesse momento, o MVP libera a visualização das informações completas das duas partes.

# ❤️ Sistema de Likes

O MVP possui um sistema de curtidas que permite que os dois lados demonstrem interesse.

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

Isso permite que uma empresa possua várias vagas. Depois do Match, candidato e empresa podem discutir qual vaga é mais adequada.

### Informações após o Match

Antes do Match, os dados identificadores permanecem ocultos.

Após o Match, o sistema permite visualizar:

* dados completos do candidato;
* dados completos da empresa;
* skills;
* vagas da empresa;
* informações de contato.

# 🚀 Evolução planejada

O MVP atual representa apenas a primeira etapa do Linketinder.

A arquitetura está sendo construída para permitir a evolução do projeto para uma experiência completa baseada em **Skills, filtros, anonimato, Likes e Match**.

## 1. Filtro por Skills

O candidato poderá visualizar ou filtrar vagas de acordo com suas competências.

Exemplo:

```text
Candidato possui:

Java
Groovy
```

Ao selecionar uma Skill:

```text
Filtro: Java
```

O sistema poderá apresentar vagas que necessitem dessa competência.

Da mesma forma, uma empresa poderá filtrar candidatos pelas Skills necessárias para suas vagas.

---

## 2. Experiência anônima

Antes do Match:

```text
CANDIDATO
     │
     │ visualiza
     ▼
VAGA ANÔNIMA
```

E:

```text
EMPRESA
     │
     │ visualiza
     ▼
CANDIDATO ANÔNIMO
```

A identidade das partes deverá permanecer protegida durante essa etapa.

---

## 3. Likes múltiplos

O sistema já permite que os dois lados avaliem diversas oportunidades.

```text
Candidato ──► várias vagas
Empresa   ──► vários candidatos
```

Isso permitirá que candidatos e empresas tenham liberdade para demonstrar interesse em diferentes oportunidades antes que qualquer Match aconteça.

---

## 4. Match

O sistema já identifica o interesse mútuo:

```text
Candidato
     │
     │ Like
     ▼
    Vaga
     ▲
     │ Like
     │
  Empresa

     ↓

   MATCH
```

A partir desse momento, poderão ser liberadas informações adicionais de acordo com as regras de negócio.

---

## 5. Cadastro

Como requisito opcional do enunciado, futuramente poderá ser implementado o cadastro de:

* novos candidatos;
* novas empresas;
* novas vagas;
* novas Skills.

---

## 6. Persistência

O MVP atual trabalha com dados carregados em memória pelo `DataInicializador`.

Uma evolução futura poderá substituir esse mecanismo por uma camada de persistência, permitindo armazenar os dados de forma permanente.

---

## 7. Evolução da arquitetura

Conforme o sistema crescer, novas camadas e componentes poderão ser introduzidos para tratar:

* autenticação;
* persistência;
* filtros;
* Likes;
* Match;
* controle de acesso;
* anonimato;
* cadastro;
* gerenciamento de vagas;
* relacionamento entre candidatos e empresas.

Essas funcionalidades serão implementadas gradualmente.

---

# 📋 Status atual do projeto

| Funcionalidade                 | Status         |
| ------------------------------ | -------------- |
| Modelagem de candidatos        | ✅ Implementado |
| Modelagem de empresas          | ✅ Implementado |
| Modelagem de Skills            | ✅ Implementado |
| Modelagem de vagas             | ✅ Implementado |
| Classe Informacoes             | ✅ Implementado |
| CPF                            | ✅ Implementado |
| CNPJ                           | ✅ Implementado |
| País da empresa                | ✅ Implementado |
| Estado                         | ✅ Implementado |
| DataInicializador              | ✅ Implementado |
| 5 candidatos pré-cadastrados   | ✅ Implementado |
| 5 empresas pré-cadastradas     | ✅ Implementado |
| 5 vagas pré-cadastradas        | ✅ Implementado |
| CandidatoService               | ✅ Implementado |
| EmpresaService                 | ✅ Implementado |
| Busca de candidato             | ✅ Implementado |
| Menu de terminal               | ✅ Implementado |
| Listagem de candidatos         | ✅ Implementado |
| Listagem de empresas           | ✅ Implementado |
| Validação do MVP               | ✅ Implementado |
| Cadastro de novos usuários     | ⏳ Futuro       |
| Filtro por Skills              | ⏳ Futuro       |
| Anonimização básica            | ✅ Implementado |
| Sistema de Likes               | ✅ Implementado |
| Match                          | ✅ Implementado |


---

# 📚 Conceitos aplicados

O projeto tem como objetivo principal praticar conceitos de **Programação Orientada a Objetos** e **Estruturas de Dados** utilizando Groovy.

Entre os conceitos utilizados estão:

* Classes;
* Objetos;
* Herança;
* Composição;
* Encapsulamento;
* Listas;
* Relacionamento entre objetos;
* Métodos;
* Construtores;
* `Optional`;
* Closures;
* Busca em coleções;
* Separação de responsabilidades;
* Organização em camadas.

---

# 🧠 Decisões de arquitetura

Algumas decisões foram tomadas considerando não apenas o MVP atual, mas também a evolução planejada.

### Candidato e Pessoa

`Candidato` herda de `Pessoa`, permitindo compartilhar informações gerais.

```text
Pessoa
   ▲
   │
Candidato
```

### Empresa independente

`Empresa` não herda de `Pessoa`.

Essa decisão foi tomada porque candidato e empresa representam entidades diferentes e possuem identificadores próprios:

```text
Candidato → CPF
Empresa   → CNPJ
```

### Informacoes

Os dados de identificação e contato foram concentrados em `Informacoes`.

```text
Informacoes
├── nome
├── cep
├── email
└── whatsapp
```

Essa organização facilita o controle futuro sobre quais informações serão disponibilizadas durante a experiência anônima.

### Vaga pertence à Empresa

Uma empresa possui uma lista de vagas:

```text
Empresa
   └── List<Vaga>
```

No MVP atual, cada empresa possui uma vaga, mas o modelo permite que uma empresa possua várias vagas futuramente.

### Skills compartilhadas

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

---

# 🎓 Requisitos do desafio

O projeto foi desenvolvido considerando os requisitos obrigatórios apresentados no desafio.

### Candidatos

* [x] Array/lista com pelo menos 5 candidatos;
* [x] Nome;
* [x] E-mail;
* [x] CPF;
* [x] Idade;
* [x] Estado;
* [x] CEP;
* [x] Descrição pessoal;
* [x] Skills.

### Empresas

* [x] Array/lista com pelo menos 5 empresas;
* [x] Nome;
* [x] E-mail corporativo;
* [x] CNPJ;
* [x] País;
* [x] Estado;
* [x] CEP;
* [x] Descrição da empresa;
* [x] Vagas com Skills.

### Interface

* [x] Menu no terminal;
* [x] Listagem de candidatos;
* [x] Listagem de empresas.

### Requisito opcional

* [ ] Cadastro de novos candidatos;
* [ ] Cadastro de novas empresas.

---

# 👨‍💻 Autor

**Nelson Lima da Costa Júnior**

Projeto desenvolvido durante o **Acelera ZG / ZG-HERO 2026**.

---

# 📌 Status

**MVP funcional**

O MVP funcional do Linketinder já possui:

* estrutura de domínio;
* candidatos;
* empresas;
* vagas;
* Skills;
* informações de contato;
* carregamento dos dados;
* serviços;
* anonimização nas listagens;
* sistema de curtidas;
* verificação de curtidas mútuas;
* criação de Matches;
* listagem de Matches;
* liberação das informações completas após o Match;
* validações;
* interface de terminal.

O próximo estágio será evoluir essa estrutura para uma experiência de contratação mais completa baseada em:

**Skills → Filtros → Anonimato → Likes → Match → Comunicação**

O objetivo é transformar gradualmente o MVP em um sistema de recrutamento inspirado no conceito do LinkedIn e na mecânica de interação do Tinder.

---

# 📄 Licença

Este projeto foi desenvolvido por Nelson Lima para fins educacionais no contexto do **Acelera ZG / ZG-HERO 2026**.
