package com.github.cyberryan1.netuno;

import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.commands.NetunoCommand;
import com.github.cyberryan1.netuno.commands.PunishCommand;
import com.github.cyberryan1.netuno.database.ConnectionManager;
import com.github.cyberryan1.netuno.listeners.ChatListener;
import com.github.cyberryan1.netuno.listeners.PreLoginListener;
import com.github.cyberryan1.netuno.models.AltService;
import com.github.cyberryan1.netuno.models.NetunoService;
import com.github.cyberryan1.netuno.models.PunishmentService;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

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
 */

public final class Netuno extends JavaPlugin {

    // Database Connection
    public static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    // API Things
    public static final PunishmentService PUNISHMENT_SERVICE = new PunishmentService();
    public static final AltService ALT_SERVICE = new AltService();
    public static final NetunoService SERVICE = new NetunoService( PUNISHMENT_SERVICE, ALT_SERVICE );

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

        // Initializing bStats
        metrics = new Metrics( this, BSTATS_PLUGIN_ID );

        // Initializing databases
        CONNECTION_MANAGER.initialize();

        // Initializing the API services
        SERVICE.initialize();

        // Registering commands
        registerCommands();

        // Registering listeners
    }

    @Override
    public void onDisable() {
        // Closing any API services
        SERVICE.close();

        // Closing database connections
        // This should be one of the last things done
        CONNECTION_MANAGER.closeConnection();
    }

    private void registerCommands() {
        new NetunoCommand();
        new PunishCommand( 1 );
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents( new PreLoginListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
    }
}