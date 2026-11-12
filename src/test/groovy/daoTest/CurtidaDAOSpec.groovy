package daoTest

import com.linketinder.dao.CurtidaDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.model.CurtidaDTO
import com.linketinder.model.TipoCurtida
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CurtidaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    Connection conexaoExterna
    PreparedStatement statement
    ResultSet resultado
    CurtidaDAO curtidaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        conexaoExterna = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        curtidaDAO = new CurtidaDAO(conexaoProvider)
    }

    void "deve inserir curtida de candidato em vaga utilizando conexao padrao e retornar data_curtida"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T18:00:00Z"))

        when:
        OffsetDateTime dataRetorno = curtidaDAO.inserirCurtidaCandidatoVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("INSERT INTO curtidas_candidato_vaga") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> null
        1 * resultado.getTimestamp("data_curtida") >> ts
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        dataRetorno == ts.toInstant().atOffset(ZoneOffset.UTC)
    }

    void "deve inserir curtida de candidato em vaga utilizando conexao externa fornecida sem fecha-la"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        OffsetDateTime dataRetorno = curtidaDAO.inserirCurtidaCandidatoVaga(1L, 2L, conexaoExterna)

        then:
        0 * conexaoProvider.obterConexao()
        1 * conexaoExterna.prepareStatement({ String sql -> sql.contains("INSERT INTO curtidas_candidato_vaga") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> agora
        1 * resultado.close()
        1 * statement.close()
        0 * conexaoExterna.close()

        dataRetorno == agora
    }

    void "deve lancar IllegalStateException ao falhar insercao de curtida candidato vaga por SQLException"() {
        when:
        curtidaDAO.inserirCurtidaCandidatoVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Duplicate key violation") }
        1 * conexao.close()

        IllegalStateException e = thrown(IllegalStateException)
        e.message.contains("Não foi possível registrar a curtida do candidato na vaga.")
        e.cause instanceof SQLException
    }

    void "deve inserir curtida de empresa em candidato utilizando conexao padrao e retornar data_curtida"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        OffsetDateTime dataRetorno = curtidaDAO.inserirCurtidaEmpresaCandidato(3L, 4L, 5L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("INSERT INTO curtidas_empresa_candidato") }) >> statement
        1 * statement.setLong(1, 3L)
        1 * statement.setLong(2, 4L)
        1 * statement.setLong(3, 5L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> agora
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        dataRetorno == agora
    }

    void "deve inserir curtida de empresa com recrutador nulo"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        OffsetDateTime dataRetorno = curtidaDAO.inserirCurtidaEmpresaCandidato(3L, 4L, null, conexaoExterna)

        then:
        1 * conexaoExterna.prepareStatement(_) >> statement
        1 * statement.setLong(1, 3L)
        1 * statement.setLong(2, 4L)
        1 * statement.setNull(3, java.sql.Types.INTEGER)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> agora
        1 * resultado.close()
        1 * statement.close()

        dataRetorno == agora
    }

    void "deve lancar IllegalStateException ao falhar insercao de curtida empresa por SQLException"() {
        when:
        curtidaDAO.inserirCurtidaEmpresaCandidato(3L, 4L, 5L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> statement
        1 * statement.executeQuery() >> { throw new SQLException("Foreign key violation") }
        1 * statement.close()
        1 * conexao.close()

        IllegalStateException e = thrown(IllegalStateException)
        e.message.contains("Não foi possível registrar a curtida da empresa no candidato.")
        e.cause instanceof SQLException
    }

    void "deve verificar existencia de curtida de candidato em vaga retornando true e false"() {
        when:
        boolean existe = curtidaDAO.existeCurtidaCandidatoVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("SELECT 1 FROM curtidas_candidato_vaga") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        existe
    }

    void "deve lancar IllegalStateException ao falhar verificacao de existencia de curtida candidato vaga"() {
        when:
        curtidaDAO.existeCurtidaCandidatoVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Connection broken") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }

    void "deve verificar existencia de curtida de empresa em candidato retornando true e false"() {
        when:
        boolean existe = curtidaDAO.existeCurtidaEmpresaCandidato(1L, 2L, conexaoExterna)

        then:
        0 * conexaoProvider.obterConexao()
        1 * conexaoExterna.prepareStatement({ String sql -> sql.contains("SELECT 1 FROM curtidas_empresa_candidato") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> false
        1 * resultado.close()
        1 * statement.close()

        !existe
    }

    void "deve lancar IllegalStateException ao falhar verificacao de existencia de curtida empresa candidato"() {
        when:
        curtidaDAO.existeCurtidaEmpresaCandidato(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Connection broken") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }

    void "deve listar curtidas de candidato com sucesso"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T15:00:00Z"))

        when:
        List<CurtidaDTO> lista = curtidaDAO.listarCurtidasCandidato(10L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("FROM curtidas_candidato_vaga") }) >> statement
        1 * statement.setLong(1, 10L)
        1 * statement.executeQuery() >> resultado
        2 * resultado.next() >>> [true, false]
        1 * resultado.getLong("candidato_id") >> 10L
        1 * resultado.getLong("vaga_id") >> 20L
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> null
        1 * resultado.getTimestamp("data_curtida") >> ts
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        lista.size() == 1
        lista[0].candidatoId == 10L
        lista[0].vagaId == 20L
        lista[0].tipoCurtida == TipoCurtida.CANDIDATO
    }

    void "deve listar curtidas de empresa tratando recrutador_id nulo e nao nulo"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T15:30:00Z"))

        when:
        List<CurtidaDTO> lista = curtidaDAO.listarCurtidasEmpresa(5L, conexaoExterna)

        then:
        1 * conexaoExterna.prepareStatement({ String sql -> sql.contains("FROM curtidas_empresa_candidato") }) >> statement
        1 * statement.setLong(1, 5L)
        1 * statement.executeQuery() >> resultado
        3 * resultado.next() >>> [true, true, false]

        1 * resultado.getLong("empresa_id") >> 5L
        1 * resultado.getLong("candidato_id") >> 1L
        1 * resultado.getLong("recrutador_id") >> 8L
        1 * resultado.wasNull() >> false
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> null
        1 * resultado.getTimestamp("data_curtida") >> ts

        1 * resultado.getLong("empresa_id") >> 5L
        1 * resultado.getLong("candidato_id") >> 2L
        1 * resultado.getLong("recrutador_id") >> 0L
        1 * resultado.wasNull() >> true
        1 * resultado.getObject("data_curtida", OffsetDateTime) >> null
        1 * resultado.getTimestamp("data_curtida") >> ts

        1 * resultado.close()
        1 * statement.close()

        lista.size() == 2
        lista[0].recrutadorId == 8L
        lista[0].tipoCurtida == TipoCurtida.EMPRESA
        lista[1].recrutadorId == null
        lista[1].tipoCurtida == TipoCurtida.EMPRESA
    }

    void "deve lancar IllegalStateException ao falhar listagem de curtidas"() {
        when:
        curtidaDAO.listarCurtidasEmpresa(1L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Query timeout") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }
}
