-- LINKETINDER
-- Banco de dados PostgreSQL

-- TABELA: candidatos

CREATE TABLE candidatos
(
    id                SERIAL PRIMARY KEY,
    nome              VARCHAR(100) NOT NULL,
    sobrenome         VARCHAR(100) NOT NULL,
    data_nascimento   DATE         NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    cpf               VARCHAR(11)  NOT NULL UNIQUE,
    pais              VARCHAR(100) NOT NULL,
    cep               VARCHAR(20)  NOT NULL,
    descricao_pessoal TEXT,
    senha             VARCHAR(255) NOT NULL,
    estado            VARCHAR(2)   NOT NULL
);

-- TABELA: empresas

CREATE TABLE empresas
(
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    cnpj      VARCHAR(14)  NOT NULL UNIQUE,
    email     VARCHAR(150) NOT NULL UNIQUE,
    descricao TEXT,
    pais      VARCHAR(100) NOT NULL,
    cep       VARCHAR(20)  NOT NULL,
    senha     VARCHAR(255) NOT NULL,
    estado    VARCHAR(2)   NOT NULL
);

-- TABELA: competencias

CREATE TABLE competencias
(
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);


-- TABELA: vagas

CREATE TABLE vagas
(
    id         SERIAL PRIMARY KEY,
    empresa_id INTEGER      NOT NULL,
    nome       VARCHAR(150) NOT NULL,
    descricao  TEXT,
    estado     VARCHAR(100) NOT NULL,
    cidade     VARCHAR(100) NOT NULL,

    CONSTRAINT fk_vagas_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresas (id)
            ON DELETE CASCADE
);


-- TABELA: recrutadores
-- Relação 1:N entre empresas e recrutadores

