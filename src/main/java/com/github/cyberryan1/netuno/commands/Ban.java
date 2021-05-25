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
    // /ban (player) (length/forever) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "ban.perm" ) ) == false ) {
            sender.sendMessage( ConfigUtils.getColoredStr( "perm-denied-msg" ) );
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
                            staff.sendMessage( Utils.getColored( "&6" + target.getName() + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    target.kickPlayer( ConfigUtils.replaceAllVariables( ConfigUtils.getColoredStrFromList( "ban.banned-lines" ), pun ) );
                    DATA.addPunishment( pun );
                }

                else if ( Bukkit.getServer().getOfflinePlayer( args[0] ).hasPlayedBefore() ) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer( args[0] );

                    pun.setPlayerUUID( offline.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            staff.sendMessage( Utils.getColored( "&6" + target.getName() + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    int id = DATA.addPunishment( pun );
                    DATA.addNotif( id, offline.getUniqueId().toString() );
                }

                else {
                    sender.sendMessage( Utils.getColored( "&7Could not find any player named &6" + args[0] ) );
                    return true;
                }

                Utils.doPublicPunBroadcast( pun );
                Utils.doStaffPunBroadcast( pun );
            }

            else {
                sender.sendMessage( Utils.getColored( "&7Invalid timespan &8(&6\"" + args[1] + "&6\"&8)" ) );
            }
        }

        else {
            sender.sendMessage( Utils.getColored( "&8/&6ban &7(player) (length/forever) (reason)" ) );
        }

        return true;
    }
}
