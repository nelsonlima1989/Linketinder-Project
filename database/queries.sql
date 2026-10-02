
-- LINKETINDER


-- 1. COMPETÊNCIAS

INSERT INTO competencias (nome)
VALUES
    ('Java'),
    ('Groovy'),
    ('JavaScript'),
    ('TypeScript'),
    ('React'),
    ('C#'),
    ('SQL'),
    ('PostgreSQL'),
    ('HTML'),
    ('CSS');



-- 2. CANDIDATOS

INSERT INTO candidatos (
    nome,
    sobrenome,
    data_nascimento,
    email,
    cpf,
    pais,
    cep,
    descricao_pessoal,
    senha,
    estado
)
VALUES
    (
        'João',
        'Silva',
        '1995-03-15',
        'joao.silva@email.com',
        '12345678901',
        'Brasil',
        '20000000',
        'Desenvolvedor interessado em desenvolvimento web e APIs.',
        'senha123',
        'RJ'
    ),
    (
        'Maria',
        'Oliveira',
        '1997-06-20',
        'maria.oliveira@email.com',
        '12345678902',
        'Brasil',
        '21000000',
        'Desenvolvedora com interesse em aplicações full stack.',
        'senha123',
        'RJ'
    ),
    (
        'Carlos',
        'Souza',
        '1993-09-10',
        'carlos.souza@email.com',
        '12345678903',
        'Brasil',
        '22000000',
        'Desenvolvedor com foco em backend e integração de sistemas.',
        'senha123',
        'RJ'
    ),
    (
        'Ana',
        'Costa',
        '1998-01-25',
        'ana.costa@email.com',
        '12345678904',
        'Brasil',
        '23000000',
        'Desenvolvedora interessada em interfaces web e experiência do usuário.',
        'senha123',
        'RJ'
    ),
    (
        'Pedro',
        'Santos',
        '1994-11-05',
        'pedro.santos@email.com',
        '12345678905',
        'Brasil',
        '24000000',
        'Desenvolvedor com interesse em C#, SQL e aplicações corporativas.',
        'senha123',
        'RJ'
    );


-- 3. EMPRESAS

INSERT INTO empresas (
    nome,
    cnpj,
    email,
    descricao,
    pais,
    cep,
    senha,
    estado
)
VALUES
    (
        'Pastelsoft',
        '12345678000101',
        'contato@pastelsoft.com',
        'Empresa de tecnologia especializada em soluções de software.',
        'Brasil',
        '20010000',
        'senha123',
        'RJ'
    ),
    (
        'SanduTech',
        '12345678000102',
        'contato@sandutech.com',
        'Empresa focada em produtos digitais e desenvolvimento frontend.',
        'Brasil',
        '01001000',
        'senha123',
        'SP'
    ),
    (
        'CodeMais',
        '12345678000103',
        'contato@codemais.com',
        'Empresa especializada em desenvolvimento de aplicações web.',
        'Brasil',
        '30110000',
        'senha123',
        'MG'
    ),
    (
        'DevHouse',
        '12345678000104',
        'contato@devhouse.com',
        'Empresa de desenvolvimento de sistemas corporativos.',
        'Brasil',
        '80010000',
        'senha123',
        'PR'
    ),
    (
        'TechBrasil',
        '12345678000105',
        'contato@techbrasil.com',
        'Empresa de tecnologia voltada para APIs e serviços backend.',
        'Brasil',
        '90010000',
        'senha123',
        'RS'
    );



-- 4. VAGAS

INSERT INTO vagas (
    empresa_id,
    nome,
    descricao,
    estado,
    cidade
)
VALUES
    (
        1,
        'Desenvolvedor Java',
        'Desenvolvimento e manutenção de aplicações utilizando Java e SQL.',
        'Rio de Janeiro',
        'Rio de Janeiro'
    ),
    (
        2,
        'Desenvolvedor Frontend',
        'Desenvolvimento de interfaces web utilizando JavaScript, React e CSS.',
        'São Paulo',
        'São Paulo'
    ),
    (
        3,
        'Desenvolvedor Full Stack',
        'Desenvolvimento de aplicações web e APIs utilizando TypeScript, React e PostgreSQL.',
        'Minas Gerais',
        'Belo Horizonte'
    ),
    (
        4,
        'Desenvolvedor C#',
        'Desenvolvimento de APIs e sistemas utilizando C# e SQL.',
        'Paraná',
        'Curitiba'
    ),
    (
        5,
        'Desenvolvedor Backend',
        'Desenvolvimento de sistemas backend e integração com bancos de dados.',
        'Rio Grande do Sul',
        'Porto Alegre'
    );



-- 5. CANDIDATO x COMPETÊNCIA

