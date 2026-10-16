package com.linketinder.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConexaoDB {

    static Connection conectar() throws SQLException {

        String url = System.getenv("DB_URL")
        String usuario = System.getenv("DB_USER")
        String senha = System.getenv("DB_PASSWORD")

        if (!url || !usuario || !senha) {
            throw new IllegalStateException(
                    "Variáveis de ambiente do banco não configuradas."
            )
        }

        return DriverManager.getConnection(
                url,
                usuario,
                senha
        )
    }
}