package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.guis.punish.MainPunishGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Punish implements CommandExecutor {

    @Override
    // /punish (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "punish.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }
        Player staff = ( Player ) sender;

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            if ( target != null ) {
                MainPunishGUI gui = new MainPunishGUI( staff, target );
                gui.openInventory();
            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "punish" );
        }

        return true;
    }
}
