
package com.linketinder.api

import com.linketinder.model.Vaga
import com.linketinder.service.VagaService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import java.util.NoSuchElementException

import static com.linketinder.api.HttpResponseUtil.responder

class VagaHttpHandler implements HttpHandler {

    private final VagaService vagaService

    VagaHttpHandler(VagaService vagaService) {
        this.vagaService = vagaService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {

        try {
            if (!HttpRequestUtil.validarPost(exchange, "/api/vagas")) {
                return
            }

            Map dados = HttpRequestUtil.lerJson(exchange)

            if (dados == null) {
                return
            }

            List<String> campos = [
                    "titulo",
                    "descricaoVaga",
                    "estado",
                    "cidade"
            ]

            List<String> invalidos =
                    HttpRequestUtil.camposInvalidos(dados, campos)

            if (!invalidos.isEmpty()) {
                responder(exchange, 400, [
                        erro: "Campos obrigatórios inválidos.",
                        campos: invalidos
                ])
                return
            }

            Object valorEmpresaId = dados["empresaId"]

            if (!(valorEmpresaId instanceof Number) ||
                    valorEmpresaId instanceof BigDecimal ||
                    valorEmpresaId instanceof Float ||
                    valorEmpresaId instanceof Double ||
                    valorEmpresaId.longValue() <= 0) {

                responder(exchange, 400, [
                        erro: "empresaId deve ser um inteiro positivo."
                ])
                return
            }

            Long empresaId

            try {
                empresaId = valorEmpresaId.longValue()

                if (new BigInteger(valorEmpresaId.toString()) !=
                        BigInteger.valueOf(empresaId)) {
                    throw new IllegalArgumentException()
                }

            } catch (IllegalArgumentException e) {
                responder(exchange, 400, [
                        erro: "empresaId inválido."
                ])
                return
            }

            Vaga vaga = new Vaga(
                    titulo: dados.titulo,
                    descricaoVaga: dados.descricaoVaga,
                    estado: dados.estado,
                    cidade: dados.cidade,
                    skills: []
            )

            try {
                vagaService.cadastrarVaga(vaga, empresaId)

                if (vaga.id == null) {
                    throw new IllegalStateException(
                            "Cadastro sem ID retornado."
                    )
                }

                exchange.responseHeaders.set(
                        "Location",
                        "/api/vagas/${vaga.id}"
                )

                responder(exchange, 201, [
                        id: vaga.id,
                        empresaId: empresaId,
                        mensagem: "Vaga cadastrada com sucesso."
                ])

            } catch (NoSuchElementException e) {

                responder(exchange, 404, [
                        erro: "Empresa não encontrada."
                ])

            } catch (IllegalArgumentException e) {

                responder(exchange, 400, [
                        erro: "Dados inválidos para cadastro."
                ])

            } catch (IllegalStateException e) {

                System.err.println(
                        "Erro ao cadastrar vaga: ${e.message}"
                )

                e.printStackTrace(System.err)

                responder(exchange, 500, [
                        erro: "Não foi possível cadastrar a vaga."
                ])
            }

        } finally {
            exchange.close()
        }
    }
}
