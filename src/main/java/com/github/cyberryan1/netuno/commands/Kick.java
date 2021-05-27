package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // TODO can be optimized more
    // /kick (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "kick.perm" ) ) ) {
            if ( Utils.isOutOfBounds( args, 1 ) == false ) {
                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {
                    Punishment pun = new Punishment();
                    pun.setPlayerUUID( target.getUniqueId().toString() );
                    pun.setReason( Utils.getRemainingArgs( args, 1 ) );
                    pun.setDate( Time.getCurrentTimestamp() );
                    pun.setType( "Kick" );
                    pun.setActive( false );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            CommandErrors.sendPlayerCannotBePunished( sender, target.getName() );
                            return true;
                        }
                    }

                    DATA.addPunishment( pun );

                    String kickMsg = "";
                    for ( String str : ConfigUtils.getColoredStrList( "kick.kicked-lines" ) ) {
                        String replaced = ConfigUtils.replaceAllVariables( str, pun );
                        kickMsg += replaced + "\n";
                    }

                    target.kickPlayer( kickMsg );

                    Utils.doPublicPunBroadcast( pun );
                    Utils.doStaffPunBroadcast( pun );
                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "kick" );
            }
        }

        else {
            CommandErrors.sendInvalidPerms( sender );
        }


        return true;
    }
}