CREATE TABLE recrutadores
(
    id                SERIAL PRIMARY KEY,
    empresa_id        INTEGER      NOT NULL,
    nome              VARCHAR(150) NOT NULL,
    area_recrutamento VARCHAR(100) NOT NULL,
    descricao         TEXT         NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    cpf               VARCHAR(11)  NOT NULL UNIQUE,
    data_nascimento   DATE         NOT NULL,
    senha             VARCHAR(255) NOT NULL,

    CONSTRAINT fk_recrutadores_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresas (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_recrutadores_empresa_id
    ON recrutadores (empresa_id);


-- TABELA: candidato_competencias
-- Relação N:N entre candidatos e competências

CREATE TABLE candidato_competencias
(
    candidato_id   INTEGER NOT NULL,
    competencia_id INTEGER NOT NULL,

    PRIMARY KEY (candidato_id, competencia_id),

    CONSTRAINT fk_candidato_competencias_candidato
        FOREIGN KEY (candidato_id)
            REFERENCES candidatos (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_candidato_competencias_competencia
        FOREIGN KEY (competencia_id)
            REFERENCES competencias (id)
            ON DELETE CASCADE
);


-- TABELA: vaga_competencias
-- Relação N:N entre vagas e competências

CREATE TABLE vaga_competencias
(
    vaga_id        INTEGER NOT NULL,
    competencia_id INTEGER NOT NULL,

    PRIMARY KEY (vaga_id, competencia_id),

    CONSTRAINT fk_vaga_competencias_vaga
        FOREIGN KEY (vaga_id)
            REFERENCES vagas (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_vaga_competencias_competencia
        FOREIGN KEY (competencia_id)
            REFERENCES competencias (id)
            ON DELETE CASCADE
);


-- TABELA: curtidas_candidato_vaga
-- Relação N:N entre candidatos e vagas

CREATE TABLE curtidas_candidato_vaga
(
    candidato_id INTEGER     NOT NULL,
    vaga_id      INTEGER     NOT NULL,
    data_curtida TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_curtidas_candidato_vaga
        PRIMARY KEY (candidato_id, vaga_id),

    CONSTRAINT fk_curtidas_candidato_vaga_candidato
        FOREIGN KEY (candidato_id)
            REFERENCES candidatos (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_curtidas_candidato_vaga_vaga
        FOREIGN KEY (vaga_id)
            REFERENCES vagas (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_curtidas_cand_vaga_vaga_id
    ON curtidas_candidato_vaga (vaga_id);


-- TABELA: curtidas_empresa_candidato
-- Relação N:N entre empresas e candidatos, identificando o recrutador responsável

CREATE TABLE curtidas_empresa_candidato
(
    empresa_id    INTEGER     NOT NULL,
    candidato_id  INTEGER     NOT NULL,
    recrutador_id INTEGER     NULL,
    data_curtida  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_curtidas_empresa_candidato
        PRIMARY KEY (empresa_id, candidato_id),

    CONSTRAINT fk_curtidas_empresa_candidato_empresa
        FOREIGN KEY (empresa_id)
            REFERENCES empresas (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_curtidas_empresa_candidato_candidato
        FOREIGN KEY (candidato_id)
            REFERENCES candidatos (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_curtidas_empresa_candidato_recrutador
        FOREIGN KEY (recrutador_id)
            REFERENCES recrutadores (id)
            ON DELETE SET NULL
);

CREATE INDEX idx_curtidas_emp_cand_candidato_id
    ON curtidas_empresa_candidato (candidato_id);

CREATE INDEX idx_curtidas_emp_cand_recrutador_id
    ON curtidas_empresa_candidato (recrutador_id);


-- TABELA: matches
-- Registro de interesse mútuo (1 match por empresa e candidato)

CREATE TABLE matches
(
    id            SERIAL      PRIMARY KEY,
    empresa_id    INTEGER     NOT NULL,
    candidato_id  INTEGER     NOT NULL,
    recrutador_id INTEGER     NULL,
    data_match    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_matches_empresa_candidato
        UNIQUE (empresa_id, candidato_id),

    CONSTRAINT fk_matches_curtida_empresa_candidato
        FOREIGN KEY (empresa_id, candidato_id)
            REFERENCES curtidas_empresa_candidato (empresa_id, candidato_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_matches_recrutador
        FOREIGN KEY (recrutador_id)
            REFERENCES recrutadores (id)
            ON DELETE SET NULL
);

CREATE INDEX idx_matches_candidato_id
    ON matches (candidato_id);

CREATE INDEX idx_matches_recrutador_id
    ON matches (recrutador_id);


-- =============================================================================
-- FUNÇÕES E TRIGGERS: INTEGRIDADE, RECIPROCIDADE E CONSISTÊNCIA DE MATCHES
-- =============================================================================

-- 0. Garante a imutabilidade do vínculo empresa_id de um recrutador (RN-02),
--    impedindo que curtidas ou matches históricos fiquem associados a recrutador de outra empresa.
CREATE OR REPLACE FUNCTION fn_impedir_alteracao_empresa_recrutador()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.empresa_id <> OLD.empresa_id THEN
        RAISE EXCEPTION 'Nao e permitido alterar empresa_id de um recrutador.'
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_impedir_alteracao_empresa_recrutador
    BEFORE UPDATE OF empresa_id ON recrutadores
    FOR EACH ROW
    EXECUTE FUNCTION fn_impedir_alteracao_empresa_recrutador();


-- 1. Valida obrigatoriedade e vínculo do recrutador na curtida da empresa,
--    permitindo transição para NULL exclusivamente via ON DELETE SET NULL,
--    e serializa operações no par (empresa_id, candidato_id) antes do lock de tupla.
CREATE OR REPLACE FUNCTION fn_validar_curtida_empresa_recrutador()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        PERFORM pg_advisory_xact_lock(OLD.empresa_id, OLD.candidato_id);
        RETURN OLD;
    END IF;

    IF TG_OP = 'UPDATE' THEN
        IF NEW.empresa_id <> OLD.empresa_id OR NEW.candidato_id <> OLD.candidato_id THEN
            RAISE EXCEPTION 'Nao e permitido alterar empresa_id ou candidato_id de uma curtida.'
                USING ERRCODE = 'integrity_constraint_violation';
        END IF;

        PERFORM pg_advisory_xact_lock(NEW.empresa_id, NEW.candidato_id);

        IF NEW.recrutador_id IS NULL THEN
            IF OLD.recrutador_id IS NOT NULL
               AND EXISTS (SELECT 1 FROM recrutadores r WHERE r.id = OLD.recrutador_id) THEN
                RAISE EXCEPTION 'recrutador_id nao pode ser definido como NULL manualmente enquanto o recrutador existir.'
                    USING ERRCODE = 'not_null_violation';
            END IF;
            RETURN NEW;
        END IF;
    ELSE
        PERFORM pg_advisory_xact_lock(NEW.empresa_id, NEW.candidato_id);

        IF NEW.recrutador_id IS NULL THEN
            RAISE EXCEPTION 'recrutador_id e obrigatorio para registrar curtida de empresa em candidato.'
                USING ERRCODE = 'not_null_violation';
        END IF;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM recrutadores r
        WHERE r.id = NEW.recrutador_id
          AND r.empresa_id = NEW.empresa_id
        FOR KEY SHARE OF r
    ) THEN
        RAISE EXCEPTION 'O recrutador informado (%) nao pertence a empresa (%).',
            NEW.recrutador_id, NEW.empresa_id
            USING ERRCODE = 'foreign_key_violation';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_curtida_empresa_recrutador
    BEFORE INSERT OR UPDATE OR DELETE ON curtidas_empresa_candidato
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_curtida_empresa_recrutador();


-- 1b. Adquire o advisory lock determinístico antes do lock de tupla em
--     curtidas_candidato_vaga e vagas, evitando inversão de locks e deadlocks.
CREATE OR REPLACE FUNCTION fn_serializar_curtida_candidato_vaga()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        PERFORM pg_advisory_xact_lock(v.empresa_id, NEW.candidato_id)
        FROM vagas v
        WHERE v.id = NEW.vaga_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
        FROM (
            SELECT v.empresa_id, OLD.candidato_id AS candidato_id
            FROM vagas v
            WHERE v.id = OLD.vaga_id
            UNION
            SELECT m.empresa_id, m.candidato_id
            FROM matches m
            WHERE m.candidato_id = OLD.candidato_id
        ) pares
        ORDER BY pares.empresa_id, pares.candidato_id;
        RETURN OLD;
    ELSE
        PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
        FROM (
            SELECT v.empresa_id, OLD.candidato_id AS candidato_id
            FROM vagas v
            WHERE v.id = OLD.vaga_id
            UNION
            SELECT v.empresa_id, NEW.candidato_id AS candidato_id
            FROM vagas v
            WHERE v.id = NEW.vaga_id
            UNION
            SELECT m.empresa_id, m.candidato_id
            FROM matches m
            WHERE m.candidato_id IN (OLD.candidato_id, NEW.candidato_id)
        ) pares
        ORDER BY pares.empresa_id, pares.candidato_id;
        RETURN NEW;
    END IF;
END;
$$;

CREATE TRIGGER trg_serializar_curtida_candidato_vaga
    BEFORE INSERT OR UPDATE OR DELETE ON curtidas_candidato_vaga
    FOR EACH ROW
    EXECUTE FUNCTION fn_serializar_curtida_candidato_vaga();


CREATE OR REPLACE FUNCTION fn_serializar_vaga_matches()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        PERFORM pg_advisory_xact_lock(OLD.empresa_id, ccv.candidato_id)
        FROM curtidas_candidato_vaga ccv
        WHERE ccv.vaga_id = OLD.id
        ORDER BY ccv.candidato_id;
        RETURN OLD;
    END IF;

    IF NEW.empresa_id <> OLD.empresa_id THEN
        PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
        FROM (
            SELECT OLD.empresa_id AS empresa_id, ccv.candidato_id
            FROM curtidas_candidato_vaga ccv
            WHERE ccv.vaga_id = OLD.id
            UNION
            SELECT NEW.empresa_id AS empresa_id, ccv.candidato_id
            FROM curtidas_candidato_vaga ccv
            WHERE ccv.vaga_id = OLD.id
        ) pares
        ORDER BY pares.empresa_id, pares.candidato_id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_serializar_vaga_matches
    BEFORE UPDATE OF empresa_id OR DELETE ON vagas
    FOR EACH ROW
    EXECUTE FUNCTION fn_serializar_vaga_matches();


-- 2. Impede criação manual de match sem reciprocidade ativa e protege
--    atualizações durante ON DELETE SET NULL de recrutadores.
CREATE OR REPLACE FUNCTION fn_validar_match()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_cec_recrutador_id INTEGER;
BEGIN
    IF TG_OP = 'UPDATE' THEN
        IF NEW.empresa_id <> OLD.empresa_id OR NEW.candidato_id <> OLD.candidato_id THEN
            RAISE EXCEPTION 'Nao e permitido alterar empresa_id ou candidato_id de um match.'
                USING ERRCODE = 'integrity_constraint_violation';
        END IF;

        IF NEW.recrutador_id IS NULL THEN
            IF OLD.recrutador_id IS NOT NULL
               AND EXISTS (SELECT 1 FROM recrutadores r WHERE r.id = OLD.recrutador_id) THEN
                RAISE EXCEPTION 'recrutador_id do match nao pode ser definido como NULL manualmente enquanto o recrutador existir.'
                    USING ERRCODE = 'not_null_violation';
            END IF;
            RETURN NEW;
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM recrutadores r
            WHERE r.id = NEW.recrutador_id
              AND r.empresa_id = NEW.empresa_id
            FOR KEY SHARE OF r
        ) THEN
            RAISE EXCEPTION 'O recrutador informado (%) nao pertence a empresa (%) do match.',
                NEW.recrutador_id, NEW.empresa_id
                USING ERRCODE = 'foreign_key_violation';
        END IF;

        RETURN NEW;
    END IF;

    PERFORM pg_advisory_xact_lock(NEW.empresa_id, NEW.candidato_id);

    SELECT cec.recrutador_id
    INTO v_cec_recrutador_id
    FROM curtidas_empresa_candidato cec
    WHERE cec.empresa_id = NEW.empresa_id
      AND cec.candidato_id = NEW.candidato_id
    FOR KEY SHARE OF cec;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Match invalido: nao existe curtida da empresa (%) para o candidato (%).',
            NEW.empresa_id, NEW.candidato_id
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM curtidas_candidato_vaga ccv
        INNER JOIN vagas v ON v.id = ccv.vaga_id
        WHERE ccv.candidato_id = NEW.candidato_id
          AND v.empresa_id = NEW.empresa_id
        FOR KEY SHARE OF ccv, v
    ) THEN
        RAISE EXCEPTION 'Match invalido: nao existe curtida do candidato (%) em vaga da empresa (%).',
            NEW.candidato_id, NEW.empresa_id
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    IF NEW.recrutador_id IS DISTINCT FROM v_cec_recrutador_id THEN
        RAISE EXCEPTION 'recrutador_id do match (%) diverge do recrutador da curtida da empresa (%).',
            NEW.recrutador_id, v_cec_recrutador_id
            USING ERRCODE = 'integrity_constraint_violation';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_match
    BEFORE INSERT OR UPDATE ON matches
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_match();


-- 3. Registra match automaticamente quando o candidato curte uma vaga
--    e a empresa titular da vaga já havia curtido o candidato.
CREATE OR REPLACE FUNCTION fn_registrar_match_apos_curtida_candidato()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_empresa_id    INTEGER;
    v_recrutador_id INTEGER;
BEGIN
    IF TG_OP = 'UPDATE' THEN
        PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
        FROM (
            SELECT v.empresa_id, OLD.candidato_id AS candidato_id
            FROM vagas v
            WHERE v.id = OLD.vaga_id
            UNION
            SELECT v.empresa_id, NEW.candidato_id AS candidato_id
            FROM vagas v
            WHERE v.id = NEW.vaga_id
            UNION
            SELECT m.empresa_id, m.candidato_id
            FROM matches m
            WHERE m.candidato_id IN (OLD.candidato_id, NEW.candidato_id)
        ) pares
        ORDER BY pares.empresa_id, pares.candidato_id;
    END IF;

    SELECT v.empresa_id
    INTO v_empresa_id
    FROM vagas v
    WHERE v.id = NEW.vaga_id
    FOR KEY SHARE OF v;

    IF NOT FOUND THEN
        RETURN NEW;
    END IF;

    PERFORM pg_advisory_xact_lock(v_empresa_id, NEW.candidato_id);

    SELECT cec.recrutador_id
    INTO v_recrutador_id
    FROM curtidas_empresa_candidato cec
    WHERE cec.empresa_id = v_empresa_id
      AND cec.candidato_id = NEW.candidato_id
    FOR KEY SHARE OF cec;

    IF FOUND THEN
        INSERT INTO matches (empresa_id, candidato_id, recrutador_id)
        VALUES (v_empresa_id, NEW.candidato_id, v_recrutador_id)
        ON CONFLICT (empresa_id, candidato_id) DO NOTHING;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_registrar_match_apos_curtida_candidato
    AFTER INSERT OR UPDATE OF candidato_id, vaga_id ON curtidas_candidato_vaga
    FOR EACH ROW
    EXECUTE FUNCTION fn_registrar_match_apos_curtida_candidato();


-- 4. Registra match automaticamente quando o recrutador curte um candidato
--    em nome da empresa e o candidato já havia curtido vaga dessa empresa,
--    e sincroniza recrutador_id em caso de atualização válida.
CREATE OR REPLACE FUNCTION fn_registrar_match_apos_curtida_empresa()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    PERFORM pg_advisory_xact_lock(NEW.empresa_id, NEW.candidato_id);

    IF TG_OP = 'UPDATE' THEN
        IF NEW.recrutador_id IS NOT NULL THEN
            UPDATE matches
            SET recrutador_id = NEW.recrutador_id
            WHERE empresa_id = NEW.empresa_id
              AND candidato_id = NEW.candidato_id
              AND recrutador_id IS DISTINCT FROM NEW.recrutador_id;
        END IF;
        RETURN NEW;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM curtidas_candidato_vaga ccv
        INNER JOIN vagas v ON v.id = ccv.vaga_id
        WHERE ccv.candidato_id = NEW.candidato_id
          AND v.empresa_id = NEW.empresa_id
        FOR KEY SHARE OF ccv, v
    ) THEN
        INSERT INTO matches (empresa_id, candidato_id, recrutador_id)
        VALUES (NEW.empresa_id, NEW.candidato_id, NEW.recrutador_id)
        ON CONFLICT (empresa_id, candidato_id) DO NOTHING;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_registrar_match_apos_curtida_empresa
    AFTER INSERT OR UPDATE OF recrutador_id ON curtidas_empresa_candidato
    FOR EACH ROW
    EXECUTE FUNCTION fn_registrar_match_apos_curtida_empresa();


-- 5. Remove o match quando a exclusão (ou alteração) de curtida em vaga
--    faz desaparecer a última curtida válida do candidato em vagas daquela empresa.
CREATE OR REPLACE FUNCTION fn_remover_match_sem_reciprocidade()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
    FROM (
        SELECT v.empresa_id, OLD.candidato_id AS candidato_id
        FROM vagas v
        WHERE v.id = OLD.vaga_id
        UNION
        SELECT m.empresa_id, m.candidato_id
        FROM matches m
        WHERE m.candidato_id = OLD.candidato_id
    ) pares
    ORDER BY pares.empresa_id, pares.candidato_id;

    DELETE FROM matches m
    WHERE m.candidato_id = OLD.candidato_id
      AND NOT EXISTS (
          SELECT 1
          FROM curtidas_candidato_vaga ccv
          INNER JOIN vagas v ON v.id = ccv.vaga_id
          WHERE ccv.candidato_id = m.candidato_id
            AND v.empresa_id = m.empresa_id
          FOR KEY SHARE OF ccv, v
      );

    RETURN OLD;
END;
$$;

CREATE TRIGGER trg_remover_match_sem_reciprocidade
    AFTER DELETE OR UPDATE OF candidato_id, vaga_id ON curtidas_candidato_vaga
    FOR EACH ROW
    EXECUTE FUNCTION fn_remover_match_sem_reciprocidade();


-- 6. Reavalia matches caso uma vaga tenha seu empresa_id alterado via UPDATE:
--    remove matches da empresa antiga que perderam reciprocidade e cria matches
--    para a nova empresa quando já houver curtida recíproca.
CREATE OR REPLACE FUNCTION fn_sincronizar_matches_apos_update_vaga()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.empresa_id = OLD.empresa_id THEN
        RETURN NEW;
    END IF;

    PERFORM pg_advisory_xact_lock(pares.empresa_id, pares.candidato_id)
    FROM (
        SELECT OLD.empresa_id AS empresa_id, ccv.candidato_id
        FROM curtidas_candidato_vaga ccv
        WHERE ccv.vaga_id = NEW.id
        UNION
        SELECT NEW.empresa_id AS empresa_id, ccv.candidato_id
        FROM curtidas_candidato_vaga ccv
        WHERE ccv.vaga_id = NEW.id
    ) pares
    ORDER BY pares.empresa_id, pares.candidato_id;

    DELETE FROM matches m
    WHERE m.empresa_id = OLD.empresa_id
      AND m.candidato_id IN (
          SELECT ccv.candidato_id
          FROM curtidas_candidato_vaga ccv
          WHERE ccv.vaga_id = NEW.id
      )
      AND NOT EXISTS (
          SELECT 1
          FROM curtidas_candidato_vaga ccv2
          INNER JOIN vagas v2 ON v2.id = ccv2.vaga_id
          WHERE ccv2.candidato_id = m.candidato_id
            AND v2.empresa_id = m.empresa_id
          FOR KEY SHARE OF ccv2, v2
      );

    INSERT INTO matches (empresa_id, candidato_id, recrutador_id)
    SELECT cec.empresa_id, cec.candidato_id, cec.recrutador_id
    FROM curtidas_candidato_vaga ccv
    INNER JOIN curtidas_empresa_candidato cec
        ON cec.empresa_id = NEW.empresa_id
       AND cec.candidato_id = ccv.candidato_id
    WHERE ccv.vaga_id = NEW.id
    FOR KEY SHARE OF ccv, cec
    ON CONFLICT (empresa_id, candidato_id) DO NOTHING;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_sincronizar_matches_apos_update_vaga
    AFTER UPDATE OF empresa_id ON vagas
    FOR EACH ROW
    EXECUTE FUNCTION fn_sincronizar_matches_apos_update_vaga();
