package daoTest

import com.linketinder.dao.CompetenciaDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Skill
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CompetenciaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    PreparedStatement statement
    ResultSet resultado

    CompetenciaDAO competenciaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        competenciaDAO = new CompetenciaDAO(conexaoProvider)
    }


    void "deve inserir competencia com sucesso"() {

        given:
        Skill skill = new Skill(nome: "Java")

        when:
        competenciaDAO.inserir(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO competencias") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setString(1, "Java")

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 10L

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        skill.id == 10L
    }


    void "nao deve atribuir ID quando insercao nao retornar resultado"() {

        given:
        Skill skill = new Skill(nome: "Groovy")

        when:
        competenciaDAO.inserir(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO competencias") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setString(1, "Groovy")

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong("id")

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        skill.id == null
    }


    void "deve propagar erro SQL ao inserir competencia"() {

        given:
        Skill skill = new Skill(nome: "Java")
        SQLException erroSql = new SQLException("Falha simulada no INSERT")

        when:
        competenciaDAO.inserir(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO competencias") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setString(1, "Java")

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível inserir a competência."
        excecao.cause.is(erroSql)
    }


    void "deve listar competencias cadastradas"() {

        given:
        List<Skill> competencias

        when:
        competencias = competenciaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("ORDER BY id")
        }) >> statement

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


    void "deve retornar lista vazia quando nao houver competencias"() {

        when:
        List<Skill> competencias = competenciaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("ORDER BY id")
        }) >> statement

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


    void "deve propagar erro SQL ao listar competencias"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        competenciaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar as competências."
        excecao.cause.is(erroSql)
    }


    void "deve buscar competencia por ID com sucesso"() {

        given:
        Long id = 5L

        when:
        Skill competencia = competenciaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true

        1 * resultado.getLong("id") >> 5L
        1 * resultado.getString("nome") >> "Java"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencia != null
        competencia.id == 5L
        competencia.nome == "Java"
    }



    void "deve retornar null ao buscar competencia inexistente"() {

        given:
        Long id = 999L

        when:
        Skill competencia = competenciaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_)
        0 * resultado.getString(_)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        competencia == null
    }



    void "deve propagar erro SQL ao buscar competencia por ID"() {

        given:
        Long id = 5L
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        competenciaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("SELECT") &&
                        sql.contains("FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível buscar a competência por ID."
        excecao.cause.is(erroSql)
    }


    void "deve atualizar competencia com sucesso"() {

        given:
        Skill skill = new Skill(
                id: 5L,
                nome: "Java Avançado"
        )

        when:
        boolean atualizada = competenciaDAO.atualizar(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE competencias") &&
                        sql.contains("SET nome = ?") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Java Avançado")
        1 * statement.setLong(2, 5L)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        atualizada
    }


    void "deve retornar false ao atualizar competencia inexistente"() {

        given:
        Skill skill = new Skill(
                id: 999L,
                nome: "Java Avançado"
        )

        when:
        boolean atualizada = competenciaDAO.atualizar(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE competencias") &&
                        sql.contains("SET nome = ?") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Java Avançado")
        1 * statement.setLong(2, 999L)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !atualizada
    }


    void "deve propagar erro SQL ao atualizar competencia"() {

        given:
        Skill skill = new Skill(
                id: 5L,
                nome: "Java Avançado"
        )

        SQLException erroSql = new SQLException("Falha simulada no UPDATE")

        when:
        competenciaDAO.atualizar(skill)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE competencias") &&
                        sql.contains("SET nome = ?") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Java Avançado")
        1 * statement.setLong(2, 5L)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível atualizar a competência."
        excecao.cause.is(erroSql)
    }


    void "deve excluir competencia com sucesso"() {

        given:
        Long id = 5L

        when:
        boolean excluida = competenciaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        excluida
    }

    void "deve retornar false ao excluir competencia inexistente"() {

        given:
        Long id = 999L

        when:
        boolean excluida = competenciaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !excluida
    }


    void "deve propagar erro SQL ao excluir competencia"() {

        given:
        Long id = 5L
        SQLException erroSql = new SQLException("Falha simulada no DELETE")

        when:
        competenciaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM competencias") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível excluir a competência."
        excecao.cause.is(erroSql)
    }


}