package com.linketinder.dao

import java.sql.Connection
import java.sql.DriverManager
import  java.sql.SQLException

class ConexaoDB {

    private static final String URL = "jdbc:postgresql://localhost:5432/linketinder"
    private static final String USUARIO = "nelson"
    private static final String SENHA = "123qwe"

    static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA)
    }

}