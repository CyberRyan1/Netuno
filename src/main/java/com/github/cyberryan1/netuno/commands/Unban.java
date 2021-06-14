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

public class Unban implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "unban.perm" ) ) == false ) {
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
                ArrayList<Punishment> punishments = DATA.getPunishment( target.getUniqueId().toString(), "ban", true );
                if ( punishments.size() >= 1 ) {
                    for ( Punishment pun : punishments ) {
                        DATA.setPunishmentActive( pun.getID(), false );
                    }

                    Punishment unbanPun = new Punishment( "", "", "unban", -1, -1, "", false );
                    unbanPun.setPlayerUUID( target.getUniqueId().toString() );
                    unbanPun.setDate( Time.getCurrentTimestamp() );

                    unbanPun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        unbanPun.setStaffUUID( staff.getUniqueId().toString() );
                    }

                    DATA.addPunishment( unbanPun );

                    Utils.doPublicPunBroadcast( unbanPun );
                    Utils.doStaffPunBroadcast( unbanPun );
                }

                else {
                    CommandErrors.sendNoPunishments( sender, target.getName(), "ban" );
                }

            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "unban" );
        }

        return true;
    }
}
