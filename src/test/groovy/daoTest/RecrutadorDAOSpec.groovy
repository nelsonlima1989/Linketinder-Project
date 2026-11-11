package daoTest

import com.linketinder.dao.RecrutadorDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.repository.EmpresaRepository
import com.linketinder.service.SenhaHashUtil
import spock.lang.Specification

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate

class RecrutadorDAOSpec extends Specification {

    ConexaoProvider conexaoProvider
    EmpresaRepository empresaRepository
    Connection conexao
    PreparedStatement statement
    ResultSet resultado
    RecrutadorDAO recrutadorDAO

    void setup() {
        conexaoProvider = Mock(ConexaoProvider)
        empresaRepository = Mock(EmpresaRepository)
        conexao = Mock(Connection)
        statement = Mock(PreparedStatement)
        resultado = Mock(ResultSet)

        recrutadorDAO = new RecrutadorDAO(conexaoProvider, empresaRepository)
    }

    void "deve inserir recrutador e atribuir o id gerado"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        LocalDate nascimento = LocalDate.of(1992, 4, 15)
        String hashSenha = SenhaHashUtil.gerarHash("SenhaSegura123")

        Recrutador recrutador = new Recrutador(
                empresa: empresa,
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Recrutadora técnica backend",
                email: "fernanda.lima@pastelsoft.com",
                cpf: "61234567890",
                dataNascimento: nascimento,
                senha: hashSenha
        )

