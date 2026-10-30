
package com.linketinder.api

import com.linketinder.model.Candidato
import com.linketinder.model.Informacoes
import com.linketinder.service.CandidatoService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import java.time.LocalDate
import java.time.format.DateTimeParseException

import static com.linketinder.api.HttpResponseUtil.responder

class CandidatoHttpHandler implements HttpHandler {

    private final CandidatoService candidatoService

    CandidatoHttpHandler(CandidatoService candidatoService) {
        this.candidatoService = candidatoService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {

        try {

            if (!HttpRequestUtil.validarPost(
                    exchange, "/api/candidatos")) {
                return
            }

            Map dados = HttpRequestUtil.lerJson(exchange)

            if (dados == null) {
                return
            }

            List<String> campos = [
                    "nome",
                    "sobrenome",
                    "dataNascimento",
                    "email",
                    "cpf",
                    "pais",
                    "estado",
                    "cep",
                    "descricaoPessoal",
                    "senha"
            ]

            List<String> ausentes =
                    HttpRequestUtil.camposInvalidos(dados, campos)

            if (!ausentes.isEmpty()) {

                responder(exchange, 400, [
                        erro: "Campos obrigatórios inválidos.",
                        campos: ausentes
                ])
                return
            }

            LocalDate nascimento

            try {

                nascimento = LocalDate.parse(
                        (String) dados.dataNascimento
                )

            } catch (DateTimeParseException e) {

                responder(exchange, 400, [
                        erro: "Data de nascimento inválida. Use AAAA-MM-DD."
                ])
                return
            }

            Informacoes informacoes = new Informacoes(
                    nome: dados.nome,
                    sobrenome: dados.sobrenome,
                    email: dados.email,
                    pais: dados.pais,
                    cep: dados.cep
            )

            Candidato candidato = new Candidato(
                    informacoes: informacoes,
                    cpf: dados.cpf,
                    estado: dados.estado,
                    dataNascimento: nascimento,
                    descricaoPessoal: dados.descricaoPessoal,
                    senha: dados.senha,
                    skills: []
            )

            try {

                candidatoService.cadastrarCandidato(candidato)

                if (candidato.id == null) {
                    throw new IllegalStateException(
                            "Cadastro sem ID retornado."
                    )
                }

                exchange.responseHeaders.set(
                        "Location",
                        "/api/candidatos/${candidato.id}"
                )

                responder(exchange, 201, [
                        id: candidato.id,
                        mensagem: "Candidato cadastrado com sucesso."
                ])

            } catch (IllegalStateException e) {

                System.err.println(
                        "Erro ao cadastrar candidato: ${e.message}"
                )

                e.printStackTrace(System.err)

                responder(exchange, 500, [
                        erro: "Não foi possível cadastrar o candidato."
                ])
            }

        } finally {
            exchange.close()
        }
    }
}
