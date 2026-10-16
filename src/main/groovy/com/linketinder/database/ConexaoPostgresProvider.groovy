package com.linketinder.database


import java.sql.Connection
import java.sql.SQLException

class ConexaoPostgresProvider implements ConexaoProvider{

    @Override
    Connection obterConexao() throws SQLException {
        return ConexaoDB.conectar()
    }
}
