package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

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
            if ( CyberVaultUtils.hasPerms( target, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                if ( CyberVaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "general.all-perms" ) ) == false ) {
                    return false;
                }
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
            str = str.replace( "[LENGTH]", pun.getTimeLength().asFullLength( 3 ) );
            str = str.replace( "[REMAIN]", pun.getLengthRemaining().asFullLength( 3 ) );
        }
        if ( pun.getPunishmentType().isIpPunishment() ) {
            final NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( pun.getPlayerUuid() );
            List<OfflinePlayer> allAltsList = nPlayer.getAltGroup().getUuids().stream()
                    .map( Bukkit::getOfflinePlayer )
                    .collect( Collectors.toList() );
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

    // Checks if the ban max time setting is enabled, if the player doesn't have permission to bypass it,
    //      and if the time is longer than the max time. If so, returns false. Otherwise returns true
    public static boolean checkBanLengthAllowed( Player staff, String inputLength ) {
        if ( Settings.BAN_MAX_TIME_ENABLED.bool() == false ) { return true; }
        if ( CyberVaultUtils.hasPerms( staff, Settings.BAN_MAX_TIME_BYPASS_PERMISSION.string() ) ) { return true; }

        final NDuration length = TimeUtils.durationFromUnformatted( inputLength );
        final NDuration maxLength = TimeUtils.durationFromUnformatted( Settings.BAN_MAX_TIME_LENGTH.string() );
        return length.asTimestamp() <= maxLength.asTimestamp();
    }
}