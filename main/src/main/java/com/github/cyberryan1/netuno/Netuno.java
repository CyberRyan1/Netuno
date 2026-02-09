package com.github.cyberryan1.netuno;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.api.services.ApiNetunoService;
import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.database.ConnectionManager;
import com.github.cyberryan1.netuno.guis.history.HistoryEditManager;
import com.github.cyberryan1.netuno.guis.punish.managers.ActivePunishGuiManager;
import com.github.cyberryan1.netuno.listeners.ChatListener;
import com.github.cyberryan1.netuno.listeners.CommandListener;
import com.github.cyberryan1.netuno.listeners.PreLoginListener;
import com.github.cyberryan1.netuno.listeners.SignChangeListener;
import com.github.cyberryan1.netuno.services.*;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

/*
TODO List

Backend:
- Better punishment model
- Better alt searching algorithm
- Better player model
- Databases

Commands:
- Ban Command
- Chatslow Command
- Clearchat Command
- History Command (edit, list, reset subcommands)
- Ipban Command
- Ipinfo Command
- Ipmute Command
- Kick Command
- Mute Command
- Mutechat Command
- Netuno Command
- Punish Command
- Report Command
- Reports Command
- Togglesigns Command
- Unipban Command
- Unipmute Command
- Unban Command
- Unmute Command
- Warn Command

Alts Feature:
- List all alts in GUI
- Warn when player joins w/ punished alts

Reports Feature:
- Report options in GUI
- Staff can view reports in GUI

History Feature:
- Staff can view all punishments of a player in a GUI
- Staff can edit a particular punishment in a GUI

Sign Notifications Feature:
- Send sign contents to all staff
- Allow staff to disable these broadcasts for themselves with a command

Punish Feature:
- Staff can punish players via a GUI
- Staff can also quick punish, i.e. /punish (target) (quick punish)

Skript Features:
- Condition if player is netuno banned
- Condition if player is netuno ipbanned
- Condition if player is netuno muted
- Condition if player is netuno ipmuted
- Expression ban length
- Expression ipban length
- Expression ipmute length
- Expression mute length

API Feature:
- Better API player model
- Better API punishment model
- Better API alts searching

Config:
- Change config updater to better one (do research) <-- want to use ConfigUpdater
 */

public final class Netuno extends JavaPlugin {

    // Database Connection
    public static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    // API Things
    public static PunishmentService PUNISHMENT_SERVICE = null;
    public static AltService ALT_SERVICE = null;
    public static ChatService CHAT_SERVICE = null;
    public static ReportService REPORT_SERVICE = null;
    public static NetunoService SERVICE = null;

    public static final ActivePunishGuiManager ACTIVE_PUNISH_GUIS = new ActivePunishGuiManager();

    // bStats
    private static final int BSTATS_PLUGIN_ID = 21155;
    public static Metrics metrics;

    // CompletableFuture error handling
    // Should be used after the .thenAccept() method for futures
    //      so that any errors are logged rather than being
    //      silently swallowed
    // Note: you do NOT need to add error handling when you are
    //      supplying the completable future, i.e. with
    //      CompletableFuture.supplyAsync() - however; if you are
    //      directly running it, i.e. with CompletableFuture.runAsync(),
    //      then you do have to
    public static final Function<Throwable, ? extends Void> FUTURE_ERROR_HANDLING = throwable -> {
        CyberLogUtils.logError( "Detected an error within a future! See stack trace below for details" );
        if ( throwable.getCause() == null ) { throwable.printStackTrace(); }
        else { throwable.getCause().printStackTrace(); }
        return null;
    };

    @Override
    public void onEnable() {
        // Initialize things
        CyberCore.setPlugin( this );
        new CyberVaultUtils();

        // Update/reload config files
        YMLUtils.initializeConfigs();

        // Set the primary & secondary colors from the config
        CyberColorUtils.setPrimaryColor( Settings.PRIMARY_COLOR.string() );
        CyberColorUtils.setSecondaryColor( Settings.SECONDARY_COLOR.string() );

        // Initializing bStats
        metrics = new Metrics( this, BSTATS_PLUGIN_ID );

        // Initializing databases
        CONNECTION_MANAGER.initialize();

        // Initializing the API services
        PUNISHMENT_SERVICE = new PunishmentService();
        ALT_SERVICE = new AltService();
        CHAT_SERVICE = new ChatService();
        REPORT_SERVICE = new ReportService();

        SERVICE = new NetunoService( PUNISHMENT_SERVICE, ALT_SERVICE, CHAT_SERVICE, REPORT_SERVICE );
        SERVICE.initialize();
        this.getServer().getServicesManager().register( ApiNetunoService.class, SERVICE, this, ServicePriority.Normal );

        // Registering commands
        registerCommands();

        // Registering listeners
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Closing any API services
        CHAT_SERVICE.save();
        SERVICE.close();
        this.getServer().getServicesManager().unregister( ApiNetunoService.class, SERVICE );

        // Closing database connections
        // This should be one of the last things done
        CONNECTION_MANAGER.closeConnection();
    }

    private void registerCommands() {
        new NetunoCommand();
        new PunishCommand( 1 );
        // Generates all punishment commands
        // This returns where the help order for these generated commands ended at
        int helpOrder = PunishmentCommandGenerator.generateCommands( 2 );
        // From here on, we need to use an increment by 1 from the previous help order
        new IpinfoCommand( helpOrder + 1 );
        new HistoryCommand( helpOrder + 2 );
        helpOrder += 7; // 7 history subcommands
        new ToggleSignsCommand( helpOrder + 3 );
        new ChatCommand( helpOrder + 4 );
        helpOrder += 5; // 5 chat subcommands
        helpOrder += 20; // idk why this is needed but it is
        new ReportCommand( helpOrder + 1 );
        new ViewReportsCommand( helpOrder + 2 );
        new WatchlistCommand( helpOrder + 3 );
        helpOrder += 3; // 3 watchlist subcommands
        new TeleportToSignCommand();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents( new PreLoginListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new HistoryEditManager(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );
    }
}