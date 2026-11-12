package com.linketinder.dao

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.MatchAnonimo
import com.linketinder.model.RecrutadorAnonimo
import com.linketinder.model.Skill
import com.linketinder.repository.CandidatoCompetenciaRepository
import com.linketinder.repository.MatchRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MatchDAO implements MatchRepository {

    private final ConexaoProvider conexaoProvider
    private final CandidatoCompetenciaRepository candidatoCompetenciaRepository

    MatchDAO(
            ConexaoProvider conexaoProvider,
            CandidatoCompetenciaRepository candidatoCompetenciaRepository = null
    ) {
        this.conexaoProvider = conexaoProvider
        this.candidatoCompetenciaRepository = candidatoCompetenciaRepository
    }

    @Override
    Long buscarMatchIdPorEmpresaECandidato(Long empresaId, Long candidatoId) {
        return buscarMatchIdPorEmpresaECandidato(empresaId, candidatoId, null)
    }

    @Override
    Long buscarMatchIdPorEmpresaECandidato(Long empresaId, Long candidatoId, Connection conexao) {
        String sql = "SELECT id FROM matches WHERE empresa_id = ? AND candidato_id = ?"

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, empresaId)
                statement.setLong(2, candidatoId)

                try (ResultSet resultado = statement.executeQuery()) {
                    if (resultado.next()) {
                        return resultado.getLong("id")
                    }
                    return null
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao buscar ID do match por empresa e candidato.", e)
            }
        }
    }

    @Override
    Long buscarMatchIdPorCandidatoEVaga(Long candidatoId, Long vagaId) {
        return buscarMatchIdPorCandidatoEVaga(candidatoId, vagaId, null)
    }

    @Override
    Long buscarMatchIdPorCandidatoEVaga(Long candidatoId, Long vagaId, Connection conexao) {
        String sql = """
            SELECT m.id
            FROM matches m
            INNER JOIN vagas v ON m.empresa_id = v.empresa_id
            WHERE m.candidato_id = ? AND v.id = ?
            LIMIT 1
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, candidatoId)
                statement.setLong(2, vagaId)

                try (ResultSet resultado = statement.executeQuery()) {
                    if (resultado.next()) {
                        return resultado.getLong("id")
                    }
                    return null
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao buscar ID do match por candidato e vaga.", e)
            }
        }
    }

    @Override
    MatchAnonimo buscarPorId(Long id) {
        return buscarPorId(id, null)
    }

    @Override
    MatchAnonimo buscarPorId(Long id, Connection conexao) {
        String sql = """
            SELECT
                m.id AS match_id,
                m.empresa_id AS match_empresa_id,
                m.candidato_id AS match_candidato_id,
                m.recrutador_id AS match_recrutador_id,
                m.data_match AS match_data_match,
                e.pais AS empresa_pais,
                e.estado AS empresa_estado,
                e.descricao AS empresa_descricao,
                c.estado AS candidato_estado,
                c.descricao_pessoal AS candidato_descricao,
                r.area_recrutamento AS recrutador_area
            FROM matches m
            INNER JOIN empresas e ON m.empresa_id = e.id
            INNER JOIN candidatos c ON m.candidato_id = c.id
            LEFT JOIN recrutadores r ON m.recrutador_id = r.id
            WHERE m.id = ?
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, id)

                try (ResultSet resultado = statement.executeQuery()) {
                    if (resultado.next()) {
                        return mapearMatchAnonimo(resultado)
                    }
                    return null
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao buscar match por ID.", e)
            }
        }
    }

    @Override
    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId) {
        return listarMatches(candidatoId, empresaId, null)
    }

    @Override
    List<MatchAnonimo> listarMatches(Long candidatoId, Long empresaId, Connection conexao) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                m.id AS match_id,
                m.empresa_id AS match_empresa_id,
                m.candidato_id AS match_candidato_id,
                m.recrutador_id AS match_recrutador_id,
                m.data_match AS match_data_match,
                e.pais AS empresa_pais,
                e.estado AS empresa_estado,
                e.descricao AS empresa_descricao,
                c.estado AS candidato_estado,
                c.descricao_pessoal AS candidato_descricao,
                r.area_recrutamento AS recrutador_area
            FROM matches m
            INNER JOIN empresas e ON m.empresa_id = e.id
            INNER JOIN candidatos c ON m.candidato_id = c.id
            LEFT JOIN recrutadores r ON m.recrutador_id = r.id
            WHERE 1 = 1
        """)

        List<Object> parametros = []
        if (candidatoId != null) {
            sql.append(" AND m.candidato_id = ?")
            parametros.add(candidatoId)
        }
        if (empresaId != null) {
            sql.append(" AND m.empresa_id = ?")
            parametros.add(empresaId)
        }
        sql.append(" ORDER BY m.data_match DESC, m.id DESC")

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < parametros.size(); i++) {
                    statement.setObject(i + 1, parametros[i])
                }

                List<MatchAnonimo> matches = []
                try (ResultSet resultado = statement.executeQuery()) {
                    while (resultado.next()) {
                        matches.add(mapearMatchAnonimo(resultado))
                    }
                }
                return matches
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao listar matches.", e)
            }
        }
    }

    private MatchAnonimo mapearMatchAnonimo(ResultSet resultado) throws SQLException {
        Long matchId = resultado.getLong("match_id")
        Long empresaId = resultado.getLong("match_empresa_id")
        Long candidatoId = resultado.getLong("match_candidato_id")
        Long recrutadorId = resultado.getLong("match_recrutador_id")
        if (resultado.wasNull()) {
            recrutadorId = null
        }
        OffsetDateTime dataMatch = extrairOffsetDateTime(resultado, "match_data_match")

        EmpresaAnonima empresa = new EmpresaAnonima(
                id: empresaId,
                pais: resultado.getString("empresa_pais"),
                estado: resultado.getString("empresa_estado"),
                descricaoEmpresarial: resultado.getString("empresa_descricao")
        )

        List<Skill> skills = []
        if (candidatoCompetenciaRepository != null) {
            try {
                skills = candidatoCompetenciaRepository.listarCompetenciasDoCandidato(candidatoId) ?: []
            } catch (Exception ignored) {
                skills = []
            }
        }

        CandidatoAnonimo candidato = new CandidatoAnonimo(
                id: candidatoId,
                estado: resultado.getString("candidato_estado"),
                descricaoPessoal: resultado.getString("candidato_descricao"),
                skills: skills
        )

        RecrutadorAnonimo recrutador = null
        if (recrutadorId != null) {
            recrutador = new RecrutadorAnonimo(
                    id: recrutadorId,
                    areaRecrutamento: resultado.getString("recrutador_area")
            )
        }

        return new MatchAnonimo(
                id: matchId,
                dataMatch: dataMatch,
                candidato: candidato,
                empresa: empresa,
                recrutador: recrutador
        )
    }

    private <T> T executarComConexao(Connection conexaoExterna, Closure<T> operacao) {
        if (conexaoExterna != null && !conexaoExterna.isClosed()) {
            return operacao.call(conexaoExterna)
        }
        try (Connection conn = conexaoProvider.obterConexao()) {
            return operacao.call(conn)
        }
    }

    private OffsetDateTime extrairOffsetDateTime(ResultSet resultado, String coluna) {
        try {
            OffsetDateTime odt = resultado.getObject(coluna, OffsetDateTime.class)
            if (odt != null) {
                return odt
            }
        } catch (Exception ignored) {
        }
        try {
            Timestamp ts = resultado.getTimestamp(coluna)
            if (ts != null) {
                return ts.toInstant().atOffset(ZoneOffset.UTC)
            }
        } catch (Exception ignored) {
        }
        return null
    }
}