        when:
        recrutadorDAO.inserir(recrutador)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("INSERT INTO recrutadores") &&
                        sql.contains("RETURNING id")
        }) >> statement

        1 * statement.setLong(1, 1L)
        1 * statement.setString(2, "Fernanda Lima")
        1 * statement.setString(3, "Engenharia de Software")
        1 * statement.setString(4, "Recrutadora técnica backend")
        1 * statement.setString(5, "fernanda.lima@pastelsoft.com")
        1 * statement.setString(6, "61234567890")
        1 * statement.setDate(7, Date.valueOf(nascimento))
        1 * statement.setString(8, hashSenha)

        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getLong("id") >> 15L

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()
        0 * _

        recrutador.id == 15L
    }

    void "deve propagar IllegalStateException quando ocorrer SQLException ao inserir"() {
        given:
        Recrutador recrutador = new Recrutador(
                empresa: new Empresa(id: 1L),
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia",
                descricao: "Descrição",
                email: "fernanda@empresa.com",
                cpf: "61234567890",
                dataNascimento: LocalDate.of(1992, 4, 15),
                senha: "pbkdf2_sha256\$65536\$00112233445566778899aabbccddeeff\$0011"
        )

        when:
        recrutadorDAO.inserir(recrutador)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement(_ as String) >> {
            throw new SQLException("Erro simulado")
        }
        1 * conexao.close()

        IllegalStateException ex = thrown(IllegalStateException)
        ex.message == "Não foi possível inserir o recrutador."
    }

    void "deve listar todos os recrutadores e associar empresas em lote"() {
        given:
        Empresa empresa1 = new Empresa(id: 1L)
        LocalDate nascimento = LocalDate.of(1992, 4, 15)

        when:
        List<Recrutador> lista = recrutadorDAO.listarTodos()

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({
            String sql -> sql.contains("FROM recrutadores")
        }) >> statement
        1 * statement.executeQuery() >> resultado

        2 * resultado.next() >>> [true, false]
        1 * resultado.getLong("empresa_id") >> 1L
        1 * resultado.getLong("id") >> 10L
        1 * resultado.getString("nome") >> "Fernanda Lima"
        1 * resultado.getString("area_recrutamento") >> "Tech"
        1 * resultado.getString("descricao") >> "Especialista"
        1 * resultado.getString("email") >> "fernanda@pastelsoft.com"
        1 * resultado.getString("cpf") >> "61234567890"
        1 * resultado.getDate("data_nascimento") >> Date.valueOf(nascimento)
        1 * resultado.getString("senha") >> "hash"

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        1 * empresaRepository.buscarPorIds([1L]) >> [1L: empresa1]
        0 * _

        lista.size() == 1
        lista[0].id == 10L
        lista[0].empresa.is(empresa1)
        lista[0].nome == "Fernanda Lima"
    }

    void "deve listar recrutadores por empresaId demonstrando relacionamento 1:N"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        LocalDate data1 = LocalDate.of(1992, 4, 15)
        LocalDate data2 = LocalDate.of(1989, 11, 22)

        when:
        List<Recrutador> lista = recrutadorDAO.listarPorEmpresaId(1L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({
            String sql -> sql.contains("WHERE empresa_id = ?")
        }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.executeQuery() >> resultado

        3 * resultado.next() >>> [true, true, false]
        2 * resultado.getLong("id") >>> [1L, 2L]
        2 * resultado.getString("nome") >>> ["Fernanda Lima", "Ricardo Alves"]
        2 * resultado.getString("area_recrutamento") >>> ["Backend", "Arquitetura"]
        2 * resultado.getString("descricao") >>> ["Desc 1", "Desc 2"]
        2 * resultado.getString("email") >>> ["f@emp.com", "r@emp.com"]
        2 * resultado.getString("cpf") >>> ["61234567890", "62345678901"]
        2 * resultado.getDate("data_nascimento") >>> [Date.valueOf(data1), Date.valueOf(data2)]
        2 * resultado.getString("senha") >>> ["hash1", "hash2"]

        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        1 * empresaRepository.buscarPorId(1L) >> empresa
        0 * _

        lista.size() == 2
        lista[0].empresa.is(empresa)
        lista[1].empresa.is(empresa)
    }

    void "deve buscar recrutador por id, por cpf e por email"() {
        given:
        Empresa empresa = new Empresa(id: 2L)
        LocalDate nascimento = LocalDate.of(1995, 7, 8)

        when:
        Recrutador porId = recrutadorDAO.buscarPorId(5L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("WHERE id = ?") }) >> statement
        1 * statement.setLong(1, 5L)
        1 * statement.executeQuery() >> resultado
        1 * resultado.next() >> true
        1 * resultado.getLong("empresa_id") >> 2L
        1 * empresaRepository.buscarPorId(2L) >> empresa
        1 * resultado.getLong("id") >> 5L
        1 * resultado.getString("nome") >> "Camila Rocha"
        1 * resultado.getString("area_recrutamento") >> "Frontend"
        1 * resultado.getString("descricao") >> "Tech Recruiter"
        1 * resultado.getString("email") >> "camila@sandutech.com"
        1 * resultado.getString("cpf") >> "63456789012"
        1 * resultado.getDate("data_nascimento") >> Date.valueOf(nascimento)
        1 * resultado.getString("senha") >> "hash"
        1 * resultado.close()
        1 * statement.close()
        1 * conexao.close()

        porId != null
        porId.id == 5L
        porId.empresa.id == 2L
    }

    void "deve atualizar recrutador sem alterar empresa_id"() {
        given:
        LocalDate nascimento = LocalDate.of(1992, 4, 15)
        Recrutador recrutador = new Recrutador(
                id: 10L,
                empresa: new Empresa(id: 1L),
                nome: "Fernanda Lima Atualizada",
                areaRecrutamento: "Engenharia Sênior",
                descricao: "Nova descrição",
                email: "fernanda@pastelsoft.com",
                cpf: "61234567890",
                dataNascimento: nascimento,
                senha: "hash_atualizado"
        )

        when:
        boolean atualizado = recrutadorDAO.atualizar(recrutador)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({
            String sql ->
                sql.contains("UPDATE recrutadores") &&
                        !sql.contains("empresa_id =")
        }) >> statement

        1 * statement.setString(1, "Fernanda Lima Atualizada")
        1 * statement.setString(2, "Engenharia Sênior")
        1 * statement.setString(3, "Nova descrição")
        1 * statement.setString(4, "fernanda@pastelsoft.com")
        1 * statement.setString(5, "61234567890")
        1 * statement.setDate(6, Date.valueOf(nascimento))
        1 * statement.setString(7, "hash_atualizado")
        1 * statement.setLong(8, 10L)
        1 * statement.executeUpdate() >> 1

        1 * statement.close()
        1 * conexao.close()
        0 * _

        atualizado
    }

    void "deve excluir recrutador por id e por empresaId"() {
        when:
        boolean excluiuIndividual = recrutadorDAO.excluir(10L)
        int excluidosDaEmpresa = recrutadorDAO.excluirPorEmpresaId(1L)

        then:
        2 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.prepareStatement({ String sql -> sql.contains("WHERE id = ?") }) >> statement
        1 * statement.setLong(1, 10L)
        1 * statement.executeUpdate() >> 1

        1 * conexao.prepareStatement({ String sql -> sql.contains("WHERE empresa_id = ?") }) >> statement
        1 * statement.setLong(1, 1L)
        1 * statement.executeUpdate() >> 2

        2 * statement.close()
        2 * conexao.close()
        0 * _

        excluiuIndividual
        excluidosDaEmpresa == 2
    }

    void "deve gerar hash PBKDF2 com salt aleatorio individual e validar senha"() {
        when:
        String hash1 = SenhaHashUtil.gerarHash("SenhaSegura123")
        String hash2 = SenhaHashUtil.gerarHash("SenhaSegura123")

        then:
        SenhaHashUtil.ehHashValido(hash1)
        SenhaHashUtil.ehHashValido(hash2)
        hash1 != hash2
        hash1.startsWith("pbkdf2_sha256\$65536\$")
        !hash1.contains("SenhaSegura123")
        SenhaHashUtil.verificarSenha("SenhaSegura123", hash1)
        !SenhaHashUtil.verificarSenha("SenhaIncorreta", hash1)
    }

    void "deve garantir que RecrutadorResumo nao exponha senha, cpf, dataNascimento nem Empresa completa"() {
        when:
        RecrutadorResumo resumo = new RecrutadorResumo(
                id: 1L,
                empresaId: 2L,
                nome: "Fernanda Lima",
                areaRecrutamento: "Backend",
                descricao: "Especialista",
                email: "fernanda@empresa.com"
        )

        then:
        !resumo.hasProperty("senha")
        !resumo.hasProperty("cpf")
        !resumo.hasProperty("dataNascimento")
        !resumo.hasProperty("empresa")
        resumo.empresaId == 2L
    }
}
