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

public class IPMute implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /ipmute (player) (length/forever) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "ipmute.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 2 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            if ( Time.isAllowableLength( args[1] ) ) {
                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[0] );
                if ( target != null ) {
                    IPPunishment pun = new IPPunishment();
                    pun.setReason( Utils.getRemainingArgs( args, 2 ) );
                    pun.setPlayerUUID( target.getUniqueId().toString() );
                    pun.setActive( true );
                    pun.setDate( Time.getCurrentTimestamp() );
                    pun.setLength( Time.getTimestampFromLength( args[1] ) );
                    pun.setType( "IPMute" );

                    ArrayList<String> altList = new ArrayList<>();
                    for ( OfflinePlayer alt : DATA.getAllAlts( target.getUniqueId().toString() ) ) {
                        altList.add( alt.getUniqueId().toString() );
                    }
                    pun.setAltList( altList );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );

                        if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                            CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                            return true;
                        }
                    }

                    DATA.addIPPunishment( pun );
                    if ( target.isOnline() ) {
                        Utils.sendPunishmentMsg( target.getPlayer(), pun );
                    }

                    Utils.doPublicPunBroadcast( pun );
                    Utils.doStaffPunBroadcast( pun );

                    // Sends a notification to all online alts for the same punishment
                    for ( OfflinePlayer alt : DATA.getAllAlts( target.getUniqueId().toString() ) ) {
                        if ( alt.isOnline() && alt.getName().equals( target.getName() ) == false ) {
                            pun.setPlayerUUID( alt.getUniqueId().toString() );
                            Utils.sendPunishmentMsg( alt.getPlayer(), pun );
                        }
                    }

                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                }

            }

            else {
                CommandErrors.sendInvalidTimespan( sender, args[1] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "ipmute" );
        }




        return true;
    }
}
