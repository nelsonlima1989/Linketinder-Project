package com.linketinder.dao

import java.sql.Connection
import java.sql.SQLException

interface ConexaoProvider {

    Connection obterConexao() throws SQLException

}