package com.github.cyberryan1.netuno;

import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.plugin.java.JavaPlugin;

// TODO unipmute needs to alert all online alts
// TODO add tab-completer
// TODO add option to disable signs for muted players
// TODO add sign-popups for staff
// TODO add a [MAIN] variable in config for the main plugin color
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

        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );

        ConfigUtils.getConfigManager().reloadConfig();
    }
}
