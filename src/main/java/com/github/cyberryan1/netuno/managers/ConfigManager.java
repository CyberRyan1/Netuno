package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.netuno.Netuno;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

public class ConfigManager {

    public Netuno plugin;

    private FileConfiguration config;
    private File configFile;

    public ConfigManager( Netuno pl ) {
        plugin = pl;
        saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        if ( config == null ) {
            reloadConfig();
        }

        return config;
    }

    public File getConfigFile() { return configFile; }

    public void reloadConfig() {
        if ( configFile == null ) {
            configFile = new File( plugin.getDataFolder(), "config.yml" );
        }

        config = YamlConfiguration.loadConfiguration( configFile );

        InputStream defaultStream = plugin.getResource( "config.yml" );
        if ( defaultStream != null ) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration( new InputStreamReader( defaultStream ) );
            config.setDefaults( defaultConfig );
        }
    }

    public void saveConfig() {
        if ( config == null || configFile == null ) {
            return;
        }

        plugin.saveConfig();
    }

    // Saves the default config
    public void saveDefaultConfig() {
        if ( configFile == null ) {
            configFile = new File( plugin.getDataFolder(), "config.yml" );
        }

        if ( configFile.exists() == false ) {
            plugin.saveResource( "config.yml", false );
        }
    }

    // Checks if any new keys, comments, etc, have been added via a plugin update
    // And adds them to the current config (if needed)
    // Note: must reload the config afterward for changes to come into effect
    public void updateConfig() {
        if ( configFile == null ) {
            configFile = new File( plugin.getDataFolder(), "config.yml" );
        }
        if ( configFile.exists() == false ) {
            plugin.saveResource( "config.yml", false );
        }

        try { ConfigUpdater.update( plugin, "config.yml", configFile, Collections.emptyList() );
        } catch ( IOException e ) { e.printStackTrace(); }

        reloadConfig();
    }
}
