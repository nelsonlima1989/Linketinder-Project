package com.linketinder.database

import java.sql.Connection
import java.sql.SQLException

interface ConexaoProvider {

    Connection obterConexao() throws SQLException

}