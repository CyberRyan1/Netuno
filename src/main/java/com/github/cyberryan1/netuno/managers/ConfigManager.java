package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.netuno.Netuno;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
