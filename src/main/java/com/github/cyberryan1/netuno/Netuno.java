package com.github.cyberryan1.netuno;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Netuno extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize things with CyberCore
        CyberCore.setPlugin( this );
        new VaultUtils();

        // Update/reload config files
        YMLUtils.getConfig().getYMLManager().reloadConfig();
        YMLUtils.getConfig().getYMLManager().updateConfig();
    }

    @Override
    public void onDisable() {
    }

    private void registerSkript() {
    }

    private void registerCommands() {
    }

    private void registerEvents() {
    }
}
