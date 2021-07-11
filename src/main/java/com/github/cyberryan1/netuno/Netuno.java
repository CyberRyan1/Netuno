package com.github.cyberryan1.netuno;

import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.plugin.java.JavaPlugin;

/* notes in to do:
    - ! = bug needs to be fixed
    - * = working on
    - space = will work on later
    - ? = unsure if doing */

// * TODO add punish GUI


// TODO add tab-completer
// TODO add option to disable books for muted/ipmuted players
// TODO add a [MAIN] variable in config for the main plugin color
// TODO add a [SECONDARY] (or something of the like) variable in config for the secondary plugin color

// ? TODO add sounds in (some) GUIs when you click
// ? TODO add a "protected" permission (basically not allowing them to be punished)
// ? TODO staffchat command & prefix
// ? TODO change signs.notifs-perm to "netuno.signs.notifs"
// ? TODO change signs to redstone/emerald, depending on active (history list GUI)
// ? TODO add a way to convert vanilla bans to netuno bans
public final class Netuno extends JavaPlugin {

    private ConfigManager config;

    private Utils util;
    private ConfigUtils configUtils;
    private VaultUtils vaultUtils;

    @Override
    public void onEnable() {
        config = new ConfigManager( this );

        util = new Utils(this, config );
        util.setupDatabase();

        configUtils = new ConfigUtils( config );
        vaultUtils = new VaultUtils();

        this.getCommand( "netuno" ).setExecutor( new NetunoCmd() );
        this.getCommand( "kick" ).setExecutor( new Kick() );
        this.getCommand( "warn" ).setExecutor( new Warn() );
        this.getCommand( "mute" ).setExecutor( new Mute() );
        this.getCommand( "unmute" ).setExecutor( new Unmute() );
        this.getCommand( "ban" ).setExecutor( new Ban() );
        this.getCommand( "unban" ).setExecutor( new Unban() );
        this.getCommand( "ipinfo" ).setExecutor( new IPInfo() );
        this.getCommand( "ipmute" ).setExecutor( new IPMute() );
        this.getCommand( "unipmute" ).setExecutor( new UnIPMute() );
        this.getCommand( "ipban" ).setExecutor( new IPBan() );
        this.getCommand( "unipban" ).setExecutor( new UnIPBan() );
        this.getCommand( "history" ).setExecutor( new History() );
        this.getCommand( "togglesigns" ).setExecutor( new Togglesigns() );
        this.getCommand( "mutechat" ).setExecutor( new Mutechat() );
        this.getCommand( "clearchat" ).setExecutor( new Clearchat() );
        this.getCommand( "reports" ).setExecutor( new Reports() );
        this.getCommand( "report" ).setExecutor( new Report() );

        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new GUIEventManager(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );

        ConfigUtils.getConfigManager().reloadConfig();
    }
}
