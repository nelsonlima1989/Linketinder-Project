package apiTest

import com.linketinder.api.CurtidaHttpHandler
import com.linketinder.model.CurtidaResultadoDTO
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.CurtidaService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.NoSuchElementException

class CurtidaHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private CurtidaService curtidaService
    private String urlBase

    void setup() {
        curtidaService = Mock(CurtidaService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        CurtidaHttpHandler handler = new CurtidaHttpHandler(curtidaService)

        servidor.createContext("/api/curtidas/candidato-vaga", handler)
        servidor.createContext("/api/curtidas/empresa-candidato", handler)

        servidor.start()

        int porta = servidor.address.port
        urlBase = "http://127.0.0.1:${porta}"
        cliente = HttpClient.newHttpClient()
    }

    void cleanup() {
        servidor?.stop(0)
        cliente?.close()
    }

    void "deve registrar curtida de candidato em vaga e retornar 201 Created com dados e match"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)
        CurtidaResultadoDTO dto = new CurtidaResultadoDTO(
                candidatoId: 1L,
                vagaId: 2L,
                dataCurtida: agora,
                match: true,
                matchId: 42L,
                mensagem: "Curtida registrada com sucesso."
        )

        String jsonEnvio = '{"candidatoId": 1, "vagaId": 2}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/candidato-vaga", jsonEnvio)

        then:
        1 * curtidaService.curtirVaga(1L, 2L) >> dto

        resposta.statusCode() == 201
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.candidatoId == 1
        corpo.vagaId == 2
        corpo.match == true
        corpo.matchId == 42
        corpo.mensagem == "Curtida registrada com sucesso."
    }

    void "deve registrar curtida de empresa em candidato e retornar 201 Created com dados e status de match"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)
        CurtidaResultadoDTO dto = new CurtidaResultadoDTO(
                empresaId: 10L,
                candidatoId: 20L,
                recrutadorId: 30L,
                dataCurtida: agora,
                match: false,
                matchId: null,
                mensagem: "Curtida registrada com sucesso."
        )

        String jsonEnvio = '{"empresaId": 10, "candidatoId": 20, "recrutadorId": 30}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/empresa-candidato", jsonEnvio)

        then:
        1 * curtidaService.curtirCandidato(10L, 20L, 30L) >> dto

        resposta.statusCode() == 201
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.empresaId == 10
        corpo.candidatoId == 20
        corpo.recrutadorId == 30
        corpo.match == false
        corpo.matchId == null
    }

    void "deve retornar 400 quando parametros obrigatorios para curtida de candidato forem invalidos"() {
        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/candidato-vaga", '{"candidatoId": 0, "vagaId": "invalido"}')

        then:
        0 * curtidaService.curtirVaga(_, _, _)
        resposta.statusCode() == 400
        resposta.body().contains("candidatoId e vagaId devem ser inteiros positivos.")
    }

    void "deve retornar 400 quando parametros obrigatorios para curtida de empresa forem invalidos"() {
        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/empresa-candidato", '{"empresaId": 1, "candidatoId": 2}')

        then:
        0 * curtidaService.curtirCandidato(_, _, _, _)
        resposta.statusCode() == 400
        resposta.body().contains("empresaId, candidatoId e recrutadorId devem ser inteiros positivos.")
    }

    void "deve retornar 415 quando Content-Type nao for application/json"() {
        when:
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/curtidas/candidato-vaga"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString('{"candidatoId": 1, "vagaId": 2}'))
                .build()
        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString())

        then:
        resposta.statusCode() == 415
    }

    void "deve retornar 405 quando metodo HTTP nao for POST"() {
        when:
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/curtidas/candidato-vaga"))
                .GET()
                .build()
        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString())

        then:
        resposta.statusCode() == 405
        resposta.headers().firstValue("Allow").orElse("") == "POST"
    }

    void "deve retornar 409 Conflict quando curtida ja existir"() {
        given:
        String json = '{"candidatoId": 1, "vagaId": 2}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/candidato-vaga", json)

        then:
        1 * curtidaService.curtirVaga(1L, 2L) >> { throw new ConflitoDadosException("Curtida já registrada anteriormente.") }

        resposta.statusCode() == 409
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.erro == "Curtida já registrada anteriormente."
    }

    void "deve retornar 404 Not Found quando entidade referenciada nao existir"() {
        given:
        String json = '{"candidatoId": 999, "vagaId": 2}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/candidato-vaga", json)

        then:
        1 * curtidaService.curtirVaga(999L, 2L) >> { throw new NoSuchElementException("Recurso não encontrado.") }

        resposta.statusCode() == 404
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.erro == "Recurso não encontrado."
    }

    void "deve retornar 400 Bad Request quando recrutador for de outra empresa"() {
        given:
        String json = '{"empresaId": 1, "candidatoId": 2, "recrutadorId": 99}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/empresa-candidato", json)

        then:
        1 * curtidaService.curtirCandidato(1L, 2L, 99L) >> { throw new IllegalArgumentException("O recrutador informado não pertence à empresa.") }

        resposta.statusCode() == 400
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.erro == "O recrutador informado não pertence à empresa."
    }

    void "deve retornar 500 sem expor detalhes SQL em caso de erro inesperado"() {
        given:
        String json = '{"candidatoId": 1, "vagaId": 2}'

        when:
        HttpResponse<String> resposta = enviarPost("/api/curtidas/candidato-vaga", json)

        then:
        1 * curtidaService.curtirVaga(1L, 2L) >> { throw new IllegalStateException("SQLException: Connection reset") }

        resposta.statusCode() == 500
        Map corpo = (Map) new JsonSlurper().parseText(resposta.body())
        corpo.erro == "Não foi possível registrar a curtida."
        !resposta.body().contains("SQLException")
    }

    private HttpResponse<String> enviarPost(String caminho, String json) {
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}${caminho}"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build()
        return cliente.send(requisicao, HttpResponse.BodyHandlers.ofString())
    }
}
