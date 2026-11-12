package daoTest

import com.linketinder.dao.MatchDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.model.MatchAnonimo
import com.linketinder.model.Skill
import com.linketinder.repository.CandidatoCompetenciaRepository
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MatchDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    CandidatoCompetenciaRepository candidatoCompetenciaRepository
    Connection conexao
    Connection conexaoExterna
    PreparedStatement statement
    ResultSet resultado
    MatchDAO matchDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        candidatoCompetenciaRepository = Mock(CandidatoCompetenciaRepository)
        conexao = Mock(Connection)
        conexaoExterna = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        matchDAO = new MatchDAO(conexaoProvider, candidatoCompetenciaRepository)
    }

    void "deve buscar match id por empresa e candidato quando existir"() {
        when:
        Long matchId = matchDAO.buscarMatchIdPorEmpresaECandidato(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("SELECT id FROM matches") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 42L
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        matchId == 42L
    }

    void "deve retornar null quando match nao existir por empresa e candidato usando conexao externa"() {
        when:
        Long matchId = matchDAO.buscarMatchIdPorEmpresaECandidato(1L, 2L, conexaoExterna)

        then:
        0 * conexaoProvider.obterConexao()
        1 * conexaoExterna.prepareStatement(_) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.setLong(2, 2L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> false
        1 * resultado.close()
        1 * statement.close()
        0 * conexaoExterna.close()

        matchId == null
    }

    void "deve lancar IllegalStateException ao falhar busca de match id por empresa e candidato"() {
        when:
        matchDAO.buscarMatchIdPorEmpresaECandidato(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Database down") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }

    void "deve buscar match id por candidato e vaga com sucesso"() {
        when:
        Long matchId = matchDAO.buscarMatchIdPorCandidatoEVaga(2L, 10L, conexaoExterna)

        then:
        1 * conexaoExterna.prepareStatement({ String sql -> sql.contains("INNER JOIN vagas") }) >> statement
        1 * statement.setLong(1, 2L)
        1 * statement.setLong(2, 10L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 99L
        1 * resultado.close()
        1 * statement.close()

        matchId == 99L
    }

    void "deve lancar IllegalStateException ao falhar busca de match id por candidato e vaga"() {
        when:
        matchDAO.buscarMatchIdPorCandidatoEVaga(2L, 10L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Error") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }

    void "deve buscar match anonimo por id com sucesso e mapear campos seguros sem nomes nem contatos"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T16:00:00Z"))
        List<Skill> skillsMock = [new Skill(id: 1L, nome: "Groovy"), new Skill(id: 2L, nome: "PostgreSQL")]

        when:
        MatchAnonimo match = matchDAO.buscarPorId(7L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("WHERE m.id = ?") }) >> statement
        1 * statement.setLong(1, 7L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true

        1 * resultado.getLong("match_id") >> 7L
        1 * resultado.getLong("match_empresa_id") >> 3L
        1 * resultado.getLong("match_candidato_id") >> 4L
        1 * resultado.getLong("match_recrutador_id") >> 5L
        1 * resultado.wasNull() >> false
        1 * resultado.getObject("match_data_match", OffsetDateTime) >> null
        1 * resultado.getTimestamp("match_data_match") >> ts

        1 * resultado.getString("empresa_pais") >> "Brasil"
        1 * resultado.getString("empresa_estado") >> "SP"
        1 * resultado.getString("empresa_descricao") >> "Empresa de TI"

        1 * resultado.getString("candidato_estado") >> "RJ"
        1 * resultado.getString("candidato_descricao") >> "Desenvolvedor Pleno"

        1 * resultado.getString("recrutador_area") >> "Tecnologia"

        1 * candidatoCompetenciaRepository.listarCompetenciasDoCandidato(4L) >> skillsMock
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        match != null
        match.id == 7L
        match.dataMatch == ts.toInstant().atOffset(ZoneOffset.UTC)

        match.empresa.id == 3L
        match.empresa.pais == "Brasil"
        match.empresa.estado == "SP"
        match.empresa.descricaoEmpresarial == "Empresa de TI"

        match.candidato.id == 4L
        match.candidato.estado == "RJ"
        match.candidato.descricaoPessoal == "Desenvolvedor Pleno"
        match.candidato.skills.size() == 2
        match.candidato.skills*.nome == ["Groovy", "PostgreSQL"]

        match.recrutador.id == 5L
        match.recrutador.areaRecrutamento == "Tecnologia"
    }

    void "deve buscar match anonimo por id com recrutador nulo"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T16:00:00Z"))

        when:
        MatchAnonimo match = matchDAO.buscarPorId(8L, conexaoExterna)

        then:
        1 * conexaoExterna.prepareStatement(_) >> statement
        1 * statement.setLong(1, 8L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true

        1 * resultado.getLong("match_id") >> 8L
        1 * resultado.getLong("match_empresa_id") >> 1L
        1 * resultado.getLong("match_candidato_id") >> 2L
        1 * resultado.getLong("match_recrutador_id") >> 0L
        1 * resultado.wasNull() >> true
        1 * resultado.getObject("match_data_match", OffsetDateTime) >> null
        1 * resultado.getTimestamp("match_data_match") >> ts

        1 * resultado.getString("empresa_pais") >> "Brasil"
        1 * resultado.getString("empresa_estado") >> "MG"
        1 * resultado.getString("empresa_descricao") >> "Logística"

        1 * resultado.getString("candidato_estado") >> "MG"
        1 * resultado.getString("candidato_descricao") >> "QA Analyst"

        1 * candidatoCompetenciaRepository.listarCompetenciasDoCandidato(2L) >> []
        1 * resultado.close()
        1 * statement.close()

        match != null
        match.recrutador == null
        match.candidato.skills.isEmpty()
    }

    void "deve retornar null quando match nao for encontrado por id"() {
        when:
        MatchAnonimo match = matchDAO.buscarPorId(999L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> statement
        1 * statement.setLong(1, 999L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> false
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        match == null
    }

    void "deve lancar IllegalStateException ao falhar busca por id"() {
        when:
        matchDAO.buscarPorId(1L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("SQL Error") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }

    void "deve listar matches aplicando filtros dinamicos e ordenacao"() {
        given:
        Timestamp ts = Timestamp.from(Instant.parse("2026-11-12T17:00:00Z"))

        when:
        List<MatchAnonimo> matches = matchDAO.listarMatches(10L, 20L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql ->
            sql.contains("AND m.candidato_id = ?") &&
            sql.contains("AND m.empresa_id = ?") &&
            sql.contains("ORDER BY m.data_match DESC, m.id DESC")
        }) >> statement
        1 * statement.setObject(1, 10L)
        1 * statement.setObject(2, 20L)
        1 * statement.executeQuery() >> resultado
        2 * resultado.next() >>> [true, false]

        1 * resultado.getLong("match_id") >> 100L
        1 * resultado.getLong("match_empresa_id") >> 20L
        1 * resultado.getLong("match_candidato_id") >> 10L
        1 * resultado.getLong("match_recrutador_id") >> 30L
        1 * resultado.wasNull() >> false
        1 * resultado.getObject("match_data_match", OffsetDateTime) >> null
        1 * resultado.getTimestamp("match_data_match") >> ts

        1 * resultado.getString("empresa_pais") >> "Brasil"
        1 * resultado.getString("empresa_estado") >> "SP"
        1 * resultado.getString("empresa_descricao") >> "Descricao Empresa"

        1 * resultado.getString("candidato_estado") >> "SP"
        1 * resultado.getString("candidato_descricao") >> "Descricao Candidato"

        1 * resultado.getString("recrutador_area") >> "Recrutamento Tech"

        1 * candidatoCompetenciaRepository.listarCompetenciasDoCandidato(10L) >> []
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        matches.size() == 1
        matches[0].id == 100L
        matches[0].empresa.id == 20L
        matches[0].candidato.id == 10L
        matches[0].recrutador.id == 30L
    }

    void "deve lancar IllegalStateException ao falhar listagem de matches"() {
        when:
        matchDAO.listarMatches(null, null)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_) >> { throw new SQLException("Timeout") }
        1 * conexao.close()

        thrown(IllegalStateException)
    }
}
