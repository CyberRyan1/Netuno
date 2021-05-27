package com.github.cyberryan1.netuno.utils.database;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {

    String dbName;

    public SQLite( Netuno instance ) {
        super(instance);
        dbName = plugin.getConfig().getString( "SQLite.Filename", "database" );
    }

    public String CREATE_PUNS_TABLE = "CREATE TABLE IF NOT EXISTS database (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`staff` STRING NOT NULL," +
            "`type` STRING NOT NULL," +
            "`date` STRING NOT NULL," +
            "`length` STRING NOT NULL," +
            "`reason` STRING NOT NULL," +
            "`active` STRING NOT NULL," +
            "PRIMARY KEY (`id`));";

    public final String CREATE_NOTIFS_TABLE = "CREATE TABLE IF NOT EXISTS notifs (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "PRIMARY KEY (`id`));";

    public final String CREATE_IP_TABLE = "CREATE TABLE IF NOT EXISTS ip (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`ip` STRING NOT NULL," +
            "PRIMARY KEY (`id`));";

    public Connection getSqlConnection() {
        File plugins = new File( "plugins" );
        File directory = new File( plugins, "Netuno" );
        if ( directory.exists() == false ) {
            directory.mkdir();
        }

        File dataFolder = new File( plugin.getDataFolder(), dbName + ".db" );
        if ( dataFolder.exists() == false ) {
            try {
                dataFolder.createNewFile();
            } catch ( IOException e ) {
                Utils.logError( "File write error: " + dbName + ".db" );
            }
        }

        try {
            if ( connection != null && connection.isClosed() == false ) {
                return connection;
            }

            Class.forName( "org.sqlite.JDBC" );
            connection = DriverManager.getConnection( "jdbc:sqlite:" + dataFolder );
            return connection;
        } catch ( SQLException ex ) {
            Utils.logError( "SQLite exception on initialize ", ex );
        } catch ( ClassNotFoundException ex ) {
            Utils.logError( "You need the SQLite JDBC libary. Put it in the /lib folder." );
        }

        return null;
    }

    public void load() {
        connection = getSqlConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate( CREATE_PUNS_TABLE );

            s = connection.createStatement();
            s.executeUpdate( CREATE_NOTIFS_TABLE );

            s = connection.createStatement();
            s.executeUpdate( CREATE_IP_TABLE );

            s.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        initialize();
    }
}
