package serviceTest

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.model.Candidato
import com.linketinder.service.CandidatoService
import spock.lang.Specification

import java.time.LocalDate

class CandidatoServiceSpec extends Specification {

    CandidatoDAO candidatoDAO
    CandidatoCompetenciaDAO candidatoCompetenciaDAO
    CandidatoService candidatoService

    void setup() {
        candidatoDAO = Mock(CandidatoDAO)
        candidatoCompetenciaDAO = Mock(CandidatoCompetenciaDAO)

        candidatoService = new CandidatoService(
                candidatoDAO,
                candidatoCompetenciaDAO
        )
    }

    void "deve cadastrar um novo candidato"() {

        given:
        Candidato novoCandidato = new Candidato(
                cpf: "99999999999",
                dataNascimento: LocalDate.of(2000, 1, 1),
                descricaoPessoal: "Novo candidato",
                skills: []
        )

        when:
        candidatoService.cadastrarCandidato(novoCandidato)

        then:
        1 * candidatoDAO.inserir(novoCandidato)
        0 * candidatoCompetenciaDAO._
    }
}