package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Chatslow implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /chatslow get
    // /chatslow set (amount)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "chatslow.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            // /chatslow get
            if ( args[0].equalsIgnoreCase( "get" ) ) {
                sender.sendMessage( Utils.getColored( "&7The chatslow is currently &6" + ChatslowManager.getSlow() ) + " seconds" );
            }

            // /chatslow set (amount)
            else if ( args[0].equalsIgnoreCase( "set" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {
                    int newSlow;
                    try {
                        newSlow = Integer.parseInt( args[1] );
                    } catch ( NumberFormatException e ) {
                        CommandErrors.sendCommandUsage( sender, "chatslow-set" );
                        return true;
                    }

                    ChatslowManager.setSlow( newSlow );
                    sender.sendMessage( Utils.getColored( "&7The chatslow has been set to &6" + ChatslowManager.getSlow() + " seconds" ) );
                }

                else {
                    CommandErrors.sendCommandUsage( sender, "chatslow-set" );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "chatslow" );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "chatslow" );
        }

        return true;
    }
}