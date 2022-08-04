package com.github.cyberryan1.netuno.api;

import com.github.cyberryan1.netuno.api.database.ConnectionManager;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.NetunoApi;
import com.github.cyberryan1.netunoapi.database.DatabaseConnection;
import com.github.cyberryan1.netunoapi.database.NetunoDatabases;

public class ApiNetuno implements NetunoApi {

    //
    // Static Methods
    //

    private static ApiNetuno instance;

    public static void setupInstance() {
        instance = new ApiNetuno();

        ConnectionManager conn = new ConnectionManager();
        // Initialize the database connection
        if ( Settings.DATABASE_USE_SQLITE.bool() ) {
            conn.initializeSqlite();
        }
        else {
            conn.initializeSql(
                    Settings.DATABASE_SQL_HOST.string(),
                    Settings.DATABASE_SQL_PORT.integer(),
                    Settings.DATABASE_SQL_DATABASE.string(),
                    Settings.DATABASE_SQL_USERNAME.string(),
                    Settings.DATABASE_SQL_PASSWORD.string()
            );
        }
        instance.setConnection( conn );

        // Initialize the punishment and report databases' cache times
        if ( Settings.CACHE_EXPIRATION.integer() > 0 ) {
            instance.getDatabases().getPun().getCache().setExpirationTime( Settings.CACHE_EXPIRATION.integer() );
            instance.getDatabases().getReports().getCache().setExpirationTime( Settings.CACHE_EXPIRATION.integer() );
        }
    }

    public static void deleteInstance() {
        ( ( ConnectionManager ) instance.getConnectionManager() ).closeConnection();

        instance = null;
    }

    public static ApiNetuno getInstance() {
        return instance;
    }

    public static NetunoDatabases getData() {
        return instance.getDatabaseManager();
    }

    //
    // Class Variables & Methods
    //

    private DatabaseConnection connection = null;
    private NetunoDatabases databases = null;

    @Override
    public DatabaseConnection getConnectionManager() {
        return connection;
    }

    public void setConnection( ConnectionManager connection ) {
        this.connection = connection;
    }

    @Override
    public NetunoDatabases getDatabaseManager() {
        return databases;
    }
}
