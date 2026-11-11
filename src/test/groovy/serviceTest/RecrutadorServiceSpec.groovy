package serviceTest

import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.model.RecrutadorResumo
import com.linketinder.model.Skill
import com.linketinder.model.Vaga
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.RecrutadorRepository
import com.linketinder.service.CandidatoService
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.EmpresaService
import com.linketinder.service.RecrutadorService
import com.linketinder.service.SenhaHashUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class RecrutadorServiceSpec extends Specification {

    RecrutadorRepository recrutadorRepository
    EmpresaRepository empresaRepository
    EmpresaService empresaService
    CandidatoService candidatoService
    RecrutadorService recrutadorService

    void setup() {
        recrutadorRepository = Mock(RecrutadorRepository)
        empresaRepository = Mock(EmpresaRepository)
        empresaService = Mock(EmpresaService)
        candidatoService = Mock(CandidatoService)

        recrutadorService = new RecrutadorService(
                recrutadorRepository,
                empresaRepository,
                empresaService,
                candidatoService
        )
    }

    private static Recrutador criarRecrutadorValido() {
        return new Recrutador(
                nome: "Fernanda Lima",
                areaRecrutamento: "Engenharia de Software",
                descricao: "Especialista em recrutamento técnico backend",
                email: "fernanda.lima@pastelsoft.com",
                cpf: "61234567890",
                dataNascimento: LocalDate.of(1992, 4, 15),
                senha: "SenhaSegura@2026"
        )
    }

    void "deve cadastrar recrutador vinculado a empresa existente com senha derivada em PBKDF2"() {
        given:
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId)
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, empresaId)

        then:
        1 * empresaRepository.buscarPorId(empresaId) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> null
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> null
        1 * recrutadorRepository.inserir({ Recrutador r ->
            r.empresa.is(empresa) &&
                    r.senha != "SenhaSegura@2026" &&
                    SenhaHashUtil.ehHashValido(r.senha) &&
                    SenhaHashUtil.verificarSenha("SenhaSegura@2026", r.senha)
        }) >> { Recrutador r ->
            r.id = 10L
        }
        0 * _

        recrutador.id == 10L
        recrutador.empresa.is(empresa)
    }

    void "deve permitir cadastrar multiplos recrutadores para a mesma empresa (relacionamento 1:N)"() {
        given:
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId)
        Recrutador r1 = criarRecrutadorValido()
        Recrutador r2 = criarRecrutadorValido()
        r2.nome = "Ricardo Alves"
        r2.cpf = "62345678901"
        r2.email = "ricardo.alves@pastelsoft.com"

        when:
        recrutadorService.cadastrarRecrutador(r1, empresaId)
        recrutadorService.cadastrarRecrutador(r2, empresaId)

        then:
        2 * empresaRepository.buscarPorId(empresaId) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> null
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> null
        1 * recrutadorRepository.buscarPorCpf("62345678901") >> null
        1 * recrutadorRepository.buscarPorEmail("ricardo.alves@pastelsoft.com") >> null
        2 * recrutadorRepository.inserir(_ as Recrutador) >> { Recrutador r ->
            r.id = (r.cpf == "61234567890") ? 1L : 2L
        }

        r1.id == 1L
        r2.id == 2L
        r1.empresa.is(empresa)
        r2.empresa.is(empresa)
    }

    void "deve lancar NoSuchElementException quando a empresa nao existir"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 999L)

        then:
        1 * empresaRepository.buscarPorId(999L) >> null
        0 * recrutadorRepository._

        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Empresa não encontrada."
    }

    @Unroll
    void "deve rejeitar cadastro quando empresaId for invalido: #empresaId"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, empresaId)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        empresaId << [null, 0L, -5L]
    }

    @Unroll
    void "deve rejeitar CPF invalido: '#cpfInvalido'"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.cpf = cpfInvalido

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        cpfInvalido << ["", "123", "123.456.789-01", "1234567890a", "123456789012"]
    }

    @Unroll
    void "deve rejeitar email invalido: '#emailInvalido'"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.email = emailInvalido

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        emailInvalido << ["", "sem-arroba.com", "usuario@", "@dominio.com", "a b@dominio.com"]
    }

    @Unroll
    void "deve rejeitar data de nascimento invalida ou menor de 18 anos: #dataInvalida"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.dataNascimento = dataInvalida

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        dataInvalida << [
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().minusYears(18).plusDays(1)
        ]
    }

    @Unroll
    void "deve rejeitar senha curta ou vazia: '#senhaInvalida'"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.senha = senhaInvalida

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        senhaInvalida << [null, "", "1234567", "       "]
    }

    void "deve rejeitar CPF duplicado ou email duplicado entre recrutadores"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * empresaRepository.buscarPorId(1L) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> new Recrutador(id: 99L)
        0 * recrutadorRepository.inserir(_ as Recrutador)

        thrown(ConflitoDadosException)
    }

    void "deve atualizar recrutador mantendo empresa original e hash existente quando nova senha nao for informada"() {
        given:
        Empresa empresaOriginal = new Empresa(id: 3L)
        String hashOriginal = SenhaHashUtil.gerarHash("SenhaOriginal123")
        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = empresaOriginal
        existente.senha = hashOriginal

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.empresa = new Empresa(id: 999L)
        atualizado.nome = "Fernanda Lima Editada"
        atualizado.senha = null

        when:
        boolean ok = recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> existente
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> existente
        1 * recrutadorRepository.atualizar({ Recrutador r ->
            r.id == 10L &&
                    r.empresa.is(empresaOriginal) &&
                    r.senha == hashOriginal &&
                    r.nome == "Fernanda Lima Editada"
        }) >> true

        ok
    }

    void "deve listar recrutadores da empresa como RecrutadorResumo sem expor senha, cpf, dataNascimento nem Empresa"() {
        given:
        Empresa empresa = new Empresa(id: 1L, cnpj: "12345678000199", senha: "segredo")
        Recrutador r = criarRecrutadorValido()
        r.id = 7L
        r.empresa = empresa
        r.senha = "pbkdf2_sha256\$65536\$salt\$hash"

        when:
        List<RecrutadorResumo> resumos = recrutadorService.listarRecrutadoresPorEmpresa(1L)

        then:
        1 * recrutadorRepository.listarPorEmpresaId(1L) >> [r]

        resumos.size() == 1
        resumos[0].id == 7L
        resumos[0].empresaId == 1L
        resumos[0].nome == "Fernanda Lima"
        resumos[0].areaRecrutamento == "Engenharia de Software"
        resumos[0].email == "fernanda.lima@pastelsoft.com"
        !resumos[0].hasProperty("senha")
        !resumos[0].hasProperty("cpf")
        !resumos[0].hasProperty("dataNascimento")
        !resumos[0].hasProperty("empresa")
    }

    void "deve listar apenas as vagas da empresa do recrutador e candidatos de forma estritamente anonima"() {
        given:
        Empresa empresa = new Empresa(
                id: 1L,
                vagas: [
                        new Vaga(id: 100L, titulo: "Dev Groovy", estado: "SP", cidade: "São Paulo")
                ]
        )
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.id = 5L
        recrutador.empresa = empresa

        CandidatoAnonimo candidatoAnonimo = new CandidatoAnonimo(
                id: 1L,
                estado: "RJ",
                descricaoPessoal: "Dev Backend",
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )

        when:
        List<Vaga> vagas = recrutadorService.listarVagasDaEmpresaDoRecrutador(5L)
        List<CandidatoAnonimo> candidatos = recrutadorService.listarCandidatosAnonimosParaRecrutador(5L)

        then:
        2 * recrutadorRepository.buscarPorId(5L) >> recrutador
        1 * empresaService.buscarPorId(1L) >> empresa
        1 * candidatoService.listarCandidatosAnonimos() >> [candidatoAnonimo]

        vagas.size() == 1
        vagas[0].titulo == "Dev Groovy"

        candidatos.size() == 1
        candidatos[0].id == 1L
        !candidatos[0].hasProperty("cpf")
        !candidatos[0].hasProperty("senha")
        !candidatos[0].hasProperty("dataNascimento")
        !candidatos[0].hasProperty("informacoes")
    }
}
