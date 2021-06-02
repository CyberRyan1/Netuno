package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UnIPMute implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "unipmute.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            if ( target != null ) {
                ArrayList<Punishment> punishments = DATA.getPunishment( target.getUniqueId().toString(), "ipmute", true );
                if ( punishments.size() >= 1 ) {
                    for ( Punishment pun : punishments ) {
                        DATA.setPunishmentActive( pun.getID(), false );
                    }

                    Punishment pun = new Punishment();
                    pun.setPlayerUUID( target.getUniqueId().toString() );
                    pun.setReason( "" );
                    pun.setLength( -1L );
                    pun.setDate( Time.getCurrentTimestamp() );
                    pun.setType( "UnIPMute" );
                    pun.setActive( false );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );
                    }

                    if ( target.isOnline() ) {
                        DATA.addPunishment( pun );
                        Player targetOnline = target.getPlayer();
                        Utils.sendPunishmentMsg( targetOnline, pun );
                    }

                    else {
                        int id = DATA.addPunishment( pun );
                        DATA.addNotif( id, target.getUniqueId().toString() );
                    }

                    Utils.doPublicPunBroadcast( pun );
                    Utils.doStaffPunBroadcast( pun );
                }

                else {
                    CommandErrors.sendNoPunishments( sender, target.getName(), "ipmute" );
                }

            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "unipmute" );
        }

        return true;
    }
}
