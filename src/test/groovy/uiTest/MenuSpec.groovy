package uiTest

import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import com.linketinder.model.Vaga
import com.linketinder.ui.Menu
import com.linketinder.service.CandidatoService
import com.linketinder.service.EmpresaService
import com.linketinder.service.CurtidaService
import com.linketinder.service.MatchService
import spock.lang.Specification

class MenuSpec extends Specification {

    CandidatoService candidatoService = Mock()
    EmpresaService empresaService = Mock()
    CurtidaService curtidaService = Mock()
    MatchService matchService = Mock()
    ByteArrayOutputStream saida
    PrintStream saidaOriginal
    PrintStream saidaCapturada

    void setup() {
        saidaOriginal = System.out
        saida = new ByteArrayOutputStream()
        saidaCapturada = new PrintStream(saida, true, "UTF-8")
        System.setOut(saidaCapturada)
    }

    void cleanup() {
        System.setOut(saidaOriginal)
        saidaCapturada.close()
    }

    void "deve encerrar o programa ao selecionar a opcao zero"() {

        given:

        Scanner scanner = new Scanner("0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        noExceptionThrown()

        0 * candidatoService._
        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve informar opcao invalida e permitir encerrar o programa"() {

        given:

        Scanner scanner = new Scanner("9\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        String resultado = saida.toString("UTF-8")

        resultado.contains("Opção inválida.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve rejeitar entrada nao numerica e permitir nova tentativa"() {

        given:
        Scanner scanner = new Scanner("abc\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )


        when:
        menu.iniciarPrograma()

        then:
        String resultado = saida.toString("UTF-8")

        resultado.contains("Opção inválida. Digite um número válido.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve informar quando nao existem candidatos cadastrados"() {

        given:
        Scanner scanner = new Scanner("1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )


        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarCandidatosAnonimos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve tratar falha na consulta de candidatos e retornar ao menu"() {

        given:
        Scanner scanner = new Scanner("1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )


        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarCandidatosAnonimos() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve informar quando nao existem empresas cadastradas ao listar vagas"() {

        given:
        Scanner scanner = new Scanner("2\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarEmpresasAnonimas() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhuma empresa cadastrada.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve tratar falha na consulta de vagas e retornar ao menu"() {

        given:
        Scanner scanner = new Scanner("2\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarEmpresasAnonimas() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar vagas: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve impedir curtida de vaga quando nao existem candidatos"() {

        given:
        Scanner scanner = new Scanner("3\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve tratar falha na consulta de candidatos ao curtir vaga"() {

        given:
        Scanner scanner = new Scanner("3\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve tratar falha na consulta de vagas ao tentar curtir"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Scanner scanner = new Scanner("3\n1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >> [candidato]

        1 * empresaService.listarTodasEmpresas() >>
                { throw new IllegalStateException("Falha simulada ao consultar vagas") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar vagas: Falha simulada ao consultar vagas")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaService._
        0 * matchService._
    }


    void "deve impedir curtida de vaga quando candidato selecionado e invalido"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Scanner scanner = new Scanner("3\n2\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >> [candidato]

        String resultado = saida.toString("UTF-8")

        resultado.contains("Candidato inválido.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaService._
        0 * curtidaService._
        0 * matchService._
    }


    void "deve impedir curtida quando a vaga selecionada e invalida"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Vaga vaga = new Vaga()
        vaga.id = 1L
        vaga.titulo = "Desenvolvedor Java"

        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"
        empresa.pais = "Brasil"
        empresa.vagas = [vaga]

        Scanner scanner = new Scanner("3\n1\n2\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >> [candidato]
        1 * empresaService.listarTodasEmpresas() >> [empresa]

        String resultado = saida.toString("UTF-8")

        resultado.contains("Vaga inválida.")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaService._
        0 * matchService._
    }

    void "deve chamar o servico de curtidas com candidato e vaga selecionados"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Vaga vaga = new Vaga()
        vaga.id = 1L
        vaga.titulo = "Desenvolvedor Java"

        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"
        empresa.pais = "Brasil"
        empresa.vagas = [vaga]

        Scanner scanner = new Scanner("3\n1\n1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoService.listarTodosCandidatos() >> [candidato]
        1 * empresaService.listarTodasEmpresas() >> [empresa]
        1 * curtidaService.curtirVaga(candidato, vaga)

        0 * matchService._
    }


    void "deve impedir curtida de candidato quando nao existem empresas"() {

        given:
        Scanner scanner = new Scanner("4\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarTodasEmpresas() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhuma empresa cadastrada.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve tratar falha na consulta de empresas ao curtir candidato"() {

        given:
        Scanner scanner = new Scanner("4\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarTodasEmpresas() >>
                { throw new IllegalStateException("Falha simulada ao consultar empresas") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar empresas: Falha simulada ao consultar empresas")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoService._
        0 * curtidaService._
        0 * matchService._
    }

    void "deve impedir curtida quando nao existem candidatos disponiveis"() {

        given:
        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"

        Scanner scanner = new Scanner("4\n1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarTodasEmpresas() >> [empresa]
        1 * candidatoService.listarTodosCandidatos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaService._
        0 * matchService._
    }

    void "deve tratar falha na consulta de candidatos ao tentar curtir"() {

        given:
        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"

        Scanner scanner = new Scanner("4\n1\n0\n")

        Menu menu = new Menu(
                candidatoService,
                empresaService,
                curtidaService,
                matchService,
                scanner
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaService.listarTodasEmpresas() >> [empresa]

        1 * candidatoService.listarTodosCandidatos() >>
                { throw new IllegalStateException("Falha simulada ao consultar candidatos") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada ao consultar candidatos")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaService._
        0 * matchService._
    }






}