package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Mute implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /mute (player) (length/forever) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "mute.perm" ) ) == false ) {
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
                pun.setType( "Warn" );
                pun.setLength( Time.getTimestampFromLength( args[1] ) );

                String staffName = "CONSOLE";
                String targetName;
                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {

                    targetName = target.getName();
                    pun.setPlayerUUID( target.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );
                        staffName = staff.getName();

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            staff.sendMessage( Utils.getColored( "&6" + targetName + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    DATA.addPunishment( pun );
                    Utils.sendPunishmentMsg( target, pun );
                }

                else if ( Bukkit.getServer().getOfflinePlayer( args[0] ).hasPlayedBefore() ) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer( args[0] );
                    targetName = offline.getName();

                    pun.setPlayerUUID( offline.getUniqueId().toString() );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );
                        staffName = staff.getName();

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            staff.sendMessage( Utils.getColored( "&6" + targetName + " &7is a staff member, so they cannot be punished" ) );
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
        }


        return true;
    }
}
