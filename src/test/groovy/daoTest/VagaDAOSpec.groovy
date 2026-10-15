package daoTest

import com.linketinder.dao.ConexaoProvider
import com.linketinder.dao.VagaDAO
import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VagaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    EmpresaRepository empresaRepository
    Connection conexao
    PreparedStatement statement
    ResultSet resultado
    VagaDAO vagaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        empresaRepository = Mock(EmpresaRepository)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        vagaDAO = new VagaDAO(conexaoProvider, empresaRepository)
    }

    void "deve inserir vaga e atribuir o id gerado"() {

        given:
        Empresa empresa = new Empresa(id: 1L)

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Full Stack",
                descricaoVaga: "Desenvolvimento de aplicações web",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: empresa
        )

        when:
        vagaDAO.inserir(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO vagas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setLong(1, 1L)
        1 * statement.setString(2, "Desenvolvedor Full Stack")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setString(
                5,
                "Desenvolvimento de aplicações web"
        )

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 10L

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vaga.id == 10L
    }


    void "nao deve atribuir id quando insercao nao retornar resultado"() {

        given:
        Empresa empresa = new Empresa(id: 1L)

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento de APIs",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: empresa
        )

        when:
        vagaDAO.inserir(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO vagas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setLong(1, 1L)
        1 * statement.setString(2, "Desenvolvedor Backend")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setString(5, "Desenvolvimento de APIs")

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false
        0 * resultado.getLong(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vaga.id == null
    }


    void "deve propagar erro SQL ao inserir vaga e fechar recursos"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no INSERT")

        Empresa empresa = new Empresa(id: 1L)

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento de APIs",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: empresa
        )

        when:
        vagaDAO.inserir(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO vagas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setLong(1, 1L)
        1 * statement.setString(2, "Desenvolvedor Backend")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setString(5, "Desenvolvimento de APIs")

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível inserir a vaga."
        excecao.cause.is(erroSql)

        vaga.id == null
    }



    void "deve listar vagas cadastradas"() {

        given:
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId)

        when:
        List<Vaga> vagas = vagaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        2 * resultado.next() >>> [true, false]

        1 * resultado.getLong("empresa_id") >> empresaId

        1 * empresaRepository.buscarPorIds([1L]) >> [(1L): empresa]

        1 * resultado.getLong("id") >> 10L
        1 * resultado.getString("nome") >> "Desenvolvedor Full Stack"
        1 * resultado.getString("descricao") >> "Desenvolvimento de aplicações web"
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("cidade") >> "Rio de Janeiro"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vagas.size() == 1

        Vaga vaga = vagas[0]

        vaga.id == 10L
        vaga.titulo == "Desenvolvedor Full Stack"
        vaga.descricaoVaga == "Desenvolvimento de aplicações web"
        vaga.estado == "RJ"
        vaga.cidade == "Rio de Janeiro"
        vaga.empresa.is(empresa)
    }


    void "deve retornar lista vazia quando nao houver vagas"() {

        when:
        List<Vaga> vagas = vagaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * empresaRepository._

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vagas != null
        vagas.isEmpty()
    }




    void "deve propagar erro SQL ao listar vagas e fechar recursos"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        vagaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar as vagas."
        excecao.cause.is(erroSql)
    }


    void "deve buscar vaga pelo id"() {

        given:
        Long id = 10L
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId)

        when:
        Vaga vaga = vagaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true

        1 * resultado.getLong("empresa_id") >> empresaId

        1 * empresaRepository.buscarPorId(empresaId) >> empresa

        1 * resultado.getLong("id") >> id
        1 * resultado.getString("nome") >> "Desenvolvedor Full Stack"
        1 * resultado.getString("descricao") >>
                "Desenvolvimento de aplicações web"
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("cidade") >> "Rio de Janeiro"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vaga != null
        vaga.id == id
        vaga.titulo == "Desenvolvedor Full Stack"
        vaga.descricaoVaga == "Desenvolvimento de aplicações web"
        vaga.estado == "RJ"
        vaga.cidade == "Rio de Janeiro"
        vaga.empresa.is(empresa)
    }


    void "deve retornar null quando vaga nao for encontrada"() {

        given:
        Long id = 999L

        when:
        Vaga vaga = vagaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)

        0 * empresaRepository._

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vaga == null
    }


    void "deve propagar erro SQL ao buscar vaga por id"() {

        given:
        Long id = 10L
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        vagaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível buscar a vaga por ID."
        excecao.cause.is(erroSql)
    }


    void "deve atualizar vaga com sucesso"() {

        given:
        Vaga vaga = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Full Stack",
                descricaoVaga: "Desenvolvimento de aplicações web",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: new Empresa(id: 1L)
        )

        when:
        boolean atualizado = vagaDAO.atualizar(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Desenvolvedor Full Stack")
        1 * statement.setString(2, "Desenvolvimento de aplicações web")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setLong(5, 10L)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        atualizado
    }


    void "deve retornar false ao atualizar vaga inexistente"() {

        given:
        Vaga vaga = new Vaga(
                id: 999L,
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento de APIs",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: new Empresa(id: 1L)
        )

        when:
        boolean atualizado = vagaDAO.atualizar(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Desenvolvedor Backend")
        1 * statement.setString(2, "Desenvolvimento de APIs")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setLong(5, 999L)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !atualizado
    }


    void "deve propagar erro SQL ao atualizar vaga"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no UPDATE")

        Vaga vaga = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento de APIs",
                estado: "RJ",
                cidade: "Rio de Janeiro",
                empresa: new Empresa(id: 1L)
        )

        when:
        vagaDAO.atualizar(vaga)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Desenvolvedor Backend")
        1 * statement.setString(2, "Desenvolvimento de APIs")
        1 * statement.setString(3, "RJ")
        1 * statement.setString(4, "Rio de Janeiro")
        1 * statement.setLong(5, 10L)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível atualizar a vaga."
        excecao.cause.is(erroSql)
    }


    void "deve excluir vaga com sucesso"() {

        given:
        Long id = 10L

        when:
        boolean excluida = vagaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 1

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        excluida
    }


    void "deve retornar false ao excluir vaga inexistente"() {

        given:
        Long id = 999L

        when:
        boolean excluida = vagaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 0

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !excluida
    }


    void "deve propagar erro SQL ao excluir vaga"() {

        given:
        Long id = 10L
        SQLException erroSql = new SQLException("Falha simulada no DELETE")

        when:
        vagaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM vagas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        0 * empresaRepository._

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível excluir a vaga."
        excecao.cause.is(erroSql)
    }


    void "deve buscar empresas em lote ao listar vagas"() {

        given:
        Empresa empresa = new Empresa(id: 5L)

        when:
        List<Vaga> vagas = vagaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql -> sql.contains("FROM vagas")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        3 * resultado.next() >>> [true, true, false]

        2 * resultado.getLong("empresa_id") >> 5L
        2 * resultado.getLong("id") >>> [10L, 11L]

        2 * resultado.getString("nome") >>> ["Backend", "Frontend"]
        2 * resultado.getString("descricao") >> "Desenvolvimento"
        2 * resultado.getString("estado") >> "RJ"
        2 * resultado.getString("cidade") >> "Rio de Janeiro"

        1 * empresaRepository.buscarPorIds([5L]) >> [(5L): empresa]

        0 * empresaRepository.buscarPorId(_)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        vagas.size() == 2
        vagas[0].id == 10L
        vagas[1].id == 11L
        vagas.every { Vaga vaga -> vaga.empresa.is(empresa) }
    }


}