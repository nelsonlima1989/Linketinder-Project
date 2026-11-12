package integrationTest

import com.linketinder.api.CurtidaHttpHandler
import com.linketinder.api.MatchHttpHandler
import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.dao.CurtidaDAO
import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.MatchDAO
import com.linketinder.dao.RecrutadorDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.database.ConexaoProvider
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.CurtidaService
import com.linketinder.service.MatchService
import com.sun.net.httpserver.HttpServer
import groovy.json.JsonSlurper
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicLong

@Requires({ CurtidaMatchApiIntegrationSpec.isPostgresDisponivel() })
class CurtidaMatchApiIntegrationSpec extends Specification {

    @Shared
    static String jdbcUrl
    @Shared
    static String jdbcUser
    @Shared
    static String jdbcPassword

    private static final AtomicLong sequencia = new AtomicLong(System.currentTimeMillis() % 100000000L)

    private static String gerarCpf() {
        return String.format("%011d", Math.abs(sequencia.incrementAndGet()) % 100000000000L)
    }

    private static String gerarCnpj() {
        return String.format("%014d", Math.abs(sequencia.incrementAndGet()) % 100000000000000L)
    }

    private HttpServer servidor
    private HttpClient cliente
    private String urlBase
    private ConexaoProvider conexaoProvider

