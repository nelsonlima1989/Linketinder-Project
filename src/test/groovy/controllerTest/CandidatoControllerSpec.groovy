package controllerTest

import com.linketinder.controller.CandidatoController
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.service.CandidatoService
import spock.lang.Specification

class CandidatoControllerSpec extends Specification {

    CandidatoService candidatoService = Mock()
    CandidatoController candidatoController =
            new CandidatoController(candidatoService)

    def "deve listar candidatos anonimos por meio do service"() {
        given:
        List<CandidatoAnonimo> candidatos = [
                new CandidatoAnonimo()
        ]

        when:
        List<CandidatoAnonimo> resultado =
                candidatoController.listarCandidatosAnonimos()

        then:
        1 * candidatoService.listarCandidatosAnonimos() >> candidatos

        resultado == candidatos
    }

    def "deve retornar lista vazia quando nao houver candidatos"() {
        when:
        List<CandidatoAnonimo> resultado =
                candidatoController.listarCandidatosAnonimos()

        then:
        1 * candidatoService.listarCandidatosAnonimos() >> []

        resultado.isEmpty()
    }

    def "deve propagar erro ocorrido na consulta"() {
        when:
        candidatoController.listarCandidatosAnonimos()

        then:
        1 * candidatoService.listarCandidatosAnonimos() >>
                { throw new IllegalStateException("Falha na consulta") }

        thrown(IllegalStateException)
    }

    def "deve listar todos os candidatos por meio do service"() {
        given:
        List<Candidato> candidatos = [new Candidato()]

        when:
        List<Candidato> resultado =
                candidatoController.listarTodosCandidatos()

        then:
        1 * candidatoService.listarTodosCandidatos() >> candidatos
        resultado == candidatos
    }

    def "deve retornar lista vazia quando nao houver candidatos cadastrados"() {
        when:
        List<Candidato> resultado =
                candidatoController.listarTodosCandidatos()

        then:
        1 * candidatoService.listarTodosCandidatos() >> []
        resultado.isEmpty()
    }



}