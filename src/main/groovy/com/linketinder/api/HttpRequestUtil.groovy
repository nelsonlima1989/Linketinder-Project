
package com.linketinder.api

import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonSlurper

import java.nio.charset.StandardCharsets
import java.util.Locale

import static com.linketinder.api.HttpResponseUtil.responder

class HttpRequestUtil {

    private static final int MAX_BODY_BYTES = 16 * 1024

    static boolean validarPost(
            HttpExchange exchange,
            String caminho
    ) throws IOException {

        if (exchange.requestURI.path != caminho) {
            responder(exchange, 404, [
                    erro: "Recurso não encontrado."
            ])
            return false
        }

        if (exchange.requestMethod != "POST") {
            exchange.responseHeaders.set("Allow", "POST")

            responder(exchange, 405, [
                    erro: "Método não permitido."
            ])
            return false
        }

        String contentType = exchange.requestHeaders
                .getFirst("Content-Type") ?: ""

        String tipo = contentType
                .split(";", 2)[0]
                .trim()
                .toLowerCase(Locale.ROOT)

        if (tipo != "application/json") {
            responder(exchange, 415, [
                    erro: "Content-Type deve ser application/json."
            ])
            return false
        }

        return true
    }

    static Map lerJson(HttpExchange exchange) throws IOException {

        byte[] corpo = exchange.requestBody
                .readNBytes(MAX_BODY_BYTES + 1)

        if (corpo.length > MAX_BODY_BYTES) {
            responder(exchange, 413, [
                    erro: "Corpo da requisição muito grande."
            ])
            return null
        }

        Object json

        try {
            json = new JsonSlurper().parseText(
                    new String(corpo, StandardCharsets.UTF_8)
            )
        } catch (Exception e) {
            responder(exchange, 400, [
                    erro: "JSON inválido."
            ])
            return null
        }

        if (!(json instanceof Map)) {
            responder(exchange, 400, [
                    erro: "O JSON deve conter um objeto."
            ])
            return null
        }

        return (Map) json
    }

    static List<String> camposInvalidos(
            Map dados,
            List<String> camposObrigatorios
    ) {
        return camposObrigatorios.findAll { String campo ->
            !(dados[campo] instanceof String) ||
                    ((String) dados[campo]).trim().isEmpty()
        }
    }
}
