package com.github.cyberryan1.netuno.apimplement;

import com.github.cyberryan1.netuno.apimplement.database.ConnectionManager;
import com.github.cyberryan1.netuno.apimplement.database.DatabaseManager;
import com.github.cyberryan1.netuno.apimplement.models.alts.NetunoAltsCache;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.NetunoApi;
import com.github.cyberryan1.netunoapi.database.DatabaseConnection;
import com.github.cyberryan1.netunoapi.database.NetunoDatabases;
import com.github.cyberryan1.netunoapi.events.NetunoEventDispatcher;
import com.github.cyberryan1.netunoapi.models.alts.NAltLoader;
import com.github.cyberryan1.netunoapi.models.players.NPlayerLoader;
import com.github.cyberryan1.netunoapi.models.punishments.NPrePunishment;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

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

        // Initialize the alts database
        getData().getNetunoAlts().initialize();

        // Initialize the alts security level
        ( ( NetunoAltsCache ) instance.getAltLoader() ).reloadSecurityLevel();

        // Initialize the alts cache
        instance.getAltLoader().initialize();

        // Initialize the reports cache
        getData().getNetunoReports().initializeCache();

        // Initialize the player cache
        ( ( NetunoPlayerCache ) instance.getPlayerLoader() ).initialize();
    }

    public static void deleteInstance() {
        // Save any information needed for the alts database
        instance.getAltLoader().save();
        getData().getNetunoAlts().save();

        // Save the reports cache
        getData().getNetunoReports().saveAllReportEdits();

        // Close the database connection
        instance.getNetunoConnection().closeConnection();

        instance = null;
    }

    public static ApiNetuno getInstance() {
        return instance;
    }

    public static DatabaseManager getData() {
        return instance.getNetunoDatabase();
    }

    //
    // Class Variables & Methods
    //

    private ConnectionManager connection = new ConnectionManager();
    private DatabaseManager databases = new DatabaseManager();
    private NetunoAltsCache altsCache = new NetunoAltsCache();
    private NetunoPlayerCache playerCache = new NetunoPlayerCache();
    private NetunoEventDispatcher eventDispatcher = new NetunoEventDispatcher();

    //
    // Interface Methods
    //

    @Override
    public DatabaseConnection getConnectionManager() {
        return connection;
    }

    @Override
    public NetunoDatabases getDatabaseManager() {
        return databases;
    }

    @Override
    public NAltLoader getAltLoader() {
        return altsCache;
    }

    @Override
    public NPlayerLoader getPlayerLoader() {
        return playerCache;
    }

    @Override
    public NPrePunishment getPrePunishment( NPunishment nPunishment ) {
        return new NetunoPrePunishment( nPunishment );
    }

    public NetunoEventDispatcher getEventDispatcher() { return eventDispatcher; }

    //
    // Class Methods
    //

    public ConnectionManager getNetunoConnection() {
        return connection;
    }

    public void setConnection( ConnectionManager connection ) {
        this.connection = connection;
    }

    public DatabaseManager getNetunoDatabase() {
        return databases;
    }
}