
package apiTest

import com.linketinder.api.VagaHttpHandler
import com.linketinder.model.Vaga
import com.linketinder.service.VagaService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class VagaHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private VagaService vagaService
    private String urlBase

    def setup() {

        vagaService = Mock(VagaService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        servidor.createContext(
                "/api/vagas",
                new VagaHttpHandler(vagaService)
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

    def "deve cadastrar vaga e retornar 201 Created"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * vagaService.cadastrarVaga(
                _ as Vaga, 6L
        ) >> { Vaga vaga, Long empresaId ->
            vaga.id = 10L
        }

        resposta.statusCode() == 201

        resposta.headers()
                .firstValue("Location")
                .orElse("") == "/api/vagas/10"

        Map corpo = lerJson(resposta.body())

        corpo.id == 10
        corpo.empresaId == 6
        corpo.mensagem == "Vaga cadastrada com sucesso."
    }

    def "deve retornar 400 para campos obrigatorios ausentes"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"empresaId":6}',
                "application/json"
        )

        then:
        0 * vagaService._

        resposta.statusCode() == 400

        Map corpo = lerJson(resposta.body())
        corpo.erro == "Campos obrigatórios inválidos."
        corpo.campos.contains("titulo")
        corpo.campos.contains("descricaoVaga")
    }

    def "deve retornar 400 para JSON invalido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"empresaId":',
                "application/json"
        )

        then:
        0 * vagaService._

        resposta.statusCode() == 400

        lerJson(resposta.body()).erro == "JSON inválido."
    }

    def "deve retornar 405 para metodo nao permitido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "GET", null, null
        )

        then:
        0 * vagaService._

        resposta.statusCode() == 405

        resposta.headers()
                .firstValue("Allow")
                .orElse("") == "POST"
    }

    def "deve retornar 415 para Content-Type incorreto"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST", jsonValido(), "text/plain"
        )

        then:
        0 * vagaService._

        resposta.statusCode() == 415
    }

    def "deve retornar 404 quando empresa nao existir"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * vagaService.cadastrarVaga(
                _ as Vaga, 6L
        ) >> {
            throw new NoSuchElementException(
                    "Empresa não encontrada."
            )
        }

        resposta.statusCode() == 404

        lerJson(resposta.body()).erro ==
                "Empresa não encontrada."
    }

    def "deve retornar 500 quando cadastro da vaga falhar"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * vagaService.cadastrarVaga(
                _ as Vaga, 6L
        ) >> {
            throw new IllegalStateException(
                    "Falha simulada no cadastro."
            )
        }

        resposta.statusCode() == 500

        lerJson(resposta.body()).erro ==
                "Não foi possível cadastrar a vaga."
    }

    private HttpResponse<String> enviar(
            String metodo,
            String corpo,
            String contentType
    ) {
        HttpRequest.Builder requisicao = HttpRequest
                .newBuilder()
                .uri(URI.create("${urlBase}/api/vagas"))

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
            "empresaId": 6,
            "titulo": "Desenvolvedor Backend Júnior",
            "descricaoVaga": "Desenvolvimento de APIs REST.",
            "estado": "RJ",
            "cidade": "Rio de Janeiro"
        }
        '''
    }



    def "deve retornar 400 quando empresaId nao for informado"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '''
            {
                "titulo": "Desenvolvedor Backend",
                "descricaoVaga": "Desenvolvimento de APIs",
                "estado": "RJ",
                "cidade": "Rio de Janeiro"
            }
            ''',
                "application/json"
        )

        then:
        resposta.statusCode() == 400
        lerJson(resposta.body()).erro ==
                "empresaId deve ser um inteiro positivo."

        0 * vagaService._
    }


    def "deve retornar 400 para empresaId invalido: #empresaId"() {
        given:
        String json = """
    {
        "empresaId": ${empresaId},
        "titulo": "Desenvolvedor Backend",
        "descricaoVaga": "Desenvolvimento de APIs",
        "estado": "RJ",
        "cidade": "Rio de Janeiro"
    }
    """

        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                json,
                "application/json"
        )

        then:
        resposta.statusCode() == 400
        lerJson(resposta.body()).erro ==
                "empresaId deve ser um inteiro positivo."

        0 * vagaService._

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



    def "deve retornar 400 quando JSON for um array"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '["vaga1", "vaga2"]',
                "application/json"
        )

        then:
        resposta.statusCode() == 400
        lerJson(resposta.body()).erro ==
                "O JSON deve conter um objeto."

        0 * vagaService._
    }



    def "deve retornar 400 para campos vazios"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '''
            {
                "empresaId": 6,
                "titulo": "   ",
                "descricaoVaga": "",
                "estado": "RJ",
                "cidade": "Rio de Janeiro"
            }
            ''',
                "application/json"
        )

        then:
        resposta.statusCode() == 400

        Map corpo = lerJson(resposta.body())

        corpo.campos.contains("titulo")
        corpo.campos.contains("descricaoVaga")

        0 * vagaService._
    }



    def "deve retornar 413 para corpo acima de 16KB"() {
        given:
        String corpoGrande = "A" * 17000

        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                corpoGrande,
                "application/json"
        )

        then:
        resposta.statusCode() == 413

        lerJson(resposta.body()).erro ==
                "Corpo da requisição muito grande."

        0 * vagaService._
    }



    def "deve retornar 404 para caminho inexistente"() {
        given:
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/vagas/999"))
                .GET()
                .build()

        when:
        HttpResponse<String> resposta = cliente.send(
                requisicao,
                HttpResponse.BodyHandlers.ofString(
                        StandardCharsets.UTF_8
                )
        )

        then:
        resposta.statusCode() == 404
        lerJson(resposta.body()).erro ==
                "Recurso não encontrado."

        0 * vagaService._
    }



}
