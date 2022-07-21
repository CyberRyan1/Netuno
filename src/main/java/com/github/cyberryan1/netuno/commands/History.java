package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.history.HistoryEditGUI;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
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
import java.util.UUID;

public class History extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public History() {
        super( "history", YMLUtils.getConfig().getStr( "history.perm" ), YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ), null );
    }


    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length <= 1 ) {
                if ( args[0].length() == 0 ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "list", "edit", "reset" );
                    return toReturn;
                }

                else if ( "LIST".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add( "list" );
                    return toReturn;
                }

                else if ( "EDIT".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add( "edit" );
                    return toReturn;
                }

                else if ( "RESET".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add( "reset" );
                    return toReturn;
                }
            }

            else if ( args.length == 2 && args[0].equalsIgnoreCase( "edit" ) == false ) {
                if ( args[1].length() == 0 ) {
                    return getAllOnlinePlayerNames();
                }

                else {
                    return matchOnlinePlayers( args[1] );
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    // /history list (player)
    // /history edit (pun ID)
    // /history reset (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "history.perm" ) ) == false ) {
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
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "history.reset.perm" ) ) == false ) {
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
                            sender.sendMessage( Utils.getColored( "&hSuccessfully deleted &g" + ( ipPuns.size() + allPuns.size() )
                                    + " &h" + plural + " from &g" + target.getName() + "&h's history" ) );
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
