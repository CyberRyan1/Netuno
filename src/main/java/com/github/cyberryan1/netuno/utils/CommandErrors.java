package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.time.NTimeLength;
import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        sender.sendMessage( CoreUtils.getColored( "&sInvalid timespan &8(&p\"" + time + "&p\"&8)" ) );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        sender.sendMessage( CoreUtils.getColored( "&sNo player with the name &p" + player + " &sfound" ) );
    }

    public static void sendPlayerNeverJoined( CommandSender sender, String player ) {
        sender.sendMessage( CoreUtils.getColored( "&sPlayer with the name &p" + player + " &shas never joined the server before" ) );
    }
    public static void sendPlayerCannotBePunished( CommandSender sender, String target ) {
        sender.sendMessage( CoreUtils.getColored( "&p" + target + " &sis a staff member, so they cannot be punished" ) );
    }

    public static void sendNoPunishments( CommandSender sender, String target, String punType ) {
        String plural = switch ( punType.toLowerCase() ) {
            default -> "bans"; // bans/unbans
            case "mute" -> "mutes";
            case "unmute" -> "mutes";
            case "ipmute" -> "ipmutes";
        };
        // more cases will come as more punishments are added

        sender.sendMessage( CoreUtils.getColored( "&p" + target + " &sdoes not have any active " + plural ) );
    }

    public static void sendNoAltAccounts( CommandSender sender, String target ) {
        sender.sendMessage( CoreUtils.getColored( "&p" + target + " &shas no other alt accounts" ) );
    }

    public static void sendNoPreviousPunishments( CommandSender sender, String targetName ) {
        sender.sendMessage( CoreUtils.getColored( "&p" + targetName + " &shas no previous punishments" ) );
    }

    public static void sendConfigError( CommandSender sender ) {
        sender.sendMessage( CoreUtils.getColored( "&sAn error occurred because the config file is not setup correctly. Please contact a server administrator about this!" ) );
    }

    public static void sendInvalidPunishmentID( CommandSender sender, String arg ) {
        sender.sendMessage( CoreUtils.getColored( "&sInvalid punishment ID &8(&p\"" + arg + "&p\"&8)" ) );
    }

    public static void sendPunishmentIDNotFound( CommandSender sender, int id ) {
        sender.sendMessage( CoreUtils.getColored( "&sPunishment with ID &p" + id + " &snot found" ) );
    }

    public static void sendPlayerExempt( CommandSender sender, String targetName ) {
        sender.sendMessage( CoreUtils.getColored( "&p" + targetName + " &sis exempt from this!" ) );
    }

    public static void sendReportNeedsOneReason( CommandSender sender ) {
        sender.sendMessage( CoreUtils.getColored( "&sYou must provide at least 1 reason in your report" ) );
    }

    public static void sendBanLengthTooLarge( CommandSender sender ) {
        sender.sendMessage( CoreUtils.getColored( "&sYou can only ban for a maximum of " +
                NTimeLength.fromUnformatted( YMLUtils.getConfig().getStr( "ban.max-time-length" ) ).asFormatted() ) );
    }
}
