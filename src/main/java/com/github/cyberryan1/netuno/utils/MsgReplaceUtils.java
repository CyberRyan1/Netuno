package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.helpers.ANetunoPunishment;
import com.github.cyberryan1.netunoapi.helpers.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class MsgReplaceUtils {

    public static String getReplacedDisconnectMessage( ANetunoPunishment punishment ) {
        String toReturn;

        if ( punishment.getPunishmentType() == PunishmentType.IPBAN ) {
            toReturn = String.join( "\n" , Settings.IPBAN_ATTEMPT.coloredStringlist() );
        }

        else if ( punishment.getPunishmentType() == PunishmentType.BAN ) {
            toReturn = String.join( "\n" , Settings.BAN_ATTEMPT.coloredStringlist() );
        }

        else if ( punishment.getPunishmentType() == PunishmentType.KICK ) {
            toReturn = String.join( "\n" , Settings.KICK_KICKED_LINES.coloredStringlist() );
        }

        else {
            return null;
        }

        toReturn = replaceStaff( toReturn, punishment.getStaffUuid() );
        toReturn = replaceTarget( toReturn, punishment.getPlayerUuid() );
        toReturn = replaceReason( toReturn, punishment.getReason() );
        if ( punishment.getPunishmentType() != PunishmentType.KICK ) {
            toReturn = replaceLength( toReturn, punishment.getLength() );
            long timeRemaining = punishment.getLength() - ( Utils.getCurrentTimestamp() - punishment.getTimestamp() );
            toReturn = replaceRemain( toReturn, timeRemaining );
        }
        return toReturn;
    }

    public static String replaceStaff( String msg, String staffUuid ) {
        if ( staffUuid.equalsIgnoreCase( "console" ) ) { return msg.replace( "[STAFF]", "CONSOLE" ); }

        OfflinePlayer staff = Bukkit.getOfflinePlayer( UUID.fromString( staffUuid ) );
        return msg.replace( "[STAFF]", staff.getName() );
    }

    public static String replaceTarget( String msg, String targetUuid ) {
        OfflinePlayer target = Bukkit.getOfflinePlayer( UUID.fromString( targetUuid ) );
        return msg.replace( "[TARGET]", target.getName() );
    }

    public static String replaceLength( String msg, long length ) {
        String beautifiedLength = TimeUtils.getFormattedLengthFromTimestampLength( length );
        return msg.replace( "[LENGTH]", beautifiedLength );
    }

    public static String replaceRemain( String msg, long remain ) {
        String beautifiedRemain = TimeUtils.getFormattedLengthFromTimestampLength( remain );
        return msg.replace( "[REMAIN]", beautifiedRemain );
    }

    public static String replaceReason( String msg, String reason ) {
        return msg.replace( "[REASON]", reason );
    }
}