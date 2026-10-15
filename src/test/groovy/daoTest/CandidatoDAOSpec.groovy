package daoTest

import com.linketinder.dao.CandidatoDAO
import com.linketinder.dao.ConexaoProvider
import com.linketinder.model.Candidato
import com.linketinder.model.Informacoes
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class CandidatoDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    PreparedStatement statement
    ResultSet resultado
    CandidatoDAO candidatoDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        candidatoDAO = new CandidatoDAO(conexaoProvider)
    }

    void "deve inserir candidato e atribuir o id gerado"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Nelson",
                sobrenome: "Lima",
                email: "teste@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Desenvolvedor",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        candidatoDAO.inserir(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("INSERT INTO candidatos")
                    && sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setString(1, "Nelson")
        1 * statement.setString(2, "Lima")
        1 * statement.setDate(
                3,
                java.sql.Date.valueOf(LocalDate.of(2000, 1, 1))
        )
        1 * statement.setString(4, "teste@exemplo.com")
        1 * statement.setString(5, "12345678900")
        1 * statement.setString(6, "Brasil")
        1 * statement.setString(7, "RJ")
        1 * statement.setString(8, "20000000")
        1 * statement.setString(9, "Desenvolvedor")
        1 * statement.setString(10, "senhaTeste")

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 1L

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidato.id == 1L
    }

    void "nao deve atribuir id quando insercao nao retornar resultado"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Teste",
                sobrenome: "Candidato",
                email: "teste@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Candidato de teste",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        candidatoDAO.inserir(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("INSERT INTO candidatos")
        }) >> statement

        9 * statement.setString(_, _)
        1 * statement.setDate(3, _ as java.sql.Date)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false
        0 * resultado.getLong(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidato.id == null
    }



    void "deve propagar erro ao inserir candidato e fechar recursos"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Teste",
                sobrenome: "Candidato",
                email: "teste@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Candidato de teste",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        candidatoDAO.inserir(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("INSERT INTO candidatos")
        }) >> statement

        9 * statement.setString(_, _)
        1 * statement.setDate(3, _ as java.sql.Date)

        1 * statement.executeQuery() >> {
            throw new SQLException("Falha simulada no INSERT")
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível inserir o candidato."
        excecao.cause instanceof SQLException
        excecao.cause.message == "Falha simulada no INSERT"

        candidato.id == null
    }


    void "deve listar candidatos cadastrados"() {

        given:
        LocalDate dataNascimento = LocalDate.of(2000, 1, 1)

        when:
        List<Candidato> candidatos = candidatoDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        2 * resultado.next() >>> [true, false]

        1 * resultado.getLong("id") >> 1L
        1 * resultado.getString("nome") >> "Nelson"
        1 * resultado.getString("sobrenome") >> "Lima"
        1 * resultado.getString("email") >> "teste@exemplo.com"
        1 * resultado.getString("pais") >> "Brasil"
        1 * resultado.getString("cep") >> "20000000"
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("cpf") >> "12345678900"
        1 * resultado.getString("descricao_pessoal") >> "Desenvolvedor"
        1 * resultado.getString("senha") >> "senhaTeste"

        1 * resultado.getDate("data_nascimento") >>
                java.sql.Date.valueOf(dataNascimento)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidatos.size() == 1

        Candidato candidato = candidatos[0]

        candidato.id == 1L
        candidato.cpf == "12345678900"
        candidato.estado == "RJ"
        candidato.dataNascimento == dataNascimento
        candidato.descricaoPessoal == "Desenvolvedor"

        candidato.informacoes.nome == "Nelson"
        candidato.informacoes.sobrenome == "Lima"
        candidato.informacoes.email == "teste@exemplo.com"
        candidato.informacoes.pais == "Brasil"
        candidato.informacoes.cep == "20000000"
    }


    void "deve retornar lista vazia quando nao houver candidatos"() {

        when:
        List<Candidato> candidatos = candidatoDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)
        0 * resultado.getDate(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidatos != null
        candidatos.isEmpty()
    }

    void "deve propagar erro ao listar candidatos e fechar recursos"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        candidatoDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar os candidatos."
        excecao.cause.is(erroSql)
    }



    void "deve buscar candidato pelo id"() {

        given:
        Long id = 1L
        LocalDate dataNascimento = LocalDate.of(2000, 1, 1)

        when:
        Candidato candidato = candidatoDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)
        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true

        1 * resultado.getString("nome") >> "Nelson"
        1 * resultado.getString("sobrenome") >> "Lima"
        1 * resultado.getString("email") >> "teste@exemplo.com"
        1 * resultado.getString("pais") >> "Brasil"
        1 * resultado.getString("cep") >> "20000000"

        1 * resultado.getLong("id") >> id
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("cpf") >> "12345678900"
        1 * resultado.getDate("data_nascimento") >>
                java.sql.Date.valueOf(dataNascimento)
        1 * resultado.getString("descricao_pessoal") >> "Desenvolvedor"
        1 * resultado.getString("senha") >> "senhaTeste"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidato != null
        candidato.id == id
        candidato.cpf == "12345678900"
        candidato.estado == "RJ"
        candidato.dataNascimento == dataNascimento
        candidato.descricaoPessoal == "Desenvolvedor"

        candidato.informacoes.nome == "Nelson"
        candidato.informacoes.sobrenome == "Lima"
        candidato.informacoes.email == "teste@exemplo.com"
        candidato.informacoes.pais == "Brasil"
        candidato.informacoes.cep == "20000000"
    }

    void "deve retornar null quando candidato nao for encontrado"() {

        given:
        Long id = 999L

        when:
        Candidato candidato = candidatoDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)
        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)
        0 * resultado.getDate(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        candidato == null
    }



    void "deve propagar erro SQL ao buscar candidato por id"() {

        given:
        Long id = 1L
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        candidatoDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM candidatos") &&
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

        excecao.message == "Não foi possível buscar o candidato por ID."
        excecao.cause.is(erroSql)
    }


    void "deve atualizar candidato com sucesso"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Nelson",
                sobrenome: "Lima",
                email: "nelson@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Desenvolvedor Full Stack",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        boolean atualizado = candidatoDAO.atualizar(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Nelson")
        1 * statement.setString(2, "Lima")
        1 * statement.setDate(
                3,
                java.sql.Date.valueOf(LocalDate.of(2000, 1, 1))
        )
        1 * statement.setString(4, "nelson@exemplo.com")
        1 * statement.setString(5, "12345678900")
        1 * statement.setString(6, "Brasil")
        1 * statement.setString(7, "RJ")
        1 * statement.setString(8, "20000000")
        1 * statement.setString(9, "Desenvolvedor Full Stack")
        1 * statement.setString(10, "senhaTeste")
        1 * statement.setLong(11, 1L)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        atualizado
    }



    void "deve tratar atualizacao de candidato inexistente"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Teste",
                sobrenome: "Candidato",
                email: "teste@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                id: 999L,
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Candidato de teste",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        boolean atualizado = candidatoDAO.atualizar(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        9 * statement.setString(_, _)
        1 * statement.setDate(
                3,
                java.sql.Date.valueOf(candidato.dataNascimento)
        )
        1 * statement.setLong(11, 999L)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !atualizado
    }


    void "deve propagar erro SQL ao atualizar candidato"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no UPDATE")

        Informacoes informacoes = new Informacoes(
                nome: "Teste",
                sobrenome: "Candidato",
                email: "teste@exemplo.com",
                pais: "Brasil",
                cep: "20000000"
        )

        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "12345678900",
                dataNascimento: LocalDate.of(2000, 1, 1),
                estado: "RJ",
                descricaoPessoal: "Candidato de teste",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        candidatoDAO.atualizar(candidato)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        9 * statement.setString(_, _)
        1 * statement.setDate(3, _ as java.sql.Date)
        1 * statement.setLong(11, 1L)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível atualizar o candidato."
        excecao.cause.is(erroSql)
    }

    void "deve excluir candidato com sucesso"() {

        given:
        Long id = 1L

        when:
        boolean excluido = candidatoDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        excluido
    }



    void "deve retornar false ao excluir candidato inexistente"() {

        given:
        Long id = 999L

        when:
        boolean excluido = candidatoDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM candidatos") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !excluido
    }


    void "deve propagar erro SQL ao excluir candidato"() {

        given:
        Long id = 1L
        SQLException erroSql = new SQLException("Falha simulada no DELETE")

        when:
        candidatoDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM candidatos") &&
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

        excecao.message == "Não foi possível excluir o candidato."
        excecao.cause.is(erroSql)
    }


    void "deve propagar erro ao obter conexao para listar candidatos"() {

        given:
        SQLException erroSql =
                new SQLException("Falha simulada ao conectar")

        when:
        candidatoDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> {
            throw erroSql
        }

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar os candidatos."
        excecao.cause.is(erroSql)
    }



}