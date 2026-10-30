
package com.linketinder.api

import com.linketinder.model.Empresa
import com.linketinder.model.Informacoes
import com.linketinder.service.EmpresaService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import static com.linketinder.api.HttpResponseUtil.responder

class EmpresaHttpHandler implements HttpHandler {

    private final EmpresaService empresaService

    EmpresaHttpHandler(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {

        try {
            if (!HttpRequestUtil.validarPost(exchange, "/api/empresas")) {
                return
            }

            Map dados = HttpRequestUtil.lerJson(exchange)

            if (dados == null) {
                return
            }

            List<String> campos = [
                    "nome",
                    "cnpj",
                    "email",
                    "descricaoEmpresarial",
                    "pais",
                    "estado",
                    "cep",
                    "senha"
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

            Informacoes informacoes = new Informacoes(
                    nome: dados.nome,
                    email: dados.email,
                    pais: dados.pais,
                    cep: dados.cep
            )

            Empresa empresa = new Empresa(
                    cnpj: dados.cnpj,
                    pais: dados.pais,
                    estado: dados.estado,
                    descricaoEmpresarial: dados.descricaoEmpresarial,
                    senha: dados.senha,
                    informacoes: informacoes,
                    vagas: []
            )

            try {
                empresaService.cadastrarEmpresa(empresa)

                if (empresa.id == null) {
                    throw new IllegalStateException(
                            "Cadastro sem ID retornado."
                    )
                }

                exchange.responseHeaders.set(
                        "Location",
                        "/api/empresas/${empresa.id}"
                )

                responder(exchange, 201, [
                        id: empresa.id,
                        mensagem: "Empresa cadastrada com sucesso."
                ])

            } catch (IllegalStateException e) {

                System.err.println(
                        "Erro ao cadastrar empresa: ${e.message}"
                )

                e.printStackTrace(System.err)

                responder(exchange, 500, [
                        erro: "Não foi possível cadastrar a empresa."
                ])
            }

        } finally {
            exchange.close()
        }
    }
}
