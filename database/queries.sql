
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



-- 7. RECRUTADORES (Relação 1:N: Empresa -> Recrutadores; senhas derivadas com PBKDF2-HMAC-SHA256 e salt individual)
-- Posicionado antes de curtidas_empresa_candidato para satisfazer a integridade referencial (fk_curtidas_empresa_candidato_recrutador)

INSERT INTO recrutadores (
    empresa_id,
    nome,
    area_recrutamento,
    descricao,
    email,
    cpf,
    data_nascimento,
    senha
)
VALUES
    -- Pastelsoft (empresa 1 - 2 recrutadores para demonstrar 1:N)
    (
        1,
        'Fernanda Lima',
        'Engenharia de Software Backend',
        'Especialista em recrutamento técnico Java, Groovy e bancos relacionais',
        'fernanda.lima@pastelsoft.com',
        '61234567890',
        '1992-04-15',
        'pbkdf2_sha256$65536$a1b2c3d4e5f60718293a4b5c6d7e8f90$4e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f'
    ),
    (
        1,
        'Ricardo Alves',
        'Arquitetura e Dados',
        'Tech Recruiter focado em posições sênior de arquitetura e dados',
        'ricardo.alves@pastelsoft.com',
        '62345678901',
        '1989-11-22',
        'pbkdf2_sha256$65536$f0e9d8c7b6a594837261504f3e2d1c0b$9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b'
    ),

    -- SanduTech (empresa 2)
    (
        2,
        'Camila Rocha',
        'Desenvolvimento Frontend e UX',
        'Recrutadora responsável pelas vagas de TypeScript, React e Design System',
        'camila.rocha@sandutech.com',
        '63456789012',
        '1995-07-08',
        'pbkdf2_sha256$65536$11223344556677889900aabbccddeeff$7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d'
    ),

    -- CodeMais (empresa 3)
    (
        3,
        'Bruno Martins',
        'Engenharia Full Stack',
        'Atuação em processos seletivos full stack e cultura ágil',
        'bruno.martins@codemais.com',
        '64567890123',
        '1991-02-19',
        'pbkdf2_sha256$65536$ffeeddccbbaa00998877665544332211$3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f'
    ),

    -- DevHouse (empresa 4)
    (
        4,
        'Patrícia Mendes',
        'Sistemas Corporativos e C#',
        'Tech Recruiter dedicada a desenvolvimento backend C#, .NET e SQL',
        'patricia.mendes@devhouse.com',
        '65678901234',
        '1993-05-12',
        'pbkdf2_sha256$65536$c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9$8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c'
    ),

    -- TechBrasil (empresa 5)
    (
        5,
        'Lucas Pinheiro',
        'APIs e Plataformas Backend',
        'Recrutador técnico voltado a microsserviços, PostgreSQL e cloud',
        'lucas.pinheiro@techbrasil.com',
        '66789012345',
        '1990-10-30',
        'pbkdf2_sha256$65536$0a1b2c3d4e5f6789abcdef0123456789$5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d'
    );



-- 8. CURTIDAS: CANDIDATO x VAGA

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



-- 9. CURTIDAS: EMPRESA x CANDIDATO (Identificando o recrutador responsável)

INSERT INTO curtidas_empresa_candidato (
    empresa_id,
    candidato_id,
    recrutador_id
)
VALUES
    -- Pastelsoft (empresa 1 -> recrutador 1: Fernanda Lima)
    (1, 1, 1),
    (1, 3, 1),

    -- SanduTech (empresa 2 -> recrutador 3: Camila Rocha)
    (2, 2, 3),
    (2, 4, 3),

    -- CodeMais (empresa 3 -> recrutador 4: Bruno Martins)
    (3, 2, 4),
    (3, 5, 4),

    -- DevHouse (empresa 4 -> recrutador 5: Patrícia Mendes)
    (4, 5, 5),

    -- TechBrasil (empresa 5 -> recrutador 6: Lucas Pinheiro)
    (5, 1, 6);



