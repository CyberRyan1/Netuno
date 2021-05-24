package com.github.cyberryan1.netuno.commands;

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

                String reason = Utils.getRemainingArgs( args, 1 );
                Punishment pun = new Punishment();

                pun.setReason( reason );
                pun.setDate( Time.getCurrentTimestamp() );
                pun.setType( "Warn" );
                pun.setActive( false );

                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {

                    pun.setPlayerUUID( target.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player player = ( Player ) sender;
                        pun.setStaffUUID( player.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( player, target ) == false ) {
                            player.sendMessage( Utils.getColored( "&6" + target.getName() + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    DATA.addPunishment( pun );

                    Utils.sendPunishmentMsg( target, pun );
                }

                // target is offline
                else if ( Bukkit.getServer().getOfflinePlayer( args[0] ).hasPlayedBefore() ) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer( args[0] );

                    pun.setPlayerUUID( offline.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player player = ( Player ) sender;
                        pun.setStaffUUID( player.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( player, target ) == false ) {
                            player.sendMessage( Utils.getColored( "&6" + target.getName() + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    int id = DATA.addPunishment( pun );
                    DATA.addNotif( id, offline.getUniqueId().toString() );
                }

                else {
                    sender.sendMessage( ConfigUtils.getColoredStr( "&7Could not find any player named &6" + args[0] ) );
                    return true;
                }

                Utils.doPublicPunBroadcast( pun );
                Utils.doStaffPunBroadcast( pun );
            }

            else {
                sender.sendMessage( Utils.getColored( "&8/&6warn &7(player) (reason)" ) );
            }
        }

        else {
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
        }

        return true;
    }
}
