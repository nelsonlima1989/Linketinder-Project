package serviceTest

import com.linketinder.data.DataInicializador
import com.linketinder.model.Empresa
import com.linketinder.service.EmpresaService
import spock.lang.Specification

class EmpresaServiceSpec extends Specification{

    def "deve cadastrar uma nova empresa na lista de empresas"(){

        given:
        def inicializador = new DataInicializador()
        def empresaService = new EmpresaService(inicializador)

        def novaEmpresa = new Empresa(
                cnpj: "99999999999",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Nova empresa"
        )

        def quantidadeInicial = empresaService.listarTodasEmpresas().size()

        when:
        empresaService.cadastrarEmpresa(novaEmpresa)

        then:
        empresaService.listarTodasEmpresas().size() == quantidadeInicial + 1
        empresaService.listarTodasEmpresas().contains(novaEmpresa)

    }


}