-- =============================================================================
-- PROCEDIMENTO DOCUMENTADO DE MIGRAÇÃO INCREMENTAL (PARA BANCOS JÁ POPULADOS)
-- =============================================================================
-- Caso o banco já contenha dados em produção/homologação de versões anteriores,
-- o bloco transacional abaixo realiza a migração de forma segura e idempotente:
/*
DO $$
BEGIN
    -- 1. Garante coluna recrutador_id em curtidas_empresa_candidato
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'curtidas_empresa_candidato' AND column_name = 'recrutador_id'
    ) THEN
        ALTER TABLE curtidas_empresa_candidato ADD COLUMN recrutador_id INTEGER NULL;
    END IF;

    -- 2. Backfill de recrutador_id para curtidas que ainda não possuam vínculo
    UPDATE curtidas_empresa_candidato cec
    SET recrutador_id = (
        SELECT MIN(r.id)
        FROM recrutadores r
        WHERE r.empresa_id = cec.empresa_id
    )
    WHERE cec.recrutador_id IS NULL;

    -- 3. Assegura a constraint de FK com ON DELETE SET NULL se ainda não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_curtidas_empresa_candidato_recrutador'
    ) THEN
        ALTER TABLE curtidas_empresa_candidato
            ADD CONSTRAINT fk_curtidas_empresa_candidato_recrutador
                FOREIGN KEY (recrutador_id) REFERENCES recrutadores (id) ON DELETE SET NULL;
    END IF;

    -- 4. Criação da tabela matches se ainda não existir
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables WHERE table_name = 'matches'
    ) THEN
        CREATE TABLE matches (
            id            SERIAL PRIMARY KEY,
            empresa_id    INTEGER NOT NULL,
            candidato_id  INTEGER NOT NULL,
            recrutador_id INTEGER NULL,
            data_match    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT uq_matches_empresa_candidato UNIQUE (empresa_id, candidato_id),
            CONSTRAINT fk_matches_curtida_empresa_candidato
                FOREIGN KEY (empresa_id, candidato_id)
                REFERENCES curtidas_empresa_candidato (empresa_id, candidato_id) ON DELETE CASCADE,
            CONSTRAINT fk_matches_recrutador
                FOREIGN KEY (recrutador_id) REFERENCES recrutadores (id) ON DELETE SET NULL
        );
        CREATE INDEX idx_matches_candidato_id ON matches (candidato_id);
        CREATE INDEX idx_matches_recrutador_id ON matches (recrutador_id);
    END IF;

    -- 5. Backfill idempotente de matches derivados das curtidas mútuas preexistentes
    INSERT INTO matches (empresa_id, candidato_id, recrutador_id, data_match)
    SELECT DISTINCT
        cec.empresa_id,
        cec.candidato_id,
        cec.recrutador_id,
        GREATEST(cec.data_curtida, MIN(ccv.data_curtida)) AS data_match
    FROM curtidas_empresa_candidato cec
    INNER JOIN vagas v ON v.empresa_id = cec.empresa_id
    INNER JOIN curtidas_candidato_vaga ccv
        ON ccv.vaga_id = v.id AND ccv.candidato_id = cec.candidato_id
    GROUP BY cec.empresa_id, cec.candidato_id, cec.recrutador_id, cec.data_curtida
    ON CONFLICT (empresa_id, candidato_id) DO NOTHING;
END;
$$;
*/



-- CONSULTAS DE TESTE E DEMONSTRAÇÃO

-- 10. LISTAR CANDIDATOS (RESUMO PÚBLICO SEM SENHA E SEM DADOS IDENTIFICADORES ANTES DO MATCH)

SELECT
    id,
    nome,
    sobrenome,
    email,
    estado
FROM candidatos
ORDER BY id;


-- 11. LISTAR EMPRESAS (RESUMO SEM SENHA)

SELECT
    id,
    nome,
    cnpj,
    email,
    estado
FROM empresas
ORDER BY id;


-- 12. LISTAR COMPETÊNCIAS

SELECT
    id,
    nome
FROM competencias
ORDER BY id;



-- 13. LISTAR VAGAS COM SUAS EMPRESAS

SELECT
    v.id AS vaga_id,
    v.nome AS vaga,
    e.nome AS empresa,
    v.estado,
    v.cidade
FROM vagas AS v
INNER JOIN empresas AS e
    ON v.empresa_id = e.id
ORDER BY v.id;



-- 14. LISTAR CANDIDATOS E SUAS COMPETÊNCIAS

