package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Warn implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /warn (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "warn.perm" ) ) ) {
            if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                String reason = Utils.getRemainingArgs( args, 1 );
                Punishment pun = new Punishment();

                pun.setReason( reason );
                pun.setDate( Time.getCurrentTimestamp() );
                pun.setType( "Warn" );

                String staff;
                String targetName;
                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {

                    targetName = target.getName();
                    pun.setPlayerUUID( target.getUniqueId().toString() );

                    if ( sender instanceof Player ) {
                        Player player = ( Player ) sender;
                        pun.setStaffUUID( player.getUniqueId().toString() );
                        staff = player.getName();

                        if ( ConfigUtils.getBool( "general.staff-punishments" ) == false ) {
                            if ( VaultUtils.hasPerms( target, ConfigUtils.getStr( "general.staff-perm" ) ) &&
                                    VaultUtils.hasPerms( player, ConfigUtils.getStr( "general.all-perms" ) ) == false ) {
                                player.sendMessage( Utils.getColored( "&6" + targetName + " &7is a staff member, so they cannot be punished" ) );
                                return true;
                            }
                        }
                    }

                    else {
                        pun.setStaffUUID( "CONSOLE" );
                        staff = "CONSOLE";
                    }

                    DATA.addPunishment( pun );

                    String warnMsg = ConfigUtils.getColoredStrFromList( "warn.message" );
                    warnMsg = ConfigUtils.replaceAllVariables( warnMsg, staff, targetName, "", reason );
                    target.sendMessage( warnMsg );
                    if ( warnMsg.charAt( warnMsg.length() - 1 ) == '\n' ) {
                        target.sendMessage( "" );
                    }
                }

                // target is offline
                else if ( Bukkit.getServer().getOfflinePlayer( args[0] ).hasPlayedBefore() ) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer( args[0] );
                    targetName = offline.getName();

                    String uuid = offline.getUniqueId().toString();
                    pun.setPlayerUUID( uuid );

                    if ( sender instanceof Player ) {
                        Player player = ( Player ) sender;
                        pun.setStaffUUID( player.getUniqueId().toString() );
                        staff = player.getName();

                        if ( ConfigUtils.getBool( "general.staff-punishments" ) == false ) {
                            if ( VaultUtils.hasPerms( offline, ConfigUtils.getStr( "general.staff-perm" ) ) &&
                                    VaultUtils.hasPerms( player, ConfigUtils.getStr( "general.all-perms" ) ) == false ) {
                                player.sendMessage( Utils.getColored( "&6" + targetName + " &7is a staff member, so they cannot be punished" ) );
                                return true;
                            }
                        }
                    }

                    else {
                        pun.setStaffUUID( "CONSOLE" );
                        staff = "CONSOLE";
                    }

                    int id = DATA.addPunishment( pun );
                    DATA.addNotif( id, uuid );
                }

                else {
                    sender.sendMessage( ConfigUtils.getColoredStr( "&7Could not find any played named &6" + args[0] ) );
                    return true;
                }

                boolean staffBroadcast = ConfigUtils.checkListNotEmpty( "warn.staff-broadcast" );
                if ( ConfigUtils.checkListNotEmpty( "warn.broadcast" ) ) {
                    String broadcastMsg = ConfigUtils.getColoredStrFromList( "warn.broadcast" );
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
                    String broadcastMsg = ConfigUtils.getColoredStrFromList( "warn.staff-broadcast" );
                    broadcastMsg = ConfigUtils.replaceAllVariables( broadcastMsg, staff, targetName, "", reason );

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
                sender.sendMessage( Utils.getColored( "&8/&6warn &7(player) (reason)" ) );
            }
        }

        else {
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
        }

        return true;
    }
}
