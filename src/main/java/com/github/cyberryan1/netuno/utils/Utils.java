package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

public class Utils {

    private static Netuno plugin;
    private static ConfigManager configManager;
    private static Database db;

    private static final char SPECIAL_CHARS[] = { '!', '@', '#', '$', '%', '^', '&', '(', ')', '-', '=', '+', '`', '~', '[', ']',
                                                    '{', '}', '\\', '|', ':', ';', '\'', '\"', ',', '<', '.', '>', '/', '?' };

    public Utils( Netuno pl, ConfigManager cm ) {
        plugin = pl;
        configManager = cm;
    }

    public static Netuno getPlugin() { return plugin; }

    public static PluginManager getPluginManager() { return plugin.getServer().getPluginManager(); }

    public static Database getDatabase() { return db; }

    public static void setDatabase( Database database ) { db = database; }

    // Loggers
    public static void logInfo( String info ) {
        plugin.getLogger().log( Level.INFO, info );
    }

    public static void logWarn( String warn ) {
        plugin.getLogger().log( Level.WARNING, warn );
    }

    public static void logError( String error ) {
        plugin.getLogger().log( Level.SEVERE, error );
    }

    public static void logError( String error, Throwable thrown ) {
        if ( error == null || thrown == null ) { return; }
        plugin.getLogger().log( Level.SEVERE, error, thrown );
    }

    public static String getColored( String in ) {
        in = in.replace( "&g", ConfigUtils.getStr( "general.primary-color" ) ).replace( "&h", ConfigUtils.getStr( "general.secondary-color" ) );
        return ChatColor.translateAlternateColorCodes( '&', in );
    }

    // Checks if an index in an array is out of bounds
    // true = out of bounds, false = within range
    public static boolean isOutOfBounds( Object obj[], int index ) {
        try {
            Object o = obj[ index ];
        }
        catch ( IndexOutOfBoundsException e ) {
            return true;
        }

        return false;
    }

    // Gets the remaining arguments in a list
    // Useful for getting reasons, etc
    public static String getRemainingArgs( String args[], int start ) {
        String str = "";
        for ( int index = start; index < args.length; index++ ) {
            str += args[index] + " ";
        }

        return str.substring( 0, str.length() - 1 );
    }

    public static void sendAnyMsg( CommandSender target, String str ) {
        target.sendMessage( str );
        if ( str.charAt( str.length() - 1 ) == '\n' ) {
            target.sendMessage( "" );
        }
    }

    public static void sendPunishmentMsg( Player target, Punishment pun ) {
        String type = pun.getType().toLowerCase();
        if ( ConfigUtils.checkListNotEmpty( type + ".message" ) ) {
            String message = ConfigUtils.getColoredStrFromList( type + ".message" );
            sendAnyMsg( target, ConfigUtils.replaceAllVariables( message, pun ) );
        }
    }

    public static void sendDeniedMsg( Player target, Punishment pun ) {
        String type = pun.getType().toLowerCase();
        String message = ConfigUtils.getColoredStrFromList( type + ".attempt" );

        sendAnyMsg( target, ConfigUtils.replaceAllVariables( message, pun ) );
    }