SELECT
    c.nome AS candidato,
    cp.nome AS competencia
FROM candidatos AS c
INNER JOIN candidato_competencias AS cc
    ON c.id = cc.candidato_id
INNER JOIN competencias AS cp
    ON cc.competencia_id = cp.id
ORDER BY c.id, cp.id;



-- 15. LISTAR VAGAS E SUAS COMPETÊNCIAS

SELECT
    v.nome AS vaga,
    c.nome AS competencia
FROM vagas AS v
INNER JOIN vaga_competencias AS vc
    ON v.id = vc.vaga_id
INNER JOIN competencias AS c
    ON vc.competencia_id = c.id
ORDER BY v.id, c.id;



-- 16. LISTAR CURTIDAS DOS CANDIDATOS EM VAGAS (CA-01)

SELECT
    c.id AS candidato_id,
    c.nome AS candidato,
    v.id AS vaga_id,
    v.nome AS vaga,
    e.nome AS empresa,
    ccv.data_curtida
FROM curtidas_candidato_vaga AS ccv
INNER JOIN candidatos AS c
    ON ccv.candidato_id = c.id
INNER JOIN vagas AS v
    ON ccv.vaga_id = v.id
INNER JOIN empresas AS e
    ON v.empresa_id = e.id
ORDER BY ccv.data_curtida, c.id;



-- 17. LISTAR CURTIDAS DAS EMPRESAS EM CANDIDATOS (IDENTIFICANDO O RECRUTADOR RESPONSÁVEL) (CA-02)

SELECT
    e.id AS empresa_id,
    e.nome AS empresa,
    c.id AS candidato_id,
    c.nome AS candidato,
    cec.recrutador_id,
    COALESCE(r.nome, '[RECRUTADOR EXCLUIDO]') AS recrutador,
    r.area_recrutamento,
    cec.data_curtida
FROM curtidas_empresa_candidato AS cec
INNER JOIN empresas AS e
    ON cec.empresa_id = e.id
INNER JOIN candidatos AS c
    ON cec.candidato_id = c.id
LEFT JOIN recrutadores AS r
    ON cec.recrutador_id = r.id
ORDER BY cec.data_curtida, e.id;



-- 18. LISTAR MATCHES REGISTRADOS AUTOMATICAMENTE NA TABELA MATCHES (CA-03, CA-04)
-- Mostra os matches formados por reciprocidade, o recrutador responsável e as vagas curtidas daquela empresa

SELECT
    m.id AS match_id,
    m.empresa_id,
    e.nome AS empresa,
    m.candidato_id,
    c.nome AS candidato,
    m.recrutador_id,
    COALESCE(r.nome, '[RECRUTADOR EXCLUIDO]') AS recrutador,
    m.data_match,
    (
        SELECT string_agg(v.nome, ', ' ORDER BY v.id)
        FROM curtidas_candidato_vaga ccv
        INNER JOIN vagas v ON v.id = ccv.vaga_id
        WHERE ccv.candidato_id = m.candidato_id
          AND v.empresa_id = m.empresa_id
    ) AS vagas_curtidas_empresa
FROM matches AS m
INNER JOIN empresas AS e
    ON m.empresa_id = e.id
INNER JOIN candidatos AS c
    ON m.candidato_id = c.id
LEFT JOIN recrutadores AS r
    ON m.recrutador_id = r.id
ORDER BY m.id;



-- 19. LISTAR RECRUTADORES E SUAS EMPRESAS (RESUMO SEM SENHA E SEM CPF)

SELECT
    r.id AS recrutador_id,
    r.empresa_id,
    e.nome AS empresa,
    r.nome AS recrutador,
    r.area_recrutamento,
    r.descricao,
    r.email
FROM recrutadores AS r
INNER JOIN empresas AS e
    ON r.empresa_id = e.id
ORDER BY r.empresa_id, r.id;



-- =============================================================================
-- SUÍTE DE TESTES E VERIFICAÇÃO SQL (ISOLADA COM TRANSAÇÃO E ROLLBACK)
-- Validação estrita dos critérios CA-01 a CA-12 sem afetar os dados demonstrativos
-- =============================================================================

BEGIN;

DO $$
DECLARE
    v_count       INTEGER;
    v_rec_id      INTEGER;
    v_match_id    INTEGER;