INSERT INTO candidato_competencias (
    candidato_id,
    competencia_id
)
VALUES
    -- João
    (1, 1), -- Java
    (1, 7), -- SQL
    (1, 8), -- PostgreSQL

    -- Maria
    (2, 3), -- JavaScript
    (2, 4), -- TypeScript
    (2, 5), -- React
    (2, 10), -- CSS

    -- Carlos
    (3, 1), -- Java
    (3, 2), -- Groovy
    (3, 7), -- SQL

    -- Ana
    (4, 3), -- JavaScript
    (4, 5), -- React
    (4, 9), -- HTML
    (4, 10), -- CSS

    -- Pedro
    (5, 6), -- C#
    (5, 7), -- SQL
    (5, 8); -- PostgreSQL



-- 6. VAGA x COMPETÊNCIA

INSERT INTO vaga_competencias (
    vaga_id,
    competencia_id
)
VALUES
    -- Desenvolvedor Java - Pastelsoft
    (1, 1), -- Java
    (1, 7), -- SQL

    -- Desenvolvedor Frontend - SanduTech
    (2, 3), -- JavaScript
    (2, 4), -- TypeScript
    (2, 5), -- React
    (2, 9), -- HTML
    (2, 10), -- CSS

    -- Desenvolvedor Full Stack - CodeMais
    (3, 3), -- JavaScript
    (3, 4), -- TypeScript
    (3, 5), -- React
    (3, 8), -- PostgreSQL

    -- Desenvolvedor C# - DevHouse
    (4, 6), -- C#
    (4, 7), -- SQL

    -- Desenvolvedor Backend - TechBrasil
    (5, 7), -- SQL
    (5, 8); -- PostgreSQL



-- 7. CURTIDAS: CANDIDATO x VAGA

INSERT INTO curtidas_candidato_vaga (
    candidato_id,
    vaga_id
)
VALUES
    -- João
    (1, 1),
    (1, 5),

    -- Maria
    (2, 2),
    (2, 3),

    -- Carlos
    (3, 1),

    -- Ana
    (4, 2),

    -- Pedro
    (5, 4),
    (5, 5);



-- 8. CURTIDAS: EMPRESA x CANDIDATO

INSERT INTO curtidas_empresa_candidato (
    empresa_id,
    candidato_id
)
VALUES
    -- Pastelsoft
    (1, 1),
    (1, 3),

    -- SanduTech
    (2, 2),
    (2, 4),

    -- CodeMais
    (3, 2),
    (3, 5),

    -- DevHouse
    (4, 5),

    -- TechBrasil
    (5, 1);



-- CONSULTAS DE TESTE

-- 9. LISTAR CANDIDATOS

SELECT *
FROM candidatos
ORDER BY id;


-- 10. LISTAR EMPRESAS

SELECT *
FROM empresas
ORDER BY id;


-- 11. LISTAR COMPETÊNCIAS

SELECT *
FROM competencias
ORDER BY id;



-- 12. LISTAR VAGAS COM SUAS EMPRESAS

SELECT
    v.id AS vaga_id,
    v.nome AS vaga,
    e.nome AS empresa
FROM vagas AS v
INNER JOIN empresas AS e
    ON v.empresa_id = e.id
ORDER BY v.id;



-- 13. LISTAR CANDIDATOS E SUAS COMPETÊNCIAS

SELECT
    c.nome AS candidato,
    cp.nome AS competencia
FROM candidatos AS c
INNER JOIN candidato_competencias AS cc
    ON c.id = cc.candidato_id
INNER JOIN competencias AS cp
    ON cc.competencia_id = cp.id
ORDER BY c.id, cp.id;



-- 14. LISTAR VAGAS E SUAS COMPETÊNCIAS

SELECT
    v.nome AS vaga,
    c.nome AS competencia
FROM vagas AS v
INNER JOIN vaga_competencias AS vc
    ON v.id = vc.vaga_id
INNER JOIN competencias AS c
    ON vc.competencia_id = c.id
ORDER BY v.id, c.id;



-- 15. LISTAR CURTIDAS DOS CANDIDATOS

SELECT
    c.nome AS candidato,
    v.nome AS vaga,
    ccv.data_curtida
FROM curtidas_candidato_vaga AS ccv
INNER JOIN candidatos AS c
    ON ccv.candidato_id = c.id
INNER JOIN vagas AS v
    ON ccv.vaga_id = v.id
ORDER BY ccv.data_curtida;



-- 16. LISTAR CURTIDAS DAS EMPRESAS

SELECT
    e.nome AS empresa,
    c.nome AS candidato,
    cec.data_curtida
FROM curtidas_empresa_candidato AS cec
INNER JOIN empresas AS e
    ON cec.empresa_id = e.id
INNER JOIN candidatos AS c
    ON cec.candidato_id = c.id
ORDER BY cec.data_curtida;



-- 17. LISTAR MATCHES

SELECT
    c.nome AS candidato,
    v.nome AS vaga,
    e.nome AS empresa
FROM curtidas_candidato_vaga AS ccv
INNER JOIN candidatos AS c
    ON ccv.candidato_id = c.id
INNER JOIN vagas AS v
    ON ccv.vaga_id = v.id
INNER JOIN empresas AS e
    ON v.empresa_id = e.id
INNER JOIN curtidas_empresa_candidato AS cec
    ON cec.empresa_id = e.id
    AND cec.candidato_id = c.id
ORDER BY c.nome;
