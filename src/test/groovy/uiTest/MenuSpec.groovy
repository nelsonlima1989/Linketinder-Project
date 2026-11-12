package uiTest

import com.linketinder.controller.CandidatoController
import com.linketinder.controller.CurtidaController
import com.linketinder.controller.EmpresaController
import com.linketinder.controller.MatchController
import com.linketinder.controller.RecrutadorController
import com.linketinder.model.Candidato
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.service.ConflitoDadosException
import com.linketinder.view.Menu
import spock.lang.Specification

class MenuSpec extends Specification {

    CandidatoController candidatoController = Mock()
    EmpresaController empresaController = Mock()
    CurtidaController curtidaController = Mock()
    MatchController matchController = Mock()
    RecrutadorController recrutadorController = Mock()
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
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController

        )

        when:
        menu.iniciarPrograma()

        then:
        noExceptionThrown()

        0 * candidatoController._
        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
    }

    void "deve informar opcao invalida e permitir encerrar o programa"() {

        given:

        Scanner scanner = new Scanner("9\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        String resultado = saida.toString("UTF-8")

        resultado.contains("Opção inválida.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
    }

    void "deve rejeitar entrada nao numerica e permitir nova tentativa"() {

        given:
        Scanner scanner = new Scanner("abc\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )


        when:
        menu.iniciarPrograma()

        then:
        String resultado = saida.toString("UTF-8")

        resultado.contains("Opção inválida. Digite um número válido.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
    }


    void "deve informar quando nao existem candidatos cadastrados"() {

        given:
        Scanner scanner = new Scanner("1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )


        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarCandidatosAnonimos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
        0 * candidatoController._
    }


    void "deve tratar falha na consulta de candidatos e retornar ao menu"() {

        given:
        Scanner scanner = new Scanner("1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )


        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarCandidatosAnonimos() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
        0 * candidatoController._
    }

    void "deve informar quando nao existem empresas cadastradas ao listar vagas"() {

        given:
        Scanner scanner = new Scanner("2\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarEmpresasAnonimas() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhuma empresa cadastrada.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * curtidaController._
        0 * matchController._
    }


    void "deve tratar falha na consulta de vagas e retornar ao menu"() {

        given:
        Scanner scanner = new Scanner("2\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarEmpresasAnonimas() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar vagas: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * curtidaController._
        0 * matchController._
    }


    void "deve impedir curtida de vaga quando nao existem candidatos"() {

        given:
        Scanner scanner = new Scanner("3\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
    }


    void "deve tratar falha na consulta de candidatos ao curtir vaga"() {

        given:
        Scanner scanner = new Scanner("3\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >>
                { throw new IllegalStateException("Falha simulada no banco de dados") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada no banco de dados")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
    }

    void "deve tratar falha na consulta de vagas ao tentar curtir"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Scanner scanner = new Scanner("3\n1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >> [candidato]

        1 * empresaController.listarTodasEmpresas() >>
                { throw new IllegalStateException("Falha simulada ao consultar vagas") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar vagas: Falha simulada ao consultar vagas")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaController._
        0 * matchController._
    }


    void "deve impedir curtida de vaga quando candidato selecionado e invalido"() {

        given:
        Candidato candidato = new Candidato()
        candidato.id = 1L

        Scanner scanner = new Scanner("3\n2\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >> [candidato]

        String resultado = saida.toString("UTF-8")

        resultado.contains("Candidato inválido.")
        resultado.contains("Encerrando o Linketinder...")

        0 * empresaController._
        0 * curtidaController._
        0 * matchController._
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
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >> [candidato]
        1 * empresaController.listarTodasEmpresas() >> [empresa]

        String resultado = saida.toString("UTF-8")

        resultado.contains("Vaga inválida.")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaController._
        0 * matchController._
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
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * candidatoController.listarTodosCandidatos() >> [candidato]
        1 * empresaController.listarTodasEmpresas() >> [empresa]
        1 * curtidaController.curtirVaga(candidato, vaga)

        0 * matchController._
    }


    void "deve impedir curtida de candidato quando nao existem empresas"() {

        given:
        Scanner scanner = new Scanner("4\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarTodasEmpresas() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhuma empresa cadastrada.")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * curtidaController._
        0 * matchController._
    }

    void "deve tratar falha na consulta de empresas ao curtir candidato"() {

        given:
        Scanner scanner = new Scanner("4\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarTodasEmpresas() >>
                { throw new IllegalStateException("Falha simulada ao consultar empresas") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar empresas: Falha simulada ao consultar empresas")
        resultado.contains("Encerrando o Linketinder...")

        0 * candidatoController._
        0 * curtidaController._
        0 * matchController._
    }

    void "deve impedir curtida quando nao existem candidatos disponiveis"() {

        given:
        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"

        Scanner scanner = new Scanner("4\n1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarTodasEmpresas() >> [empresa]
        1 * candidatoController.listarTodosCandidatos() >> []

        String resultado = saida.toString("UTF-8")

        resultado.contains("Nenhum candidato cadastrado.")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaController._
        0 * matchController._
    }

    void "deve tratar falha na consulta de candidatos ao tentar curtir"() {

        given:
        Empresa empresa = new Empresa()
        empresa.informacoes = new Informacoes()
        empresa.informacoes.nome = "Empresa Teste"

        Scanner scanner = new Scanner("4\n1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarTodasEmpresas() >> [empresa]

        1 * candidatoController.listarTodosCandidatos() >>
                { throw new IllegalStateException("Falha simulada ao consultar candidatos") }

        String resultado = saida.toString("UTF-8")

        resultado.contains("Erro ao consultar candidatos: Falha simulada ao consultar candidatos")
        resultado.contains("Encerrando o Linketinder...")

        0 * curtidaController._
        0 * matchController._
    }

    void "CA-02 / CA-14: deve listar recrutadores por empresa exibindo apenas RecrutadorResumo sem expor senha, CPF ou dataNascimento"() {

        given:
        Empresa empresa = new Empresa(
                id: 1L,
                cnpj: "12345678000199",
                senha: "SenhaSecretaEmpresa",
                informacoes: new Informacoes(nome: "PastelSoft")
        )
        RecrutadorResumo resumo = new RecrutadorResumo(
                id: 10L,
                empresaId: 1L,
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Recrutadora técnica backend",
                email: "fernanda.lima@pastelsoft.com"
        )

        Scanner scanner = new Scanner("6\n1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController,
                recrutadorController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * empresaController.listarTodasEmpresas() >> [empresa]
        1 * recrutadorController.listarRecrutadoresPorEmpresa(1L) >> [resumo]
        0 * _

        String resultado = saida.toString("UTF-8")

        resultado.contains("ID: 10")
        resultado.contains("Nome: Fernanda Lima")
        resultado.contains("Área de recrutamento: Engenharia de Software")
        resultado.contains("Descrição: Recrutadora técnica backend")
        resultado.contains("E-mail: fernanda.lima@pastelsoft.com")
        !resultado.contains("SenhaSecretaEmpresa")
        !resultado.contains("12345678000199")
        !resultado.contains("pbkdf2_sha256")
        !resultado.contains("61234567890")
    }

    void "CA-12 / CA-13 / CA-14: deve analisar vagas da empresa e candidatos anonimos como recrutador sem expor dados sensiveis"() {

        given:
        RecrutadorResumo recrutador = new RecrutadorResumo(
                id: 5L,
                empresaId: 1L,
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Recrutadora técnica",
                email: "fernanda.lima@pastelsoft.com"
        )
        Vaga vaga = new Vaga(
                id: 100L,
                titulo: "Desenvolvedor Groovy",
                descricaoVaga: "APIs REST e PostgreSQL",
                estado: "SP",
                cidade: "São Paulo",
                empresa: null,
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )
        CandidatoAnonimo candidatoAnonimo = new CandidatoAnonimo(
                id: 20L,
                estado: "MG",
                descricaoPessoal: "Desenvolvedor Backend Pleno",
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )

        Scanner scanner = new Scanner("7\n1\n0\n")

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController,
                recrutadorController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * recrutadorController.listarTodosResumos() >> [recrutador]
        1 * recrutadorController.listarVagasDaEmpresaDoRecrutador(5L) >> [vaga]
        1 * recrutadorController.listarCandidatosAnonimosParaRecrutador(5L) >> [candidatoAnonimo]
        0 * _

        String resultado = saida.toString("UTF-8")

        resultado.contains("Vagas da empresa do recrutador:")
        resultado.contains("ID: 100 - Desenvolvedor Groovy")
        resultado.contains("Candidatos disponíveis (anônimos):")
        resultado.contains("ID: 20")
        resultado.contains("Estado: MG")
        resultado.contains("Descrição: Desenvolvedor Backend Pleno")
        !resultado.contains("CPF:")
        !resultado.contains("CEP:")
        !resultado.contains("WhatsApp")
    }

    void "CA-01 / CA-04 / CA-05: deve cadastrar recrutador no submenu e tratar conflitos de CPF/email e entradas invalidas"() {

        given:
        String entradas = [
                "8",
                "1",
                "1",
                "Fernanda Lima",
                "Engenharia de Software",
                "Recrutamento backend",
                "fernanda@empresa.com",
                "61234567890",
                "1992-04-15",
                "SenhaForte@2026",
                "1",
                "1",
                "Fernanda Duplicada",
                "Engenharia",
                "Descrição",
                "fernanda@empresa.com",
                "61234567890",
                "1992-04-15",
                "SenhaForte@2026",
                "1",
                "1",
                "Data Invalida",
                "Engenharia",
                "Descrição",
                "data@empresa.com",
                "62345678901",
                "data-invalida",
                "SenhaForte@2026",
                "0",
                "0"
        ].join("\n") + "\n"

        Scanner scanner = new Scanner(entradas)

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController,
                recrutadorController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * recrutadorController.cadastrarRecrutador({ Recrutador r ->
            r.nome == "Fernanda Lima" && r.cpf == "61234567890"
        }, 1L) >> { Recrutador r, Long empId ->
            r.id = 15L
        }
        1 * recrutadorController.cadastrarRecrutador({ Recrutador r ->
            r.nome == "Fernanda Duplicada"
        }, 1L) >> {
            throw new ConflitoDadosException("CPF já cadastrado para outro recrutador.")
        }
        0 * _

        String resultado = saida.toString("UTF-8")

        resultado.contains("Recrutador cadastrado com sucesso (ID: 15).")
        resultado.contains("Conflito de dados: CPF já cadastrado para outro recrutador.")
        resultado.contains("Erro ao cadastrar recrutador: Data de nascimento em formato inválido. Use AAAA-MM-DD.")
        !resultado.contains("SenhaForte@2026")
    }

    void "CA-09 / CA-10 / CA-14: deve buscar por ID, atualizar, excluir e tratar recrutador inexistente no submenu sem expor dados sensiveis"() {

        given:
        RecrutadorResumo resumo = new RecrutadorResumo(
                id: 10L,
                empresaId: 1L,
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia",
                descricao: "Recrutadora",
                email: "fernanda@empresa.com"
        )

        String entradas = [
                "8",
                "2", "10",
                "2", "999",
                "3", "10", "Fernanda Editada", "Dados", "Nova descrição", "fernanda@empresa.com", "61234567890", "1992-04-15", "",
                "3", "999", "Inexistente", "Dados", "Descrição", "inexistente@empresa.com", "61234567890", "1992-04-15", "",
                "4", "10",
                "4", "999",
                "0",
                "0"
        ].join("\n") + "\n"

        Scanner scanner = new Scanner(entradas)

        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                curtidaController,
                matchController,
                recrutadorController
        )

        when:
        menu.iniciarPrograma()

        then:
        1 * recrutadorController.buscarResumoPorId(10L) >> resumo
        1 * recrutadorController.buscarResumoPorId(999L) >> null
        1 * recrutadorController.atualizarRecrutador({ Recrutador r ->
            r.id == 10L && r.nome == "Fernanda Editada" && r.senha == ""
        }) >> true
        1 * recrutadorController.atualizarRecrutador({ Recrutador r ->
            r.id == 999L
        }) >> false
        1 * recrutadorController.excluirRecrutador(10L) >> true
        1 * recrutadorController.excluirRecrutador(999L) >> false
        0 * _

        String resultado = saida.toString("UTF-8")

        resultado.contains("ID: 10")
        resultado.contains("Empresa ID: 1")
        resultado.contains("Nome: Fernanda Lima")
        resultado.contains("Recrutador atualizado com sucesso.")
        resultado.contains("Recrutador excluído com sucesso.")
        resultado.contains("Recrutador não encontrado.")
        !resultado.contains("61234567890")
        !resultado.contains("1992-04-15")
    }

}