package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.history.HistoryEditGUI;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class History implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /history list (player)
    // /history edit (pun ID)
    // /history reset (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "history.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( args[0].equalsIgnoreCase( "list" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                    if ( sender instanceof Player == false ) {
                        CommandErrors.sendCanOnlyBeRanByPlayer( sender );
                        return true;
                    }

                    if ( Utils.isValidUsername( args[1] ) == false ) {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[1] );
                    if ( target == null ) {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                        return true;
                    }

                    Player staff = ( Player ) sender;
                    HistoryListGUI gui = new HistoryListGUI( target, staff, 1 );
                    gui.openInventory( staff );

                }

                else {
                    CommandErrors.sendCommandUsage( sender, "history-list" );
                }
            }

            else if ( args[0].equalsIgnoreCase( "edit" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                    if ( sender instanceof Player == false ) {
                        CommandErrors.sendCanOnlyBeRanByPlayer( sender );
                        return true;
                    }

                    int id = -1;
                    try {
                        id = Integer.parseInt( args[1] );
                    } catch ( NumberFormatException e ) {
                        CommandErrors.sendInvalidPunishmentID( sender, args[1] );
                        return true;
                    }

                    if ( DATA.checkPunIDExists( id ) || DATA.checkIpPunIDExists( id ) ) {
                        Punishment pun;
                        if ( DATA.checkIpPunIDExists( id ) ) {
                            pun = ( Punishment ) DATA.getIPPunishment( id );
                        }
                        else {
                            pun = DATA.getPunishment( id );
                        }

                        OfflinePlayer target = Bukkit.getOfflinePlayer( UUID.fromString( pun.getPlayerUUID() ) );
                        Player staff = ( Player ) sender;
                        HistoryEditGUI editGUI = new HistoryEditGUI( target, staff, id );
                        editGUI.openInventory( staff );
                        Utils.getPlugin().getServer().getPluginManager().registerEvents( editGUI, Utils.getPlugin() );
                    }

                    else {
                        CommandErrors.sendPunishmentIDNotFound( sender, id );
                    }

                }

                else {
                    CommandErrors.sendCommandUsage( sender, "history-edit" );
                }
            }


            else if ( args[0].equalsIgnoreCase( "reset" ) ) {
                if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "history.reset.perm" ) ) == false ) {
                    CommandErrors.sendInvalidPerms( sender );
                    return true;
                }

                if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                    if ( Utils.isValidUsername( args[1] ) == false ) {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getOfflinePlayer( args[1] );
                    if ( target != null ) {
                        ArrayList<Punishment> allPuns = DATA.getPunishment( target.getUniqueId().toString() );
                        for ( Punishment pun : allPuns ) {
                            DATA.deletePunishment( pun.getID() );
                        }

                        ArrayList<IPPunishment> ipPuns = DATA.getIPPunishment( target.getUniqueId().toString() );
                        for ( IPPunishment pun : ipPuns ) {
                            DATA.deletePunishment( pun.getID() );
                        }

                        ArrayList<Integer> notifs = DATA.searchNotifByUUID( target.getUniqueId().toString() );
                        for ( int id : notifs ) {
                            DATA.removeNotif( id );
                        }

                        DATA.removeAllGUIPun( target );

                        if ( ipPuns.size() + allPuns.size() == 0 ) { CommandErrors.sendNoPreviousPunishments( sender, target.getName() ); }
                        else {
                            String plural = "punishments";
                            if ( ipPuns.size() + allPuns.size() == 1 ) { plural = "punishment"; }
                            sender.sendMessage( Utils.getColored( "&7Successfully deleted &6" + ( ipPuns.size() + allPuns.size() )
                                    + " &7" + plural + " from &6" + target.getName() + "&7's history" ) );
                        }
                    }

                    else {
                        CommandErrors.sendPlayerNotFound( sender, args[1] );
                    }

                }

                else {
                    CommandErrors.sendCommandUsage( sender, "history-reset" );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "history" );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "history" );
        }

        return true;
    }

}
