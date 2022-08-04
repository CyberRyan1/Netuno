package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Utils {

    private static final char SPECIAL_CHARS[] = { '!', '@', '#', '$', '%', '^', '&', '(', ')', '-', '=', '+', '`', '~', '[', ']',
                                                    '{', '}', '\\', '|', ':', ';', '\'', '\"', ',', '<', '>', '/', '?' };

    private static final ArrayList<String> punsWithNoLength = new ArrayList<>( List.of( "kick", "warn", "unmute", "unban" ) );

    // Loggers
    public static void logInfo( String info ) {
        CyberCore.getPlugin().getLogger().log( Level.INFO, info );
    }

    public static void logWarn( String warn ) {
        CyberCore.getPlugin().getLogger().log( Level.WARNING, warn );
    }

    public static void logError( String error ) {
        CyberCore.getPlugin().getLogger().log( Level.SEVERE, error );
    }

    public static void logError( String error, Throwable thrown ) {
        if ( error == null || thrown == null ) { return; }
        CyberCore.getPlugin().getLogger().log( Level.SEVERE, error, thrown );
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
        if ( str.length() < 1 ) { return; }
        if ( str.charAt( str.length() - 1 ) == '\n' ) {
            target.sendMessage( "" );
        }
    }

    public static void sendPunishmentMsg( Player target, NPunishment pun ) {
        String type = pun.getPunishmentType().name().toLowerCase();
        String list[] = YMLUtils.getConfig().getColoredStrList( type + ".message" );
        if ( list != null && list.length > 0 ) {
            String msg = getCombinedString( list );
            sendAnyMsg( target, replaceAllVariables( msg, pun ) );
        }
    }

    public static void sendDeniedMsg( Player target, NPunishment pun ) {
        String type = pun.getPunishmentType().name().toLowerCase();
        String message = getCombinedString( YMLUtils.getConfig().getColoredStrList( type + ".attempt" ) );

        sendAnyMsg( target, replaceAllVariables( message, pun ) );
    }

    // Checks if a staff punishing another staff is allowable or not
    // false = not allowed, true = allowed
    public static boolean checkStaffPunishmentAllowable( Player staff, OfflinePlayer target ) {
        if ( YMLUtils.getConfig().getBool( "general.staff-punishments" ) == false ) {
            if ( VaultUtils.hasPerms( target, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                if ( VaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "general.all-perms" ) ) == false ) {
                    return false;
                }
            }
        }

        return true;
    }

    // Sends a public punishment broadcast, if needed
    public static void doPublicPunBroadcast( NPunishment pun ) {
        String type = pun.getPunishmentType().name().toLowerCase();
        String staffMessageList[] = YMLUtils.getConfig().getColoredStrList( type + ".staff-broadcast" );
        boolean sendToStaff = staffMessageList != null && staffMessageList.length > 0;

        String publicBroadcastList[] = YMLUtils.getConfig().getColoredStrList( type + ".broadcast" );
        if ( publicBroadcastList != null && publicBroadcastList.length > 0 ) {
            String broadcast = getCombinedString( publicBroadcastList );
            broadcast = replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) == false || sendToStaff == false ) {
                    if ( p.getUniqueId().toString().equals( pun.getPlayerUuid() ) == false ) {
                        sendAnyMsg( p, broadcast );
                    }

                    else if ( type.equals( "kick" ) == false && type.equals( "ban" ) == false ) {
                        String msgList[] = YMLUtils.getConfig().getColoredStrList( type + ".message" );
                        if ( msgList != null && msgList.length > 0 ) {
                            sendAnyMsg( p, broadcast );
                        }
                    }
                }
            }
        }
    }

    // Sends a staff punishment broadcast, if needed
    public static void doStaffPunBroadcast( NPunishment pun ) {
        String type = pun.getPunishmentType().name().toLowerCase();

        String staffMessageList[] = YMLUtils.getConfig().getColoredStrList( type + ".staff-broadcast" );
        if ( staffMessageList != null && staffMessageList.length > 0 ) {
            String broadcast = getCombinedString( staffMessageList );
            broadcast = replaceAllVariables( broadcast, pun );

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
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
            input = input.replace( CoreUtils.getColored( "&" + cc ), "" );
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

    // gets the server version
    // ex: if server is 1.8, returns 8. if server is 1.16, returns 16
    public static int getServerVersion() {
        String ver = Bukkit.getServer().getVersion();
        for ( int i = 8; i < 18; i++ ) {
            if ( ver.contains( "1." + i ) ) { return i; }
        }
        return 18;
    }

    // Combines a string list into a singular string
    public static String getCombinedString( String list[] ) {
        if ( list == null ) { return null; }
        if ( list.length == 0 ) { return ""; }
        return String.join( "\n", list );
    }

    public static String replaceAllVariables( String str, NPunishment pun ) {
        String targetName = pun.getPlayer().getName();
        String staffName = "CONSOLE";
        if ( pun.getStaffUuid().equalsIgnoreCase( "CONSOLE" ) == false ) {
            staffName = pun.getStaff().getName();
        }

        str = str.replace( "[STAFF]", staffName ).replace( "[TARGET]", targetName );

        if ( pun.getPunishmentType().hasNoLength() == false ) {
            str = str.replace( "[LENGTH]", pun.getTimeLength().asFormatted() );
            str = str.replace( "[REMAIN]", pun.getLengthRemaining().asFormatted() );
        }
        if ( pun.getPunishmentType().isIpPunishment() ) {
            final NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( pun.getPlayerUuid() );
            List<OfflinePlayer> allAltsList = nPlayer.getAltGroup().getAlts();
            allAltsList.remove( nPlayer.getPlayer() );

            List<String> priorityAltsList = new ArrayList<>();
            priorityAltsList.add( nPlayer.getPlayer().getName() );
            priorityAltsList.addAll( allAltsList.stream()
                    .map( OfflinePlayer::getName )
                    .collect( Collectors.toList() )
            );

            str = str.replace( "[ACCOUNTS]", formatListIntoAmountString( priorityAltsList.toArray( new String[0] ) ) );
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

    public static String replacePunGUIVariables( String str, OfflinePlayer target, int previous ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", previous + "" );
    }
}