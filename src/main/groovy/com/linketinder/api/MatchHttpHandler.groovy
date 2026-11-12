package com.linketinder.api

import com.linketinder.model.MatchAnonimo
import com.linketinder.service.MatchService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import static com.linketinder.api.HttpResponseUtil.responder

class MatchHttpHandler implements HttpHandler {

    private final MatchService matchService

    MatchHttpHandler(MatchService matchService) {
        this.matchService = matchService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.requestURI.path != "/api/matches") {
                responder(exchange, 404, [
                        erro: "Recurso não encontrado."
                ])
                return
            }

            if (exchange.requestMethod != "GET") {
                exchange.responseHeaders.set("Allow", "GET")
                responder(exchange, 405, [
                        erro: "Método não permitido."
                ])
                return
            }

            Map<String, String> queryParams = extrairQueryParams(exchange.requestURI.rawQuery)

            Long candidatoId = null
            if (queryParams.containsKey("candidatoId")) {
                candidatoId = extrairLongPositivo(queryParams.get("candidatoId"))
                if (candidatoId == null) {
                    responder(exchange, 400, [
                            erro: "candidatoId deve ser um inteiro positivo."
                    ])
                    return
                }
            }

            Long empresaId = null
            if (queryParams.containsKey("empresaId")) {
                empresaId = extrairLongPositivo(queryParams.get("empresaId"))
                if (empresaId == null) {
                    responder(exchange, 400, [
                            erro: "empresaId deve ser um inteiro positivo."
                    ])
                    return
                }
            }

            List<MatchAnonimo> matches = matchService.listarMatches(candidatoId, empresaId)

            List<Map> resposta = matches.collect { MatchAnonimo m ->
                Map candidatoMap = [
                        id: m.candidato?.id,
                        estado: m.candidato?.estado,
                        descricaoPessoal: m.candidato?.descricaoPessoal,
                        skills: m.candidato?.skills ? m.candidato.skills.collect { it.nome } : []
                ]

                Map empresaMap = [
                        id: m.empresa?.id,
                        pais: m.empresa?.pais,
                        estado: m.empresa?.estado,
                        descricaoEmpresarial: m.empresa?.descricaoEmpresarial
                ]

                Map recrutadorMap = null
                if (m.recrutador != null) {
                    recrutadorMap = [
                            id: m.recrutador.id,
                            areaRecrutamento: m.recrutador.areaRecrutamento
                    ]
                }

                return [
                        id: m.id,
                        dataMatch: m.dataMatch?.toString(),
                        candidato: candidatoMap,
                        empresa: empresaMap,
                        recrutador: recrutadorMap
                ]
            }

            responder(exchange, 200, resposta)

        } catch (IllegalArgumentException e) {
            responder(exchange, 400, [
                    erro: e.message ?: "Parâmetros inválidos."
            ])
        } catch (Exception e) {
            responder(exchange, 500, [
                    erro: "Não foi possível consultar os matches."
            ])
        } finally {
            exchange.close()
        }
    }

    private Map<String, String> extrairQueryParams(String rawQuery) {
        Map<String, String> params = [:]
        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            return params
        }

        String[] pares = rawQuery.split("&")
        for (String par : pares) {
            String[] chaveValor = par.split("=", 2)
            if (chaveValor.length == 2) {
                String chave = URLDecoder.decode(chaveValor[0], StandardCharsets.UTF_8)
                String valor = URLDecoder.decode(chaveValor[1], StandardCharsets.UTF_8)
                params.put(chave, valor)
            }
        }
        return params
    }

    private Long extrairLongPositivo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null
        }
        try {
            long l = Long.parseLong(valor.trim())
            return l > 0L ? l : null
        } catch (NumberFormatException ignored) {
            return null
        }
    }
}
