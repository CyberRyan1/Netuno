package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.managers.PunishGUIManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;

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

    public static ArrayList<String> getStrList( String path ) {
        checkPath( path );
        ArrayList<String> results = new ArrayList<>();
        String list[] = manager.getConfig().getStringList( path ).toArray( new String[0] );
        Collections.addAll( results, list );
        return results;
    }

    public static String getColoredStr( String path ) {
        checkPath( path );
        return Utils.getColored( manager.getConfig().getString( path ) );
    }

    public static ArrayList<String> getAllKeys() {
        ArrayList<String> results = new ArrayList<>();
        String keys[] = manager.getConfig().getKeys( true ).toArray( new String[0] );
        Collections.addAll( results, keys );
        return results;
    }

    public static ArrayList<String> getKeys( String path ) {
        checkPath( path );
        ArrayList<String> results = getAllKeys();
        for ( int index = results.size() - 1; index >= 0; index-- ) {
            String str = results.get( index );
            if ( str.startsWith( path ) == false ) {
                results.remove( index );
            }
            else if ( str.replace( path, "" ).contains( "." ) ) {
                results.remove( index );
            }
        }

        return results;
    }

    private static void checkPath( String path ) {
        if ( manager.getConfig().get( path ) == null ) {
            Utils.logError( "Punish GUI path " + path + " was not found, please check your punishgui file!" );
        }
    }
}
