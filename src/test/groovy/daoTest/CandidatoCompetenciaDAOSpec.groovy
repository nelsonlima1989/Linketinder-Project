package daoTest

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Skill
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CandidatoCompetenciaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    PreparedStatement statement
    CandidatoCompetenciaDAO candidatoCompetenciaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)

        candidatoCompetenciaDAO =
                new CandidatoCompetenciaDAO(conexaoProvider)
    }

    void "deve associar competencia ao candidato"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 10L

        when:
        candidatoCompetenciaDAO.adicionarCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_ as String) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.setLong(2, competenciaId)
        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _
    }

    void "deve listar competencias associadas ao candidato"() {

        given:
        Long candidatoId = 1L
        ResultSet resultado = Mock(ResultSet)

        when:
        List<Skill> competencias =
                candidatoCompetenciaDAO.listarCompetenciasDoCandidato(candidatoId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_ as String) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.executeQuery() >> resultado

        3 * resultado.next() >>> [true, true, false]

        2 * resultado.getLong("id") >>> [10L, 20L]
        2 * resultado.getString("nome") >>> ["Java", "Groovy"]

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencias.size() == 2
        competencias*.id == [10L, 20L]
        competencias*.nome == ["Java", "Groovy"]
    }


    void "deve retornar lista vazia quando candidato nao possui competencias"() {

        given:
        Long candidatoId = 1L
        ResultSet resultado = Mock(ResultSet)

        when:
        List<Skill> competencias =
                candidatoCompetenciaDAO.listarCompetenciasDoCandidato(candidatoId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_ as String) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencias.isEmpty()
    }

    void "deve remover competencia associada ao candidato"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 10L

        when:
        candidatoCompetenciaDAO.removerCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("DELETE FROM candidato_competencias")
        }) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _
    }


    void "deve informar quando competencia nao estiver associada ao candidato"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 999L

        when:
        candidatoCompetenciaDAO.removerCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("DELETE FROM candidato_competencias")
        }) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _
    }


    void "deve propagar erro ao associar competencia e fechar recursos"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 10L

        when:
        candidatoCompetenciaDAO.adicionarCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_ as String) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> {
            throw new SQLException("Falha simulada no INSERT")
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)
        excecao.message == "Não foi possível associar a competência ao candidato."
        excecao.cause instanceof SQLException
        excecao.cause.message == "Falha simulada no INSERT"
    }


    void "deve propagar erro ao listar competencias e fechar recursos"() {

        given:
        Long candidatoId = 1L

        when:
        candidatoCompetenciaDAO.listarCompetenciasDoCandidato(candidatoId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement(_ as String) >> statement

        1 * statement.setLong(1, candidatoId)

        1 * statement.executeQuery() >> {
            throw new SQLException("Falha simulada no SELECT")
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar as competências do candidato."
        excecao.cause instanceof SQLException
        excecao.cause.message == "Falha simulada no SELECT"
    }


    void "deve propagar erro ao remover competencia e fechar recursos"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 10L

        when:
        candidatoCompetenciaDAO.removerCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("DELETE FROM candidato_competencias")
        }) >> statement

        1 * statement.setLong(1, candidatoId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> {
            throw new SQLException("Falha simulada no DELETE")
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível remover a competência do candidato."
        excecao.cause instanceof SQLException
        excecao.cause.message == "Falha simulada no DELETE"
    }


    void "deve propagar erro quando nao conseguir obter conexao"() {

        given:
        Long candidatoId = 1L
        Long competenciaId = 10L

        when:
        candidatoCompetenciaDAO.adicionarCompetencia(
                candidatoId,
                competenciaId
        )

        then:
        1 * conexaoProvider.obterConexao() >> {
            throw new SQLException("Falha simulada na conexao")
        }

        0 * conexao._
        0 * statement._
        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível associar a competência ao candidato."
        excecao.cause instanceof SQLException
        excecao.cause.message == "Falha simulada na conexao"
    }


}