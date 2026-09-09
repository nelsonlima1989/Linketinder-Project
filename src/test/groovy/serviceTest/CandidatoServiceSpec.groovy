package serviceTest

import com.linketinder.data.DataInicializador
import com.linketinder.model.Candidato
import com.linketinder.service.CandidatoService
import spock.lang.Specification


class CandidatoServiceSpec extends Specification{

    def "deve cadastrar um novo candidato na lista de candidatos"(){

        given:
        def inicializador = new DataInicializador()
        def candidatoService = new CandidatoService(inicializador)

        def novoCandidato = new Candidato(
                cpf: "999999999",
                idade: 25,
                descricaoPessoal: "Novo candidato"
        )

        def quantidadeInicial = candidatoService.listarTodosCandidatos().size()

        when:
        candidatoService.cadastrarCandidato(novoCandidato)

        then:
        candidatoService.listarTodosCandidatos().size() == quantidadeInicial + 1
        candidatoService.listarTodosCandidatos().contains(novoCandidato)
    }
}
