package com.github.cyberryan1.netuno.utils.database.sqlite;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

    protected static Netuno plugin;
    protected static String DATABASE_NAME;
    protected static boolean isEnabled;
    protected static Connection connection;

    public SQLite( Netuno plugin ) {
        this.plugin = plugin;
        isEnabled = ConfigUtils.getBool( "database.use-sqlite" );

        if ( isEnabled ) {
            enableSQLite();
        }
    }

    public void enableSQLite() {
        DATABASE_NAME = plugin.getConfig().getString( "SQLite.Filename", "database" );

        try {
            setupConnection();
        } catch ( SQLException e ) {
            Utils.logError( "SQLite exception on initalization: ", e );
        } catch ( ClassNotFoundException e ) {
            Utils.logError( "SQLite Exception - You need the SQLite JDBC library. Put it in the /lib folder. ", e );
        }

        createTables();
    }

    private void setupConnection() throws SQLException, ClassNotFoundException {
        File dataFolder = new File( plugin.getDataFolder(), DATABASE_NAME + ".db" );
        Class.forName( "org.sqlite.JDBC" );
        connection = DriverManager.getConnection( "jdbc:sqlite:" + dataFolder );
    }

    public static boolean isEnabled() { return isEnabled; }

    public static Connection getConn() { return connection; }

    public static Connection getConnection() { return connection; }

    public static void closeConn() { closeConnection(); }

    public static void closeConnection() {
        try {
            connection.close();
        } catch ( SQLException e ) {
            Utils.logError( "Failed to close the SQLite connection: ", e );
        }
    }

    public void createTables() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate( SQLiteTableCreate.CREATE_PUNS_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_NOTIFS_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_IP_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_IP_PUNS_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_NO_SIGN_NOTIFS_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_REPORTS_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_PUNISH_GUI_TABLE.toString() );
            stmt.executeUpdate( SQLiteTableCreate.CREATE_OTHER_TABLE.toString() );
        } catch ( SQLException e ) {
            Utils.logError( "Failed to setup the SQL server: ", e );
        }
    }
}
