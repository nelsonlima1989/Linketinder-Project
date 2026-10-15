package daoTest

import com.linketinder.dao.ConexaoProvider
import com.linketinder.dao.EmpresaDAO
import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import com.linketinder.model.Vaga
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EmpresaDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    Connection conexao
    PreparedStatement statement
    ResultSet resultado
    EmpresaDAO empresaDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        empresaDAO = new EmpresaDAO(conexaoProvider)
    }

    void "deve inserir empresa e atribuir o id gerado"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        empresaDAO.inserir(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO empresas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setString(1, "Empresa Teste")
        1 * statement.setString(2, "12345678000190")
        1 * statement.setString(3, "contato@empresa.com")
        1 * statement.setString(4, "Empresa de tecnologia")
        1 * statement.setString(5, "Brasil")
        1 * statement.setString(6, "RJ")
        1 * statement.setString(7, "20000000")
        1 * statement.setString(8, "senhaTeste")

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 1L

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresa.id == 1L
    }


    void "nao deve atribuir id quando insercao nao retornar resultado"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        empresaDAO.inserir(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO empresas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        8 * statement.setString(_, _)

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false
        0 * resultado.getLong(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresa.id == null
    }


    void "deve propagar erro SQL ao inserir empresa e fechar recursos"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no INSERT")

        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        empresaDAO.inserir(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO empresas") &&
                        sql.contains("RETURNING id")
        }) >> statement

        8 * statement.setString(_, _)

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível inserir a empresa."
        excecao.cause.is(erroSql)

        empresa.id == null
    }



    void "deve listar empresas cadastradas"() {

        when:
        List<Empresa> empresas = empresaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        2 * resultado.next() >>> [true, false]

        1 * resultado.getLong("id") >> 1L
        1 * resultado.getString("nome") >> "Empresa Teste"
        1 * resultado.getString("email") >> "contato@empresa.com"
        1 * resultado.getString("cep") >> "20000000"
        1 * resultado.getString("pais") >> "Brasil"
        1 * resultado.getString("cnpj") >> "12345678000190"
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("descricao") >> "Empresa de tecnologia"
        1 * resultado.getString("senha") >> "senhaTeste"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresas.size() == 1

        Empresa empresa = empresas[0]

        empresa.id == 1L
        empresa.cnpj == "12345678000190"
        empresa.pais == "Brasil"
        empresa.estado == "RJ"
        empresa.descricaoEmpresarial == "Empresa de tecnologia"

        empresa.informacoes.nome == "Empresa Teste"
        empresa.informacoes.email == "contato@empresa.com"
        empresa.informacoes.cep == "20000000"
        empresa.informacoes.pais == "Brasil"
    }


    void "deve retornar lista vazia quando nao houver empresas"() {

        when:
        List<Empresa> empresas = empresaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresas != null
        empresas.isEmpty()
    }





    void "deve propagar erro SQL ao listar empresas e fechar recursos"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        empresaDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("ORDER BY id")
        }) >> statement

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível listar as empresas."
        excecao.cause.is(erroSql)
    }


    void "deve buscar empresa pelo id"() {

        given:
        Long id = 1L

        when:
        Empresa empresa = empresaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)
        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> true

        1 * resultado.getString("nome") >> "Empresa Teste"
        1 * resultado.getString("email") >> "contato@empresa.com"
        1 * resultado.getString("cep") >> "20000000"
        1 * resultado.getString("pais") >> "Brasil"

        1 * resultado.getLong("id") >> id
        1 * resultado.getString("cnpj") >> "12345678000190"
        1 * resultado.getString("estado") >> "RJ"
        1 * resultado.getString("descricao") >> "Empresa de tecnologia"
        1 * resultado.getString("senha") >> "senhaTeste"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresa != null
        empresa.id == id
        empresa.cnpj == "12345678000190"
        empresa.pais == "Brasil"
        empresa.estado == "RJ"
        empresa.descricaoEmpresarial == "Empresa de tecnologia"

        empresa.informacoes.nome == "Empresa Teste"
        empresa.informacoes.email == "contato@empresa.com"
        empresa.informacoes.cep == "20000000"
        empresa.informacoes.pais == "Brasil"
    }


    void "deve retornar null quando empresa nao for encontrada"() {

        given:
        Long id = 999L

        when:
        Empresa empresa = empresaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)
        1 * statement.executeQuery() >> resultado

        1 * resultado.next() >> false

        0 * resultado.getLong(_ as String)
        0 * resultado.getString(_ as String)

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresa == null
    }


    void "deve propagar erro SQL ao buscar empresa por id"() {

        given:
        Long id = 1L
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        empresaDAO.buscarPorId(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
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

        excecao.message == "Não foi possível buscar a empresa por ID."
        excecao.cause.is(erroSql)
    }


    void "deve atualizar empresa com sucesso"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        boolean atualizado = empresaDAO.atualizar(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Empresa Teste")
        1 * statement.setString(2, "12345678000190")
        1 * statement.setString(3, "contato@empresa.com")
        1 * statement.setString(4, "Empresa de tecnologia")
        1 * statement.setString(5, "Brasil")
        1 * statement.setString(6, "RJ")
        1 * statement.setString(7, "20000000")
        1 * statement.setString(8, "senhaTeste")
        1 * statement.setLong(9, 1L)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        atualizado
    }


    void "deve retornar false ao atualizar empresa inexistente"() {

        given:
        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                id: 999L,
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        boolean atualizado = empresaDAO.atualizar(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Empresa Teste")
        1 * statement.setString(2, "12345678000190")
        1 * statement.setString(3, "contato@empresa.com")
        1 * statement.setString(4, "Empresa de tecnologia")
        1 * statement.setString(5, "Brasil")
        1 * statement.setString(6, "RJ")
        1 * statement.setString(7, "20000000")
        1 * statement.setString(8, "senhaTeste")
        1 * statement.setLong(9, 999L)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !atualizado
    }


    void "deve propagar erro SQL ao atualizar empresa"() {

        given:
        SQLException erroSql = new SQLException("Falha simulada no UPDATE")

        Informacoes informacoes = new Informacoes(
                nome: "Empresa Teste",
                email: "contato@empresa.com",
                cep: "20000000",
                pais: "Brasil"
        )

        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "12345678000190",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senhaTeste",
                informacoes: informacoes
        )

        when:
        empresaDAO.atualizar(empresa)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setString(1, "Empresa Teste")
        1 * statement.setString(2, "12345678000190")
        1 * statement.setString(3, "contato@empresa.com")
        1 * statement.setString(4, "Empresa de tecnologia")
        1 * statement.setString(5, "Brasil")
        1 * statement.setString(6, "RJ")
        1 * statement.setString(7, "20000000")
        1 * statement.setString(8, "senhaTeste")
        1 * statement.setLong(9, 1L)

        1 * statement.executeUpdate() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível atualizar a empresa."
        excecao.cause.is(erroSql)
    }


    void "deve excluir empresa com sucesso"() {

        given:
        Long id = 1L

        when:
        boolean excluida = empresaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()

        0 * _

        excluida
    }


    void "deve retornar false ao excluir empresa inexistente"() {

        given:
        Long id = 999L

        when:
        boolean excluida = empresaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM empresas") &&
                        sql.contains("WHERE id = ?")
        }) >> statement

        1 * statement.setLong(1, id)

        1 * statement.executeUpdate() >> 0

        1 * statement.close()
        1 * conexao.close()

        0 * _

        !excluida
    }




    void "deve propagar erro SQL ao excluir empresa"() {

        given:
        Long id = 1L
        SQLException erroSql = new SQLException("Falha simulada no DELETE")

        when:
        empresaDAO.excluir(id)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("DELETE FROM empresas") &&
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

        excecao.message == "Não foi possível excluir a empresa."
        excecao.cause.is(erroSql)
    }



    void "deve buscar empresas por IDs em uma unica consulta"() {

        given:
        List<Long> ids = [5L, 8L]

        when:
        Map<Long, Empresa> empresas = empresaDAO.buscarPorIds(ids)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("WHERE id IN (?, ?)")
        }) >> statement

        1 * statement.setLong(1, 5L)
        1 * statement.setLong(2, 8L)

        1 * statement.executeQuery() >> resultado

        3 * resultado.next() >>> [true, true, false]

        2 * resultado.getLong("id") >>> [5L, 8L]

        2 * resultado.getString("nome") >>> ["Empresa A", "Empresa B"]
        2 * resultado.getString("email") >>> ["a@email.com", "b@email.com"]
        2 * resultado.getString("cnpj") >>> ["111", "222"]
        2 * resultado.getString("descricao") >> "Empresa de tecnologia"
        2 * resultado.getString("pais") >> "Brasil"
        2 * resultado.getString("estado") >> "RJ"
        2 * resultado.getString("cep") >> "20000000"
        2 * resultado.getString("senha") >> "senha123"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        0 * _

        empresas.size() == 2
        empresas[5L].id == 5L
        empresas[8L].id == 8L
        empresas[5L].informacoes.nome == "Empresa A"
        empresas[8L].informacoes.nome == "Empresa B"
    }



    void "deve retornar mapa vazio sem consultar banco quando lista de IDs estiver vazia"() {

        given:
        List<Long> ids = []

        when:
        Map<Long, Empresa> empresas = empresaDAO.buscarPorIds(ids)

        then:
        0 * conexaoProvider._
        0 * conexao._
        0 * statement._
        0 * resultado._

        empresas.isEmpty()
    }


    void "deve propagar erro SQL ao buscar empresas por IDs"() {

        given:
        List<Long> ids = [5L, 8L]
        SQLException erroSql = new SQLException("Falha simulada no SELECT")

        when:
        empresaDAO.buscarPorIds(ids)

        then:
        1 * conexaoProvider.obterConexao() >> conexao

        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("FROM empresas") &&
                        sql.contains("WHERE id IN (?, ?)")
        }) >> statement

        1 * statement.setLong(1, 5L)
        1 * statement.setLong(2, 8L)

        1 * statement.executeQuery() >> {
            throw erroSql
        }

        1 * statement.close()
        1 * conexao.close()

        0 * _

        IllegalStateException excecao = thrown(IllegalStateException)

        excecao.message == "Não foi possível buscar as empresas por IDs."
        excecao.cause.is(erroSql)
    }


}