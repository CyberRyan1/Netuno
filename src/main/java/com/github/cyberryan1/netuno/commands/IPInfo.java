package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.guis.ipinfo.AltsListGUI;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IPInfo implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /ipinfo (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "ipinfo.perm" ) ) == false ) {
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
            // want the target to have joined before
            if ( target.hasPlayedBefore() ) {
                AltsListGUI gui = new AltsListGUI( target, staff, 1 );
                gui.openInventory( staff );
                Utils.getPlugin().getServer().getPluginManager().registerEvents( gui, Utils.getPlugin() );
            }

            else {
                CommandErrors.sendPlayerNeverJoined( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "ipinfo" );
        }



        return true;
    }
}
