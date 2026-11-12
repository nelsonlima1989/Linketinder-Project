package apiTest

import com.linketinder.api.MatchHttpHandler
import com.linketinder.model.CandidatoAnonimo
import com.linketinder.model.EmpresaAnonima
import com.linketinder.model.MatchAnonimo
import com.linketinder.model.RecrutadorAnonimo
import com.linketinder.model.Skill
import com.linketinder.service.MatchService
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

class MatchHttpHandlerSpec extends Specification {

    private HttpServer servidor
    private HttpClient cliente
    private MatchService matchService
    private String urlBase

    void setup() {
        matchService = Mock(MatchService)

        servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        )

        servidor.createContext("/api/matches", new MatchHttpHandler(matchService))
        servidor.start()

        int porta = servidor.address.port
        urlBase = "http://127.0.0.1:${porta}"
        cliente = HttpClient.newHttpClient()
    }

    void cleanup() {
        servidor?.stop(0)
        cliente?.close()
    }

    void "deve listar matches e retornar 200 OK sob anonimato estrito sem nomes nem contatos"() {
        given:
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC)
        MatchAnonimo m = new MatchAnonimo(
                id: 1L,
                dataMatch: agora,
                candidato: new CandidatoAnonimo(
                        id: 10L,
                        estado: "SP",
                        descricaoPessoal: "Dev Groovy",
                        skills: [new Skill(id: 1L, nome: "Groovy"), new Skill(id: 2L, nome: "PostgreSQL")]
                ),
                empresa: new EmpresaAnonima(
                        id: 20L,
                        pais: "Brasil",
                        estado: "SP",
                        descricaoEmpresarial: "Empresa Inovadora"
                ),
                recrutador: new RecrutadorAnonimo(
                        id: 30L,
                        areaRecrutamento: "TI"
                )
        )

        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches"))
                .GET()
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        1 * matchService.listarMatches(null, null) >> [m]

        resp.statusCode() == 200
        List lista = (List) new JsonSlurper().parseText(resp.body())
        lista.size() == 1
        Map item = (Map) lista[0]

        item.id == 1
        item.candidato.id == 10
        item.candidato.estado == "SP"
        item.candidato.descricaoPessoal == "Dev Groovy"
        item.candidato.skills == ["Groovy", "PostgreSQL"]

        item.empresa.id == 20
        item.empresa.pais == "Brasil"
        item.empresa.estado == "SP"
        item.empresa.descricaoEmpresarial == "Empresa Inovadora"

        item.recrutador.id == 30
        item.recrutador.areaRecrutamento == "TI"

        // Asserções estritas de privacidade e anonimato:
        !resp.body().contains('"nome"')
        !resp.body().contains('"email"')
        !resp.body().contains('"whatsapp"')
        !resp.body().contains('"telefone"')
        !resp.body().contains('"cpf"')
        !resp.body().contains('"cnpj"')
        !resp.body().contains('"cep"')
        !resp.body().contains('"senha"')
    }

    void "deve retornar recrutador nulo quando recrutadorId for nulo no match"() {
        given:
        MatchAnonimo m = new MatchAnonimo(
                id: 2L,
                dataMatch: OffsetDateTime.now(ZoneOffset.UTC),
                candidato: new CandidatoAnonimo(id: 1L, estado: "RJ"),
                empresa: new EmpresaAnonima(id: 2L, pais: "Brasil", estado: "RJ"),
                recrutador: null
        )

        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches"))
                .GET()
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        1 * matchService.listarMatches(null, null) >> [m]

        resp.statusCode() == 200
        List lista = (List) new JsonSlurper().parseText(resp.body())
        lista[0].recrutador == null
    }

    void "deve repassar filtros de query params candidatoId e empresaId com sucesso"() {
        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches?candidatoId=5&empresaId=7"))
                .GET()
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        1 * matchService.listarMatches(5L, 7L) >> []

        resp.statusCode() == 200
        resp.body() == "[]"
    }

    void "deve retornar 400 quando parametros de busca forem invalidos"() {
        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches?candidatoId=invalido"))
                .GET()
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        0 * matchService.listarMatches(_, _)
        resp.statusCode() == 400
        resp.body().contains("candidatoId deve ser um inteiro positivo.")
    }

    void "deve retornar 405 Method Not Allowed quando requisicao nao for GET"() {
        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        resp.statusCode() == 405
        resp.headers().firstValue("Allow").orElse("") == "GET"
    }

    void "deve retornar 500 sem expor detalhes internos quando service falhar"() {
        when:
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}/api/matches"))
                .GET()
                .build()
        HttpResponse<String> resp = cliente.send(req, HttpResponse.BodyHandlers.ofString())

        then:
        1 * matchService.listarMatches(null, null) >> { throw new IllegalStateException("SQLException: timeout") }

        resp.statusCode() == 500
        Map corpo = (Map) new JsonSlurper().parseText(resp.body())
        corpo.erro == "Não foi possível consultar os matches."
        !resp.body().contains("SQLException")
    }
}
