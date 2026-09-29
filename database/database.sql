-- LINKETINDER
-- Banco de dados PostgreSQL

-- TABELA: candidatos

CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    pais VARCHAR(100) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    descricao_pessoal TEXT,
    senha VARCHAR(255) NOT NULL
);

-- TABELA: empresas

CREATE TABLE empresas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    descricao TEXT,
    pais VARCHAR(100) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    senha VARCHAR(255) NOT NULL
);


-- TABELA: competencias

CREATE TABLE competencias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);



-- TABELA: vagas

CREATE TABLE vagas (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    estado VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,

    CONSTRAINT fk_vagas_empresa
        FOREIGN KEY (empresa_id)
        REFERENCES empresas(id)
);



-- TABELA: candidato_competencias
-- Relação N:N entre candidatos e competências

CREATE TABLE candidato_competencias (
    candidato_id INTEGER NOT NULL,
    competencia_id INTEGER NOT NULL,

    PRIMARY KEY (candidato_id, competencia_id),

    CONSTRAINT fk_candidato_competencias_candidato
        FOREIGN KEY (candidato_id)
        REFERENCES candidatos(id),

    CONSTRAINT fk_candidato_competencias_competencia
        FOREIGN KEY (competencia_id)
        REFERENCES competencias(id)
);



-- TABELA: vaga_competencias
-- Relação N:N entre vagas e competências

CREATE TABLE vaga_competencias (
    vaga_id INTEGER NOT NULL,
    competencia_id INTEGER NOT NULL,

    PRIMARY KEY (vaga_id, competencia_id),

    CONSTRAINT fk_vaga_competencias_vaga
        FOREIGN KEY (vaga_id)
        REFERENCES vagas(id),

    CONSTRAINT fk_vaga_competencias_competencia
        FOREIGN KEY (competencia_id)
        REFERENCES competencias(id)
);



-- TABELA: curtidas_candidato_vaga
-- Relação N:N entre candidatos e vagas

CREATE TABLE curtidas_candidato_vaga (
    candidato_id INTEGER NOT NULL,
    vaga_id INTEGER NOT NULL,
    data_curtida TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (candidato_id, vaga_id),

    CONSTRAINT fk_curtidas_candidato_vaga_candidato
        FOREIGN KEY (candidato_id)
        REFERENCES candidatos(id),

    CONSTRAINT fk_curtidas_candidato_vaga_vaga
        FOREIGN KEY (vaga_id)
        REFERENCES vagas(id)
);



-- TABELA: curtidas_empresa_candidato
-- Relação N:N entre empresas e candidatos

CREATE TABLE curtidas_empresa_candidato (
    empresa_id INTEGER NOT NULL,
    candidato_id INTEGER NOT NULL,
    data_curtida TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (empresa_id, candidato_id),

    CONSTRAINT fk_curtidas_empresa_candidato_empresa
        FOREIGN KEY (empresa_id)
        REFERENCES empresas(id),

    CONSTRAINT fk_curtidas_empresa_candidato_candidato
        FOREIGN KEY (candidato_id)
        REFERENCES candidatos(id)
);