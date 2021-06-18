package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Mutechat implements CommandExecutor {

    private final String CHAT_MUTE_ENABLE = ConfigUtils.getColoredStrFromList( "mutechat.enable-broadcast" );
    private final String CHAT_MUTE_DISABLE = ConfigUtils.getColoredStrFromList( "mutechat.disable-broadcast" );

    @Override
    // /mutechat [toggle/enable/disable/status]
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "mutechat.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
            if ( args[0].equalsIgnoreCase( "enable" ) || args[0].equalsIgnoreCase( "e" ) ) {
                if ( MutechatManager.chatIsMuted() ) {
                    sender.sendMessage( Utils.getColored( "&7Chat is already muted" ) );
                    return true;
                }

                MutechatManager.setChatMuted( false );
                if ( ConfigUtils.checkListNotEmpty( "mutechat.enable-broadcast" ) ) {
                    String msg = ConfigUtils.replaceStaffVariable( CHAT_MUTE_ENABLE, sender );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    sender.sendMessage( Utils.getColored( "&7Chatmute has been &aenabled&7!" ) );
                }

            }

            else if ( args[0].equalsIgnoreCase( "disable" ) || args[0].equalsIgnoreCase( "d" ) ) {
                if ( MutechatManager.chatIsMuted() == false ) {
                    sender.sendMessage( Utils.getColored( "&7Chat is already muted" ) );
                    return true;
                }

                MutechatManager.setChatMuted( true );
                if ( ConfigUtils.checkListNotEmpty( "mutechat.disable-broadcast" ) ) {
                    String msg = ConfigUtils.replaceStaffVariable( CHAT_MUTE_DISABLE, sender );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    sender.sendMessage( Utils.getColored( "&7Chatmute has been &cdisabled&7!" ) );
                }
            }

            else if ( args[0].equalsIgnoreCase( "toggle" ) || args[0].equalsIgnoreCase( "t" ) ) {
                // mutechat enabled -> disabled
                if ( MutechatManager.chatIsMuted() == false ) {
                    MutechatManager.setChatMuted( true );
                    if ( ConfigUtils.checkListNotEmpty( "mutechat.disable-broadcast" ) ) {
                        String msg = ConfigUtils.replaceStaffVariable( CHAT_MUTE_DISABLE, sender );
                        for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&7Chatmute has been &cdisabled&7!" ) );
                    }

                }

                // mutechat disabled -> enabled
                else {
                    MutechatManager.setChatMuted( false );
                    if ( ConfigUtils.checkListNotEmpty( "mutechat.enable-broadcast" ) ) {
                        String msg = ConfigUtils.replaceStaffVariable( CHAT_MUTE_ENABLE, sender );
                        for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&7Chatmute has been &aenabled&7!" ) );
                    }
                }
            }

            else if ( args[0].equalsIgnoreCase( "status" ) || args[0].equalsIgnoreCase( "s" ) ) {
                // mutechat is enabled
                if ( MutechatManager.chatIsMuted() ) {
                    sender.sendMessage( Utils.getColored( "&7Chatmute is currently &aenabled" ) );
                }

                // mutechat is disabled
                else {
                    sender.sendMessage( Utils.getColored( "&7Chatemute is currently &cdisabled" ) );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "mutechat" );
            }

        }

        else { // run "/mutechat toggle" here
            String newArgs[] = new String[1];
            newArgs[0] = "toggle";
            return onCommand( sender, command, label, newArgs );
        }

        return true;
    }

}