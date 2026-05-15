package com.retailhub.dao;

import com.retailhub.exception.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    private static final String URL = "jdbc:sqlite:shopee.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new PersistenciaException("Erro na conexão com o banco de dados.", e);
        }
    }
}
