
package com.linketinder.api

import com.linketinder.dao.CandidatoCompetenciaDAO
import com.linketinder.dao.CandidatoDAO
import com.linketinder.dao.CurtidaDAO
import com.linketinder.dao.EmpresaDAO
import com.linketinder.dao.MatchDAO
import com.linketinder.dao.RecrutadorDAO
import com.linketinder.dao.VagaDAO
import com.linketinder.dao.VagaCompetenciaDAO
import com.linketinder.database.ConexaoPostgresProvider
import com.linketinder.database.ConexaoProvider
import com.linketinder.service.CandidatoService
import com.linketinder.service.CurtidaService
import com.linketinder.service.EmpresaService
import com.linketinder.service.MatchService
import com.linketinder.service.RecrutadorService
import com.linketinder.service.VagaService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class ApiServer {

    static void main(String[] args) {

        // Configuração da conexão com o PostgreSQL
        ConexaoProvider conexaoProvider =
                new ConexaoPostgresProvider()

        // Inicialização dos DAOs
        CandidatoDAO candidatoDAO =
                new CandidatoDAO(conexaoProvider)

        CandidatoCompetenciaDAO candidatoCompetenciaDAO =
                new CandidatoCompetenciaDAO(conexaoProvider)

        EmpresaDAO empresaDAO =
                new EmpresaDAO(conexaoProvider)

        VagaDAO vagaDAO =
                new VagaDAO(conexaoProvider, empresaDAO)

        VagaCompetenciaDAO vagaCompetenciaDAO =
                new VagaCompetenciaDAO(conexaoProvider)

        RecrutadorDAO recrutadorDAO =
                new RecrutadorDAO(conexaoProvider, empresaDAO)

        CurtidaDAO curtidaDAO =
                new CurtidaDAO(conexaoProvider)

        MatchDAO matchDAO =
                new MatchDAO(conexaoProvider, candidatoCompetenciaDAO)

        // Inicialização do Service de candidatos
        CandidatoService candidatoService =
                new CandidatoService(
                        candidatoDAO,
                        candidatoCompetenciaDAO
                )

        // Inicialização do Service de empresas
        EmpresaService empresaService =
                new EmpresaService(
                        empresaDAO,
                        vagaDAO,
                        vagaCompetenciaDAO,
                        recrutadorDAO
                )
        // inicialização do service de vagas
        VagaService vagaService =
                new VagaService(
                        vagaDAO,
                        empresaDAO
                )

        RecrutadorService recrutadorService =
                new RecrutadorService(
                        recrutadorDAO,
                        empresaDAO,
                        empresaService,
                        candidatoService
                )

        CurtidaService curtidaService =
                new CurtidaService(
                        curtidaDAO,
                        matchDAO,
                        conexaoProvider
                )

        MatchService matchService =
                new MatchService(matchDAO)

        // Configuração do servidor HTTP
        InetSocketAddress endereco =
                new InetSocketAddress("127.0.0.1", 8080)

        HttpServer servidor = HttpServer.create(endereco, 0)

        // Endpoint de saúde
        servidor.createContext("/api/health", new HttpHandler() {

            @Override
            void handle(HttpExchange exchange) throws IOException {

                try {
                    if (exchange.requestMethod != "GET") {

                        exchange.responseHeaders.set(
                                "Allow", "GET"
                        )

                        exchange.sendResponseHeaders(405, -1)
                        return
                    }

                    byte[] resposta = '{"status":"UP"}'
                            .getBytes(StandardCharsets.UTF_8)

                    exchange.responseHeaders.set(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                    )

                    exchange.sendResponseHeaders(
                            200,
                            resposta.length
                    )

                    exchange.responseBody.write(resposta)

                } finally {
                    exchange.close()
                }
            }
        })

        // Endpoint de cadastro de candidatos
        servidor.createContext(
                "/api/candidatos",
                new CandidatoHttpHandler(candidatoService)
        )

        // Endpoint de cadastro de empresas
        servidor.createContext(
                "/api/empresas",
                new EmpresaHttpHandler(empresaService)
        )

        //Endpoint de cadastro vagas
        servidor.createContext(
                "/api/vagas",
                new VagaHttpHandler(vagaService)
        )

        // Endpoint de cadastro de recrutadores
        servidor.createContext(
                "/api/recrutadores",
                new RecrutadorHttpHandler(recrutadorService)
        )

        CurtidaHttpHandler curtidaHttpHandler =
                new CurtidaHttpHandler(curtidaService)

        // Endpoints de curtidas
        servidor.createContext(
                "/api/curtidas/candidato-vaga",
                curtidaHttpHandler
        )

        servidor.createContext(
                "/api/curtidas/empresa-candidato",
                curtidaHttpHandler
        )

        // Endpoint de consulta de matches sob anonimato estrito
        servidor.createContext(
                "/api/matches",
                new MatchHttpHandler(matchService)
        )

        // Inicialização do servidor
        servidor.start()

        println "API Linketinder iniciada em http://localhost:8080"
        println "GET  /api/health"
        println "POST /api/candidatos"
        println "POST /api/empresas"
        println "POST /api/vagas"
        println "POST /api/recrutadores"
        println "POST /api/curtidas/candidato-vaga"
        println "POST /api/curtidas/empresa-candidato"
        println "GET  /api/matches"
    }
}
