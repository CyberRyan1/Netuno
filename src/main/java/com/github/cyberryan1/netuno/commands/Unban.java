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
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
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

                    int id = DATA.addPunishment( unbanPun );
                    DATA.addNotif( id, target.getUniqueId().toString() );

                    Utils.doPublicPunBroadcast( unbanPun );
                    Utils.doStaffPunBroadcast( unbanPun );
                }

                else {
                    sender.sendMessage( Utils.getColored( "&6" + target.getName() + " &7does not have any active bans" ) );
                }

            }

            else {
                sender.sendMessage( Utils.getColored( "&7Found no player named &6" + target.getName() ) );
            }

        }

        else {
            sender.sendMessage( Utils.getColored( "&8/&6unban &7(player)" ) );
        }

        return true;
    }
}
