package com.github.cyberryan1.netuno;

import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Netuno extends JavaPlugin {

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
    }

    @Override
    public void onDisable() {

    }
}