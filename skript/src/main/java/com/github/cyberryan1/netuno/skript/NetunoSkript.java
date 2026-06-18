package com.github.cyberryan1.netuno.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.api.services.ApiNetunoService;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class NetunoSkript extends JavaPlugin {

    public static ApiNetunoService SERVICE = null;

    public SkriptAddon addon;
    public boolean enabled = true;

    @Override
    public void onEnable() {
        CyberCore.setPlugin( this );
        new CyberVaultUtils();

        SERVICE = this.getServer().getServicesManager().load( ApiNetunoService.class );

        try {
            addon = Skript.registerAddon( this );
            try {
                addon.loadClasses( "com.github.cyberryan1.netuno.skript", "elements" );
            } catch ( IOException e ) {
                CyberLogUtils.logError( "Could not enable as a skript addon" );
                e.printStackTrace();
                enabled = false;
            }

            CyberLogUtils.logInfo( "skNetuno addon enabled" );

        } catch ( NoClassDefFoundError e ) {
            CyberLogUtils.logError( "Could not enable as a skript addon" );
            e.printStackTrace();
            enabled = false;
        }
    }

    @Override
    public void onDisable() {

    }
}