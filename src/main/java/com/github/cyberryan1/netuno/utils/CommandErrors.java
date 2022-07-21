package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        sender.sendMessage( Utils.getColored( "&hInvalid timespan &8(&g\"" + time + "&g\"&8)" ) );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&hNo player with the name &g" + player + " &hfound" ) );
    }

    public static void sendPlayerNeverJoined( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&hPlayer with the name &g" + player + " &hhas never joined the server before" ) );
    }

    public static void sendInvalidPerms( CommandSender sender ) {
        sender.sendMessage( YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ) );
    }

    public static void sendPlayerCannotBePunished( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&g" + target + " &his a staff member, so they cannot be punished" ) );
    }

    public static void sendCommandUsage( CommandSender sender, String command ) {
        sender.sendMessage( getCommandUsage( command ) );
    }

    public static String getCommandUsage( String command ) {
        switch ( command.toLowerCase() ) {
            case "warn":
                return Utils.getColored( "&8/&gwarn &h(player) (reason)" );
            case "clearchat":
                return Utils.getColored( "&8/&gclearchat" );
            case "kick":
                return Utils.getColored( "&8/&gkick &h(player) (reason)" );
            case "mute":
                return Utils.getColored( "&8/&gmute &h(player) (time) (reason) " );
            case "unmute":
                return Utils.getColored( "&8/&gunmute &h(player)" );
            case "ban":
                return Utils.getColored( "&8/&gban &h(player) (time) (reason)" );
            case "unban":
                return Utils.getColored( "&8/&gunban &h(player) (time) (reason)" );
            case "ipinfo":
                return Utils.getColored( "&8/&gipinfo &h(player)" );
            case "ipmute":
                return Utils.getColored( "&8/&gipmute &h(player) (time) (reason)" );
            case "unipmute":
                return Utils.getColored( "&8/&gunipmute &h(player)" );
            case "ipban":
                return Utils.getColored( "&8/&gipban &h(player) (time) (reason)" );
            case "unipban":
                return Utils.getColored( "&8/&gunipban &h(player)" );
            case "history":
                return getCommandUsage( "history-list" ) + "\n" + getCommandUsage( "history-edit" ) + "\n" + getCommandUsage( "history-reset" );
            case "history-list":
                return Utils.getColored( "&8/&ghistory &hlist (player)" );
            case "history-edit":
                return Utils.getColored( "&8/&ghistory &hedit (pun ID)" );
            case "history-reset":
                return Utils.getColored( "&8/&ghistory &hreset (player)" );
            case "togglesigns":
                return Utils.getColored( "&8/&gtogglesigns &h[enable/disable]" );
            case "mutechat":
                return Utils.getColored( "&8/&gmutechat &h[enable/disable/toggle/status]" );
            case "report":
                return Utils.getColored( "&8/&greport &h(player)" );
            case "reports":
                return Utils.getColored( "&8/&greports &h[player]" );
            case "help":
                return Utils.getColored( "&8/&gnetuno &hhelp [page]" );
            case "reload":
                return Utils.getColored( "&8/&gnetuno &hreload" );
            case "punish":
                return Utils.getColored( "&8/&gpunish &h(player)" );
            case "chatslow":
                return getCommandUsage( "chatslow-get" ) + "\n" + getCommandUsage( "chatslow-set" );
            case "chatslow-get":
                return Utils.getColored( "&8/&gchatslow &hget" );
            case "chatslow-set":
                return Utils.getColored( "&8/&gchatslow &hset (amount)");
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

        sender.sendMessage( Utils.getColored( "&g" + target + " &hdoes not have any active " + plural ) );
    }

    public static void sendNoAltAccounts( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&g" + target + " &hhas no other alt accounts" ) );
    }

    public static void sendInvalidPageNumber( CommandSender sender, String page ) {
        sender.sendMessage( Utils.getColored( "&hInvalid page number &8(&g\"" + page + "&g\"&8)" ) );
    }

    public static void sendHistoryPageNoExist( CommandSender sender, String targetName, int maxPage ) {
        sender.sendMessage( Utils.getColored( "&hMaximum page number for &g" + targetName + " &his &g" + maxPage ) );
    }

    public static void sendNoPreviousPunishments( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&g" + targetName + " &hhas no previous punishments" ) );
    }

    public static void sendCanOnlyBeRanByPlayer( CommandSender sender ) {
        sender.sendMessage( YMLUtils.getConfig().getColoredStr( "general.player-only-msg" ) );
    }

    public static void sendUnexpectedError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&g&lNETUNO ERROR! &hAn unexpected error occurred. Please contact the plugin developer about this!" ) );
    }

    public static void sendConfigError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&hAn error occurred because the config file is not setup correctly. Please contact a server administrator about this!" ) );
    }

    public static void sendInvalidPunishmentID( CommandSender sender, String arg ) {
        sender.sendMessage( Utils.getColored( "&hInvalid punishment ID &8(&g\"" + arg + "&g\"&8)" ) );
    }

    public static void sendPunishmentIDNotFound( CommandSender sender, int id ) {
        sender.sendMessage( Utils.getColored( "&hPunishment with ID &g" + id + " &hnot found" ) );
    }

    public static void sendPlayerExempt( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&g" + targetName + " &his exempt from this!" ) );
    }

    public static void sendReportNeedsOneReason( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&hYou must provide at least 1 reason in your report" ) );
    }

    public static void sendTargetAlreadyBeingPunished( CommandSender sender, String targetName, String staffPunishingName ) {
        sender.sendMessage( Utils.getColored( "&g" + targetName + " &his already being punished by &g" + staffPunishingName ) );
    }

    public static void sendTargetHasNoPunishedAlts( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&g" + targetName + " &hhas no punished alts" ) );
    }

    public static void sendBanLengthTooLarge( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&hYou can only ban for a maximum of " + Time.getFormattedLength( YMLUtils.getConfig().getStr( "ban.max-time-length" ) ) ) );
    }
}
