package controllerTest

import com.linketinder.controller.CurtidaController
import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Vaga
import com.linketinder.service.CurtidaService
import spock.lang.Specification

class CurtidaControllerSpec extends Specification {

    CurtidaService curtidaService = Mock()

    CurtidaController curtidaController =
            new CurtidaController(curtidaService)

    def "deve encaminhar curtida de vaga ao service"() {
        given:
        Candidato candidato = new Candidato()
        Vaga vaga = new Vaga()

        when:
        curtidaController.curtirVaga(candidato, vaga)

        then:
        1 * curtidaService.curtirVaga(candidato, vaga)
    }

    def "deve encaminhar curtida de candidato ao service"() {
        given:
        Empresa empresa = new Empresa()
        Candidato candidato = new Candidato()

        when:
        curtidaController.curtirCandidato(empresa, candidato)

        then:
        1 * curtidaService.curtirCandidato(empresa, candidato)
    }
}