    static boolean isPostgresDisponivel() {
        jdbcUrl = System.getenv("DB_URL") ?:
                "jdbc:postgresql://${System.getenv('PGHOST') ?: 'localhost'}:${System.getenv('PGPORT') ?: '5432'}/${System.getenv('PGDATABASE') ?: 'postgres'}"
        jdbcUser = System.getenv("DB_USER") ?: (System.getenv("PGUSER") ?: "postgres")
        jdbcPassword = System.getenv("DB_PASSWORD") ?: (System.getenv("PGPASSWORD") ?: "")

        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)
            conn.close()
            return true
        } catch (Exception ignored) {
            return false
        }
    }

    void setup() {
        conexaoProvider = new ConexaoProvider() {
            @Override
            Connection obterConexao() throws SQLException {
                return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)
            }
        }

        iniciarServidor()
        cliente = HttpClient.newHttpClient()
    }

    void cleanup() {
        pararServidor()
        cliente?.close()
    }

    private void iniciarServidor() {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0)

        CandidatoCompetenciaDAO candidatoCompetenciaDAO = new CandidatoCompetenciaDAO(conexaoProvider)
        CurtidaDAO curtidaDAO = new CurtidaDAO(conexaoProvider)
        MatchDAO matchDAO = new MatchDAO(conexaoProvider, candidatoCompetenciaDAO)

        CurtidaService curtidaService = new CurtidaService(curtidaDAO, matchDAO, conexaoProvider)
        MatchService matchService = new MatchService(matchDAO)

        CurtidaHttpHandler curtidaHttpHandler = new CurtidaHttpHandler(curtidaService)
        MatchHttpHandler matchHttpHandler = new MatchHttpHandler(matchService)

        servidor.createContext("/api/curtidas/candidato-vaga", curtidaHttpHandler)
        servidor.createContext("/api/curtidas/empresa-candidato", curtidaHttpHandler)
        servidor.createContext("/api/matches", matchHttpHandler)

        servidor.start()
        urlBase = "http://127.0.0.1:${servidor.address.port}"
    }

    private void pararServidor() {
        servidor?.stop(0)
    }

    // -------------------------------------------------------------------------
    // 1. Curtidas HTTP nas duas ordens e criação automática de match
    // -------------------------------------------------------------------------
    void "1. deve registrar curtidas nas duas ordens via HTTP com criacao automatica de match pelos triggers"() {
        given: "Dados de teste isolados para ordem A (Candidato -> Vaga primeiro)"
        Long candA = criarCandidato("SP", "Dev Ordem A")
        Long empA = criarEmpresa("Brasil", "SP", "Empresa Ordem A")
        Long vagaA = criarVaga(empA, "Vaga A")
        Long recA = criarRecrutador(empA, "Recrutador A", "recA_${System.nanoTime()}@teste.com")

        when: "Candidato curte a vaga primeiro"
        HttpResponse<String> resp1 = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${candA}, "vagaId": ${vagaA}}""")

        then: "Curtida é 201 Created e match ainda é falso"
        resp1.statusCode() == 201
        Map corpo1 = parseJson(resp1.body())
        corpo1.match == false
        corpo1.matchId == null

        when: "Empresa retribui curtindo o candidato com recrutador responsável"
        HttpResponse<String> resp2 = postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${empA}, "candidatoId": ${candA}, "recrutadorId": ${recA}}""")

        then: "Match é detectado e registrado automaticamente"
        resp2.statusCode() == 201
        Map corpo2 = parseJson(resp2.body())
        corpo2.match == true
        corpo2.matchId != null

        and: "Dados de teste isolados para ordem B (Empresa -> Candidato primeiro)"
        Long candB = criarCandidato("RJ", "Dev Ordem B")
        Long empB = criarEmpresa("Brasil", "RJ", "Empresa Ordem B")
        Long vagaB = criarVaga(empB, "Vaga B")
        Long recB = criarRecrutador(empB, "Recrutador B", "recB_${System.nanoTime()}@teste.com")

        when: "Empresa curte o candidato primeiro"
        HttpResponse<String> resp3 = postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${empB}, "candidatoId": ${candB}, "recrutadorId": ${recB}}""")

        then: "Curtida de empresa é 201 Created e match ainda é falso"
        resp3.statusCode() == 201
        Map corpo3 = parseJson(resp3.body())
        corpo3.match == false

        when: "Candidato retribui curtindo a vaga da empresa"
        HttpResponse<String> resp4 = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${candB}, "vagaId": ${vagaB}}""")

        then: "Match é detectado e registrado automaticamente"
        resp4.statusCode() == 201
        Map corpo4 = parseJson(resp4.body())
        corpo4.match == true
        corpo4.matchId != null
    }

    // -------------------------------------------------------------------------
    // 2. Persistência após reiniciar o servidor
    // -------------------------------------------------------------------------
    void "2. deve manter matches e curtidas persistidos no PostgreSQL apos reiniciar o servidor HTTP"() {
        given:
        Long cand = criarCandidato("MG", "Dev Persistencia")
        Long emp = criarEmpresa("Brasil", "MG", "Empresa Persistencia")
        Long vaga = criarVaga(emp, "Vaga Persistente")
        Long rec = criarRecrutador(emp, "Recrutador P", "recP_${System.nanoTime()}@teste.com")

        postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga}}""")
        postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""")

        when: "O servidor HTTP é reiniciado completamente"
        pararServidor()
        iniciarServidor()

        and: "Consultamos os matches através da nova instância do servidor"
        HttpResponse<String> resp = get("/api/matches?candidatoId=${cand}&empresaId=${emp}")

        then: "Os dados persistiram intocados no PostgreSQL"
        resp.statusCode() == 200
        List matches = (List) parseJson(resp.body())
        matches.size() == 1
        matches[0].candidato.id == cand
        matches[0].empresa.id == emp
    }

    // -------------------------------------------------------------------------
    // 3. Match único para múltiplas vagas da mesma empresa
    // -------------------------------------------------------------------------
    void "3. deve preservar unicidade estrita de match quando candidato curte multiplas vagas da mesma empresa"() {
        given:
        Long cand = criarCandidato("RS", "Dev Multiplas Vagas")
        Long emp = criarEmpresa("Brasil", "RS", "Empresa Multi Vagas")
        Long vaga1 = criarVaga(emp, "Vaga Frontend")
        Long vaga2 = criarVaga(emp, "Vaga Fullstack")
        Long rec = criarRecrutador(emp, "Recrutador MV", "recMV_${System.nanoTime()}@teste.com")

        when: "Empresa curte candidato e candidato curte a primeira vaga"
        postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""")
        HttpResponse<String> rVaga1 = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga1}}""")

        then:
        parseJson(rVaga1.body()).match == true
        Long matchId = parseJson(rVaga1.body()).matchId

        when: "Candidato curte uma segunda vaga da mesma empresa"
        HttpResponse<String> rVaga2 = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga2}}""")

        then: "Curtida é aceita, mas continua associada ao mesmo match único"
        rVaga2.statusCode() == 201
        parseJson(rVaga2.body()).match == true
        parseJson(rVaga2.body()).matchId == matchId

        and: "Listagem de matches retorna estritamente 1 match para o par empresa-candidato"
        HttpResponse<String> rMatches = get("/api/matches?candidatoId=${cand}&empresaId=${emp}")
        List lista = (List) parseJson(rMatches.body())
        lista.size() == 1
        lista[0].id == matchId
    }

    // -------------------------------------------------------------------------
    // 4. Duplicidade 409, IDs inexistentes 404 e recrutador inválido 400
    // -------------------------------------------------------------------------
    void "4. deve mapear corretamente erros HTTP para 409, 404 e 400"() {
        given:
        Long cand = criarCandidato("PR", "Dev Erros")
        Long emp1 = criarEmpresa("Brasil", "PR", "Empresa 1")
        Long emp2 = criarEmpresa("Brasil", "PR", "Empresa 2")
        Long vaga = criarVaga(emp1, "Vaga Erros")
        Long recEmp1 = criarRecrutador(emp1, "Recrutador E1", "recE1_${System.nanoTime()}@teste.com")
        Long recEmp2 = criarRecrutador(emp2, "Recrutador E2", "recE2_${System.nanoTime()}@teste.com")

        when: "Registramos uma curtida válida"
        HttpResponse<String> r1 = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga}}""")

        then:
        r1.statusCode() == 201

        when: "Tentamos registrar a mesma curtida novamente (duplicidade)"
        HttpResponse<String> rDuplicada = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga}}""")

        then:
        rDuplicada.statusCode() == 409
        parseJson(rDuplicada.body()).erro == "Curtida já registrada anteriormente."

        when: "Tentamos curtir com candidato inexistente"
        HttpResponse<String> rInexistente = postJson("/api/curtidas/candidato-vaga", """{"candidatoId": 999999, "vagaId": ${vaga}}""")

        then:
        rInexistente.statusCode() == 404

        when: "Empresa tenta curtir candidato usando recrutador de outra empresa"
        HttpResponse<String> rRecInvalido = postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp1}, "candidatoId": ${cand}, "recrutadorId": ${recEmp2}}""")

        then: "Trigger do banco rejeita e API responde 400 Bad Request"
        rRecInvalido.statusCode() == 400
        parseJson(rRecInvalido.body()).erro.contains("O recrutador informado não pertence à empresa")
    }

    // -------------------------------------------------------------------------
    // 5. Rollback em falhas SQL e ausência de registros parciais
    // -------------------------------------------------------------------------
    void "5. deve garantir rollback integral da transacao em caso de falha de consistencia"() {
        given:
        Long cand = criarCandidato("SC", "Dev Rollback")
        Long emp = criarEmpresa("Brasil", "SC", "Empresa Rollback")

        when: "Tentativa de curtida de empresa com recrutador de outra empresa falha"
        HttpResponse<String> r = postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": 999999}""")

        then:
        r.statusCode() == 400 || r.statusCode() == 404

        and: "Nenhuma linha parcial foi gravada em curtidas_empresa_candidato"
        int totalCurtidas = contarLinhas("SELECT count(*) FROM curtidas_empresa_candidato WHERE empresa_id = ${emp} AND candidato_id = ${cand}")
        totalCurtidas == 0

        and: "Nenhum match foi registrado"
        int totalMatches = contarLinhas("SELECT count(*) FROM matches WHERE empresa_id = ${emp} AND candidato_id = ${cand}")
        totalMatches == 0
    }

    // -------------------------------------------------------------------------
    // 6. GET /api/matches com filtros e anonimato estrito
    // -------------------------------------------------------------------------
    void "6. deve retornar matches com filtros e garantir ausencia absoluta de nomes e contatos pessoais"() {
        given:
        Long cand = criarCandidato("BA", "Dev Anonimo")
        Long emp = criarEmpresa("Brasil", "BA", "Empresa Anonima")
        Long vaga = criarVaga(emp, "Vaga Anonima")
        Long rec = criarRecrutador(emp, "Recrutador Sigiloso", "sigilo_${System.nanoTime()}@teste.com")

        postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga}}""")
        postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""")

        when:
        HttpResponse<String> resp = get("/api/matches?candidatoId=${cand}&empresaId=${emp}")

        then:
        resp.statusCode() == 200
        String body = resp.body()

        // Anonimato estrito comprovado:
        !body.contains('"nome"')
        !body.contains('"email"')
        !body.contains('"whatsapp"')
        !body.contains('"telefone"')
        !body.contains('"cep"')
        !body.contains('"cpf"')
        !body.contains('"cnpj"')
        !body.contains('"senha"')

        List matches = (List) parseJson(body)
        matches.size() == 1
        matches[0].candidato.id == cand
        matches[0].candidato.estado == "BA"
        matches[0].empresa.id == emp
        matches[0].empresa.pais == "Brasil"
        matches[0].recrutador.id == rec
    }

    // -------------------------------------------------------------------------
    // 7. Recrutador excluído com referência NULL (ON DELETE SET NULL)
    // -------------------------------------------------------------------------
    void "7. deve preservar match e curtida com recrutador nulo apos exclusao do recrutador"() {
        given:
        Long cand = criarCandidato("ES", "Dev Set Null")
        Long emp = criarEmpresa("Brasil", "ES", "Empresa Set Null")
        Long vaga = criarVaga(emp, "Vaga Set Null")
        Long rec = criarRecrutador(emp, "Recrutador Temporario", "recTemp_${System.nanoTime()}@teste.com")

        postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga}}""")
        postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""")

        when: "Excluímos o recrutador do banco de dados (simulando demissão)"
        executarSql("DELETE FROM recrutadores WHERE id = ${rec}")

        and: "Consultamos os matches via API HTTP"
        HttpResponse<String> resp = get("/api/matches?candidatoId=${cand}&empresaId=${emp}")

        then: "O match permanece íntegro com recrutador nulo"
        resp.statusCode() == 200
        List matches = (List) parseJson(resp.body())
        matches.size() == 1
        matches[0].recrutador == null
        matches[0].empresa.id == emp
        matches[0].candidato.id == cand
    }

    // -------------------------------------------------------------------------
    // 8. Concorrência entre curtidas, sem duplicação de matches
    // -------------------------------------------------------------------------
    void "8. deve tratar concorrencia massiva entre curtidas simultaneas sem gerar duplicacao de matches"() {
        given:
        Long cand = criarCandidato("DF", "Dev Concorrente")
        Long emp = criarEmpresa("Brasil", "DF", "Empresa Concorrente")
        Long vaga1 = criarVaga(emp, "Vaga Conc 1")
        Long vaga2 = criarVaga(emp, "Vaga Conc 2")
        Long rec = criarRecrutador(emp, "Recrutador Conc", "recConc_${System.nanoTime()}@teste.com")

        ExecutorService executor = Executors.newFixedThreadPool(4)
        List<Callable<HttpResponse<String>>> tarefas = [
                { postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga1}}""") },
                { postJson("/api/curtidas/candidato-vaga", """{"candidatoId": ${cand}, "vagaId": ${vaga2}}""") },
                { postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""") },
                { postJson("/api/curtidas/empresa-candidato", """{"empresaId": ${emp}, "candidatoId": ${cand}, "recrutadorId": ${rec}}""") }
        ]

        when: "Disparamos as requisições simultaneamente em threads paralelas"
        List<Future<HttpResponse<String>>> futuros = executor.invokeAll(tarefas)
        executor.shutdown()

        then: "Nenhum deadlock ocorre e todas as threads terminam"
        futuros.each { Future<HttpResponse<String>> f ->
            HttpResponse<String> r = f.get()
            assert r.statusCode() in [201, 409]
        }

        and: "Existe rigorosamente apenas 1 match registrado no PostgreSQL para o par"
        int totalMatches = contarLinhas("SELECT count(*) FROM matches WHERE empresa_id = ${emp} AND candidato_id = ${cand}")
        totalMatches == 1
    }

    // -------------------------------------------------------------------------
    // Utilitários de Teste e Criação de Entidades Isoladas
    // -------------------------------------------------------------------------
    private HttpResponse<String> postJson(String caminho, String json) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}${caminho}"))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build()
        return cliente.send(req, HttpResponse.BodyHandlers.ofString())
    }

    private HttpResponse<String> get(String caminho) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("${urlBase}${caminho}"))
                .GET()
                .build()
        return cliente.send(req, HttpResponse.BodyHandlers.ofString())
    }

    private Object parseJson(String json) {
        return new JsonSlurper().parseText(json)
    }

    private Long criarCandidato(String estado, String descricao) {
        String cpf = gerarCpf()
        String sql = """
            INSERT INTO candidatos (nome, sobrenome, data_nascimento, email, cpf, pais, estado, cep, descricao_pessoal, senha)
            VALUES ('Cand', 'Teste', '1995-01-01', ?, ?, 'Brasil', ?, '01001-000', ?, 'senhaHash')
            RETURNING id
        """
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "cand_${cpf}@teste.com")
            ps.setString(2, cpf)
            ps.setString(3, estado)
            ps.setString(4, descricao)
            try (ResultSet rs = ps.executeQuery()) {
                rs.next()
                return rs.getLong("id")
            }
        }
    }

    private Long criarEmpresa(String pais, String estado, String descricao) {
        String cnpj = gerarCnpj()
        String sql = """
            INSERT INTO empresas (nome, cnpj, email, descricao, pais, estado, cep, senha)
            VALUES ('Empresa Teste', ?, ?, ?, ?, ?, '01001-000', 'senhaHash')
            RETURNING id
        """
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cnpj)
            ps.setString(2, "emp_${cnpj}@teste.com")
            ps.setString(3, descricao)
            ps.setString(4, pais)
            ps.setString(5, estado)
            try (ResultSet rs = ps.executeQuery()) {
                rs.next()
                return rs.getLong("id")
            }
        }
    }

    private Long criarVaga(Long empresaId, String titulo) {
        String sql = """
            INSERT INTO vagas (empresa_id, nome, estado, cidade, descricao)
            VALUES (?, ?, 'SP', 'Sao Paulo', 'Descricao Vaga Teste')
            RETURNING id
        """
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, empresaId)
            ps.setString(2, titulo)
            try (ResultSet rs = ps.executeQuery()) {
                rs.next()
                return rs.getLong("id")
            }
        }
    }

    private Long criarRecrutador(Long empresaId, String nome, String email, String cpf = null) {
        String cpfValido = cpf ?: gerarCpf()
        String sql = """
            INSERT INTO recrutadores (empresa_id, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha)
            VALUES (?, ?, 'Tecnologia', 'Descricao Recrutador', ?, ?, '1990-05-10', 'senhaHash')
            RETURNING id
        """
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, empresaId)
            ps.setString(2, nome)
            ps.setString(3, email)
            ps.setString(4, cpfValido)
            try (ResultSet rs = ps.executeQuery()) {
                rs.next()
                return rs.getLong("id")
            }
        }
    }

    private int contarLinhas(String sql) {
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next()
            return rs.getInt(1)
        }
    }

    private void executarSql(String sql) {
        try (Connection c = conexaoProvider.obterConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate()
        }
    }
}
