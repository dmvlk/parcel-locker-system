package ru.mirea.postamat.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String URL = "jdbc:postgresql://localhost:5432/postamat_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private Db() {}

    public static Connection get_conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}