package com.linketinder.api

import com.linketinder.model.CurtidaResultadoDTO
import com.linketinder.service.ConflitoDadosException
import com.linketinder.service.CurtidaService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.NoSuchElementException

import static com.linketinder.api.HttpResponseUtil.responder

class CurtidaHttpHandler implements HttpHandler {

    private final CurtidaService curtidaService

    CurtidaHttpHandler(CurtidaService curtidaService) {
        this.curtidaService = curtidaService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.requestURI.path

            if (exchange.requestMethod != "POST") {
                exchange.responseHeaders.set("Allow", "POST")
                responder(exchange, 405, [
                        erro: "Método não permitido."
                ])
                return
            }

            String contentType = exchange.requestHeaders.getFirst("Content-Type") ?: ""
            String tipo = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT)
            if (tipo != "application/json") {
                responder(exchange, 415, [
                        erro: "Content-Type deve ser application/json."
                ])
                return
            }

            if (path == "/api/curtidas/candidato-vaga") {
                tratarCurtidaCandidatoVaga(exchange)
                return
            }

            if (path == "/api/curtidas/empresa-candidato") {
                tratarCurtidaEmpresaCandidato(exchange)
                return
            }

            responder(exchange, 404, [
                    erro: "Recurso não encontrado."
            ])

        } finally {
            exchange.close()
        }
    }

    private void tratarCurtidaCandidatoVaga(HttpExchange exchange) throws IOException {
        Map dados = HttpRequestUtil.lerJson(exchange)
        if (dados == null) {
            return
        }

        Long candidatoId = extrairLongPositivo(dados["candidatoId"])
        Long vagaId = extrairLongPositivo(dados["vagaId"])

        if (candidatoId == null || vagaId == null) {
            responder(exchange, 400, [
                    erro: "candidatoId e vagaId devem ser inteiros positivos."
            ])
            return
        }

        try {
            CurtidaResultadoDTO resultado = curtidaService.curtirVaga(candidatoId, vagaId)

            responder(exchange, 201, [
                    candidatoId: resultado.candidatoId,
                    vagaId: resultado.vagaId,
                    dataCurtida: resultado.dataCurtida?.toString(),
                    match: resultado.match,
                    matchId: resultado.matchId,
                    mensagem: resultado.mensagem
            ])
        } catch (ConflitoDadosException e) {
            responder(exchange, 409, [
                    erro: e.message ?: "Curtida já registrada anteriormente."
            ])
        } catch (NoSuchElementException e) {
            responder(exchange, 404, [
                    erro: e.message ?: "Recurso não encontrado."
            ])
        } catch (IllegalArgumentException e) {
            responder(exchange, 400, [
                    erro: e.message ?: "Dados inválidos para curtida."
            ])
        } catch (Exception e) {
            responder(exchange, 500, [
                    erro: "Não foi possível registrar a curtida."
            ])
        }
    }

    private void tratarCurtidaEmpresaCandidato(HttpExchange exchange) throws IOException {
        Map dados = HttpRequestUtil.lerJson(exchange)
        if (dados == null) {
            return
        }

        Long empresaId = extrairLongPositivo(dados["empresaId"])
        Long candidatoId = extrairLongPositivo(dados["candidatoId"])
        Long recrutadorId = extrairLongPositivo(dados["recrutadorId"])

        if (empresaId == null || candidatoId == null || recrutadorId == null) {
            responder(exchange, 400, [
                    erro: "empresaId, candidatoId e recrutadorId devem ser inteiros positivos."
            ])
            return
        }

        try {
            CurtidaResultadoDTO resultado = curtidaService.curtirCandidato(empresaId, candidatoId, recrutadorId)

            responder(exchange, 201, [
                    empresaId: resultado.empresaId,
                    candidatoId: resultado.candidatoId,
                    recrutadorId: resultado.recrutadorId,
                    dataCurtida: resultado.dataCurtida?.toString(),
                    match: resultado.match,
                    matchId: resultado.matchId,
                    mensagem: resultado.mensagem
            ])
        } catch (ConflitoDadosException e) {
            responder(exchange, 409, [
                    erro: e.message ?: "Curtida já registrada anteriormente."
            ])
        } catch (NoSuchElementException e) {
            responder(exchange, 404, [
                    erro: e.message ?: "Recurso não encontrado."
            ])
        } catch (IllegalArgumentException e) {
            responder(exchange, 400, [
                    erro: e.message ?: "Dados inválidos para curtida."
            ])
        } catch (Exception e) {
            responder(exchange, 500, [
                    erro: "Não foi possível registrar a curtida."
            ])
        }
    }

    private Long extrairLongPositivo(Object valor) {
        if (valor instanceof Integer || valor instanceof Long) {
            long l = ((Number) valor).longValue()
            return l > 0L ? l : null
        }
        return null
    }
}
