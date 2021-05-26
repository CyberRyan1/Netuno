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

public class Unmute implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /unmute (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "unmute.perm" ) ) == false ) {
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
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

                    if ( target.isOnline() ) {
                        DATA.addPunishment( unmutePun );
                        Player targetOnline = target.getPlayer();
                        Utils.sendPunishmentMsg( targetOnline, unmutePun );
                    }

                    else {
                        int id = DATA.addPunishment( unmutePun );
                        DATA.addNotif( id, target.getUniqueId().toString() );
                    }

                    Utils.doPublicPunBroadcast( unmutePun );
                    Utils.doStaffPunBroadcast( unmutePun );
                }

                else {
                    sender.sendMessage( "&6" + target.getName() + " &7does not have any active mutes" );
                }

            }

            else {
                sender.sendMessage( "&7Found no player named &6" + target.getName() );
            }
        }

        return true;
    }
}
