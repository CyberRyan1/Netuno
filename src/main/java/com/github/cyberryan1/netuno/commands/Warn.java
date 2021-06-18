package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Warn implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /warn (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "warn.perm" ) ) ) {
            if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                if ( Utils.isValidUsername( args[0] ) == false ) {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                    return true;
                }

                Punishment pun = new Punishment();
                pun.setReason( Utils.getRemainingArgs( args, 1 ) );
                pun.setDate( Time.getCurrentTimestamp() );
                pun.setType( "Warn" );
                pun.setActive( false );

                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[0] );
                if ( target != null ) {

                    pun.setPlayerUUID( target.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                            return true;
                        }
                    }

                    int id = DATA.addPunishment( pun );

                    if ( target.isOnline() ) {
                        Utils.sendPunishmentMsg( target.getPlayer(), pun );
                    }

                    else {
                        DATA.addNotif( id, target.getUniqueId().toString() );
                    }

                    Utils.doPublicPunBroadcast( pun );
                    Utils.doStaffPunBroadcast( pun );
                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                }

            }

            else {
                CommandErrors.sendCommandUsage( sender, "warn" );
            }

        }

        else {
            CommandErrors.sendInvalidPerms( sender );
        }

        return true;
    }
}
