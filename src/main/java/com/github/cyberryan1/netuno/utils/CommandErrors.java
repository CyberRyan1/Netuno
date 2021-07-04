package com.github.cyberryan1.netuno.utils;

import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        sender.sendMessage( Utils.getColored( "&7Invalid timespan &8(&6\"" + time + "&6\"&8)" ) );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&7No player with the name &6" + player + " &7found" ) );
    }

    public static void sendPlayerNeverJoined( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&7Player with the name &6" + player + " &7has never joined the server before" ) );
    }

    public static void sendInvalidPerms( CommandSender sender ) {
        sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
    }

    public static void sendPlayerCannotBePunished( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&6" + target + " &7is a staff member, so they cannot be punished" ) );
    }

    public static void sendCommandUsage( CommandSender sender, String command ) {
        sender.sendMessage( getCommandUsage( command ) );
    }

    public static String getCommandUsage( String command ) {
        switch ( command.toLowerCase() ) {
            case "warn":
                return Utils.getColored( "&8/&6warn &7(player) (reason)" );
            case "kick":
                return Utils.getColored( "&8/&6kick &7(player) (reason)" );
            case "mute":
                return Utils.getColored( "&8/&6mute &7(player) (time) (reason) " );
            case "unmute":
                return Utils.getColored( "&8/&6unmute &7(player)" );
            case "ban":
                return Utils.getColored( "&8/&6ban &7(player) (time) (reason)" );
            case "unban":
                return Utils.getColored( "&8/&6unban &7(player) (time) (reason)" );
            case "ipinfo":
                return Utils.getColored( "&8/&6ipinfo &7(player)" );
            case "ipmute":
                return Utils.getColored( "&8/&6ipmute &7(player) (time) (reason)" );
            case "unipmute":
                return Utils.getColored( "&8/&6unipmute &7(player)" );
            case "ipban":
                return Utils.getColored( "&8/&6ipban &7(player) (time) (reason)" );
            case "unipban":
                return Utils.getColored( "&8/&6unipban &7(player)" );
            case "history":
                return getCommandUsage( "history-list" ) + "\n" + getCommandUsage( "history-edit" ) + "\n" + getCommandUsage( "history-reset" );
            case "history-list":
                return Utils.getColored( "&8/&6history &7list (player)" );
            case "history-edit":
                return Utils.getColored( "&8/&6history &7edit (pun ID)" );
            case "history-reset":
                return Utils.getColored( "&8/&6history &7reset (player)" );
            case "togglesigns":
                return Utils.getColored( "&8/&6togglesigns &7[enable/disable]" );
            case "mutechat":
                return Utils.getColored( "&8/&6mutechat &7[enable/disable/toggle/status]" );
            case "report":
                return Utils.getColored( "&8/&6report &7(player)" );
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

        sender.sendMessage( Utils.getColored( "&6" + target + " &7does not have any active " + plural ) );
    }

    public static void sendNoAltAccounts( CommandSender sender, String target ) {
        sender.sendMessage( Utils.getColored( "&6" + target + " &7has no other alt accounts" ) );
    }

    public static void sendInvalidPageNumber( CommandSender sender, String page ) {
        sender.sendMessage( Utils.getColored( "&7Invalid page number &8(&6\"" + page + "&6\"&8)" ) );
    }

    public static void sendHistoryPageNoExist( CommandSender sender, String targetName, int maxPage ) {
        sender.sendMessage( Utils.getColored( "&7Maximum page number for &6" + targetName + " &7is &6" + maxPage ) );
    }

    public static void sendNoPreviousPunishments( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&6" + targetName + " &7has no previous punishments" ) );
    }

    public static void sendCanOnlyBeRanByPlayer( CommandSender sender ) {
        sender.sendMessage( ConfigUtils.getColoredStr( "general.player-only-msg" ) );
    }

    public static void sendUnexpectedError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&6&lNETUNO ERROR! &7An unexpected error occurred. Please contact the plugin developer about this!" ) );
    }

    public static void sendConfigError( CommandSender sender ) {
        sender.sendMessage( Utils.getColored( "&7An error occurred because the config file is not setup correctly. Please contact a server administrator about this!" ) );
    }

    public static void sendInvalidPunishmentID( CommandSender sender, String arg ) {
        sender.sendMessage( Utils.getColored( "&7Invalid punishment ID &8(&6\"" + arg + "&6\"&8)" ) );
    }

    public static void sendPunishmentIDNotFound( CommandSender sender, int id ) {
        sender.sendMessage( Utils.getColored( "&7Punishment with ID &6" + id + " &7not found" ) );
    }

    public static void sendPlayerExempt( CommandSender sender, String targetName ) {
        sender.sendMessage( Utils.getColored( "&6" + targetName + " &7is exempt from this!" ) );
    }
}
