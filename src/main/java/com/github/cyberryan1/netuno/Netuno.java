package com.github.cyberryan1.netuno;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.guis.history.HistoryEditManager;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.skriptelements.conditions.RegisterConditions;
import com.github.cyberryan1.netuno.skriptelements.expressions.RegisterExpressions;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.NetunoApi;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Note about the below TODO list: it is EXTREMELY outdated. It is here
 * mostly as a... memorial piece? I guess that's a good way to put it. idk lol
 */
/* notes in to do:
    - ! = bug needs to be fixed
    - * = working on
    - space = will work on later
    - ? = unsure if doing
    - % = needs testing */
// ! TODO history and alts may not work if they have none in Punish GUI

// TODO add silent option in punish GUI
// TODO add option to disable books for muted/ipmuted players

// ? TODO add a "protected" permission (basically not allowing them to be punished)
// ? TODO staffchat command & prefix
// ? TODO add a way to convert vanilla bans to netuno bans
public final class Netuno extends JavaPlugin {

    public static final List<BaseCommand> registeredCommands = new ArrayList<>();

    private ChatslowManager chatslowManager;

    // Skript
    public SkriptAddon addon;
    public boolean enabled = true;

    // bStats
    private static final int BSTATS_PLUGIN_ID = 21155;
    public static Metrics metrics;

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

        ApiNetuno.setupInstance();
        this.getServer().getServicesManager().register( NetunoApi.class, ApiNetuno.getInstance(), this, ServicePriority.Highest );
        chatslowManager = new ChatslowManager();

        // Setup the watchlist manager
        WatchlistManager.initialize();

        registerSkript();
        registerCommands();
        registerEvents();

        // Initializing bStats
        metrics = new Metrics( this, BSTATS_PLUGIN_ID );
    }

    @Override
    public void onDisable() {
        // Save the watchlist
        WatchlistManager.save();

        this.getServer().getServicesManager().unregister( NetunoApi.class, ApiNetuno.getInstance() );
        ApiNetuno.deleteInstance();
    }

    private void registerSkript() {
        try {
            addon = Skript.registerAddon( this ).setLanguageFileDirectory( "lang" );;
            try {
                addon.loadClasses( "com.github.cyberryan1.netuno.skriptelements" );
            } catch ( IOException e ) {
                CyberLogUtils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
                enabled = false;
            }
            CyberLogUtils.logInfo( "Successfully enabled as a skript addon" );
            RegisterExpressions.register();
            RegisterConditions.register();
        } catch ( NoClassDefFoundError error ) {
            CyberLogUtils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
            enabled = false;
        }

        Skript.getEvents().forEach( event -> {
            CyberLogUtils.logWarn( event.getName() + event.getOriginClassPath() );
        } );
    }

    private void registerCommands() {
        // Note: this is order in the same order the /netuno help command should show them
        // Lower helpOrder variables -> will be shown first
        registeredCommands.add( new NetunoCommand() );

        registeredCommands.add( new PunishCommand( 100 ) );
        registeredCommands.add( new HistorySuperCommand( 200 ) ); // Subcommands of this take up
                                                                            // 210, 220, and 230

        registeredCommands.add( new WarnCommand( 300 ) );
        registeredCommands.add( new KickCommand( 400 ) );
        registeredCommands.add( new MuteCommand( 500 ) );
        registeredCommands.add( new UnmuteCommand( 600 ) );
        registeredCommands.add( new BanCommand( 700 ) );
        registeredCommands.add( new UnbanCommand( 800 ) );
        registeredCommands.add( new IpMuteCommand( 900 ) );
        registeredCommands.add( new UnIpmuteCommand( 1000 ) );
        registeredCommands.add( new IpBanCommand( 1100 ) );
        registeredCommands.add( new UnIpbanCommand( 1200 ) );
        registeredCommands.add( new IpInfoCommand( 1300 ) );

        registeredCommands.add( new ReportCommand( 1400 ) );
        registeredCommands.add( new ReportsCommand( 1500 ) );

        registeredCommands.add( new MutechatCommand( 1600 ) );
        registeredCommands.add( new ClearchatCommand( 1700 ) );
        registeredCommands.add( new ChatslowCommand( 1800 ) );

        registeredCommands.add( new WatchlistSuperCommand( 1900 ) ); // Subcommands of this take up
                                                                            // 1910, 1920, and 1930

        registeredCommands.add( new ToggleSignsCommand( 2000 ) );

        // Registering all subcommands of each supercommands
        List<CyberSubCommand> toRegister = new ArrayList<>();
        for ( BaseCommand cmd : registeredCommands ) {
            if ( cmd instanceof CyberSuperCommand == false ) { continue; }
            toRegister.addAll( ( ( CyberSuperCommand ) cmd ).getSubCommandList() );
        }
        registeredCommands.addAll( toRegister );
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );
        this.getServer().getPluginManager().registerEvents( new HistoryEditManager(), this );
    }
}
