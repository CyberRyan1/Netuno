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

import java.util.ArrayList;

public class Unmute implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /unmute (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "unmute.perm" ) ) == false ) {
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
                ArrayList<Punishment> punishments = DATA.getPunishment( target.getUniqueId().toString(), "mute", true );
                if ( punishments.size() >= 1 ) {
                    for ( Punishment pun : punishments ) {
                        DATA.setPunishmentActive( pun.getID(), false );
                    }

                    Punishment unmutePun = new Punishment();
                    unmutePun.setPlayerUUID( target.getUniqueId().toString() );
                    unmutePun.setReason( "" );
                    unmutePun.setLength( -1L );
                    unmutePun.setDate( Time.getCurrentTimestamp() );
                    unmutePun.setType( "Unmute" );
                    unmutePun.setActive( false );

                    unmutePun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        unmutePun.setStaffUUID( staff.getUniqueId().toString() );
                    }

                    DATA.addPunishment( unmutePun );
                    if ( target.isOnline() ) {
                        Player targetOnline = target.getPlayer();
                        Utils.sendPunishmentMsg( targetOnline, unmutePun );
                    }

                    Utils.doPublicPunBroadcast( unmutePun );
                    Utils.doStaffPunBroadcast( unmutePun );
                }

                else {
                    CommandErrors.sendNoPunishments( sender, target.getName(), "mute" );
                }

            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "unmute" );
        }

        return true;
    }
}
