package com.github.cyberryan1.netuno.utils;

import org.bukkit.command.CommandSender;

public class CommandErrors {

    public static void sendInvalidTimespan( CommandSender sender, String time ) {
        sender.sendMessage( Utils.getColored( "&7Invalid timespan &8(&6\"" + time + "&6\"&8)" ) );
    }

    public static void sendPlayerNotFound( CommandSender sender, String player ) {
        sender.sendMessage( Utils.getColored( "&7No player with the name &6" + player + " &7found" ) );
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
        }

        return null;
    }

    public static void sendNoPunishments( CommandSender sender, String target, String punType ) {
        String plural;
        // more cases will come as more punishments are added
        switch ( punType.toLowerCase() ) {
            case "mute":
                plural = "mutes";
                break;
            case "unmute":
                plural = "mutes";
                break;
            default: // bans/unbans
                plural = "bans";
                break;
        }

        sender.sendMessage( Utils.getColored( "&6" + target + " &7does not have any active " + plural ) );
    }
}
