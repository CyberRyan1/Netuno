package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /kick (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "kick.perm" ) ) ) {
            if ( Utils.isOutOfBounds( args, 1 ) == false ) {
                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {

                    String reason = Utils.getRemainingArgs( args, 1 );

                    Punishment pun = new Punishment();
                    pun.setPlayerUUID( target.getUniqueId().toString() );
                    pun.setReason( reason );
                    pun.setDate( Time.getCurrentTimestamp() );
                    pun.setType( "Kick" );

                    String staff;
                    if ( sender instanceof Player ) {
                        Player player = ( Player ) sender;
                        pun.setStaffUUID( player.getUniqueId().toString() );
                        staff = player.getName();

                        if ( Utils.checkStaffPunishmentAllowable( player, target ) == false ) {
                            player.sendMessage( Utils.getColored( "&6" + target.getName() + " &7is a staff member, so they cannot be punished" ) );
                            return true;
                        }
                    }

                    else {
                        pun.setStaffUUID( "CONSOLE" );
                        staff = "CONSOLE";
                    }

                    DATA.addPunishment( pun );

                    String kickMsg = "";
                    for ( String str : ConfigUtils.getColoredStrList( "kick.kicked-lines" ) ) {
                        String replaced = ConfigUtils.replaceAllVariables( str, staff, target.getName(), "", reason );
                        kickMsg += replaced + "\n";
                    }

                    target.kickPlayer( kickMsg );

                    boolean staffBroadcast = ConfigUtils.checkListNotEmpty( "kick.staff-broadcast" );
                    if ( ConfigUtils.checkListNotEmpty( "kick.broadcast" ) ) {
                        String broadcastMsg = ConfigUtils.getColoredStrFromList( "kick.broadcast" );
                        broadcastMsg = ConfigUtils.replaceAllVariables( broadcastMsg, staff, target.getName(), "", reason );

                        for ( Player p : Bukkit.getOnlinePlayers() ) {
                            if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) == false || staffBroadcast == false ) {
                                p.sendMessage( broadcastMsg );
                                if ( broadcastMsg.charAt( broadcastMsg.length() - 1 ) == '\n' ) {
                                    p.sendMessage( "" );
                                }
                            }
                        }
                    }

                    if ( staffBroadcast ) {
                        String broadcastMsg = ConfigUtils.getColoredStrFromList( "kick.staff-broadcast" );
                        broadcastMsg = ConfigUtils.replaceAllVariables( broadcastMsg, staff, target.getName(), "", reason );

                        for ( Player p : Bukkit.getOnlinePlayers() ) {
                            if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                                p.sendMessage( broadcastMsg );
                                if ( broadcastMsg.charAt( broadcastMsg.length() - 1 ) == '\n' ) {
                                    p.sendMessage( "" );
                                }
                            }
                        }
                    }
                }

                else {
                    sender.sendMessage( Utils.getColored( "&7Could not find an online player named &6" + args[0] ) );
                }
            }

            else {
                sender.sendMessage( Utils.getColored( "&8/&6kick &7(player) (reason)" ) );
            }
        }

        else {
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
        }


        return true;
    }
}
