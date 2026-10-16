package daoTest

import com.linketinder.database.ConexaoProvider
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.model.Skill
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaCompetenciaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    PreparedStatement statement
    ResultSet resultado

    VagaCompetenciaDAO vagaCompetenciaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        vagaCompetenciaDAO = new VagaCompetenciaDAO(conexaoProvider)
    }


    void "deve associar competencia a vaga com sucesso"() {

        given:
        Long vagaId = 10L
        Long competenciaId = 5L

        when:
        vagaCompetenciaDAO.adicionarCompetencia(vagaId, competenciaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO vaga_competencias") &&
                        sql.contains("VALUES (?, ?)")
        }) >> statement

        1 * statement.setLong(1, vagaId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _
    }


    void "deve propagar erro SQL ao associar competencia a vaga"() {

        given:
        Long vagaId = 10L
        Long competenciaId = 5L
        SQLException erroSql = new SQLException("Falha simulada no INSERT")

        when:
        vagaCompetenciaDAO.adicionarCompetencia(vagaId, competenciaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO vaga_competencias") &&
                        sql.contains("VALUES (?, ?)")
        }) >> statement

        1 * statement.setLong(1, vagaId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível associar a competência à vaga."
        excecao.cause.is(erroSql)
    }


    void "deve listar competencias associadas a vaga"() {

        given:
        Long vagaId = 10L

        when:
        List<Skill> competencias = vagaCompetenciaDAO.listarCompetenciasDaVaga(vagaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM competencias AS c") &&
                        sql.contains("INNER JOIN vaga_competencias AS vc") &&
                        sql.contains("WHERE vc.vaga_id = ?")
        }) >> statement

        1 * statement.setLong(1, vagaId)

        1 * statement.executeQuery() >> resultado

        3 * resultado.next() >>> [true, true, false]

        2 * resultado.getLong("id") >>> [5L, 8L]
        2 * resultado.getString("nome") >>> ["Java", "Groovy"]

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencias.size() == 2

        competencias[0].id == 5L
        competencias[0].nome == "Java"

        competencias[1].id == 8L
        competencias[1].nome == "Groovy"
    }



    void "deve retornar lista vazia quando vaga nao possuir competencias"() {

        given:
        Long vagaId = 999L

        when:
        List<Skill> competencias = vagaCompetenciaDAO.listarCompetenciasDaVaga(vagaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM competencias AS c") &&
                        sql.contains("INNER JOIN vaga_competencias AS vc") &&
                        sql.contains("WHERE vc.vaga_id = ?")
        }) >> statement

        1 * statement.setLong(1, vagaId)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_)
        0 * resultado.getString(_)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencias.isEmpty()
    }


    void "deve propagar erro SQL ao listar competencias da vaga"() {

        given:
        Long vagaId = 10L
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        vagaCompetenciaDAO.listarCompetenciasDaVaga(vagaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM competencias AS c") &&
                        sql.contains("INNER JOIN vaga_competencias AS vc") &&
                        sql.contains("WHERE vc.vaga_id = ?")
        }) >> statement

        1 * statement.setLong(1, vagaId)

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar as competências da vaga."
        excecao.cause.is(erroSql)
    }


    void "deve remover competencia da vaga com sucesso"() {

        given:
        Long vagaId = 10L
        Long competenciaId = 5L

        when:
        vagaCompetenciaDAO.removerCompetencia(vagaId, competenciaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM vaga_competencias") &&
                        sql.contains("WHERE vaga_id = ?") &&
                        sql.contains("AND competencia_id = ?")
        }) >> statement

        1 * statement.setLong(1, vagaId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _
    }



    void "deve propagar erro SQL ao remover competencia da vaga"() {

        given:
        Long vagaId = 10L
        Long competenciaId = 5L
        SQLException erroSql = new SQLException("Falha simulada no DELETE")

        when:
        vagaCompetenciaDAO.removerCompetencia(vagaId, competenciaId)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM vaga_competencias") &&
                        sql.contains("WHERE vaga_id = ?") &&
                        sql.contains("AND competencia_id = ?")
        }) >> statement

        1 * statement.setLong(1, vagaId)
        1 * statement.setLong(2, competenciaId)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível remover a competência da vaga."
        excecao.cause.is(erroSql)
    }


}