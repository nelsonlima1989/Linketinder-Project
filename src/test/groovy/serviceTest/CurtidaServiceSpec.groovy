package serviceTest

import com.linketinder.database.ConexaoProvider
import com.linketinder.model.Candidato
import com.linketinder.model.CurtidaResultadoDTO
import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.repository.CurtidaRepository
import com.linketinder.repository.MatchRepository
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.CurtidaService
import spock.lang.Specification

import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CurtidaServiceSpec extends Specification {

    CurtidaRepository curtidaRepository
    MatchRepository matchRepository
    ConexaoProvider conexaoProvider
    Connection conexao
    Connection conexaoExterna
    CurtidaService curtidaService

    void setup() {
        curtidaRepository = Mock(CurtidaRepository)
        matchRepository = Mock(MatchRepository)
        conexaoProvider = Mock(ConexaoProvider)
        conexao = Mock(Connection)
        conexaoExterna = Mock(Connection)

        curtidaService = new CurtidaService(curtidaRepository, matchRepository, conexaoProvider)
    }

    void "deve curtir vaga com sucesso sem formar match e comitar transacao"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        CurtidaResultadoDTO resultado = curtidaService.curtirVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexao) >> agora
        1 * matchRepository.buscarMatchIdPorCandidatoEVaga(1L, 2L, conexao) >> null
        1 * conexao.commit()
        0 * conexao.rollback()
        1 * conexao.close()

        resultado != null
        resultado.candidatoId == 1L
        resultado.vagaId == 2L
        resultado.dataCurtida == agora
        !resultado.match
        resultado.matchId == null
    }

    void "deve curtir vaga com sucesso formando match gerado pelo trigger"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        CurtidaResultadoDTO resultado = curtidaService.curtirVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexao) >> agora
        1 * matchRepository.buscarMatchIdPorCandidatoEVaga(1L, 2L, conexao) >> 99L
        1 * conexao.commit()
        1 * conexao.close()

        resultado.match
        resultado.matchId == 99L
    }

    void "deve curtir vaga utilizando conexao externa sem fecha-la nem comitar internamente"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        CurtidaResultadoDTO resultado = curtidaService.curtirVaga(1L, 2L, conexaoExterna)

        then:
        0 * conexaoProvider.obterConexao()
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexaoExterna) >> agora
        1 * matchRepository.buscarMatchIdPorCandidatoEVaga(1L, 2L, conexaoExterna) >> 42L
        0 * conexaoExterna.commit()
        0 * conexaoExterna.close()

        resultado.match
        resultado.matchId == 42L
    }

    void "deve executar rollback e lancar ConflitoDadosException ao detectar duplicidade (SQLState 23505)"() {
        given:
        SQLException sqlEx = new SQLException("duplicate key", "23505")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        curtidaService.curtirVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        0 * conexao.commit()
        1 * conexao.close()

        ConflitoDadosException e = thrown(ConflitoDadosException)
        e.message == "Curtida já registrada anteriormente."
        e.sqlState == "23505"
        e.cause == sqlEx
    }

    void "deve executar rollback e lancar NoSuchElementException em chave estrangeira inexistente"() {
        given:
        SQLException sqlEx = new SQLException("fk candidato inexistente", "23503")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        curtidaService.curtirVaga(999L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(999L, 2L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        1 * conexao.close()

        NoSuchElementException e = thrown(NoSuchElementException)
        e.cause == sqlEx
    }

    void "deve validar parametros de entrada para curtir vaga"() {
        when:
        curtidaService.curtirVaga(0L, 2L)

        then:
        thrown(IllegalArgumentException)

        when:
        curtidaService.curtirVaga(1L, null)

        then:
        thrown(IllegalArgumentException)
    }

    void "deve curtir candidato com sucesso informando recrutador valido sem match"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        CurtidaResultadoDTO resultado = curtidaService.curtirCandidato(10L, 20L, 30L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaEmpresaCandidato(10L, 20L, 30L, conexao) >> agora
        1 * matchRepository.buscarMatchIdPorEmpresaECandidato(10L, 20L, conexao) >> null
        1 * conexao.commit()
        1 * conexao.close()

        resultado.empresaId == 10L
        resultado.candidatoId == 20L
        resultado.recrutadorId == 30L
        !resultado.match
    }

    void "deve curtir candidato com sucesso formando match gerado pelo trigger"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        CurtidaResultadoDTO resultado = curtidaService.curtirCandidato(10L, 20L, 30L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaEmpresaCandidato(10L, 20L, 30L, conexao) >> agora
        1 * matchRepository.buscarMatchIdPorEmpresaECandidato(10L, 20L, conexao) >> 55L
        1 * conexao.commit()
        1 * conexao.close()

        resultado.match
        resultado.matchId == 55L
    }

    void "deve rejeitar recrutador de outra empresa disparado pelo trigger PostgreSQL (23503)"() {
        given:
        SQLException sqlEx = new SQLException("O recrutador informado (5) nao pertence a empresa (1)", "23503")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        curtidaService.curtirCandidato(1L, 2L, 5L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaEmpresaCandidato(1L, 2L, 5L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        1 * conexao.close()

        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message.contains("O recrutador informado não pertence à empresa")
        e.cause == sqlEx
    }

    void "deve validar parametros de entrada para curtir candidato"() {
        when:
        curtidaService.curtirCandidato(null, 1L, 1L)

        then:
        thrown(IllegalArgumentException)

        when:
        curtidaService.curtirCandidato(1L, -5L, 1L)

        then:
        thrown(IllegalArgumentException)

        when:
        curtidaService.curtirCandidato(1L, 1L, 0L)

        then:
        thrown(IllegalArgumentException)
    }

    void "deve executar rollback e propagar IllegalStateException em caso de erro SQL desconhecido"() {
        given:
        SQLException sqlEx = new SQLException("deadlock detectado", "40P01")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        curtidaService.curtirCandidato(1L, 2L, 3L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaEmpresaCandidato(1L, 2L, 3L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        1 * conexao.close()

        IllegalStateException e = thrown(IllegalStateException)
        e.cause == sqlEx
        ((SQLException) e.cause).getSQLState() == "40P01"
    }

    void "deve manter compatibilidade com metodo legado curtirVaga recebendo entidades"() {
        given:
        Candidato c = new Candidato(id: 1L)
        Vaga v = new Vaga(id: 2L)
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)

        when:
        curtidaService.curtirVaga(c, v)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexao) >> agora
        1 * matchRepository.buscarMatchIdPorCandidatoEVaga(1L, 2L, conexao) >> null
        1 * conexao.commit()
        1 * conexao.close()
    }

    void "deve rejeitar curtirCandidato legado sem recrutador quando repositório configurado"() {
        given:
        Empresa e = new Empresa(id: 1L)
        Candidato c = new Candidato(id: 2L)

        when:
        curtidaService.curtirCandidato(e, c)

        then:
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message.contains("Recrutador responsável é obrigatório")
    }

    void "deve lancar IllegalStateException se repositorio nao configurado no fluxo JDBC"() {
        given:
        CurtidaService semRepo = new CurtidaService(null, null, null)

        when:
        semRepo.curtirVaga(1L, 2L)

        then:
        IllegalStateException e1 = thrown(IllegalStateException)
        e1.message.contains("Repositório de persistência não configurado")

        when:
        semRepo.curtirCandidato(1L, 2L, 3L)

        then:
        IllegalStateException e2 = thrown(IllegalStateException)
        e2.message.contains("Repositório de persistência não configurado")
    }

    void "deve lancar IllegalStateException se ConexaoProvider nao configurado para transacao"() {
        given:
        CurtidaService semProvider = new CurtidaService(curtidaRepository, matchRepository, null)

        when:
        semProvider.curtirVaga(1L, 2L)

        then:
        IllegalStateException e = thrown(IllegalStateException)
        e.message.contains("ConexaoProvider não configurado")
    }

    void "deve mapear erro de campos obrigatorios nao informados no banco (SQLState 23502)"() {
        given:
        SQLException sqlEx = new SQLException("null value in column violates not-null constraint", "23502")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        curtidaService.curtirCandidato(1L, 2L, 3L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaEmpresaCandidato(1L, 2L, 3L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        1 * conexao.close()

        IllegalArgumentException e = thrown(IllegalArgumentException)
        e.message == "Campos obrigatórios não informados."
        e.cause == sqlEx
    }

    void "deve propagar IllegalStateException original quando a causa nao for SQLException"() {
        given:
        IllegalStateException daoEx = new IllegalStateException("erro de memoria inesperado", new RuntimeException("falha"))

        when:
        curtidaService.curtirVaga(1L, 2L)

        then:
        1 * conexaoProvider.obterConexao() >> conexao
        1 * conexao.setAutoCommit(false)
        1 * curtidaRepository.inserirCurtidaCandidatoVaga(1L, 2L, conexao) >> { throw daoEx }
        1 * conexao.rollback()
        1 * conexao.close()

        IllegalStateException e = thrown(IllegalStateException)
        e == daoEx
    }

    void "deve executar fluxo legado em memoria quando instanciado com servicos legados"() {
        given:
        com.linketinder.service.EmpresaService empService = Mock(com.linketinder.service.EmpresaService)
        com.linketinder.service.MatchService matchSvc = Mock(com.linketinder.service.MatchService)
        CurtidaService servicoLegado = new CurtidaService(empService, matchSvc)

        Candidato c = new Candidato(id: 10L)
        Vaga v = new Vaga(id: 20L, titulo: "Vaga Legada")
        Empresa e = new Empresa(id: 30L, vagas: [v])

        when:
        servicoLegado.curtirVaga(c, v)

        then:
        1 * empService.listarTodasEmpresas() >> [e]
        1 * matchSvc.verificarMatch(c, e, _)
        servicoLegado.listarCurtidas().size() == 1

        when:
        servicoLegado.curtirCandidato(e, c)

        then:
        1 * matchSvc.verificarMatch(c, e, _)
        servicoLegado.listarCurtidas().size() == 2
    }
}
