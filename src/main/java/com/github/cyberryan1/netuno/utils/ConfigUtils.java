package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.managers.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class ConfigUtils {

    private static ConfigManager config;

    public ConfigUtils( ConfigManager con ) {
        config = con;
    }

    // returns the config of the ConfigManager
    public static FileConfiguration getConfig() { return config.getConfig(); }

    // returns the ConfigManager
    public static ConfigManager getConfigManager() { return config; }

    // returns a boolean value from the path
    public static boolean getBool( String path ) {
        checkPath( path );
        return config.getConfig().getBoolean( path );
    }

    // returns an int from the path
    public static int getInt( String path ) {
        checkPath( path );
        return config.getConfig().getInt( path );
    }

    // returns a float from the path
    public static float getFloat( String path ) {
        return ( float ) getDouble( path );
    }

    // returns a double from the path
    public static double getDouble( String path ) {
        checkPath( path );
        return config.getConfig().getDouble( path );
    }

    // returns a string from the path
    public static String getStr( String path ) {
        checkPath( path );
        return config.getConfig().getString( path );
    }

    // gets a colored string from the path
    public static String getColoredStr( String path ) {
        if ( getStr( path ) == null ) { return null; }
        return Utils.getColored( getStr( path ) );
    }

    // gets a colored string list from the path
    public static ArrayList<String> getColoredStrList( String path ) {
        checkPath( path );
        ArrayList<String> toReturn = new ArrayList<>();
        String list[] = config.getConfig().getStringList( path ).toArray( new String[0] );

        for ( String str : list ) {
            toReturn.add( Utils.getColored( str ) );
        }

        return toReturn;
    }

    // gets a colored string from a list from the path
    public static String getColoredStrFromList( String path ) {
        ArrayList<String> list = getColoredStrList( path );
        String toReturn = "";

        for ( int index = 0; index < list.size(); index++ ) {
            if ( index + 1 < list.size() ) {
                toReturn += list.get( index ) + "\n";
            }
            else if ( list.get( index ).equals( "" ) ) {
                toReturn += "\n";
            }
            else {
                toReturn += list.get( index );
            }
        }

        return toReturn;
    }

    // replaces all the config variables with the actual things
    public static String replaceAllVariables( String str, String staff, String target, String length, String reason ) {
        str = str.replace( "[STAFF]", staff ).replace( "[TARGET]", target );
        if ( length.length() > 0 ) {
            str = str.replace( "[LENGTH]", length ).replace( "[FANCY_LENGTH]", Time.getFormattedLength( length ) );
        }
        str = str.replace( "[REASON]", reason );

        return str;
    }

    // checks if a list in the config is just full of nothing ("")
    public static boolean checkListNotEmpty( String path ) {
        checkPath( path );
        String list[] = config.getConfig().getStringList( path ).toArray( new String[0] );

        for ( String str : list ) {
            if ( str.equals( "" ) == false ) { return true; }
        }

        return false;
    }

    // sends a console warning if a path returns null
    private static void checkPath( String path ) {
        if ( config.getConfig().get( path ) == null ) {
            Utils.logError( "Config path " + path + " was not found, please check your config!" );
        }
    }
}
