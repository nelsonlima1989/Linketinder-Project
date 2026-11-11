package controllerTest

import com.linketinder.controller.RecrutadorController
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.RecrutadorService
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorControllerSpec extends Specification {

    RecrutadorService recrutadorService = Mock(RecrutadorService)
    RecrutadorController recrutadorController = new RecrutadorController(recrutadorService)

    void "CA-01: deve delegar cadastro valido de recrutador ao service"() {
        given:
        Recrutador recrutador = new Recrutador(
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Especialista em recrutamento backend",
                email: "fernanda.lima@pastelsoft.com",
                cpf: "61234567890",
                dataNascimento: LocalDate.of(1992, 4, 15),
                senha: "SenhaSegura@2026"
        )

        when:
        recrutadorController.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * recrutadorService.cadastrarRecrutador(recrutador, 1L) >> { Recrutador r, Long empId ->
            r.id = 10L
        }
        0 * _
        recrutador.id == 10L
    }

    void "CA-03 / CA-05: deve propagar IllegalArgumentException e NoSuchElementException no cadastro invalido"() {
        given:
        Recrutador recrutadorInvalido = new Recrutador(nome: "")

        when:
        recrutadorController.cadastrarRecrutador(recrutadorInvalido, 1L)

        then:
        1 * recrutadorService.cadastrarRecrutador(recrutadorInvalido, 1L) >> {
            throw new IllegalArgumentException("Nome do recrutador inválido.")
        }
        thrown(IllegalArgumentException)

        when:
        recrutadorController.cadastrarRecrutador(recrutadorInvalido, 999L)

        then:
        1 * recrutadorService.cadastrarRecrutador(recrutadorInvalido, 999L) >> {
            throw new NoSuchElementException("Empresa não encontrada.")
        }
        thrown(NoSuchElementException)
    }

    void "CA-04: deve propagar ConflitoDadosException para CPF duplicado e email duplicado"() {
        given:
        Recrutador recrutador = new Recrutador(cpf: "61234567890", email: "fernanda@empresa.com")

        when:
        recrutadorController.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * recrutadorService.cadastrarRecrutador(recrutador, 1L) >> {
            throw new ConflitoDadosException("CPF já cadastrado para outro recrutador.")
        }
        ConflitoDadosException exCpf = thrown(ConflitoDadosException)
        exCpf.message == "CPF já cadastrado para outro recrutador."

        when:
        recrutadorController.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * recrutadorService.cadastrarRecrutador(recrutador, 1L) >> {
            throw new ConflitoDadosException("E-mail já cadastrado para outro recrutador.")
        }
        ConflitoDadosException exEmail = thrown(ConflitoDadosException)
        exEmail.message == "E-mail já cadastrado para outro recrutador."
    }

    void "CA-02 / CA-14: deve listar recrutadores por empresa retornando RecrutadorResumo sem dados sensiveis"() {
        given:
        RecrutadorResumo resumo = new RecrutadorResumo(
                id: 5L,
                empresaId: 1L,
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Recrutamento técnico",
                email: "fernanda.lima@pastelsoft.com"
        )

        when:
        List<RecrutadorResumo> resultado = recrutadorController.listarRecrutadoresPorEmpresa(1L)

        then:
        1 * recrutadorService.listarRecrutadoresPorEmpresa(1L) >> [resumo]
        0 * _

        resultado.size() == 1
        resultado[0].id == 5L
        resultado[0].empresaId == 1L
        resultado[0].nome == "Fernanda Lima"
        !resultado[0].hasProperty("senha")
        !resultado[0].hasProperty("cpf")
        !resultado[0].hasProperty("dataNascimento")
        !resultado[0].hasProperty("empresa")
    }

    void "CA-14: deve buscar recrutador por ID retornando apenas RecrutadorResumo ou null quando inexistente"() {
        given:
        RecrutadorResumo resumo = new RecrutadorResumo(
                id: 7L,
                empresaId: 2L,
                nome: "Ricardo Alves",
                areaRecrutamento: "Dados",
                descricao: "Especialista em Dados",
                email: "ricardo@empresa.com"
        )

        when:
        RecrutadorResumo encontrado = recrutadorController.buscarResumoPorId(7L)
        RecrutadorResumo inexistente = recrutadorController.buscarResumoPorId(999L)

        then:
        1 * recrutadorService.buscarResumoPorId(7L) >> resumo
        1 * recrutadorService.buscarResumoPorId(999L) >> null
        0 * _

        encontrado.is(resumo)
        !encontrado.hasProperty("senha")
        !encontrado.hasProperty("cpf")
        !encontrado.hasProperty("dataNascimento")
        inexistente == null
    }

    void "CA-09: deve atualizar recrutador existente e retornar false quando inexistente"() {
        given:
        Recrutador recrutador = new Recrutador(id: 10L, nome: "Fernanda Editada")
        Recrutador inexistente = new Recrutador(id: 999L, nome: "Inexistente")

        when:
        boolean ok = recrutadorController.atualizarRecrutador(recrutador)
        boolean naoEncontrado = recrutadorController.atualizarRecrutador(inexistente)

        then:
        1 * recrutadorService.atualizarRecrutador(recrutador) >> true
        1 * recrutadorService.atualizarRecrutador(inexistente) >> false
        0 * _

        ok
        !naoEncontrado
    }

    void "CA-10: deve excluir recrutador individualmente e retornar false quando inexistente"() {
        when:
        boolean excluido = recrutadorController.excluirRecrutador(10L)
        boolean naoEncontrado = recrutadorController.excluirRecrutador(999L)

        then:
        1 * recrutadorService.excluirRecrutador(10L) >> true
        1 * recrutadorService.excluirRecrutador(999L) >> false
        0 * _

        excluido
        !naoEncontrado
    }

    void "CA-12 / CA-13 / CA-14: deve consultar vagas da empresa do recrutador sem Empresa completa e candidatos anonimos"() {
        given:
        Vaga vagaSanitizada = new Vaga(
                id: 100L,
                titulo: "Desenvolvedor Groovy",
                descricaoVaga: "APIs e SQL",
                estado: "SP",
                cidade: "São Paulo",
                empresa: null,
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )
        CandidatoAnonimo candidatoAnonimo = new CandidatoAnonimo(
                id: 1L,
                estado: "MG",
                descricaoPessoal: "Backend Developer",
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )

        when:
        List<Vaga> vagas = recrutadorController.listarVagasDaEmpresaDoRecrutador(5L)
        List<CandidatoAnonimo> candidatos = recrutadorController.listarCandidatosAnonimosParaRecrutador(5L)

        then:
        1 * recrutadorService.listarVagasDaEmpresaDoRecrutador(5L) >> [vagaSanitizada]
        1 * recrutadorService.listarCandidatosAnonimosParaRecrutador(5L) >> [candidatoAnonimo]
        0 * _

        vagas.size() == 1
        vagas[0].titulo == "Desenvolvedor Groovy"
        vagas[0].empresa == null

        candidatos.size() == 1
        candidatos[0].id == 1L
        !candidatos[0].hasProperty("cpf")
        !candidatos[0].hasProperty("senha")
        !candidatos[0].hasProperty("dataNascimento")
        !candidatos[0].hasProperty("informacoes")
    }
}
