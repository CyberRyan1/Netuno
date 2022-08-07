package com.github.cyberryan1.netuno.apimplement;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.netuno.apimplement.database.ConnectionManager;
import com.github.cyberryan1.netuno.apimplement.database.DatabaseManager;
import com.github.cyberryan1.netuno.apimplement.database.helpers.AltSecurityLevel;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.NetunoApi;
import com.github.cyberryan1.netunoapi.database.DatabaseConnection;
import com.github.cyberryan1.netunoapi.database.NetunoDatabases;
import com.github.cyberryan1.netunoapi.models.players.NPlayerLoader;
import com.github.cyberryan1.netunoapi.models.punishments.NPrePunishment;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.plugin.ServicePriority;

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

        // Initialize the alts security level
        AltSecurityLevel altSecurityLevel = switch ( Settings.IPINFO_STRICTNESS.string().toUpperCase() ) {
            case "LOW" -> AltSecurityLevel.LOW;
            case "HIGH" -> AltSecurityLevel.HIGH;
            default -> AltSecurityLevel.MEDIUM;
        };
        getData().getNetunoAlts().setSecurityLevel( altSecurityLevel );

        // Initialize the alts cache (note that the cache must be initialized after the security level)
        getData().getNetunoAlts().initializeCache();

        // Initialize the reports cache
        getData().getNetunoReports().initializeCache();

        // Initialize the player cache
        ( ( NetunoPlayerCache ) instance.getPlayerLoader() ).initialize();

        // Register the api to the services manager
        CyberCore.getPlugin().getServer().getServicesManager().register( NetunoApi.class, instance, CyberCore.getPlugin(), ServicePriority.Normal );
    }

    public static void deleteInstance() {
        // Save the alts cache
        getData().getNetunoAlts().saveAll();

        // Save the reports cache
        getData().getNetunoReports().saveAll();

        // Close the database connection
        instance.getNetunoConnection().closeConnection();

        // Unregister the api from the services manager
        CyberCore.getPlugin().getServer().getServicesManager().unregister( NetunoApi.class, instance );
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
    private NetunoPlayerCache playerCache = new NetunoPlayerCache();

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
    public NPlayerLoader getPlayerLoader() {
        return playerCache;
    }

    @Override
    public NPrePunishment getPrePunishment( NPunishment nPunishment ) {
        return new NetunoPrePunishment( nPunishment );
    }

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