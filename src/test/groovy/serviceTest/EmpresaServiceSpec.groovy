package serviceTest

import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.model.Empresa
import com.linketinder.service.EmpresaService
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    EmpresaDAO empresaDAO
    VagaDAO vagaDAO
    VagaCompetenciaDAO vagaCompetenciaDAO
    EmpresaService empresaService

    void setup() {
        empresaDAO = Mock(EmpresaDAO)
        vagaDAO = Mock(VagaDAO)
        vagaCompetenciaDAO = Mock(VagaCompetenciaDAO)

        empresaService = new EmpresaService(
                empresaDAO,
                vagaDAO,
                vagaCompetenciaDAO
        )
    }

    void "deve cadastrar uma nova empresa"() {

        given:
        Empresa novaEmpresa = new Empresa(
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Nova empresa",
                vagas: []
        )

        when:
        empresaService.cadastrarEmpresa(novaEmpresa)

        then:
        1 * empresaDAO.inserir(novaEmpresa)
        0 * vagaDAO._
        0 * vagaCompetenciaDAO._
    }
}