package serviceTest

import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
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

    void "CA-01 / CA-07: deve cadastrar recrutador vinculado a empresa existente com senha derivada em PBKDF2"() {
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

    void "CA-02: deve permitir cadastrar multiplos recrutadores para a mesma empresa (relacionamento 1:N)"() {
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

    void "CA-03: deve lancar NoSuchElementException quando a empresa nao existir"() {
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
    void "CA-03: deve rejeitar cadastro quando empresaId for invalido: #empresaId"() {
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

    void "CA-04: deve rejeitar CPF duplicado no cadastro entre recrutadores"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * empresaRepository.buscarPorId(1L) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> new Recrutador(id: 99L)
        0 * recrutadorRepository.inserir(_ as Recrutador)

        ConflitoDadosException ex = thrown(ConflitoDadosException)
        ex.message == "CPF já cadastrado para outro recrutador."
    }

    void "CA-04: deve rejeitar email duplicado no cadastro entre recrutadores"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        Recrutador recrutador = criarRecrutadorValido()

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * empresaRepository.buscarPorId(1L) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> null
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> new Recrutador(id: 88L)
        0 * recrutadorRepository.inserir(_ as Recrutador)

        ConflitoDadosException ex = thrown(ConflitoDadosException)
        ex.message == "E-mail já cadastrado para outro recrutador."
    }

    void "CA-04: deve rejeitar CPF duplicado na atualizacao quando pertencer a outro recrutador"() {
        given:
        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = new Empresa(id: 1L)
        existente.senha = SenhaHashUtil.gerarHash("SenhaOriginal123")

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.cpf = "99988877766"
        atualizado.senha = null

        when:
        recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        1 * recrutadorRepository.buscarPorCpf("99988877766") >> new Recrutador(id: 25L)
        0 * recrutadorRepository.atualizar(_ as Recrutador)

        ConflitoDadosException ex = thrown(ConflitoDadosException)
        ex.message == "CPF já cadastrado para outro recrutador."
    }

    void "CA-04: deve rejeitar email duplicado na atualizacao quando pertencer a outro recrutador"() {
        given:
        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = new Empresa(id: 1L)
        existente.senha = SenhaHashUtil.gerarHash("SenhaOriginal123")

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.email = "outro@empresa.com"
        atualizado.senha = null

        when:
        recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> existente
        1 * recrutadorRepository.buscarPorEmail("outro@empresa.com") >> new Recrutador(id: 30L)
        0 * recrutadorRepository.atualizar(_ as Recrutador)

        ConflitoDadosException ex = thrown(ConflitoDadosException)
        ex.message == "E-mail já cadastrado para outro recrutador."
    }

    @Unroll
    void "CA-05: deve rejeitar CPF com formato invalido: '#cpfInvalido'"() {
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
        cpfInvalido << [null, "", "   ", "123", "123.456.789-01", "1234567890a", "123456789012"]
    }

    @Unroll
    void "CA-05: deve rejeitar email malformado ou acima do limite: '#descricaoCaso'"() {
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
        descricaoCaso          | emailInvalido
        "nulo"                 | null
        "vazio"                | ""
        "sem arroba"           | "sem-arroba.com"
        "sem dominio"          | "usuario@"
        "sem usuario"          | "@dominio.com"
        "com espaco"           | "a b@dominio.com"
        "acima de 150 chars"   | ("a" * 140) + "@dominio.com"
    }

    @Unroll
    void "CA-05: deve rejeitar campos obrigatorios invalidos ou acima do limite maximo (#campo)"() {
        given:
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.nome = nome
        recrutador.areaRecrutamento = area
        recrutador.descricao = descricao

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        0 * empresaRepository._
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        campo                        | nome          | area          | descricao
        "nome nulo"                  | null          | "Engenharia"  | "Descrição válida"
        "nome em branco"             | "   "         | "Engenharia"  | "Descrição válida"
        "nome > 150 chars"           | ("N" * 151)   | "Engenharia"  | "Descrição válida"
        "area nula"                  | "Fernanda"    | null          | "Descrição válida"
        "area em branco"             | "Fernanda"    | "   "         | "Descrição válida"
        "area > 100 chars"           | "Fernanda"    | ("A" * 101)   | "Descrição válida"
        "descricao nula"             | "Fernanda"    | "Engenharia"  | null
        "descricao em branco"        | "Fernanda"    | "Engenharia"  | "   "
    }

    @Unroll
    void "CA-06: deve rejeitar data de nascimento invalida ou menor de 18 anos: #dataInvalida"() {
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

    void "CA-06: deve aceitar recrutador com idade exatamente igual a 18 anos na data atual"() {
        given:
        Empresa empresa = new Empresa(id: 1L)
        Recrutador recrutador = criarRecrutadorValido()
        recrutador.dataNascimento = LocalDate.now().minusYears(18)

        when:
        recrutadorService.cadastrarRecrutador(recrutador, 1L)

        then:
        1 * empresaRepository.buscarPorId(1L) >> empresa
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> null
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> null
        1 * recrutadorRepository.inserir(_ as Recrutador) >> { Recrutador r ->
            r.id = 18L
        }
        0 * _

        recrutador.id == 18L
    }

    @Unroll
    void "CA-07: deve rejeitar senha invalida no cadastro e na atualizacao: '#descricaoCaso'"() {
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
        descricaoCaso          | senhaInvalida
        "nula"                 | null
        "vazia"                | ""
        "apenas espacos"       | "       "
        "curta (7 chars)"      | "1234567"
        "acima de 128 chars"   | ("S" * 129)
    }

    void "CA-07: deve rejeitar nova senha curta na atualizacao de recrutador"() {
        given:
        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = new Empresa(id: 1L)
        existente.senha = SenhaHashUtil.gerarHash("SenhaOriginal123")

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.senha = "curta7"

        when:
        recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        0 * recrutadorRepository.atualizar(_ as Recrutador)

        thrown(IllegalArgumentException)
    }

    void "CA-07: deve derivar novo hash PBKDF2 quando nova senha valida for informada na atualizacao"() {
        given:
        Empresa empresaOriginal = new Empresa(id: 3L)
        String hashAntigo = SenhaHashUtil.gerarHash("SenhaAntiga@123")
        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = empresaOriginal
        existente.senha = hashAntigo

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.senha = "NovaSenhaForte@2026"

        when:
        boolean ok = recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> existente
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> existente
        1 * recrutadorRepository.atualizar({ Recrutador r ->
            r.id == 10L &&
                    r.senha != "NovaSenhaForte@2026" &&
                    r.senha != hashAntigo &&
                    SenhaHashUtil.ehHashValido(r.senha) &&
                    SenhaHashUtil.verificarSenha("NovaSenhaForte@2026", r.senha)
        }) >> true

        ok
    }

    void "CA-07: nunca deve aceitar entrada com aparencia de hash PBKDF2 sem derivar novo hash na atualizacao"() {
        given:
        Empresa empresaOriginal = new Empresa(id: 3L)
        String hashExistente = SenhaHashUtil.gerarHash("SenhaAntiga@123")
        String entradaComAparenciaDeHash = SenhaHashUtil.gerarHash("OutraSenhaQualquer@123")

        Recrutador existente = criarRecrutadorValido()
        existente.id = 10L
        existente.empresa = empresaOriginal
        existente.senha = hashExistente

        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 10L
        atualizado.senha = entradaComAparenciaDeHash

        when:
        boolean ok = recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(10L) >> existente
        1 * recrutadorRepository.buscarPorCpf("61234567890") >> existente
        1 * recrutadorRepository.buscarPorEmail("fernanda.lima@pastelsoft.com") >> existente
        1 * recrutadorRepository.atualizar({ Recrutador r ->
            r.senha != entradaComAparenciaDeHash &&
                    r.senha != hashExistente &&
                    SenhaHashUtil.ehHashValido(r.senha) &&
                    SenhaHashUtil.verificarSenha(entradaComAparenciaDeHash, r.senha)
        }) >> true

        ok
    }

    @Unroll
    void "CA-07 / CA-09: deve atualizar recrutador mantendo empresa original e hash existente quando nova senha for #descricaoSenha"() {
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
        atualizado.senha = senhaEntrada

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

        where:
        descricaoSenha | senhaEntrada
        "nula"         | null
        "vazia"        | ""
        "em branco"    | "    "
    }

    void "CA-09: deve retornar false ao tentar atualizar recrutador inexistente"() {
        given:
        Recrutador atualizado = criarRecrutadorValido()
        atualizado.id = 404L

        when:
        boolean ok = recrutadorService.atualizarRecrutador(atualizado)

        then:
        1 * recrutadorRepository.buscarPorId(404L) >> null
        0 * recrutadorRepository.atualizar(_ as Recrutador)

        !ok
    }

    void "CA-10: deve excluir recrutador individualmente por id valido"() {
        when:
        boolean excluiu = recrutadorService.excluirRecrutador(7L)

        then:
        1 * recrutadorRepository.excluir(7L) >> true
        0 * _

        excluiu
    }

    @Unroll
    void "CA-10: deve rejeitar exclusao individual de recrutador com id invalido: #idInvalido"() {
        when:
        recrutadorService.excluirRecrutador(idInvalido)

        then:
        0 * recrutadorRepository._
        thrown(IllegalArgumentException)

        where:
        idInvalido << [null, 0L, -3L]
    }

    void "CA-12 / CA-13 / CA-14: deve listar apenas vagas da empresa do recrutador sem expor Empresa completa e candidatos de forma estritamente anonima"() {
        given:
        Empresa empresaCompleta = new Empresa(
                id: 1L,
                cnpj: "12345678000199",
                senha: "senhaEmpresaSecreta",
                descricaoEmpresarial: "Empresa",
                informacoes: new Informacoes(
                        nome: "PastelSoft",
                        email: "contato@pastelsoft.com",
                        cep: "01001000"
                )
        )
        Vaga vagaComEmpresaCompleta = new Vaga(
                id: 100L,
                titulo: "Dev Groovy",
                descricaoVaga: "Backend Groovy e SQL",
                estado: "SP",
                cidade: "São Paulo",
                empresa: empresaCompleta,
                skills: [new Skill(id: 1L, nome: "Groovy")]
        )
        empresaCompleta.vagas = [vagaComEmpresaCompleta]

        Recrutador recrutador = criarRecrutadorValido()
        recrutador.id = 5L
        recrutador.empresa = empresaCompleta

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
        1 * empresaService.buscarPorId(1L) >> empresaCompleta
        1 * candidatoService.listarCandidatosAnonimos() >> [candidatoAnonimo]

        vagas.size() == 1
        vagas[0].id == 100L
        vagas[0].titulo == "Dev Groovy"
        vagas[0].empresa == null
        vagaComEmpresaCompleta.empresa.is(empresaCompleta)

        candidatos.size() == 1
        candidatos[0].id == 1L
        !candidatos[0].hasProperty("cpf")
        !candidatos[0].hasProperty("senha")
        !candidatos[0].hasProperty("dataNascimento")
        !candidatos[0].hasProperty("informacoes")
    }

    void "CA-12 / CA-13: deve lancar NoSuchElementException ao consultar vagas ou candidatos para recrutador inexistente"() {
        when:
        recrutadorService.listarVagasDaEmpresaDoRecrutador(999L)

        then:
        1 * recrutadorRepository.buscarPorId(999L) >> null
        0 * empresaService._
        thrown(NoSuchElementException)

        when:
        recrutadorService.listarCandidatosAnonimosParaRecrutador(999L)

        then:
        1 * recrutadorRepository.buscarPorId(999L) >> null
        0 * candidatoService._
        thrown(NoSuchElementException)
    }

    void "deve lancar IllegalStateException quando empresaService ou candidatoService nao estiverem configurados"() {
        given:
        RecrutadorService serviceSemDependencias = new RecrutadorService(
                recrutadorRepository,
                empresaRepository
        )

        when:
        serviceSemDependencias.listarVagasDaEmpresaDoRecrutador(1L)

        then:
        0 * recrutadorRepository._
        IllegalStateException exVagas = thrown(IllegalStateException)
        exVagas.message == "Serviço de empresas não configurado para consulta de vagas."

        when:
        serviceSemDependencias.listarCandidatosAnonimosParaRecrutador(1L)

        then:
        0 * recrutadorRepository._
        IllegalStateException exCandidatos = thrown(IllegalStateException)
        exCandidatos.message == "Serviço de candidatos não configurado para consulta anônima."
    }

    void "CA-14: deve listar e buscar recrutadores como RecrutadorResumo sem expor senha, cpf, dataNascimento nem Empresa"() {
        given:
        Empresa empresa = new Empresa(id: 1L, cnpj: "12345678000199", senha: "segredo")
        Recrutador r = criarRecrutadorValido()
        r.id = 7L
        r.empresa = empresa
        r.senha = "pbkdf2_sha256\$65536\$salt\$hash"

        when:
        List<RecrutadorResumo> resumosPorEmpresa = recrutadorService.listarRecrutadoresPorEmpresa(1L)
        RecrutadorResumo resumoIndividual = recrutadorService.buscarResumoPorId(7L)

        then:
        1 * recrutadorRepository.listarPorEmpresaId(1L) >> [r]
        1 * recrutadorRepository.buscarPorId(7L) >> r

        resumosPorEmpresa.size() == 1
        resumosPorEmpresa[0].id == 7L
        resumosPorEmpresa[0].empresaId == 1L
        resumosPorEmpresa[0].nome == "Fernanda Lima"
        resumosPorEmpresa[0].areaRecrutamento == "Engenharia de Software"
        resumosPorEmpresa[0].email == "fernanda.lima@pastelsoft.com"
        !resumosPorEmpresa[0].hasProperty("senha")
        !resumosPorEmpresa[0].hasProperty("cpf")
        !resumosPorEmpresa[0].hasProperty("dataNascimento")
        !resumosPorEmpresa[0].hasProperty("empresa")

        resumoIndividual != null
        resumoIndividual.id == 7L
        resumoIndividual.empresaId == 1L
        !resumoIndividual.hasProperty("senha")
        !resumoIndividual.hasProperty("cpf")
        !resumoIndividual.hasProperty("dataNascimento")
        !resumoIndividual.hasProperty("empresa")
    }
}
