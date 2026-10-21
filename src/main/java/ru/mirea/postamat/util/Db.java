package ru.mirea.postamat.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String URL = String.format("jdbc:postgresql://localhost:%s/%s",
            System.getenv("DB_PORT"),
            System.getenv("DB_NAME"));

    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASS");

    private Db() {}

    public static Connection get_conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}