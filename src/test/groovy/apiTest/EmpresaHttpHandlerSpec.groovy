
package apiTest

import com.linketinder.api.EmpresaHttpHandler
import com.linketinder.model.Empresa
import com.linketinder.service.EmpresaService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class EmpresaHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private EmpresaService empresaService
    private String urlBase

    def setup() {

        empresaService = Mock(EmpresaService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        servidor.createContext(
                "/api/empresas",
                new EmpresaHttpHandler(empresaService)
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

    def "deve cadastrar empresa e retornar 201 Created"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * empresaService.cadastrarEmpresa(
                _ as Empresa
        ) >> { Empresa empresa ->
            empresa.id = 10L
        }

        resposta.statusCode() == 201

        resposta.headers()
                .firstValue("Location")
                .orElse("") == "/api/empresas/10"

        Map corpo = lerJson(resposta.body())

        corpo.id == 10
        corpo.mensagem == "Empresa cadastrada com sucesso."
    }

    def "deve retornar 400 para campos obrigatorios ausentes"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"nome":"ZG Tecnologia"}',
                "application/json"
        )

        then:
        0 * empresaService._

        resposta.statusCode() == 400

        Map corpo = lerJson(resposta.body())
        corpo.erro == "Campos obrigatórios inválidos."
        corpo.campos.contains("cnpj")
        corpo.campos.contains("email")
    }

    def "deve retornar 400 para JSON invalido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "POST",
                '{"nome":',
                "application/json"
        )

        then:
        0 * empresaService._

        resposta.statusCode() == 400
        lerJson(resposta.body()).erro == "JSON inválido."
    }

    def "deve retornar 405 para metodo nao permitido"() {
        when:
        HttpResponse<String> resposta = enviar(
                "GET", null, null
        )

        then:
        0 * empresaService._

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
        0 * empresaService._

        resposta.statusCode() == 415
    }

    def "deve retornar 500 quando o cadastro falhar"() {
        given:
        String json = jsonValido()

        when:
        HttpResponse<String> resposta = enviar(
                "POST", json, "application/json"
        )

        then:
        1 * empresaService.cadastrarEmpresa(
                _ as Empresa
        ) >> {
            throw new IllegalStateException(
                    "Falha simulada no cadastro."
            )
        }

        resposta.statusCode() == 500

        lerJson(resposta.body()).erro ==
                "Não foi possível cadastrar a empresa."
    }

    private HttpResponse<String> enviar(
            String metodo,
            String corpo,
            String contentType
    ) {
        HttpRequest.Builder requisicao = HttpRequest
                .newBuilder()
                .uri(URI.create("${urlBase}/api/empresas"))

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
            "nome": "ZG Tecnologia",
            "cnpj": "11222333000181",
            "email": "contato@example.com",
            "descricaoEmpresarial": "Empresa de tecnologia",
            "pais": "Brasil",
            "estado": "RJ",
            "cep": "20000000",
            "senha": "SenhaTeste123!"
        }
        '''
    }
}
