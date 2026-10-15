package serviceTest

import com.linketinder.repository.EmpresaRepository
import com.linketinder.model.Empresa
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.repository.VagaCompetenciaRepository
import com.linketinder.repository.VagaRepository
import com.linketinder.service.EmpresaService
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    EmpresaRepository empresaDAO
    VagaRepository vagaDAO
    VagaCompetenciaRepository vagaCompetenciaDAO
    EmpresaService empresaService

    void setup() {
        empresaDAO = Mock(EmpresaRepository)
        vagaDAO = Mock(VagaRepository)
        vagaCompetenciaDAO = Mock(VagaCompetenciaRepository)

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


    void "deve retornar lista vazia quando nao existem empresas"() {

        when:
        List<Empresa> resultado =
                empresaService.listarTodasEmpresas()

        then:
        1 * empresaDAO.listarTodos() >> []
        1 * vagaDAO.listarTodos() >> []

        0 * vagaCompetenciaDAO._
        0 * _

        resultado.isEmpty()
    }


    void "deve listar empresas com suas vagas e competencias"() {

        given:
        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia"
        )

        Vaga vaga = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento com Java",
                empresa: empresa,
                skills: []
        )

        Skill java = new Skill(
                id: 100L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 200L,
                nome: "Groovy"
        )

        when:
        List<Empresa> resultado =
                empresaService.listarTodasEmpresas()

        then:
        1 * empresaDAO.listarTodos() >> [empresa]

        1 * vagaDAO.listarTodos() >> [vaga]

        1 * vagaCompetenciaDAO
                .listarCompetenciasDaVaga(10L) >> [java, groovy]

        0 * _

        resultado.size() == 1
        resultado[0].id == 1L
        resultado[0].vagas.size() == 1
        resultado[0].vagas[0].titulo == "Desenvolvedor Backend"
        resultado[0].vagas[0].skills*.nome == ["Java", "Groovy"]
    }


    void "deve associar vagas apenas as suas respectivas empresas"() {

        given:
        Empresa empresaA = new Empresa(
                id: 1L,
                cnpj: "11111111111111"
        )

        Empresa empresaB = new Empresa(
                id: 2L,
                cnpj: "22222222222222"
        )

        Vaga vagaA = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Java",
                empresa: empresaA
        )

        Vaga vagaB = new Vaga(
                id: 20L,
                titulo: "Desenvolvedor Groovy",
                empresa: empresaB
        )

        when:
        List<Empresa> resultado =
                empresaService.listarTodasEmpresas()

        then:
        1 * empresaDAO.listarTodos() >> [empresaA, empresaB]

        1 * vagaDAO.listarTodos() >> [vagaA, vagaB]

        1 * vagaCompetenciaDAO.listarCompetenciasDaVaga(10L) >> []
        1 * vagaCompetenciaDAO.listarCompetenciasDaVaga(20L) >> []

        0 * _

        resultado.size() == 2

        resultado[0].vagas.size() == 1
        resultado[0].vagas[0].id == 10L
        resultado[0].vagas[0].empresa.id == 1L

        resultado[1].vagas.size() == 1
        resultado[1].vagas[0].id == 20L
        resultado[1].vagas[0].empresa.id == 2L
    }

    void "deve cadastrar empresa com vaga e competencias"() {

        given:
        Skill java = new Skill(
                id: 100L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 200L,
                nome: "Groovy"
        )

        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Backend",
                descricaoVaga: "Desenvolvimento de APIs",
                skills: [java, groovy]
        )

        Empresa empresa = new Empresa(
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                vagas: [vaga]
        )

        when:
        empresaService.cadastrarEmpresa(empresa)

        then:
        1 * empresaDAO.inserir(empresa) >> {
            Empresa empresaInserida ->
                empresaInserida.id = 1L
        }

        1 * vagaDAO.inserir(vaga) >> {
            Vaga vagaInserida ->
                assert vagaInserida.empresa.is(empresa)
                vagaInserida.id = 10L
        }

        1 * vagaCompetenciaDAO.adicionarCompetencia(10L, 100L)
        1 * vagaCompetenciaDAO.adicionarCompetencia(10L, 200L)

        0 * _

        empresa.id == 1L
        vaga.id == 10L
        vaga.empresa.is(empresa)
    }

    void "deve cadastrar empresa com vaga sem competencias"() {

        given:
        Vaga vaga = new Vaga(
                titulo: "Desenvolvedor Junior",
                descricaoVaga: "Desenvolvimento de sistemas",
                skills: []
        )

        Empresa empresa = new Empresa(
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                vagas: [vaga]
        )

        when:
        empresaService.cadastrarEmpresa(empresa)

        then:
        1 * empresaDAO.inserir(empresa) >> {
            Empresa empresaInserida ->
                empresaInserida.id = 1L
        }

        1 * vagaDAO.inserir(vaga) >> {
            Vaga vagaInserida ->
                vagaInserida.id = 10L
        }

        0 * vagaCompetenciaDAO._
        0 * _

        empresa.id == 1L
        vaga.id == 10L
        vaga.empresa.is(empresa)
    }

    void "deve retornar null quando empresa nao for encontrada"() {

        given:
        Long idInexistente = 999L

        when:
        Empresa resultado = empresaService.buscarPorId(idInexistente)

        then:
        1 * empresaDAO.buscarPorId(idInexistente) >> null

        0 * vagaDAO._
        0 * vagaCompetenciaDAO._
        0 * _

        resultado == null
    }


    void "deve buscar empresa por id com suas vagas e competencias"() {

        given:
        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ"
        )

        Empresa outraEmpresa = new Empresa(
                id: 2L,
                cnpj: "88888888888888"
        )

        Vaga vaga = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Backend",
                empresa: empresa
        )

        Vaga vagaDeOutraEmpresa = new Vaga(
                id: 20L,
                titulo: "Desenvolvedor Frontend",
                empresa: outraEmpresa
        )

        Skill java = new Skill(
                id: 100L,
                nome: "Java"
        )

        Skill groovy = new Skill(
                id: 200L,
                nome: "Groovy"
        )

        when:
        Empresa resultado = empresaService.buscarPorId(1L)

        then:
        1 * empresaDAO.buscarPorId(1L) >> empresa

        1 * vagaDAO.listarTodos() >> [
                vaga,
                vagaDeOutraEmpresa
        ]

        1 * vagaCompetenciaDAO
                .listarCompetenciasDaVaga(10L) >> [java, groovy]

        0 * _

        resultado.is(empresa)
        resultado.id == 1L
        resultado.vagas.size() == 1
        resultado.vagas[0].id == 10L
        resultado.vagas[0].titulo == "Desenvolvedor Backend"
        resultado.vagas[0].skills*.nome == ["Java", "Groovy"]
    }


    void "deve atualizar os dados de uma empresa"() {

        given:
        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Descricao atualizada"
        )

        when:
        empresaService.atualizarEmpresa(empresa)

        then:
        1 * empresaDAO.atualizar(empresa)
        0 * _
    }


    void "deve excluir empresa pelo id"() {

        given:
        Long idEmpresa = 1L

        when:
        empresaService.excluirEmpresa(idEmpresa)

        then:
        1 * empresaDAO.excluir(idEmpresa)
        0 * _
    }


    void "deve listar empresas de forma anonima"() {

        given:
        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "99999999999999",
                pais: "Brasil",
                estado: "RJ",
                descricaoEmpresarial: "Empresa de tecnologia",
                senha: "senha123"
        )

        Vaga vaga = new Vaga(
                id: 10L,
                titulo: "Desenvolvedor Backend",
                empresa: empresa
        )

        Skill java = new Skill(
                id: 100L,
                nome: "Java"
        )

        when:
        List<EmpresaAnonima> resultado =
                empresaService.listarEmpresasAnonimas()

        then:
        1 * empresaDAO.listarTodos() >> [empresa]

        1 * vagaDAO.listarTodos() >> [vaga]

        1 * vagaCompetenciaDAO
                .listarCompetenciasDaVaga(10L) >> [java]

        0 * _

        resultado.size() == 1

        EmpresaAnonima empresaAnonima = resultado[0]

        empresaAnonima.id == 1L
        empresaAnonima.pais == "Brasil"
        empresaAnonima.estado == "RJ"
        empresaAnonima.descricaoEmpresarial == "Empresa de tecnologia"
        empresaAnonima.vagas.size() == 1
        empresaAnonima.vagas[0].titulo == "Desenvolvedor Backend"
        empresaAnonima.vagas[0].skills*.nome == ["Java"]

        !empresaAnonima.hasProperty("cnpj")
        !empresaAnonima.hasProperty("senha")
    }


    void "deve retornar lista vazia quando nao existem empresas anonimas"() {

        when:
        List<EmpresaAnonima> resultado =
                empresaService.listarEmpresasAnonimas()

        then:
        1 * empresaDAO.listarTodos() >> []
        1 * vagaDAO.listarTodos() >> []

        0 * vagaCompetenciaDAO._
        0 * _

        resultado.isEmpty()
    }

}