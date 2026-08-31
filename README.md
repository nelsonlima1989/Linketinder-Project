# 🔗 Linketinder

Sistema de contratação de profissionais inspirado na combinação das principais ideias do **LinkedIn** e do **Tinder**.

O projeto foi desenvolvido como um **MVP (Minimum Viable Product)** para o desafio **ZG-HERO / Acelera ZG**, utilizando **Groovy**, com foco na aplicação prática de conceitos de **Programação Orientada a Objetos (POO)** e **Estruturas de Dados**.

A proposta do Linketinder é aproximar **candidatos** e **empresas** com base em suas **Skills**, reduzindo a influência de popularidade e priorizando a compatibilidade entre as competências do candidato e as necessidades da vaga.

---

# 📌 Sobre o projeto

O Linketinder surgiu a partir de uma ideia apresentada pelo empresário **Dr. Antônio Paçoca**, que identificou uma dificuldade nos processos tradicionais de recrutamento: encontrar profissionais com competências relevantes sem depender exclusivamente de perfis com maior visibilidade.

A proposta combina:

* O conceito de **Skills** utilizado em plataformas profissionais;
* A relação entre **candidato e empresa**;
* O conceito de **Like e Match** inspirado em aplicativos de relacionamento;
* Uma experiência de interação que deverá priorizar o **anonimato antes do Match**.

Neste momento, o projeto encontra-se em sua primeira versão funcional, com foco na estruturação do domínio, carregamento dos dados, serviços e menu de terminal.

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
* permitir a visualização dos candidatos;
* permitir a visualização das empresas;
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
                │   └── Vaga.groovy
                │
                ├── service/
                │   ├── CandidatoService.groovy
                │   └── EmpresaService.groovy
                │
                └── ui/
                    └── Menu.groovy
```

A estrutura poderá receber novos componentes conforme novas funcionalidades forem implementadas.

---

# 🧩 Model

A camada `model` representa as entidades principais do domínio.

## Pessoa

`Pessoa` representa atributos gerais utilizados pelo candidato.

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

---

# 🖥️ Interface de usuário

O projeto atualmente utiliza uma interface simples baseada em terminal.

O menu principal apresenta:

```text
================================
          LINKETINDER
================================
1 - Listar candidatos
2 - Listar empresas
0 - Sair
================================
```

## Listar candidatos

A opção `1` apresenta os candidatos cadastrados, incluindo informações necessárias para validação do MVP, como:

* ID;
* Nome;
* Estado;
* Idade;
* Skills.

## Listar empresas

A opção `2` apresenta as empresas cadastradas, incluindo:

* ID;
* Nome;
* País;
* Estado;
* Descrição;
* Vagas.

O CNPJ existe no modelo da empresa, mas não é apresentado na listagem atual.

## Sair

A opção `0` encerra a aplicação.

---

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
* listagem de candidatos;
* listagem de empresas;
* navegação pelo menu;
* encerramento da aplicação.

---

# 🔐 Anonimato

Uma das principais decisões de negócio do Linketinder é trabalhar com **anonimato antes do Match**.

A ideia é permitir que candidato e empresa avaliem oportunidades sem conhecer inicialmente a identidade da outra parte.

## Experiência do candidato

O candidato deverá visualizar informações relacionadas à vaga, como:

```text
Vaga
├── Título
├── Descrição
└── Skills necessárias
```

Sem visualizar inicialmente informações que identifiquem diretamente a empresa.

## Experiência da empresa

A empresa deverá visualizar candidatos de maneira semelhante:

```text
Candidato
├── Skills
├── Estado
├── Idade
└── Descrição
```

Sem expor inicialmente informações pessoais de contato ou identificação.

Dados como **CPF, CNPJ, CEP, e-mail, WhatsApp e outras informações identificadoras** deverão ser protegidos de acordo com as regras de negócio da aplicação.

O objetivo é que a identificação das partes aconteça somente após uma interação que resulte em Match.

> **Importante:** o mecanismo completo de anonimização ainda não está implementado nesta versão. A arquitetura atual está sendo preparada para suportar essa regra de negócio futuramente.

---

# ❤️ Sistema de Likes

O sistema de Likes será baseado em uma relação de **muitos-para-muitos**.

Um candidato poderá demonstrar interesse em **uma ou várias vagas**, assim como uma empresa poderá demonstrar interesse em **um ou vários candidatos**.

## Candidato curtindo vagas

```text
Candidato
   │
   ├── Like ──► Vaga 1
   ├── Like ──► Vaga 2
   └── Like ──► Vaga 3
```

Uma mesma vaga também poderá receber Likes de diversos candidatos:

```text
Candidato 1 ──► Vaga 1
Candidato 2 ──► Vaga 1
Candidato 3 ──► Vaga 1
```

## Empresa curtindo candidatos

Da mesma forma, uma empresa poderá demonstrar interesse em diversos candidatos:

```text
Empresa
   │
   ├── Like ──► Candidato 1
   ├── Like ──► Candidato 2
   └── Like ──► Candidato 3
```

Um candidato também poderá receber interesse de diversas empresas:

```text
Empresa 1 ──► Candidato 1
Empresa 2 ──► Candidato 1
Empresa 3 ──► Candidato 1
```

Dessa forma, o sistema não estabelece uma relação limitada a apenas um candidato e uma vaga.

---

# 🤝 Match

O Match acontecerá quando houver **interesse mútuo na mesma relação**.

Por exemplo:

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

Nesse cenário:

1. O candidato demonstra interesse na vaga;
2. A empresa demonstra interesse no candidato relacionado àquela vaga;
3. O sistema identifica o interesse mútuo;
4. A relação é considerada um Match.

O Match poderá ser utilizado posteriormente para permitir a comunicação entre as partes e liberar determinadas informações que permanecem protegidas durante a etapa anônima.

---

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

O sistema permitirá que os dois lados avaliem diversas oportunidades.

```text
Candidato ──► várias vagas
Empresa   ──► vários candidatos
```

Isso permitirá que candidatos e empresas tenham liberdade para demonstrar interesse em diferentes oportunidades antes que qualquer Match aconteça.

---

## 4. Match

Quando houver interesse mútuo:

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
| Anonimização completa          | ⏳ Futuro       |
| Sistema de Likes               | ⏳ Futuro       |
| Match                          | ⏳ Futuro       |


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

**MVP em desenvolvimento**

A primeira versão funcional do Linketinder já possui:

* estrutura de domínio;
* candidatos;
* empresas;
* vagas;
* Skills;
* informações de contato;
* carregamento dos dados;
* serviços;
* validações;
* interface de terminal.

O próximo estágio será evoluir essa estrutura para uma experiência de contratação baseada em:

**Skills → Filtros → Anonimato → Likes → Match → Comunicação**

O objetivo é transformar gradualmente o MVP em um sistema de recrutamento inspirado no conceito do LinkedIn e na mecânica de interação do Tinder.

---

# 📄 Licença

Este projeto foi desenvolvido para fins educacionais no contexto do **Acelera ZG / ZG-HERO 2026**.