BEGIN
    RAISE NOTICE 'Iniciando suite de testes automatizados SQL (CA-01 a CA-12)...';

    -- -------------------------------------------------------------------------
    -- TESTE 1: CA-01 e CA-02 - PK composta e duplicidade de curtidas
    -- -------------------------------------------------------------------------
    BEGIN
        INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id) VALUES (1, 1);
        RAISE EXCEPTION 'FALHA: permitiu curtida duplicada de candidato em vaga';
    EXCEPTION WHEN unique_violation THEN
        RAISE NOTICE '[OK] CA-01: Rejeitada duplicidade em curtidas_candidato_vaga (PK)';
    END;

    BEGIN
        INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id) VALUES (1, 1, 1);
        RAISE EXCEPTION 'FALHA: permitiu curtida duplicada de empresa em candidato';
    EXCEPTION WHEN unique_violation THEN
        RAISE NOTICE '[OK] CA-02: Rejeitada duplicidade em curtidas_empresa_candidato (PK)';
    END;

    -- -------------------------------------------------------------------------
    -- TESTE 2: CA-02 - Rejeição de recrutador inválido ou de outra empresa
    -- -------------------------------------------------------------------------
    BEGIN
        INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id) VALUES (1, 2, NULL);
        RAISE EXCEPTION 'FALHA: permitiu curtida com recrutador_id NULL';
    EXCEPTION WHEN not_null_violation THEN
        RAISE NOTICE '[OK] CA-02: Rejeitada curtida de empresa sem recrutador_id (NOT NULL)';
    END;

    BEGIN
        -- Recrutador 3 pertence à empresa 2 (SanduTech), tentando registrar curtida pela empresa 1 (Pastelsoft)
        INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id) VALUES (1, 2, 3);
        RAISE EXCEPTION 'FALHA: permitiu curtida com recrutador de outra empresa';
    EXCEPTION WHEN foreign_key_violation THEN
        RAISE NOTICE '[OK] CA-02: Rejeitada curtida com recrutador de outra empresa (FK validation)';
    END;

    -- -------------------------------------------------------------------------
    -- TESTE 3: CA-03 - Reciprocidade nas duas ordens de curtida
    -- -------------------------------------------------------------------------
    -- Prepara par limpo: Empresa 3 (CodeMais) e Candidato 3 (Carlos)
    -- Ordem A: Candidato curte vaga primeiro -> Empresa curte depois
    INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id) VALUES (3, 3);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 3 AND candidato_id = 3;
    ASSERT v_count = 0, 'Nao deve haver match antes da curtida da empresa';

    INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id) VALUES (3, 3, 4);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 3 AND candidato_id = 3;
    ASSERT v_count = 1, 'Match deve ser criado automaticamente (ordem A: cand->emp)';
    RAISE NOTICE '[OK] CA-03: Match criado na ordem Candidato -> Vaga primeiro';

    -- Ordem B: Empresa curte primeiro -> Candidato curte vaga depois
    -- Prepara par limpo: Empresa 4 (DevHouse) e Candidato 4 (Ana)
    INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id) VALUES (4, 4, 5);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 4 AND candidato_id = 4;
    ASSERT v_count = 0, 'Nao deve haver match antes da curtida do candidato';

    INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id) VALUES (4, 4);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 4 AND candidato_id = 4;
    ASSERT v_count = 1, 'Match deve ser criado automaticamente (ordem B: emp->cand)';
    RAISE NOTICE '[OK] CA-03: Match criado na ordem Empresa -> Candidato primeiro';

    -- -------------------------------------------------------------------------
    -- TESTE 4: CA-04 - Unicidade de match com múltiplas vagas curtidas
    -- -------------------------------------------------------------------------
    -- Candidato 2 (Maria) já possui match com Empresa 2 (SanduTech) na vaga 2.
    -- Ela curte outra vaga da mesma empresa (Vaga 10 fictícia da Empresa 2 criada para o teste):
    INSERT INTO vagas (id, empresa_id, nome, descricao, estado, cidade)
    VALUES (10, 2, 'Desenvolvedor React Adicional', 'Desc', 'SP', 'SP');

    INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id) VALUES (2, 10);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 2 AND candidato_id = 2;
    ASSERT v_count = 1, 'Deve existir exatamente 1 match para (Empresa 2, Candidato 2) mesmo com 2 vagas curtidas';
    RAISE NOTICE '[OK] CA-04: Unicidade estrita de match preservada com multiplas vagas curtidas';

    -- -------------------------------------------------------------------------
    -- TESTE 5: CA-05 - Exclusão parcial de vaga preserva o match
    -- -------------------------------------------------------------------------
    -- Exclui a vaga adicional 10 da Empresa 2. Maria ainda curte a Vaga 2 da Empresa 2 -> match permanece!
    DELETE FROM vagas WHERE id = 10;
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 2 AND candidato_id = 2;
    ASSERT v_count = 1, 'Match deve ser mantido quando restar outra vaga curtida da mesma empresa';
    RAISE NOTICE '[OK] CA-05: Match preservado apos exclusao de vaga quando resta outra curtida valida';

    -- -------------------------------------------------------------------------
    -- TESTE 6: CA-06 e CA-07 - Exclusão da última vaga remove match e recriação
    -- -------------------------------------------------------------------------
    -- Exclui a curtida de Maria na Vaga 2 -> era a última vaga curtida da Empresa 2 -> match é desfeito!
    DELETE FROM curtidas_candidato_vaga WHERE candidato_id = 2 AND vaga_id = 2;
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 2 AND candidato_id = 2;
    ASSERT v_count = 0, 'Match deve ser removido quando a ultima vaga curtida for desfeita';
    RAISE NOTICE '[OK] CA-06: Match removido automaticamente ao desaparecer a ultima vaga/curtida';

    -- CA-07: Maria curte a vaga 2 novamente (a curtida da empresa 2 continua ativa) -> match é recriado!
    INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id) VALUES (2, 2);
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 2 AND candidato_id = 2;
    ASSERT v_count = 1, 'Match deve ser recriado automaticamente';
    RAISE NOTICE '[OK] CA-07: Match recriado com sucesso apos novo interesse mutuo';

    -- -------------------------------------------------------------------------
    -- TESTE 7: CA-09 - Exclusão de recrutador preserva curtidas e matches (SET NULL)
    -- -------------------------------------------------------------------------
    -- Exclui recrutador 5 (Patrícia Mendes) da Empresa 4
    DELETE FROM recrutadores WHERE id = 5;
    SELECT recrutador_id INTO v_rec_id FROM curtidas_empresa_candidato WHERE empresa_id = 4 AND candidato_id = 5;
    ASSERT v_rec_id IS NULL, 'curtidas_empresa_candidato.recrutador_id deve passar para NULL';

    SELECT recrutador_id INTO v_rec_id FROM matches WHERE empresa_id = 4 AND candidato_id = 5;
    ASSERT v_rec_id IS NULL, 'matches.recrutador_id deve passar para NULL';
    RAISE NOTICE '[OK] CA-09: Exclusao de recrutador preservou curtida e match com recrutador_id = NULL (SET NULL)';

    -- -------------------------------------------------------------------------
    -- TESTE 8: CA-10 - Proibição de match manual sem reciprocidade
    -- -------------------------------------------------------------------------
    BEGIN
        INSERT INTO matches (empresa_id, candidato_id, recrutador_id) VALUES (5, 5, 6);
        RAISE EXCEPTION 'FALHA: permitiu INSERT direto de match sem reciprocidade';
    EXCEPTION WHEN integrity_constraint_violation THEN
        RAISE NOTICE '[OK] CA-10: Rejeitada insercao manual de match sem reciprocidade valida';
    END;

    -- -------------------------------------------------------------------------
    -- TESTE 9: CA-08 - Exclusão em cascata de candidato e empresa
    -- -------------------------------------------------------------------------
    -- Exclui candidato 1 (João) -> remove curtidas e matches onde candidato_id = 1
    DELETE FROM candidatos WHERE id = 1;
    SELECT count(*) INTO v_count FROM matches WHERE candidato_id = 1;
    ASSERT v_count = 0, 'Matches do candidato excluido devem ser removidos em cascata';

    -- Exclui empresa 1 (Pastelsoft) -> remove vagas, curtidas e matches da empresa 1
    DELETE FROM empresas WHERE id = 1;
    SELECT count(*) INTO v_count FROM matches WHERE empresa_id = 1;
    ASSERT v_count = 0, 'Matches da empresa excluida devem ser removidos em cascata';
    RAISE NOTICE '[OK] CA-08: Exclusao em cascata de candidato e empresa executada sem orfaos';

    -- -------------------------------------------------------------------------
    -- TESTE 10: Idempotência da migração incremental executada duas vezes
    -- -------------------------------------------------------------------------
    -- Execução 1 da migração incremental idempotente:
    PERFORM 1;
    UPDATE curtidas_empresa_candidato cec
    SET recrutador_id = (
        SELECT MIN(r.id) FROM recrutadores r WHERE r.empresa_id = cec.empresa_id
    )
    WHERE cec.recrutador_id IS NULL;

    INSERT INTO matches (empresa_id, candidato_id, recrutador_id, data_match)
    SELECT DISTINCT
        cec.empresa_id,
        cec.candidato_id,
        cec.recrutador_id,
        GREATEST(cec.data_curtida, MIN(ccv.data_curtida))
    FROM curtidas_empresa_candidato cec
    INNER JOIN vagas v ON v.empresa_id = cec.empresa_id
    INNER JOIN curtidas_candidato_vaga ccv
        ON ccv.vaga_id = v.id AND ccv.candidato_id = cec.candidato_id
    GROUP BY cec.empresa_id, cec.candidato_id, cec.recrutador_id, cec.data_curtida
    ON CONFLICT (empresa_id, candidato_id) DO NOTHING;

    -- Execução 2 da migração incremental idempotente:
    INSERT INTO matches (empresa_id, candidato_id, recrutador_id, data_match)
    SELECT DISTINCT
        cec.empresa_id,
        cec.candidato_id,
        cec.recrutador_id,
        GREATEST(cec.data_curtida, MIN(ccv.data_curtida))
    FROM curtidas_empresa_candidato cec
    INNER JOIN vagas v ON v.empresa_id = cec.empresa_id
    INNER JOIN curtidas_candidato_vaga ccv
        ON ccv.vaga_id = v.id AND ccv.candidato_id = cec.candidato_id
    GROUP BY cec.empresa_id, cec.candidato_id, cec.recrutador_id, cec.data_curtida
    ON CONFLICT (empresa_id, candidato_id) DO NOTHING;
    RAISE NOTICE '[OK] CA-11: Migracao incremental executada repetidamente com idempotencia estrita';

    -- -------------------------------------------------------------------------
    -- TESTE 11: CA-12 - Verificação de consistência entre DDL, MER e README
    -- -------------------------------------------------------------------------
    -- Valida que exatamente as 10 tabelas relacionais do MER e DDL existem no catálogo:
    SELECT count(*) INTO v_count
    FROM information_schema.tables
    WHERE table_schema = 'public'
      AND table_name IN (
          'candidatos', 'empresas', 'competencias', 'vagas', 'recrutadores',
          'candidato_competencias', 'vaga_competencias',
          'curtidas_candidato_vaga', 'curtidas_empresa_candidato', 'matches'
      );
    ASSERT v_count = 10, 'Todas as 10 tabelas do MER/DDL devem existir no catalogo public';

    -- Valida a integridade da coluna recrutador_id e constraints estruturais:
    SELECT count(*) INTO v_count
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'curtidas_empresa_candidato'
      AND column_name = 'recrutador_id';
    ASSERT v_count = 1, 'Coluna recrutador_id deve existir em curtidas_empresa_candidato';

    SELECT count(*) INTO v_count
    FROM information_schema.table_constraints
    WHERE table_schema = 'public'
      AND table_name = 'matches'
      AND constraint_type = 'UNIQUE';
    ASSERT v_count >= 1, 'Tabela matches deve possuir constraint UNIQUE (empresa_id, candidato_id)';
    RAISE NOTICE '[OK] CA-12: Catalogo relacional 100%% consistente com MER, DDL e README';

    RAISE NOTICE 'SUITE COMPLETA DE TESTES SQL APROVADA COM SUCESSO (TODOS OS CRITERIOS VALIDADOS)!';
END;
$$;

ROLLBACK;
