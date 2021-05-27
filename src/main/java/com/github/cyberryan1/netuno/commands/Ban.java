package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ban implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // TODO code can be optimized
    // /ban (player) (length/forever) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "ban.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 2 ) == false ) {
            if ( Time.isAllowableLength( args[1] ) ) {
                String reason = Utils.getRemainingArgs( args, 2 );
                String length = Time.getFormattedLength( args[1] );
                Punishment pun = new Punishment();

                pun.setReason( reason );
                pun.setDate( Time.getCurrentTimestamp() );
                pun.setType( "Ban" );
                pun.setLength( Time.getTimestampFromLength( args[1] ) );
                pun.setActive( true );

                Player target = Bukkit.getServer().getPlayer( args[0] );
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

                    target.kickPlayer( ConfigUtils.replaceAllVariables( ConfigUtils.getColoredStrFromList( "ban.banned-lines" ), pun ) );
                    DATA.addPunishment( pun );
                }

                else if ( Bukkit.getServer().getOfflinePlayer( args[0] ).hasPlayedBefore() ) {
                    OfflinePlayer targetOffline = Bukkit.getServer().getOfflinePlayer( args[0] );

                    pun.setPlayerUUID( targetOffline.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( staff, targetOffline ) == false ) {
                            CommandErrors.sendPlayerCannotBePunished( sender, targetOffline.getName() );
                            return true;
                        }
                    }

                    int id = DATA.addPunishment( pun );
                    DATA.addNotif( id, targetOffline.getUniqueId().toString() );
                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                    return true;
                }

                Utils.doPublicPunBroadcast( pun );
                Utils.doStaffPunBroadcast( pun );
            }

            else {
                CommandErrors.sendInvalidTimespan( sender, args[1] );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "ban" );
        }

        return true;
    }
}
