package ru.university.postamat.util;

import ru.university.postamat.exception.BusinessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Менеджер подключений к PostgreSQL. Единственное место с параметрами подключения. */
public final class DatabaseManager {

    private static final String URL      = "jdbc:postgresql://localhost:5432/postamat_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres";

    private DatabaseManager() { }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new BusinessException("Не удалось подключиться к базе данных: " + e.getMessage());
        }
    }

    // проверка подключения при старте приложения
    public static void checkConnection() {
        try (Connection c = getConnection()) {
            System.out.println("Подключение к PostgreSQL установлено (версия "
                    + c.getMetaData().getDatabaseProductVersion() + ").");
        } catch (SQLException e) {
            throw new BusinessException("База данных недоступна. Запустите PostgreSQL "
                    + "и создайте базу postamat_db (см. README): " + e.getMessage());
        }
    }
}