    // Checks if a staff punishing another staff is allowable or not
    // false = not allowed, true = allowed
    public static boolean checkStaffPunishmentAllowable( Player staff, OfflinePlayer target ) {
        if ( ConfigUtils.getBool( "general.staff-punishments" ) == false ) {
            if ( VaultUtils.hasPerms( target, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                if ( VaultUtils.hasPerms( staff, ConfigUtils.getStr( "general.all-perms" ) ) == false ) {
                    return false;
                }
            }
        }

        return true;
    }

    // Sends a public punishment broadcast, if needed
    public static void doPublicPunBroadcast( Punishment pun ) {
        String type = pun.getType().toLowerCase();
        boolean sendToStaff = ConfigUtils.checkListNotEmpty( type + ".staff-broadcast" );
        if ( ConfigUtils.checkListNotEmpty( type + ".broadcast" ) ) {
            String broadcast = ConfigUtils.getColoredStrFromList( type + ".broadcast" );
            broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) == false || sendToStaff == false ) {
                    if ( p.getUniqueId().toString().equals( pun.getPlayerUUID() ) == false ) {
                        sendAnyMsg( p, broadcast );
                    }

                    else if ( type.equals( "kick" ) == false && type.equals( "ban" ) == false ) {
                        if ( ConfigUtils.checkListNotEmpty( type + ".message" ) == false ) {
                            sendAnyMsg( p, broadcast );
                        }
                    }
                }
            }
        }
    }

    // Sends a staff punishment broadcast, if needed
    public static void doStaffPunBroadcast( Punishment pun ) {
        String type = pun.getType().toLowerCase();
        if ( ConfigUtils.checkListNotEmpty( type + ".staff-broadcast" ) ) {
            String broadcast = ConfigUtils.getColoredStrFromList( type + ".staff-broadcast" );
            broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                    sendAnyMsg( p, broadcast );
                }
            }
        }
    }

    // Checks if a username provided is a potential username allowed by Minecraft
    // Useful so that time isn't wasted searching for a wack name
    public static boolean isValidUsername( String user ) {
        if ( user.length() < 3 || user.length() > 16 ) { return false; }
        if ( user.contains( " " ) ) { return false; }
        for ( char c : SPECIAL_CHARS ) {
            if ( user.contains( c + "" ) == true ) {
                return false;
            }
        }

        return true;
    }

    // Formats a list into a string
    // Ex: { "125", "812" } -> "125 and 812"
    // Ex: { "912" } -> "912"
    // Ex: { "192", "015", "529" } -> "192, 015, and 529"
    public static String formatListIntoFancierString( String list[] ) {
        if ( list.length == 1 ) { return list[0]; }
        if ( list.length == 2 ) { return list[0] + " and " + list[1]; }

        String toReturn = "";
        for ( int index = 0; index < list.length - 1; index++ ) {
            toReturn += list[index] + ", ";
        }

        toReturn += "and " + list[ list.length - 1 ];
        return toReturn;
    }

    // formats a list into a string
    // main difference with above is it doesn't have "and", replacing that with commas
    // ex: { "125", "812" } -> "125, 812"
    public static String formatListIntoString( String list[] ) {
        if ( list.length == 1 ) { return list[0]; }

        String toReturn = "";
        for ( int index = 0; index < list.length - 1; index++ ) {
            toReturn += list[index] + ", ";
        }

        toReturn += list[ list.length - 1 ];
        return toReturn;
    }

    // formats a list into a string
    // main difference with above is that it only shows 2 things, then "and x more"
    // ex: { "125", "812" } -> "125 and 812"
    // Ex: { "912" } -> "912"
    // Ex: { "192", "015", "529" } -> "192, 015, and 1 more"
    // Ex: { "624", "856", "347", "932" } -> "192, 015, and 2 more"
    public static String formatListIntoAmountString( String list[] ) {
        if ( list.length <= 2 ) { return formatListIntoFancierString( list ); }

        String toReturn = list[0] + ", " + list[1] + ", and ";
        return toReturn + ( list.length - 2 ) + " more";
    }

    // formats a number into a string with "-st", "-nd", "-rd" or "-th" at the end
    // ex: 1 -> "1st"
    // ex: 2 -> "2nd"
    // ex: 3 -> "3rd"
    // ex: 4 -> "4th"
    // ex: 7 -> "7th"
    public static String formatIntIntoAmountString( int number ) {
        switch ( number ) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return number + "th";
        }
    }

    // removes all colorcodes from a string
    public static String removeColorCodes( String input ) {
        String colorCodes[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "a", "b", "c", "d", "e", "f", "n", "m", "l", "o" };
        for ( String cc : colorCodes ) {
            input = input.replace( Utils.getColored( "&" + cc ), "" );
        }

        return input;
    }

    // gets the capitalized version of a punishment
    public static String getCapitalizedPunishment( String type ) {
        switch ( type.toLowerCase() ) {
            case "ban": return "Ban";
            case "ipban": return "IPBan";
            case "ipmute": return "IPMute";
            case "kick": return "Kick";
            case "mute": return "Mute";
            case "unipban": return "UnIPBan";
            case "unipmute": return "UnIPMute";
            case "unban": return "Unban";
            case "unmute": return "Unmute";
            case "warn": return "Warn";
            default: return null;
        }
    }

    // gets the current java version
    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }
}