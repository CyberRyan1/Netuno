package com.github.cyberryan1.netuno;

import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.database.ConnectionManager;
import com.github.cyberryan1.netuno.models.NetunoService;
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
    public static final NetunoService SERVICE = new NetunoService();

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
    }

    @Override
    public void onDisable() {

    }
}