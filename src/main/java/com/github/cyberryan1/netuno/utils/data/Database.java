package com.github.cyberryan1.netuno.utils.data;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private static boolean usingSqlite = false;
    protected static Connection connection;

    public static void initialize() {
        SQL.initialize();
        SQLite.initialize();

        usingSqlite = SQLite.isEnabled();
        if ( SQL.isEnabled() ) { connection = SQL.getConnection(); }
        else { connection = SQLite.getConnection(); }
    }

    public static void close() {
        try {
            connection.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

}