package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ConfigUtils {

    private static ConfigManager config;

    private static ArrayList<String> punsWithNoLength = new ArrayList<>();

    public ConfigUtils( ConfigManager con ) {
        config = con;
        Collections.addAll( punsWithNoLength, "kick", "warn", "unmute", "unban" );
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

    // returns a string list from the path
    public static ArrayList<String> getStrList( String path ) {
        checkPath( path );
        ArrayList<String> toReturn = new ArrayList<>();
        String list[] = config.getConfig().getStringList( path ).toArray( new String[0] );
        Collections.addAll( toReturn, list );
        return toReturn;
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

        for ( String str : getStrList( path ) ) {
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
            str = str.replace( "[LENGTH]", Time.getFormattedLength( length ) );
        }
        str = str.replace( "[REASON]", reason );

        return str;
    }

    public static String replaceAllVariables( String str, Punishment pun ) {
        String targetName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getPlayerUUID() ) ).getName();
        String staffName = "CONSOLE";
        if ( pun.getStaffUUID().equalsIgnoreCase( "CONSOLE" ) == false ) {
            staffName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getStaffUUID() ) ).getName();
        }

        str = str.replace( "[STAFF]", staffName ).replace( "[TARGET]", targetName );

        if ( punsWithNoLength.contains( pun.getType().toLowerCase() ) == false ) {
            str = str.replace( "[LENGTH]", Time.getLengthFromTimestamp( pun.getLength() ) );
            str = str.replace( "[REMAIN]", Time.getLengthRemaining( pun ) );
        }

        str = str.replace( "[REASON]", pun.getReason() );

        return str;
    }

    public static String replaceStaffVariable( String str, CommandSender sender ) {
        if ( sender instanceof OfflinePlayer ) {
            OfflinePlayer staff = ( OfflinePlayer ) sender;
            return str.replace( "[STAFF]", staff.getName() );
        }

        return str.replace( "[STAFF]", "CONSOLE" );
    }

    public static ArrayList<String> getAllKeys() {
        ArrayList<String> results = new ArrayList<>();
        String keys[] = config.getConfig().getKeys( true ).toArray( new String[0] );
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

    public static String replacePunGUIVariables( String str, OfflinePlayer target, int previous ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", previous + "" );
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