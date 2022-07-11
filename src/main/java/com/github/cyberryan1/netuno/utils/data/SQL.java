package com.github.cyberryan1.netuno.utils.data;

import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQL {

    protected static MysqlDataSource source;
    protected static boolean isEnabled;
    protected static Connection connection;

    public static void initialize() {
        isEnabled = Settings.DATABASE_USE_SQLITE.bool() == false;

        if ( isEnabled ) {
            source = new MysqlDataSource();
            source.setServerName( Settings.DATABASE_SQL_HOST.string() );
            source.setPortNumber( Settings.DATABASE_SQL_PORT.integer() );
            source.setDatabaseName( Settings.DATABASE_SQL_DATABASE.string() );
            source.setUser( Settings.DATABASE_SQL_USERNAME.string() );
            source.setPassword( Settings.DATABASE_SQL_PASSWORD.string() );

            try {
                connection = source.getConnection();
                if ( connection.isValid( 1000 ) == false ) {
                    throw new SQLException( "SQL connection is invalid" );
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
            }

            createTables();
        }
    }

    public static boolean isEnabled() { return isEnabled; }

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
            for ( SQLCreateTables create : SQLCreateTables.values() ) {
                connection.createStatement().execute( create.toString() );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

}