package com.github.cyberryan1.netuno;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.skriptelements.conditions.RegisterConditions;
import com.github.cyberryan1.netuno.skriptelements.expressions.RegisterExpressions;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

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

    private ConfigManager config;
    private ChatslowManager chatslowManager;

    private Utils util;
    private ConfigUtils configUtils;
    private VaultUtils vaultUtils;

    // Skript
    public SkriptAddon addon;
    public boolean enabled = true;

    @Override
    public void onEnable() {
        config = new ConfigManager( this );

        util = new Utils(this, config );
        util.setupDatabase();

        configUtils = new ConfigUtils( config );
        vaultUtils = new VaultUtils();
        chatslowManager = new ChatslowManager();

        registerSkript();
        registerCommands();
        registerEvents();

        ConfigUtils.getConfigManager().updateConfig();
    }

    @Override
    public void onDisable() {
        Utils.getDatabase().close();
    }

    private void registerSkript() {
        try {
            addon = Skript.registerAddon( this );
            try {
                addon.loadClasses( "com.github.cyberryan1", "skriptelements" );
            } catch ( IOException e ) {
                Utils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
                enabled = false;
            }
            Utils.logInfo( "Successfully enabled as a skript addon" );
            RegisterExpressions.register();
            RegisterConditions.register();
        } catch ( NoClassDefFoundError error ) {
            Utils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
            enabled = false;
        }
    }

    private void registerCommands() {
        NetunoCmd netunoCmd = new NetunoCmd();
        this.getCommand( "netuno" ).setExecutor( netunoCmd );
        this.getCommand( "netuno" ).setTabCompleter( netunoCmd );

        Kick kick = new Kick();
        this.getCommand( "kick" ).setExecutor( kick );
        this.getCommand( "kick" ).setTabCompleter( kick );

        Warn warn = new Warn();
        this.getCommand( "warn" ).setExecutor( warn );
        this.getCommand( "warn" ).setTabCompleter( warn );

        Mute mute = new Mute();
        this.getCommand( "mute" ).setExecutor( mute );
        this.getCommand( "mute" ).setTabCompleter( mute );

        Unmute unmute = new Unmute();
        this.getCommand( "unmute" ).setExecutor( unmute );
        this.getCommand( "unmute" ).setTabCompleter( unmute );

        Ban ban = new Ban();
        this.getCommand( "ban" ).setExecutor( ban );
        this.getCommand( "ban" ).setTabCompleter( ban );

        Unban unban = new Unban();
        this.getCommand( "unban" ).setExecutor( unban );
        this.getCommand( "unban" ).setTabCompleter( unban );

        IPInfo ipinfo = new IPInfo();
        this.getCommand( "ipinfo" ).setExecutor( ipinfo );
        this.getCommand( "ipinfo" ).setTabCompleter( ipinfo );

        IPMute ipmute = new IPMute();
        this.getCommand( "ipmute" ).setExecutor( ipmute );
        this.getCommand( "ipmute" ).setTabCompleter( ipmute );

        UnIPMute unipmute = new UnIPMute();
        this.getCommand( "unipmute" ).setExecutor( unipmute );
        this.getCommand( "unipmute" ).setTabCompleter( unipmute );

        IPBan ipban = new IPBan();
        this.getCommand( "ipban" ).setExecutor( ipban );
        this.getCommand( "ipban" ).setTabCompleter( ipban );

        UnIPBan unipban = new UnIPBan();
        this.getCommand( "unipban" ).setExecutor( unipban );
        this.getCommand( "unipban" ).setTabCompleter( unipban );

        History history = new History();
        this.getCommand( "history" ).setExecutor( history );
        this.getCommand( "history" ).setTabCompleter( history );

        Togglesigns togglesigns = new Togglesigns();
        this.getCommand( "togglesigns" ).setExecutor( togglesigns );
        this.getCommand( "togglesigns" ).setTabCompleter( togglesigns );

        Mutechat mutechat = new Mutechat();
        this.getCommand( "mutechat" ).setExecutor( mutechat );
        this.getCommand( "mutechat" ).setTabCompleter( mutechat );

        Clearchat clearchat = new Clearchat();
        this.getCommand( "clearchat" ).setExecutor( clearchat );

        Reports reports = new Reports();
        this.getCommand( "reports" ).setExecutor( reports );
        this.getCommand( "reports" ).setTabCompleter( reports );

        Report report = new Report();
        this.getCommand( "report" ).setExecutor( report );
        this.getCommand( "report" ).setTabCompleter( report );

        Punish punish = new Punish();
        this.getCommand( "punish" ).setExecutor( punish );
        this.getCommand( "punish" ).setTabCompleter( punish );

        Chatslow chatslow = new Chatslow();
        this.getCommand( "chatslow" ).setExecutor( chatslow );
        this.getCommand( "chatslow" ).setTabCompleter( chatslow );
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new GUIEventManager(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );
    }
}
