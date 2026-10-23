package ru.mirea.postamat.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {

    private static final Properties PROPS = load();

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = Db.class.getResourceAsStream("/database.properties")) {
            if (in != null) p.load(in);
            else System.err.println("[Db] database.properties не найден в classpath");
        } catch (Exception e) {
            System.err.println("[Db] Ошибка чтения database.properties: " + e.getMessage());
        }
        return p;
    }

    private static String get(String key, String envKey, String def) {
        String v = PROPS.getProperty(key);
        if (v != null && !v.isBlank()) return v;
        v = System.getenv(envKey);
        if (v != null && !v.isBlank()) return v;
        return def;
    }

    private static final String URL = String.format(
            "jdbc:postgresql://%s:%s/%s",
            get("db.host", "DB_HOST", "localhost"),
            get("db.port", "DB_PORT", "47287"),
            get("db.name", "DB_NAME", "postamat_db")
    );

    private static final String USER     = get("db.user",     "DB_USER", "postgres");
    private static final String PASSWORD = get("db.password", "DB_PASS", "postgres");

    private Db() {}

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}