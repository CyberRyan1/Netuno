package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnIPBan extends BaseCommand {

    private Database DATA = Utils.getDatabase();

    public UnIPBan() {
        super( "unipban", ConfigUtils.getStr( "unipban.perm" ), getColorizedStr( "&8/&uunipban &y(player)" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "unipban.perm" ) ) == false ) {
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
                ArrayList<IPPunishment> punishments = DATA.getIPPunishment( target.getUniqueId().toString(), "ipban", true );
                if ( punishments.size() >= 1 ) {
                    for ( IPPunishment pun : punishments ) {
                        DATA.setIPPunishmentActive( pun.getID(), false );
                    }

                    IPPunishment pun = new IPPunishment();
                    pun.setPlayerUUID( target.getUniqueId().toString() );
                    pun.setReason( "" );
                    pun.setLength( -1L );
                    pun.setDate( Time.getCurrentTimestamp() );
                    pun.setType( "UnIPBan" );
                    pun.setActive( false );

                    pun.setStaffUUID( "CONSOLE" );
                    if ( sender instanceof Player ) {
                        Player staff = ( Player ) sender;
                        pun.setStaffUUID( staff.getUniqueId().toString() );
                    }

                    DATA.addPunishment( pun );

                    Utils.doPublicPunBroadcast( pun );
                    Utils.doStaffPunBroadcast( pun );

                }

                else {
                    CommandErrors.sendNoPunishments( sender, target.getName(), "ipban" );
                }

            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "unipban" );
        }

        return true;
    }
}
