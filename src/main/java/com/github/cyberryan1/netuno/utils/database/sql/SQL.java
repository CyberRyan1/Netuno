package com.github.cyberryan1.netuno.utils.database.sql;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

    protected static MysqlDataSource source;
    protected static boolean isEnabled;
    protected static Connection connection;

    public SQL() {
        isEnabled = !ConfigUtils.getBool( "database.use-sqlite" );

        if ( isEnabled ) {
            source = new MysqlDataSource();
            source.setServerName( ConfigUtils.getStr( "database.sql.host" ) );
            source.setPortNumber( ConfigUtils.getInt( "database.sql.port" ) );
            source.setDatabaseName( ConfigUtils.getStr( "database.sql.database" ) );
            source.setUser( ConfigUtils.getStr( "database.sql.username" ) );
            source.setPassword( ConfigUtils.getStr( "database.sql.password" ) );

            try {
                testSource();
                connection = source.getConnection();
            } catch ( SQLException e ) {
                Utils.logError( "Failed to establish a connection to the database: " + e );
                Utils.logError( "Defaulting to SQLite database..." );
                isEnabled = false;
                return;
            }

            createTables();
        }
    }

    private void testSource() throws SQLException {
        try ( Connection conn = source.getConnection() ){
            if ( conn.isValid( 1000 ) == false ) {
                throw new SQLException( "Could not establish a connection to the database" );
            }
        }
    }

    public static boolean isEnabled() { return isEnabled; }

    public static Connection getConn() { return connection; }

    public static Connection getConnection() { return connection; }

    public static void closeConn() { closeConnection(); }

    public static void closeConnection() {
        try {
            connection.close();
        } catch ( SQLException e ) {
            Utils.logError( "Failed to close the SQL connection: ", e );
        }
    }

    public void createTables() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate( SQLTableCreate.CREATE_PUNS_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_NOTIFS_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_IP_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_IP_PUNS_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_NO_SIGN_NOTIFS_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_REPORTS_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_PUNISH_GUI_TABLE.toString() );
            stmt.executeUpdate( SQLTableCreate.CREATE_OTHER_TABLE.toString() );
        } catch ( SQLException e ) {
            Utils.logError( "Failed to setup the SQL server: ", e );
        }
    }
}