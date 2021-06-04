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
                ArrayList<IPPunishment> punishments = DATA.getIPPunishment( target.getUniqueId().toString(), "ipmute", true );
                if ( punishments.size() >= 1 ) {
                    for ( IPPunishment pun : punishments ) {
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

                    DATA.addPunishment( pun );
                    if ( target.isOnline() ) {
                        Player targetOnline = target.getPlayer();
                        Utils.sendPunishmentMsg( targetOnline, pun );
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
