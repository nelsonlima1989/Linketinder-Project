
package com.linketinder.api

import com.sun.net.httpserver.HttpExchange
import groovy.json.JsonOutput

import java.nio.charset.StandardCharsets

class HttpResponseUtil {

    static void responder(
            HttpExchange exchange,
            int status,
            Map conteudo
    ) throws IOException {
        responderObjeto(exchange, status, conteudo)
    }

    static void responder(
            HttpExchange exchange,
            int status,
            List conteudo
    ) throws IOException {
        responderObjeto(exchange, status, conteudo)
    }

    static void responderObjeto(
            HttpExchange exchange,
            int status,
            Object conteudo
    ) throws IOException {

        byte[] resposta = JsonOutput.toJson(conteudo)
                .getBytes(StandardCharsets.UTF_8)

        exchange.responseHeaders.set(
                "Content-Type",
                "application/json; charset=UTF-8"
        )

        exchange.sendResponseHeaders(
                status,
                resposta.length
        )

        exchange.responseBody.write(resposta)
    }
}
