package com.linketinder.api

import com.linketinder.model.Recrutador
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.RecrutadorService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.NoSuchElementException

import static com.linketinder.api.HttpResponseUtil.responder

class RecrutadorHttpHandler implements HttpHandler {

    private final RecrutadorService recrutadorService

    RecrutadorHttpHandler(RecrutadorService recrutadorService) {
        this.recrutadorService = recrutadorService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {

        try {
            if (!HttpRequestUtil.validarPost(exchange, "/api/recrutadores")) {
                return
            }

            Map dados = HttpRequestUtil.lerJson(exchange)

            if (dados == null) {
                return
            }

            List<String> camposObrigatorios = [
                    "nome",
                    "areaRecrutamento",
                    "descricao",
                    "email",
                    "cpf",
                    "dataNascimento",
                    "senha"
            ]

            List<String> invalidos =
                    HttpRequestUtil.camposInvalidos(dados, camposObrigatorios)

            if (!invalidos.isEmpty()) {
                responder(exchange, 400, [
                        erro: "Campos obrigatórios inválidos.",
                        campos: invalidos
                ])
                return
            }

            Object valorEmpresaId = dados["empresaId"]

            if (!(valorEmpresaId instanceof Integer || valorEmpresaId instanceof Long) ||
                    ((Number) valorEmpresaId).longValue() <= 0L) {

                responder(exchange, 400, [
                        erro: "empresaId deve ser um inteiro positivo."
                ])
                return
            }

            Long empresaId = ((Number) valorEmpresaId).longValue()

            LocalDate dataNascimento

            try {
                dataNascimento = LocalDate.parse(
                        ((String) dados.dataNascimento).trim()
                )
            } catch (DateTimeParseException ignored) {
                responder(exchange, 400, [
                        erro: "Data de nascimento inválida. Use AAAA-MM-DD."
                ])
                return
            }

            Recrutador recrutador = new Recrutador(
                    nome: (String) dados.nome,
                    areaRecrutamento: (String) dados.areaRecrutamento,
                    descricao: (String) dados.descricao,
                    email: (String) dados.email,
                    cpf: (String) dados.cpf,
                    dataNascimento: dataNascimento,
                    senha: (String) dados.senha
            )

            try {
                recrutadorService.cadastrarRecrutador(recrutador, empresaId)

                if (recrutador.id == null) {
                    throw new IllegalStateException(
                            "Cadastro sem ID retornado."
                    )
                }

                exchange.responseHeaders.set(
                        "Location",
                        "/api/recrutadores/${recrutador.id}"
                )

                responder(exchange, 201, [
                        id: recrutador.id,
                        empresaId: empresaId,
                        mensagem: "Recrutador cadastrado com sucesso."
                ])

            } catch (ConflitoDadosException e) {

                responder(exchange, 409, [
                        erro: e.message ?: "CPF ou e-mail já cadastrado para outro recrutador."
                ])

            } catch (NoSuchElementException ignored) {

                responder(exchange, 404, [
                        erro: "Empresa não encontrada."
                ])

            } catch (IllegalArgumentException e) {

                responder(exchange, 400, [
                        erro: e.message ?: "Dados inválidos para cadastro."
                ])

            } catch (IllegalStateException ignored) {

                responder(exchange, 500, [
                        erro: "Não foi possível cadastrar o recrutador."
                ])
            }

        } finally {
            exchange.close()
        }
    }
}
