package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        sender.sendMessage( Utils.getColored( "&sInvalid timespan &8(&p\"" + time + "&p\"&8)" ) );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&sNo player with the name &p" + player + " &sfound" ) );
    }

    public static void sendPlayerNeverJoined( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&sPlayer with the name &p" + player + " &shas never joined the server before" ) );
    }

    public static void sendInvalidPerms( CommandSender sender ) {
        sender.sendMessage( YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ) );
    }

    public static void sendPlayerCannotBePunished( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&p" + target + " &sis a staff member, so they cannot be punished" ) );
    }

    public static void sendCommandUsage( CommandSender sender, String command ) {
        sender.sendMessage( getCommandUsage( command ) );
    }

    public static String getCommandUsage( String command ) {
        switch ( command.toLowerCase() ) {
            case "warn":
                return Utils.getColored( "&8/&pwarn &s(player) (reason)" );
            case "clearchat":
                return Utils.getColored( "&8/&pclearchat" );
            case "kick":
                return Utils.getColored( "&8/&pkick &s(player) (reason)" );
            case "mute":
                return Utils.getColored( "&8/&pmute &s(player) (time) (reason) " );
            case "unmute":
                return Utils.getColored( "&8/&punmute &s(player)" );
            case "ban":
                return Utils.getColored( "&8/&pban &s(player) (time) (reason)" );
            case "unban":
                return Utils.getColored( "&8/&punban &s(player) (time) (reason)" );
            case "ipinfo":
                return Utils.getColored( "&8/&pipinfo &s(player)" );
            case "ipmute":
                return Utils.getColored( "&8/&pipmute &s(player) (time) (reason)" );
            case "unipmute":
                return Utils.getColored( "&8/&punipmute &s(player)" );
            case "ipban":
                return Utils.getColored( "&8/&pipban &s(player) (time) (reason)" );
            case "unipban":
                return Utils.getColored( "&8/&punipban &s(player)" );
            case "history":
                return getCommandUsage( "history-list" ) + "\n" + getCommandUsage( "history-edit" ) + "\n" + getCommandUsage( "history-reset" );
            case "history-list":
                return Utils.getColored( "&8/&phistory &slist (player)" );
            case "history-edit":
                return Utils.getColored( "&8/&phistory &sedit (pun ID)" );
            case "history-reset":
                return Utils.getColored( "&8/&phistory &sreset (player)" );
            case "togglesigns":
                return Utils.getColored( "&8/&ptogglesigns &s[enable/disable]" );
            case "mutechat":
                return Utils.getColored( "&8/&pmutechat &s[enable/disable/toggle/status]" );
            case "report":
                return Utils.getColored( "&8/&preport &s(player)" );
            case "reports":
                return Utils.getColored( "&8/&preports &s[player]" );
            case "help":
                return Utils.getColored( "&8/&pnetuno &shelp [page]" );
            case "reload":
                return Utils.getColored( "&8/&pnetuno &sreload" );
            case "punish":
                return Utils.getColored( "&8/&ppunish &s(player)" );
            case "chatslow":
                return getCommandUsage( "chatslow-get" ) + "\n" + getCommandUsage( "chatslow-set" );
            case "chatslow-get":
                return Utils.getColored( "&8/&pchatslow &sget" );
            case "chatslow-set":
                return Utils.getColored( "&8/&pchatslow &sset (amount)");
        }

        return null;
    }

    public static void sendNoPunishments( CommandSender sender, String target, String punType ) {
        String plural;
        // more cases will come as more punishments are added
        switch ( punType.toLowerCase() ) {
            default: // bans/unbans
                plural = "bans";
                break;
            case "mute":
                plural = "mutes";
                break;
            case "unmute":
                plural = "mutes";
                break;
            case "ipmute":
                plural = "ipmutes";
                break;
        }

        sender.sendMessage( Utils.getColored( "&p" + target + " &sdoes not have any active " + plural ) );
    }

    public static void sendNoAltAccounts( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&p" + target + " &shas no other alt accounts" ) );
    }

    public static void sendInvalidPageNumber( CommandSender sender, String page ) {
        sender.sendMessage( Utils.getColored( "&sInvalid page number &8(&p\"" + page + "&p\"&8)" ) );
    }

    public static void sendHistoryPageNoExist( CommandSender sender, String targetName, int maxPage ) {
        sender.sendMessage( Utils.getColored( "&sMaximum page number for &p" + targetName + " &sis &p" + maxPage ) );
    }

    public static void sendNoPreviousPunishments( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&p" + targetName + " &shas no previous punishments" ) );
    }

    public static void sendCanOnlyBeRanByPlayer( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&7This command can only be ran by a player" ) );
    }

    public static void sendUnexpectedError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&p&lNETUNO ERROR! &sAn unexpected error occurred. Please contact the plugin developer about this!" ) );
    }

    public static void sendConfigError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&sAn error occurred because the config file is not setup correctly. Please contact a server administrator about this!" ) );
    }

    public static void sendInvalidPunishmentID( CommandSender sender, String arg ) {
        sender.sendMessage( Utils.getColored( "&sInvalid punishment ID &8(&p\"" + arg + "&p\"&8)" ) );
    }

    public static void sendPunishmentIDNotFound( CommandSender sender, int id ) {
        sender.sendMessage( Utils.getColored( "&sPunishment with ID &p" + id + " &snot found" ) );
    }

    public static void sendPlayerExempt( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&p" + targetName + " &sis exempt from this!" ) );
    }

    public static void sendReportNeedsOneReason( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&sYou must provide at least 1 reason in your report" ) );
    }

    public static void sendTargetAlreadyBeingPunished( CommandSender sender, String targetName, String staffPunishingName ) {
        sender.sendMessage( Utils.getColored( "&p" + targetName + " &sis already being punished by &p" + staffPunishingName ) );
    }

    public static void sendTargetHasNoPunishedAlts( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&p" + targetName + " &shas no punished alts" ) );
    }

    public static void sendBanLengthTooLarge( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&sYou can only ban for a maximum of " + Time.getFormattedLength( YMLUtils.getConfig().getStr( "ban.max-time-length" ) ) ) );
    }
}
