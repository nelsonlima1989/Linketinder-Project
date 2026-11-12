package serviceTest

import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Curtida
import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Match
import com.linketinder.model.MatchAnonimo
import com.linketinder.model.RecrutadorAnonimo
import com.linketinder.model.TipoCurtida
import com.linketinder.repository.MatchRepository
import com.linketinder.service.MatchService
import spock.lang.Specification

import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MatchServiceSpec extends Specification {

    MatchRepository matchRepository
    Connection conexao
    MatchService matchService

    void setup() {
        matchRepository = Mock(MatchRepository)
        conexao = Mock(Connection)
        matchService = new MatchService(matchRepository)
    }

    void "deve listar matches delegando ao repositório"() {
        given:
        MatchAnonimo match = new MatchAnonimo(
                id: 1L,
                dataMatch: OffsetDateTime.now(ZoneOffset.UTC),
                candidato: new CandidatoAnonimo(id: 10L, estado: "SP"),
                empresa: new EmpresaAnonima(id: 20L, pais: "Brasil", estado: "SP"),
                recrutador: new RecrutadorAnonimo(id: 30L, areaRecrutamento: "TI")
        )

        when:
        List<MatchAnonimo> lista = matchService.listarMatches(10L, 20L, conexao)

        then:
        1 * matchRepository.listarMatches(10L, 20L, conexao) >> [match]

        lista.size() == 1
        lista[0].id == 1L
        lista[0].candidato.id == 10L
        lista[0].empresa.id == 20L
        lista[0].recrutador.id == 30L
    }

    void "deve listar matches sem parametros de filtro"() {
        when:
        List<MatchAnonimo> lista = matchService.listarMatches()

        then:
        1 * matchRepository.listarMatches(null, null, null) >> []

        lista.isEmpty()
    }

    void "deve propagar IllegalStateException ao falhar listagem de matches no repositório"() {
        given:
        SQLException sqlEx = new SQLException("Connection broken", "08006")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        matchService.listarMatches()

        then:
        1 * matchRepository.listarMatches(null, null, null) >> { throw daoEx }

        IllegalStateException e = thrown(IllegalStateException)
        e.cause == sqlEx
        ((SQLException) e.cause).getSQLState() == "08006"
    }

    void "deve buscar match por ID com sucesso"() {
        given:
        MatchAnonimo match = new MatchAnonimo(id: 5L)

        when:
        MatchAnonimo resultado = matchService.buscarPorId(5L)

        then:
        1 * matchRepository.buscarPorId(5L, null) >> match

        resultado.id == 5L
    }

    void "deve rejeitar busca por ID com valores nulos ou menores ou iguais a zero"() {
        when:
        matchService.buscarPorId(null)

        then:
        thrown(IllegalArgumentException)

        when:
        matchService.buscarPorId(0L)

        then:
        thrown(IllegalArgumentException)

        when:
        matchService.buscarPorId(-1L)

        then:
        thrown(IllegalArgumentException)
    }

    void "deve propagar erro de persistencia ao buscar match por ID"() {
        given:
        SQLException sqlEx = new SQLException("query error")
        IllegalStateException daoEx = new IllegalStateException("erro dao", sqlEx)

        when:
        matchService.buscarPorId(10L)

        then:
        1 * matchRepository.buscarPorId(10L, null) >> { throw daoEx }

        thrown(IllegalStateException)
    }

    void "deve buscar match ID por empresa e candidato"() {
        when:
        Long matchId = matchService.buscarMatchIdPorEmpresaECandidato(1L, 2L, conexao)

        then:
        1 * matchRepository.buscarMatchIdPorEmpresaECandidato(1L, 2L, conexao) >> 77L

        matchId == 77L
    }

    void "deve buscar match ID por candidato e vaga"() {
        when:
        Long matchId = matchService.buscarMatchIdPorCandidatoEVaga(2L, 3L)

        then:
        1 * matchRepository.buscarMatchIdPorCandidatoEVaga(2L, 3L, null) >> 88L

        matchId == 88L
    }

    void "deve retornar lista em memoria quando servico instanciado sem repositorio (compatibilidade)"() {
        given:
        MatchService legado = new MatchService()

        when:
        List matches = legado.listarMatches()

        then:
        matches.isEmpty()
    }

    void "deve verificar match legado em memoria quando servico legado for chamado"() {
        given:
        MatchService legado = new MatchService()
        Candidato c = new Candidato(id: 1L)
        Empresa e = new Empresa(id: 2L)
        Curtida curtidaC = new Curtida(candidato: c, empresa: e, tipoCurtida: TipoCurtida.CANDIDATO)
        Curtida curtidaE = new Curtida(candidato: c, empresa: e, tipoCurtida: TipoCurtida.EMPRESA)

        when:
        Match match = legado.verificarMatch(c, e, [curtidaC, curtidaE])

        then:
        match != null
        match.candidato == c
        match.empresa == e
    }
}
