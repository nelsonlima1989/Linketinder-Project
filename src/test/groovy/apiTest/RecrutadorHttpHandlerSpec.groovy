package apiTest

import com.linketinder.api.RecrutadorHttpHandler
import com.linketinder.model.Recrutador
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.RecrutadorService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class RecrutadorHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private RecrutadorService recrutadorService
    private String urlBase

    void setup() {
        recrutadorService = Mock(RecrutadorService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        servidor.createContext(
                "/api/recrutadores",
                new RecrutadorHttpHandler(recrutadorService)
        )

        servidor.start()

        int porta = servidor.address.port
        urlBase = "http://127.0.0.1:${porta}"

        cliente = HttpClient.newHttpClient()
    }

    void cleanup() {
        servidor?.stop(0)
        cliente?.close()
    }

    void "CA-01 / CA-14: deve cadastrar recrutador e retornar 201 Created com Location sem expor senha, CPF ou dataNascimento"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json; charset=UTF-8"
        )

        then:
        1 * recrutadorService.cadastrarRecrutador(
                { Recrutador r ->
                    r.nome == "Fernanda Lima" &&
                            r.areaRecrutamento == "Engenharia de Software" &&
                            r.email == "fernanda.lima@pastelsoft.com" &&
                            r.cpf == "61234567890" &&
                            r.dataNascimento == LocalDate.of(1992, 4, 15) &&
                            r.senha == "SenhaSegura@2026"
                },
                1L
        ) >> { Recrutador r, Long empresaId ->
            r.id = 10L
        }
        0 * _

        resposta.statusCode() == 201
        resposta.headers().firstValue("Location").orElse("") == "/api/recrutadores/10"

        String textoCorpo = resposta.body()
        Map corpo = lerJson(textoCorpo)

        corpo.id == 10
        corpo.empresaId == 1
        corpo.mensagem == "Recrutador cadastrado com sucesso."
        !corpo.containsKey("senha")
        !corpo.containsKey("cpf")
        !corpo.containsKey("dataNascimento")
        !corpo.containsKey("email")
        !textoCorpo.contains("SenhaSegura@2026")
        !textoCorpo.contains("61234567890")
    }

    void "CA-05: deve retornar 400 para campos obrigatorios ausentes ou em branco"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"empresaId": 1, "nome": "   "}',
                "application/json"
        )

        then:
        0 * recrutadorService._

        resposta.statusCode() == 400
        Map corpo = lerJson(resposta.body())
        corpo.erro == "Campos obrigatórios inválidos."
        corpo.campos.containsAll([
                "nome",
                "areaRecrutamento",
                "descricao",
                "email",
                "cpf",
                "dataNascimento",
                "senha"
        ])
    }

    @Unroll
    void "CA-03: deve retornar 400 para empresaId invalido: #empresaId"() {
        given:
        String json = """
        {
            "empresaId": ${empresaId},
            "nome": "Fernanda Lima",
            "areaRecrutamento": "Engenharia de Software",
            "descricao": "Recrutadora técnica",
            "email": "fernanda.lima@pastelsoft.com",
            "cpf": "61234567890",
            "dataNascimento": "1992-04-15",
            "senha": "SenhaSegura@2026"
        }
        """

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        0 * recrutadorService._
        resposta.statusCode() == 400

        where:
        empresaId << [
                "0",
                "-1",
                "1.5",
                '"abc"',
                "null",
                "true"
        ]
    }

    void "CA-06: deve retornar 400 quando dataNascimento nao estiver no formato ISO AAAA-MM-DD"() {
        given:
        String json = '''
        {
            "empresaId": 1,
            "nome": "Fernanda Lima",
            "areaRecrutamento": "Engenharia de Software",
            "descricao": "Recrutadora técnica",
            "email": "fernanda.lima@pastelsoft.com",
            "cpf": "61234567890",
            "dataNascimento": "15/04/1992",
            "senha": "SenhaSegura@2026"
        }
        '''

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        0 * recrutadorService._
        resposta.statusCode() == 400
        lerJson(resposta.body()).erro == "Data de nascimento inválida. Use AAAA-MM-DD."
    }

    void "CA-05 / CA-06 / CA-07: deve retornar 400 quando service rejeitar dados invalidos (CPF, email, menor de 18 anos ou senha curta)"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * recrutadorService.cadastrarRecrutador(_ as Recrutador, 1L) >> {
            throw new IllegalArgumentException(
                    "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos."
            )
        }

        resposta.statusCode() == 400
        lerJson(resposta.body()).erro ==
                "Data de nascimento inválida. O recrutador deve ter pelo menos 18 anos."
    }

    void "CA-04: deve retornar 409 Conflict quando CPF ou email ja estiver cadastrado para outro recrutador"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * recrutadorService.cadastrarRecrutador(_ as Recrutador, 1L) >> {
            throw new ConflitoDadosException(
                    "CPF já cadastrado para outro recrutador."
            )
        }

        resposta.statusCode() == 409
        lerJson(resposta.body()).erro ==
                "CPF já cadastrado para outro recrutador."
    }

    void "CA-03: deve retornar 404 Not Found quando a empresa nao existir"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * recrutadorService.cadastrarRecrutador(_ as Recrutador, 1L) >> {
            throw new NoSuchElementException("Empresa não encontrada.")
        }

        resposta.statusCode() == 404
        lerJson(resposta.body()).erro == "Empresa não encontrada."
    }

    void "deve retornar 404 Not Found para subcaminho inexistente"() {
        given:
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/recrutadores/999"))
                .GET()
                .build()

        when:
        HttpResponse<String> resposta = cliente.send(
                requisicao,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )

        then:
        0 * recrutadorService._
        resposta.statusCode() == 404
        lerJson(resposta.body()).erro == "Recurso não encontrado."
    }

    void "deve retornar 405 Method Not Allowed com header Allow POST para metodo diferente de POST"() {
        when:
        HttpResponse<String> resposta = enviar("GET", null, null)

        then:
        0 * recrutadorService._
        resposta.statusCode() == 405
        resposta.headers().firstValue("Allow").orElse("") == "POST"
        lerJson(resposta.body()).erro == "Método não permitido."
    }

    void "deve retornar 415 Unsupported Media Type para Content-Type invalido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST", jsonValido(), "text/plain"
        )

        then:
        0 * recrutadorService._
        resposta.statusCode() == 415
        lerJson(resposta.body()).erro == "Content-Type deve ser application/json."
    }

    void "deve retornar 413 Content Too Large para corpo acima de 16 KiB"() {
        given:
        String corpoGrande = "A" * 17000

        when:
        HttpResponse<String> resposta = enviar(
                "POST", corpoGrande, "application/json"
        )

        then:
        0 * recrutadorService._
        resposta.statusCode() == 413
        lerJson(resposta.body()).erro == "Corpo da requisição muito grande."
    }

    void "deve retornar 400 para JSON malformado ou quando JSON for um array"() {
        when:
        HttpResponse<String> respostaInvalido = enviar(
                "POST", '{"empresaId":', "application/json"
        )
        HttpResponse<String> respostaArray = enviar(
                "POST", '[1, 2, 3]', "application/json"
        )

        then:
        0 * recrutadorService._
        respostaInvalido.statusCode() == 400
        lerJson(respostaInvalido.body()).erro == "JSON inválido."
        respostaArray.statusCode() == 400
        lerJson(respostaArray.body()).erro == "O JSON deve conter um objeto."
    }

    void "CA-14: deve retornar 500 quando persistencia falhar sem imprimir stack trace ou dados sensiveis no stderr"() {
        given:
        PrintStream errOriginal = System.err
        ByteArrayOutputStream errCapturado = new ByteArrayOutputStream()
        PrintStream streamErr = new PrintStream(errCapturado, true, "UTF-8")
        System.setErr(streamErr)

        when:
        HttpResponse<String> resposta
        try {
            resposta = enviar("POST", jsonValido(), "application/json")
        } finally {
            System.setErr(errOriginal)
            streamErr.close()
        }

        then:
        1 * recrutadorService.cadastrarRecrutador(_ as Recrutador, 1L) >> {
            throw new IllegalStateException("Falha simulada no banco de dados")
        }

        resposta.statusCode() == 500
        lerJson(resposta.body()).erro == "Não foi possível cadastrar o recrutador."
        errCapturado.toString("UTF-8").isEmpty()
    }

    private HttpResponse<String> enviar(
            String metodo,
            String corpo,
            String contentType
    ) {
        HttpRequest.Builder requisicao = HttpRequest
                .newBuilder()
                .uri(URI.create("${urlBase}/api/recrutadores"))

        if (contentType != null) {
            requisicao.header("Content-Type", contentType)
        }

        if (metodo == "POST") {
            requisicao.POST(
                    HttpRequest.BodyPublishers.ofString(
                            corpo ?: "",
                            StandardCharsets.UTF_8
                    )
            )
        } else {
            requisicao.GET()
        }

        return cliente.send(
                requisicao.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )
    }

    private static Map lerJson(String json) {
        return (Map) new JsonSlurper().parseText(json)
    }

    private static String jsonValido() {
        return '''
        {
            "empresaId": 1,
            "nome": "Fernanda Lima",
            "areaRecrutamento": "Engenharia de Software",
            "descricao": "Recrutadora técnica focada em vagas backend Java e Groovy",
            "email": "fernanda.lima@pastelsoft.com",
            "cpf": "61234567890",
            "dataNascimento": "1992-04-15",
            "senha": "SenhaSegura@2026"
        }
        '''
    }
}
