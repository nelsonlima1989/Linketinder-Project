package controllerTest

import com.linketinder.controller.EmpresaController
import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.service.EmpresaService
import spock.lang.Specification

class EmpresaControllerSpec extends Specification {

    EmpresaService empresaService = Mock()

    EmpresaController empresaController =
            new EmpresaController(empresaService)

    def "deve listar empresas anonimas por meio do service"() {
        given:
        List<EmpresaAnonima> empresas = [
                new EmpresaAnonima()
        ]

        when:
        List<EmpresaAnonima> resultado =
                empresaController.listarEmpresasAnonimas()

        then:
        1 * empresaService.listarEmpresasAnonimas() >> empresas
        resultado == empresas
    }

    def "deve retornar lista vazia quando nao houver empresas"() {
        when:
        List<EmpresaAnonima> resultado =
                empresaController.listarEmpresasAnonimas()

        then:
        1 * empresaService.listarEmpresasAnonimas() >> []
        resultado.isEmpty()
    }

    def "deve propagar erro ocorrido na consulta"() {
        when:
        empresaController.listarEmpresasAnonimas()

        then:
        1 * empresaService.listarEmpresasAnonimas() >>
                { throw new IllegalStateException("Falha na consulta") }

        thrown(IllegalStateException)
    }


    def "deve listar todas as empresas por meio do service"() {
        given:
        List<Empresa> empresas = [new Empresa()]

        when:
        List<Empresa> resultado =
                empresaController.listarTodasEmpresas()

        then:
        1 * empresaService.listarTodasEmpresas() >> empresas

        resultado == empresas
    }

    def "deve retornar lista vazia quando nao houver empresas cadastradas"() {
        when:
        List<Empresa> resultado =
                empresaController.listarTodasEmpresas()

        then:
        1 * empresaService.listarTodasEmpresas() >> []

        resultado.isEmpty()
    }

}