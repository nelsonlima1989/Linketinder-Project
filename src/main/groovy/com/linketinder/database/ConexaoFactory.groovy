package com.linketinder.database

class ConexaoFactory {

    static ConexaoProvider criarProvider(String tipoBanco) {

        if ("postgresql".equalsIgnoreCase(tipoBanco?.trim())) {
            return new ConexaoPostgresProvider()
        }

        throw new IllegalArgumentException(
                "Banco de dados não suportado: ${tipoBanco}"
        )
    }
}