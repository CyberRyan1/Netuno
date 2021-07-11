package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.managers.PunishGUIManager;
import org.bukkit.configuration.file.FileConfiguration;

public class PunishGUIUtils {

    private static PunishGUIManager manager;


    public PunishGUIUtils( PunishGUIManager pgm ) {
        manager = pgm;
    }

    public static FileConfiguration getConfig() { return manager.getConfig(); }

    public static PunishGUIManager getPunishGUIManager() { return manager; }

    public static Object get( String path ) {
        checkPath( path );
        return manager.getConfig().get( path );
    }

    public static boolean getBool( String path ) {
        checkPath( path );
        return manager.getConfig().getBoolean( path );
    }

    public static int getInt( String path ) {
        checkPath( path );
        return manager.getConfig().getInt( path );
    }

    public static double getDouble( String path ) {
        checkPath( path );
        return manager.getConfig().getDouble( path );
    }

    public static String getStr( String path ) {
        checkPath( path );
        return manager.getConfig().getString( path );
    }

    public static String getColoredStr( String path ) {
        checkPath( path );
        return manager.getConfig().getString( path );
    }

    private static void checkPath( String path ) {
        if ( manager.getConfig().get( path ) == null ) {
            Utils.logError( "Punish GUI path " + path + " was not found, please check your punishgui file!" );
        }
    }
}
