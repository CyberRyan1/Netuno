package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        CyberMsgUtils.sendMsg( sender, "&sInvalid timespan &8(&p\"" + time + "&p\"&8)" );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        CyberMsgUtils.sendMsg( sender, "&sNo player with the name &p" + player + " &sfound" );
    }

    public static void sendPlayerNeverJoined( CommandSender sender, String player ) {
        CyberMsgUtils.sendMsg( sender, "&sPlayer with the name &p" + player + " &shas never joined the server before" );
    }
    public static void sendPlayerCannotBePunished( CommandSender sender, String target ) {
        CyberMsgUtils.sendMsg( sender, "&p" + target + " &sis a staff member, so they cannot be punished" );
    }

    public static void sendNoPunishments( CommandSender sender, String target, String punType ) {
        String plural = switch ( punType.toLowerCase() ) {
            default -> "bans"; // bans/unbans
            case "mute" -> "mutes";
            case "unmute" -> "mutes";
            case "ipmute" -> "ipmutes";
        };
        // more cases will come as more punishments are added

        CyberMsgUtils.sendMsg( sender, "&p" + target + " &sdoes not have any active " + plural );
    }

    public static void sendNoAltAccounts( CommandSender sender, String target ) {
        CyberMsgUtils.sendMsg( sender, "&p" + target + " &shas no other alt accounts" );
    }

    public static void sendNoPreviousPunishments( CommandSender sender, String targetName ) {
        CyberMsgUtils.sendMsg( sender, "&p" + targetName + " &shas no previous punishments" );
    }

    public static void sendConfigError( CommandSender sender ) {
        CyberMsgUtils.sendMsg( sender, "&sAn error occurred because the config file is not setup correctly. Please contact a server administrator about this!" );
    }

    public static void sendInvalidPunishmentID( CommandSender sender, String arg ) {
        CyberMsgUtils.sendMsg( sender, "&sInvalid punishment ID &8(&p\"" + arg + "&p\"&8)" );
    }

    public static void sendPunishmentIDNotFound( CommandSender sender, int id ) {
        CyberMsgUtils.sendMsg( sender, "&sPunishment with ID &p" + id + " &snot found" );
    }

    public static void sendPlayerExempt( CommandSender sender, String targetName ) {
        CyberMsgUtils.sendMsg( sender, "&p" + targetName + " &sis exempt from this!" );
    }

    public static void sendReportNeedsOneReason( CommandSender sender ) {
        CyberMsgUtils.sendMsg( sender, "&sYou must provide at least 1 reason in your report" );
    }

    public static void sendBanLengthTooLarge( CommandSender sender ) {
        CyberMsgUtils.sendMsg( sender, "&sYou can only ban for a maximum of " +
                TimeUtils.durationFromUnformatted( YMLUtils.getConfig().getStr( "ban.max-time-length" ) ).asFormatted() );
    }
}
