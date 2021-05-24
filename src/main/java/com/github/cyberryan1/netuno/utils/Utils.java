package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.managers.ConfigManager;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.database.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;
import java.util.logging.Level;

public class Utils {

    private static Netuno plugin;
    private static ConfigManager configManager;
    private static Database db;

    public Utils( Netuno pl, ConfigManager cm ) {
        plugin = pl;
        configManager = cm;
    }

    public static Netuno getPlugin() { return plugin; }

    public static PluginManager getPluginManager() { return plugin.getServer().getPluginManager(); }

    public static Database getDatabase() { return db; }

    // Setup SQLite
    public void setupDatabase() {
        db = new SQLite( plugin );
        db.load();
    }

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

    public static String getColored( String in ) { return ChatColor.translateAlternateColorCodes( '&', in ); }

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

    public static void sendPunishmentMsg( Player target, Punishment pun ) {
        String type = pun.getType().toLowerCase();
        String message = ConfigUtils.getColoredStrFromList( type + ".message" );

        message = ConfigUtils.replaceAllVariables( message, pun );
        target.sendMessage( message );
        if ( message.charAt( message.length() - 1 ) == '\n' ) {
            target.sendMessage( "" );
        }
    }

    public static void sendDeniedMsg( Player target, Punishment pun ) {
        String type = pun.getType().toLowerCase();
        String message = ConfigUtils.getColoredStrFromList( type + ".attempt" );

        message = ConfigUtils.replaceAllVariables( message, pun );
        target.sendMessage( message );
        if ( message.charAt( message.length() - 1 ) == '\n' ) {
            target.sendMessage( "" );
        }
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

            String staffName = "CONSOLE";
            if ( pun.getStaffUUID().equalsIgnoreCase( "CONSOLE" ) == false ) {
                staffName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getStaffUUID() ) ).getName();
            }
            String targetName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getPlayerUUID() ) ).getName();

            String length = "";
            if ( pun.getType().equals( "warn" ) == false && pun.getType().equals( "kick" ) == false ) {
                length = Time.getLengthFromTimestamp( pun.getLength() );
            }

            broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) == false || sendToStaff == false ) {
                    p.sendMessage( broadcast );
                    if ( broadcast.charAt( broadcast.length() - 1 ) == '\n' ) {
                        p.sendMessage( "" );
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

            String staffName = "CONSOLE";
            if ( pun.getStaffUUID().equalsIgnoreCase( "CONSOLE" ) == false ) {
                staffName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getStaffUUID() ) ).getName();
            }
            String targetName = Bukkit.getOfflinePlayer( UUID.fromString( pun.getPlayerUUID() ) ).getName();

            String length = "";
            if ( pun.getType().equals( "warn" ) == false && pun.getType().equals( "kick" ) == false ) {
                length = Time.getLengthFromTimestamp( pun.getLength() );
            }

            broadcast = ConfigUtils.replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                    p.sendMessage( broadcast );
                    if ( broadcast.charAt( broadcast.length() - 1 ) == '\n' ) {
                        p.sendMessage( "" );
                    }
                }
            }
        }
    }
}