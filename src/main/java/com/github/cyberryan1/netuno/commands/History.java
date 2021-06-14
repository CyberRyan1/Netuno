package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.guis.HistoryGUI;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class History implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /history list (player)
    // /history reason (pun ID) (new reason)
    // /history time (pun ID) (new time)
    // /history delete (pun ID)
    // /history reset (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "history.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( args[0].equalsIgnoreCase( "list" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                    if ( sender instanceof Player == false ) {
                        CommandErrors.sendCanOnlyBeRanByPlayer( sender );
                        return true;
                    }

                    if ( Utils.isValidUsername( args[1] ) == false ) {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[1] );
                    if ( target == null ) {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                        return true;
                    }

                    Player staff = ( Player ) sender;
                    HistoryGUI gui = new HistoryGUI( target, staff, 1 );
                    gui.openInventory( staff );
                    Utils.getPlugin().getServer().getPluginManager().registerEvents( gui, Utils.getPlugin() );

                }

                else {
                    CommandErrors.sendCommandUsage( sender, "history-list" );
                }
            }

            else if ( args[0].equalsIgnoreCase( "reason" ) ) {
                return false;
            }

            else if ( args[0].equalsIgnoreCase( "time" ) ) {
                return false;
            }

            else if ( args[0].equalsIgnoreCase( "delete" ) ) {
                return false;
            }

            else if ( args[0].equalsIgnoreCase( "reset" ) ) {
                return false;
            }

            else {
                CommandErrors.sendCommandUsage( sender, "history" );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "history" );
        }

        return true;
    }

}
