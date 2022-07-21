package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnIPMute extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public UnIPMute() {
        super( "unipmute", YMLUtils.getConfig().getStr( "unipmute.perm" ), getColorizedStr( "&8/&uunipmute &y(player)" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 0 || args[0].length() == 0 ) {
                return getAllOnlinePlayerNames();
            }
            else if ( args.length == 1 ) {
                return matchOnlinePlayers( args[0] );
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "unipmute.perm" ) ) == false ) {
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

                List<OfflinePlayer> punishedAccounts = DATA.getPunishedAltsByType( target.getUniqueId().toString(), "ipmute" );
                List<IPPunishment> punishments = new ArrayList<>();
                for ( OfflinePlayer account : punishedAccounts ) {
                    punishments.addAll( DATA.getIPPunishment( account.getUniqueId().toString(), "ipmute", true ) );
                }

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
