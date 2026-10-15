package serviceTest

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Skill
import com.linketinder.repository.CandidatoRepository
import com.linketinder.service.CandidatoService
import spock.lang.Specification

import java.time.LocalDate

class CandidatoServiceSpec extends Specification {

    CandidatoRepository candidatoDAO
    CandidatoCompetenciaDAO candidatoCompetenciaDAO
    CandidatoService candidatoService

    void setup() {
        candidatoDAO = Mock(CandidatoRepository)
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


    void "deve retornar lista vazia quando nao existem candidatos"() {

        when:
        List<Candidato> resultado =
                candidatoService.listarTodosCandidatos()

        then:
        1 * candidatoDAO.listarTodos() >> []
        0 * candidatoCompetenciaDAO._

        resultado.isEmpty()
    }


    void "deve listar candidatos com suas competencias"() {

        given:
        Skill java = new Skill(
                id: 10L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 20L,
                nome: "Groovy"
        )

        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "99999999999",
                descricaoPessoal: "Desenvolvedor",
                skills: []
        )

        when:
        List<Candidato> resultado =
                candidatoService.listarTodosCandidatos()

        then:
        1 * candidatoDAO.listarTodos() >> [candidato]

        1 * candidatoCompetenciaDAO
                .listarCompetenciasDoCandidato(1L) >> [java, groovy]

        resultado.size() == 1
        resultado[0].id == 1L
        resultado[0].skills.size() == 2
        resultado[0].skills*.nome == ["Java", "Groovy"]
    }


    void "deve cadastrar candidato com duas competencias"() {

        given:
        Skill java = new Skill(
                id: 10L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 20L,
                nome: "Groovy"
        )

        Candidato candidato = new Candidato(
                cpf: "99999999999",
                descricaoPessoal: "Desenvolvedor",
                skills: [java, groovy]
        )

        when:
        candidatoService.cadastrarCandidato(candidato)

        then:
        1 * candidatoDAO.inserir(candidato) >> {
            Candidato candidatoInserido ->
                candidatoInserido.id = 1L
        }

        1 * candidatoCompetenciaDAO.adicionarCompetencia(1L, 10L)
        1 * candidatoCompetenciaDAO.adicionarCompetencia(1L, 20L)

        0 * _
    }


    void "deve buscar candidato por id e carregar suas competencias"() {

        given:
        Skill java = new Skill(
                id: 10L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 20L,
                nome: "Groovy"
        )

        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "99999999999",
                descricaoPessoal: "Desenvolvedor",
                skills: []
        )

        when:
        Candidato resultado = candidatoService.buscarPorId(1L)

        then:
        1 * candidatoDAO.buscarPorId(1L) >> candidato

        1 * candidatoCompetenciaDAO
                .listarCompetenciasDoCandidato(1L) >> [java, groovy]

        0 * _

        resultado != null
        resultado.id == 1L
        resultado.skills.size() == 2
        resultado.skills*.nome == ["Java", "Groovy"]
    }



    void "deve retornar null quando candidato nao for encontrado"() {

        given:
        Long idInexistente = 999L

        when:
        Candidato resultado = candidatoService.buscarPorId(idInexistente)

        then:
        1 * candidatoDAO.buscarPorId(idInexistente) >> null

        0 * candidatoCompetenciaDAO._
        0 * _

        resultado == null
    }


    void "deve atualizar os dados de um candidato"() {

        given:
        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "99999999999",
                descricaoPessoal: "Descricao atualizada",
                estado: "RJ"
        )

        when:
        candidatoService.atualizarCandidato(candidato)

        then:
        1 * candidatoDAO.atualizar(candidato)
        0 * _
    }


    void "deve excluir candidato pelo id"() {

        given:
        Long idCandidato = 1L

        when:
        candidatoService.excluirCandidato(idCandidato)

        then:
        1 * candidatoDAO.excluir(idCandidato)
        0 * _
    }


    void "deve listar candidatos de forma anonima"() {

        given:
        Skill java = new Skill(
                id: 10L,
                nome: "Java"
        )

        Candidato candidato = new Candidato(
                id: 1L,
                cpf: "99999999999",
                estado: "RJ",
                descricaoPessoal: "Desenvolvedor",
                senha: "senha123",
                skills: []
        )

        when:
        List<CandidatoAnonimo> resultado =
                candidatoService.listarCandidatosAnonimos()

        then:
        1 * candidatoDAO.listarTodos() >> [candidato]

        1 * candidatoCompetenciaDAO
                .listarCompetenciasDoCandidato(1L) >> [java]

        0 * _

        resultado.size() == 1

        CandidatoAnonimo candidatoAnonimo = resultado[0]

        candidatoAnonimo.id == 1L
        candidatoAnonimo.estado == "RJ"
        candidatoAnonimo.descricaoPessoal == "Desenvolvedor"
        candidatoAnonimo.skills*.nome == ["Java"]
    }


    void "deve retornar lista vazia quando nao existem candidatos anonimos"() {

        when:
        List<CandidatoAnonimo> resultado =
                candidatoService.listarCandidatosAnonimos()

        then:
        1 * candidatoDAO.listarTodos() >> []

        0 * candidatoCompetenciaDAO._
        0 * _

        resultado.isEmpty()
    }


    void "deve retornar true quando candidato for atualizado"() {

        given:
        Candidato candidato = new Candidato(id: 1L)

        when:
        boolean atualizado = candidatoService.atualizarCandidato(candidato)

        then:
        1 * candidatoDAO.atualizar(candidato) >> true
        0 * _

        atualizado
    }


    void "deve retornar false quando candidato nao for atualizado"() {

        given:
        Candidato candidato = new Candidato(id: 999L)

        when:
        boolean atualizado = candidatoService.atualizarCandidato(candidato)

        then:
        1 * candidatoDAO.atualizar(candidato) >> false
        0 * _

        !atualizado
    }


}