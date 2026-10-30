
package apiTest

import com.linketinder.api.CandidatoHttpHandler
import com.linketinder.model.Candidato
import com.linketinder.service.CandidatoService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class CandidatoHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private CandidatoService candidatoService
    private String urlBase

    def setup() {

        candidatoService = Mock(CandidatoService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        servidor.createContext(
                "/api/candidatos",
                new CandidatoHttpHandler(candidatoService)
        )

        servidor.start()

        int porta = servidor.address.port
        urlBase = "http://127.0.0.1:${porta}"

        cliente = HttpClient.newHttpClient()
    }

    def cleanup() {
        servidor?.stop(0)
        cliente?.close()
    }

    def "deve cadastrar candidato e retornar 201 Created"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                json,
                "application/json"
        )

        then:
        1 * candidatoService.cadastrarCandidato(
                _ as Candidato
        ) >> { Candidato candidato ->
            candidato.id = 10L
        }

        resposta.statusCode() == 201

        resposta.headers()
                .firstValue("Location")
                .orElse("") == "/api/candidatos/10"

        Map corpo = lerJson(resposta.body())

        corpo.id == 10
        corpo.mensagem == "Candidato cadastrado com sucesso."
    }

    def "deve retornar 400 para campos obrigatorios ausentes"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"nome":"Sandubinha"}',
                "application/json"
        )

        then:
        0 * candidatoService._

        resposta.statusCode() == 400

        Map corpo = lerJson(resposta.body())
        corpo.erro == "Campos obrigatórios inválidos."
        corpo.campos.contains("email")
        corpo.campos.contains("cpf")
    }

    def "deve retornar 400 para JSON invalido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"nome":',
                "application/json"
        )

        then:
        0 * candidatoService._

        resposta.statusCode() == 400
        lerJson(resposta.body()).erro == "JSON inválido."
    }

    def "deve retornar 405 para metodo nao permitido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "GET",
                null,
                null
        )

        then:
        0 * candidatoService._

        resposta.statusCode() == 405

        resposta.headers()
                .firstValue("Allow")
                .orElse("") == "POST"
    }

    def "deve retornar 415 para tipo de conteudo incorreto"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                jsonValido(),
                "text/plain"
        )

        then:
        0 * candidatoService._

        resposta.statusCode() == 415
    }

    def "deve retornar 500 quando o cadastro falhar"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                json,
                "application/json"
        )

        then:
        1 * candidatoService.cadastrarCandidato(
                _ as Candidato
        ) >> {
            throw new IllegalStateException(
                    "Falha simulada no cadastro."
            )
        }

        resposta.statusCode() == 500

        lerJson(resposta.body()).erro ==
                "Não foi possível cadastrar o candidato."
    }

    private HttpResponse<String> enviar(
            String metodo,
            String corpo,
            String contentType
    ) {
        HttpRequest.Builder requisicao = HttpRequest
                .newBuilder()
                .uri(URI.create("${urlBase}/api/candidatos"))

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
                HttpResponse.BodyHandlers.ofString(
                        StandardCharsets.UTF_8
                )
        )
    }

    private static Map lerJson(String json) {
        return (Map) new JsonSlurper().parseText(json)
    }

    private static String jsonValido() {
        return '''
        {
            "nome": "Sandubinha",
            "sobrenome": "Silva",
            "dataNascimento": "2000-05-15",
            "email": "sandubinha@example.com",
            "cpf": "12345678909",
            "pais": "Brasil",
            "estado": "RJ",
            "cep": "20000000",
            "descricaoPessoal": "Desenvolvedor backend",
            "senha": "SenhaDeTeste123!"
        }
        '''
    }
}
