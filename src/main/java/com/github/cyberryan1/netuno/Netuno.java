package com.github.cyberryan1.netuno;

import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.listeners.JoinListener;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.plugin.java.JavaPlugin;

// TODO make /netuno reload command
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

        this.getCommand( "kick" ).setExecutor( new Kick() );
        this.getCommand( "warn" ).setExecutor( new Warn() );
        this.getCommand( "mute" ).setExecutor( new Mute() );

        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );

        ConfigUtils.getConfigManager().reloadConfig();
    }
}
