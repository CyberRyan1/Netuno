package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Togglesigns implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /togglesigns
    // /togglesigns [enable/disable]
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "signs.notifs-perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }
        Player player = ( Player ) sender;

        // /togglesigns
        if ( Utils.isOutOfBounds( args, 0 ) ) {
            // currently has sign notifs enabled
            if ( DATA.checkPlayerNoSignNotifs( player ) == false ) {
                DATA.addPlayerNoSignNotifs( player );
                player.sendMessage( Utils.getColored( "&cDisabled&7 sign notifications" ) );
            }

            // currently has sign notifs disabled
            else {
                DATA.removePlayerNoSignNotifs( player );
                player.sendMessage( Utils.getColored( "&aEnabled&7 sign notifications" ) );
            }
        }

        // /togglesigns [enable/disable]
        else {
            if ( args[0].equalsIgnoreCase( "enable" ) ) {
                if ( DATA.checkPlayerNoSignNotifs( player ) ) {
                    DATA.removePlayerNoSignNotifs( player );
                    player.sendMessage( Utils.getColored( "&aEnabled&7 sign notifications" ) );
                }

                else {
                    player.sendMessage( Utils.getColored( "&7Sign notifications are already &aenabled" ) );
                }

            }

            else if ( args[0].equalsIgnoreCase( "disable" ) ) {
                if ( DATA.checkPlayerNoSignNotifs( player ) == false ) {
                    DATA.addPlayerNoSignNotifs( player );
                    player.sendMessage( Utils.getColored( "&cDisabled&7 sign notifications" ) );
                }

                else {
                    player.sendMessage( Utils.getColored( "&7Sign notifications are already &cdisabled" ) );
                }
            }

            else {
                CommandErrors.sendCommandUsage( player, "togglesigns" );
            }
        }

        return true;
    }
}
