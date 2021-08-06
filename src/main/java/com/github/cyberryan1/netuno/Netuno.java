package com.github.cyberryan1.netuno;

import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.managers.PunishGUIManager;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.PunishGUIUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.plugin.java.JavaPlugin;

/* notes in to do:
    - ! = bug needs to be fixed
    - * = working on
    - space = will work on later
    - ? = unsure if doing
    - % = needs testing */
// ! TODO history and alts may not work if they have none in Punish GUI

// % TODO add a chat-slow feature

// TODO add a way so that you don't have to delete the config.yml in between each update
// TODO add silent feature
// TODO add silent option in punish GUI
// TODO add option to disable books for muted/ipmuted players
// TODO add a [MAIN] variable in config for the main plugin color
// TODO add a [SECONDARY] (or something of the like) variable in config for the secondary plugin color

// ? TODO add sounds in (some) GUIs when you click
// ? TODO add a "protected" permission (basically not allowing them to be punished)
// ? TODO staffchat command & prefix
// ? TODO change signs to redstone/emerald, depending on active (history list GUI)
// ? TODO add a way to convert vanilla bans to netuno bans
public final class Netuno extends JavaPlugin {

    private ConfigManager config;
    private PunishGUIManager punishGUIConfig;

    private Utils util;
    private ConfigUtils configUtils;
    private VaultUtils vaultUtils;
    private PunishGUIUtils punishGUIUtils;

    @Override
    public void onEnable() {
        config = new ConfigManager( this );
        punishGUIConfig = new PunishGUIManager( this );

        util = new Utils(this, config );
        util.setupDatabase();

        configUtils = new ConfigUtils( config );
        vaultUtils = new VaultUtils();
        punishGUIUtils = new PunishGUIUtils( punishGUIConfig );

        this.getCommand( "netuno" ).setExecutor( new NetunoCmd() );
        this.getCommand( "netuno" ).setTabCompleter( new TabComplete( "netuno" ) );
        this.getCommand( "kick" ).setExecutor( new Kick() );
        this.getCommand( "kick" ).setTabCompleter( new TabComplete( "kick" ) );
        this.getCommand( "warn" ).setExecutor( new Warn() );
        this.getCommand( "warn" ).setTabCompleter( new TabComplete( "warn" ) );
        this.getCommand( "mute" ).setExecutor( new Mute() );
        this.getCommand( "mute" ).setTabCompleter( new TabComplete( "mute" ) );
        this.getCommand( "unmute" ).setExecutor( new Unmute() );
        this.getCommand( "unmute" ).setTabCompleter( new TabComplete( "unmute" ) );
        this.getCommand( "ban" ).setExecutor( new Ban() );
        this.getCommand( "ban" ).setTabCompleter( new TabComplete( "ban" ) );
        this.getCommand( "unban" ).setExecutor( new Unban() );
        this.getCommand( "unban" ).setTabCompleter( new TabComplete( "unban" ) );
        this.getCommand( "ipinfo" ).setExecutor( new IPInfo() );
        this.getCommand( "ipinfo" ).setTabCompleter( new TabComplete( "ipinfo" ) );
        this.getCommand( "ipmute" ).setExecutor( new IPMute() );
        this.getCommand( "ipmute" ).setTabCompleter( new TabComplete( "ipmute" ) );
        this.getCommand( "unipmute" ).setExecutor( new UnIPMute() );
        this.getCommand( "unipmute" ).setTabCompleter( new TabComplete( "unipmute" ) );
        this.getCommand( "ipban" ).setExecutor( new IPBan() );
        this.getCommand( "ipban" ).setTabCompleter( new TabComplete( "ipban" ) );
        this.getCommand( "unipban" ).setExecutor( new UnIPBan() );
        this.getCommand( "unipban" ).setTabCompleter( new TabComplete( "unipban" ) );
        this.getCommand( "history" ).setExecutor( new History() );
        this.getCommand( "history" ).setTabCompleter( new TabComplete( "history" ) );
        this.getCommand( "togglesigns" ).setExecutor( new Togglesigns() );
        this.getCommand( "togglesigns" ).setTabCompleter( new TabComplete( "togglesigns" ) );
        this.getCommand( "mutechat" ).setExecutor( new Mutechat() );
        this.getCommand( "mutechat" ).setTabCompleter( new TabComplete( "mutechat" ) );
        this.getCommand( "clearchat" ).setExecutor( new Clearchat() );
        this.getCommand( "clearchat" ).setTabCompleter( new TabComplete( "clearchat" ) );
        this.getCommand( "reports" ).setExecutor( new Reports() );
        this.getCommand( "reports" ).setTabCompleter( new TabComplete( "reports" ) );
        this.getCommand( "report" ).setExecutor( new Report() );
        this.getCommand( "report" ).setTabCompleter( new TabComplete( "report" ) );
        this.getCommand( "punish" ).setExecutor( new Punish() );
        this.getCommand( "punish" ).setTabCompleter( new TabComplete( "punish" ) );
        this.getCommand( "chatslow" ).setExecutor( new Chatslow() );
        this.getCommand( "chatslow" ).setTabCompleter( new TabComplete( "chatslow" ) );

        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new GUIEventManager(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );

        ConfigUtils.getConfigManager().reloadConfig();
        PunishGUIUtils.getPunishGUIManager().reloadConfig();
    }
}
