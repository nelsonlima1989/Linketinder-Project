package controllerTest

import com.linketinder.controller.MatchController
import com.linketinder.model.Match
import com.linketinder.service.MatchService
import spock.lang.Specification

class MatchControllerSpec extends Specification {

    MatchService matchService = Mock()

    MatchController matchController =
            new MatchController(matchService)

    def "deve listar matches por meio do service"() {
        given:
        List<Match> matches = [new Match()]

        when:
        List<Match> resultado = matchController.listarMatches()

        then:
        1 * matchService.listarMatches() >> matches

        resultado == matches
    }

    def "deve retornar lista vazia quando nao houver matches"() {
        when:
        List<Match> resultado = matchController.listarMatches()

        then:
        1 * matchService.listarMatches() >> []

        resultado.isEmpty()
    }

    def "deve propagar erro ocorrido na consulta de matches"() {
        when:
        matchController.listarMatches()

        then:
        1 * matchService.listarMatches() >>
                { throw new IllegalStateException("Falha ao consultar matches") }

        thrown(IllegalStateException)
    }
}