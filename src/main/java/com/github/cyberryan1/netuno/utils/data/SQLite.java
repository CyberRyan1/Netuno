package com.github.cyberryan1.netuno.utils.data;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {

    private static boolean usingSqlite;
    private static Connection connection;

    public static void initialize() {
        usingSqlite = Settings.DATABASE_USE_SQLITE.bool();

        if ( usingSqlite ) {
            try {
                File dataFolder = new File( CyberCore.getPlugin().getDataFolder(), "database.db" );
                Class.forName( "org.sqlite.JDBC" );
                connection = DriverManager.getConnection( "jdbc:sqlite:" + dataFolder );
            } catch ( SQLException | ClassNotFoundException e ) {
                throw new RuntimeException( e );
            }

            createTables();
        }
    }

    public static boolean isEnabled() { return usingSqlite; }

    public static Connection getConnection() { return connection; }

    public static Connection getConn() { return connection; }

    public static void closeConnection() {
        try {
            connection.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void createTables() {
        try {
            for ( SQLiteCreateTables create : SQLiteCreateTables.values() ) {
                connection.createStatement().execute( create.toString() );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}