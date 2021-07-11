package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.managers.PunishGUIManager;
import org.bukkit.configuration.file.FileConfiguration;

public class PunishGUIUtils {

    private static PunishGUIManager punishGUIManager;


    public PunishGUIUtils( PunishGUIManager pgm ) {
        punishGUIManager = pgm;
    }

    public static FileConfiguration getConfig() { return punishGUIManager.getConfig(); }

    public static PunishGUIManager getPunishGUIManager() { return punishGUIManager; }
}
