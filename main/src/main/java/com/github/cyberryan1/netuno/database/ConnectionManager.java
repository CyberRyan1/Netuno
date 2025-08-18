package com.github.cyberryan1.netuno.database;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.database.helpers.SQLTables;
import com.github.cyberryan1.netuno.database.helpers.SQLiteTables;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Used for managing the connection to the database as well
 * as setting up the databases.
 *
 * @author Ryan
 */
public class ConnectionManager {

    protected static Connection CONN = null;
    protected static boolean IS_SQLITE = false;
    protected static boolean IS_SQL = false;

    /**
     * Looks at the settings and determines whether or
     * not the user wants to use SQL or SQLite. This
     * will then initialize the databases accordingly. <br><br>
     *
     * <b>IMPORTANT!</b> This must be done <u>after</u>
     * the settings are loaded
     */
    public void initialize() {
        // Initialize the database connection
        if ( Settings.DATABASE_USE_SQLITE.bool() ) {
            initializeSqlite();
        }
        else {
            initializeSql(
                    Settings.DATABASE_SQL_HOST.string(),
                    Settings.DATABASE_SQL_PORT.integer(),
                    Settings.DATABASE_SQL_DATABASE.string(),
                    Settings.DATABASE_SQL_USERNAME.string(),
                    Settings.DATABASE_SQL_PASSWORD.string()
            );
        }
    }

    /**
     * Initializes the connection to the SQL database and creates all tables needed.
     * @param host The host of the SQL database
     * @param port The port of the SQL database
     * @param database The name of the SQL database
     * @param username The username of the SQL database
     * @param password The password of the SQL database
     */
    private void initializeSql( String host, int port, String database, String username, String password ) {
        IS_SQL = true;
        CyberLogUtils.logInfo( "[SQL Init] Initializing SQL..." );

        CyberLogUtils.logInfo( "[SQL Init] Establishing the connection for SQL..." );
        try {
            MysqlDataSource source = new MysqlDataSource();
            source.setServerName( host );
            source.setPortNumber( port );
            source.setDatabaseName( database );
            source.setUser( username );
            source.setPassword( password );

            CONN = source.getConnection();
            if ( CONN.isValid( 1000 ) == false ) {
                throw new SQLException( "Could not establish a connection: connection is invalid" );
            }
        } catch ( SQLException e ) {
            CyberLogUtils.logError( "[SQL Init] SQL exception when establishing the connection. See error below for details" );
            throw new RuntimeException( e );
        }
        CyberLogUtils.logInfo( "[SQL Init] Connection for SQL successfully established" );

        CyberLogUtils.logInfo( "[SQL Init] Creating tables for SQL..." );
        try {
            Statement stmt = CONN.createStatement();
            for ( SQLTables table : SQLTables.values() ) {
                stmt.execute( table.getSql() );
            }
            stmt.close();
        } catch ( SQLException e ) {
            CyberLogUtils.logError( "[SQL Init] SQL exception when creating tables. See error below for details" );
            throw new RuntimeException( e );
        }
        CyberLogUtils.logInfo( "[SQL Init] Successfully created all tables for SQLite" );

        CyberLogUtils.logInfo( "[SQL Init] SQL initialization complete" );
    }

    /**
     * Initializes the connection to the SQLite database and creates all tables needed.
     */
    private void initializeSqlite() {
        IS_SQLITE = true;

        CyberLogUtils.logInfo( "[SQLite Init] Initializing SQLite..." );
        String databaseName = CyberCore.getPlugin().getConfig().getString( "SQLite.Filename", "database" );

        CyberLogUtils.logInfo( "[SQLite Init] Establishing the connection for SQLite..." );
        try {
            File dataFolder = new File( CyberCore.getPlugin().getDataFolder(), databaseName + ".db" );
            Class.forName( "org.sqlite.JDBC" );
            CONN = DriverManager.getConnection( "jdbc:sqlite:" + dataFolder );
        } catch ( SQLException e ) {
            CyberLogUtils.logError( "[SQLite Init] SQLite exception when establishing the connection. See error below for details" );
            throw new RuntimeException( e );
        } catch ( ClassNotFoundException e ) {
            CyberLogUtils.logError( "[SQLite Init] SQLite exception when establishing the connection" );
            CyberLogUtils.logError( "[SQLite Init] A potential reason why this happened is because you don't have the SQLite JDBC library" );
            CyberLogUtils.logError( "[SQLite Init] See error below for details" );
            throw new RuntimeException( e );
        }
        CyberLogUtils.logInfo( "[SQLite Init] Connection for SQLite successfully established" );

        CyberLogUtils.logInfo( "[SQLite Init] Creating tables for SQLite..." );
        try {
            Statement stmt = CONN.createStatement();
            for ( SQLiteTables table : SQLiteTables.values() ) {
                stmt.execute( table.getSql() );
            }
            stmt.close();
        } catch ( SQLException e ) {
            CyberLogUtils.logError( "[SQLite Init] SQLite exception when creating tables. See error below for details" );
            throw new RuntimeException( e );
        }
        CyberLogUtils.logInfo( "[SQLite Init] Successfully created all tables for SQLite" );

        CyberLogUtils.logInfo( "[SQLite Init] SQLite initialization complete" );
    }

    /**
     * @return The connection to the database if it is set, null otherwise
     */
    public Connection getConnection() {
        return CONN;
    }

    /**
     * @return The connection to the database if it is set, null otherwise
     */
    public Connection getConn() {
        return CONN;
    }

    /**
     * Closes the connection to the database
     */
    public void closeConnection() {
        CyberLogUtils.logInfo( "[Connection Close] Closing the connection to the database..." );
        try {
            CONN.close();
        } catch ( SQLException e ) {
            CyberLogUtils.logError( "[Connection Close] An error occurred when closing the database connection. See error below for details" );
            throw new RuntimeException( e );
        }
        CyberLogUtils.logInfo( "[Connection Close] Successfully closed the database connection" );
    }

    /**
     * @return True if the connection is to a SQLite database, false otherwise
     */
    public boolean isSqlite() {
        return IS_SQLITE;
    }

    /**
     * @return True if the connection is to a SQL database, false otherwise
     */
    public boolean isSql() {
        return IS_SQL;
    }
}