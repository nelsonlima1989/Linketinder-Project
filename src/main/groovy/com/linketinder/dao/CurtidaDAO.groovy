package com.linketinder.dao

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.CurtidaDTO
import com.linketinder.model.TipoCurtida
import com.linketinder.repository.CurtidaRepository

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CurtidaDAO implements CurtidaRepository {

    private final ConexaoProvider conexaoProvider

    CurtidaDAO(ConexaoProvider conexaoProvider) {
        this.conexaoProvider = conexaoProvider
    }

    @Override
    OffsetDateTime inserirCurtidaCandidatoVaga(Long candidatoId, Long vagaId) {
        return inserirCurtidaCandidatoVaga(candidatoId, vagaId, null)
    }

    @Override
    OffsetDateTime inserirCurtidaCandidatoVaga(Long candidatoId, Long vagaId, Connection conexao) {
        String sql = """
            INSERT INTO curtidas_candidato_vaga (candidato_id, vaga_id)
            VALUES (?, ?)
            RETURNING data_curtida
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, candidatoId)
                statement.setLong(2, vagaId)

                try (ResultSet resultado = statement.executeQuery()) {
                    if (resultado.next()) {
                        return extrairOffsetDateTime(resultado, "data_curtida")
                    }
                    return null
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Não foi possível registrar a curtida do candidato na vaga.", e)
            }
        }
    }

    @Override
    OffsetDateTime inserirCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Long recrutadorId) {
        return inserirCurtidaEmpresaCandidato(empresaId, candidatoId, recrutadorId, null)
    }

    @Override
    OffsetDateTime inserirCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Long recrutadorId, Connection conexao) {
        String sql = """
            INSERT INTO curtidas_empresa_candidato (empresa_id, candidato_id, recrutador_id)
            VALUES (?, ?, ?)
            RETURNING data_curtida
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, empresaId)
                statement.setLong(2, candidatoId)
                if (recrutadorId != null) {
                    statement.setLong(3, recrutadorId)
                } else {
                    statement.setNull(3, java.sql.Types.INTEGER)
                }

                try (ResultSet resultado = statement.executeQuery()) {
                    if (resultado.next()) {
                        return extrairOffsetDateTime(resultado, "data_curtida")
                    }
                    return null
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Não foi possível registrar a curtida da empresa no candidato.", e)
            }
        }
    }

    @Override
    boolean existeCurtidaCandidatoVaga(Long candidatoId, Long vagaId) {
        return existeCurtidaCandidatoVaga(candidatoId, vagaId, null)
    }

    @Override
    boolean existeCurtidaCandidatoVaga(Long candidatoId, Long vagaId, Connection conexao) {
        String sql = "SELECT 1 FROM curtidas_candidato_vaga WHERE candidato_id = ? AND vaga_id = ?"

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, candidatoId)
                statement.setLong(2, vagaId)

                try (ResultSet resultado = statement.executeQuery()) {
                    return resultado.next()
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao verificar existência de curtida do candidato na vaga.", e)
            }
        }
    }

    @Override
    boolean existeCurtidaEmpresaCandidato(Long empresaId, Long candidatoId) {
        return existeCurtidaEmpresaCandidato(empresaId, candidatoId, null)
    }

    @Override
    boolean existeCurtidaEmpresaCandidato(Long empresaId, Long candidatoId, Connection conexao) {
        String sql = "SELECT 1 FROM curtidas_empresa_candidato WHERE empresa_id = ? AND candidato_id = ?"

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, empresaId)
                statement.setLong(2, candidatoId)

                try (ResultSet resultado = statement.executeQuery()) {
                    return resultado.next()
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao verificar existência de curtida da empresa no candidato.", e)
            }
        }
    }

    @Override
    List<CurtidaDTO> listarCurtidasCandidato(Long candidatoId) {
        return listarCurtidasCandidato(candidatoId, null)
    }

    @Override
    List<CurtidaDTO> listarCurtidasCandidato(Long candidatoId, Connection conexao) {
        String sql = """
            SELECT candidato_id, vaga_id, data_curtida
            FROM curtidas_candidato_vaga
            WHERE candidato_id = ?
            ORDER BY data_curtida DESC
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, candidatoId)

                List<CurtidaDTO> curtidas = []
                try (ResultSet resultado = statement.executeQuery()) {
                    while (resultado.next()) {
                        CurtidaDTO dto = new CurtidaDTO(
                                candidatoId: resultado.getLong("candidato_id"),
                                vagaId: resultado.getLong("vaga_id"),
                                dataCurtida: extrairOffsetDateTime(resultado, "data_curtida"),
                                tipoCurtida: TipoCurtida.CANDIDATO
                        )
                        curtidas.add(dto)
                    }
                }
                return curtidas
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao listar curtidas do candidato.", e)
            }
        }
    }

    @Override
    List<CurtidaDTO> listarCurtidasEmpresa(Long empresaId) {
        return listarCurtidasEmpresa(empresaId, null)
    }

    @Override
    List<CurtidaDTO> listarCurtidasEmpresa(Long empresaId, Connection conexao) {
        String sql = """
            SELECT empresa_id, candidato_id, recrutador_id, data_curtida
            FROM curtidas_empresa_candidato
            WHERE empresa_id = ?
            ORDER BY data_curtida DESC
        """

        return executarComConexao(conexao) { Connection conn ->
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, empresaId)

                List<CurtidaDTO> curtidas = []
                try (ResultSet resultado = statement.executeQuery()) {
                    while (resultado.next()) {
                        Long recrutadorId = resultado.getLong("recrutador_id")
                        if (resultado.wasNull()) {
                            recrutadorId = null
                        }

                        CurtidaDTO dto = new CurtidaDTO(
                                empresaId: resultado.getLong("empresa_id"),
                                candidatoId: resultado.getLong("candidato_id"),
                                recrutadorId: recrutadorId,
                                dataCurtida: extrairOffsetDateTime(resultado, "data_curtida"),
                                tipoCurtida: TipoCurtida.EMPRESA
                        )
                        curtidas.add(dto)
                    }
                }
                return curtidas
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao listar curtidas da empresa.", e)
            }
        }
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
