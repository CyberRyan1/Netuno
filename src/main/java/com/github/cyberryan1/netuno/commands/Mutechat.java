package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mutechat extends BaseCommand {

    private final String CHAT_MUTE_ENABLE = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "mutechat.enable-broadcast" ) );
    private final String CHAT_MUTE_DISABLE = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "mutechat.disable-broadcast" ) );

    public Mutechat() {
        super( "mutechat", YMLUtils.getConfig().getStr( "mutechat.perm" ), YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ), getColorizedStr( "&8/&umutechat &y[toggle/enable/disable/status]" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length <= 1 ) {
                if ( args[0].length() == 0 ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "toggle", "enable", "disable", "status" );
                    return toReturn;
                }

                else if ( "TOGGLE".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "toggle" );
                    return toReturn;
                }

                else if ( "ENABLE".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "enable" );
                    return toReturn;
                }

                else if ( "DISABLE".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "disable" );
                    return toReturn;
                }

                else if ( "STATUS".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "status" );
                    return toReturn;
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    // /mutechat [toggle/enable/disable/status]
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "mutechat.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
            if ( args[0].equalsIgnoreCase( "enable" ) || args[0].equalsIgnoreCase( "e" ) ) {
                if ( MutechatManager.chatIsMuted() ) {
                    sender.sendMessage( Utils.getColored( "&hChat is already muted" ) );
                    return true;
                }

                MutechatManager.setChatMuted( false );
                if ( CHAT_MUTE_ENABLE != null && CHAT_MUTE_ENABLE.length() > 0 ) {
                    String msg = Utils.replaceStaffVariable( CHAT_MUTE_ENABLE, sender );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    sender.sendMessage( Utils.getColored( "&hChatmute has been &aenabled&h!" ) );
                }

            }

            else if ( args[0].equalsIgnoreCase( "disable" ) || args[0].equalsIgnoreCase( "d" ) ) {
                if ( MutechatManager.chatIsMuted() == false ) {
                    sender.sendMessage( Utils.getColored( "&hChat is already muted" ) );
                    return true;
                }

                MutechatManager.setChatMuted( true );
                if ( CHAT_MUTE_DISABLE != null && CHAT_MUTE_DISABLE.length() > 0 ) {
                    String msg = Utils.replaceStaffVariable( CHAT_MUTE_DISABLE, sender );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    sender.sendMessage( Utils.getColored( "&hChatmute has been &cdisabled&h!" ) );
                }
            }

            else if ( args[0].equalsIgnoreCase( "toggle" ) || args[0].equalsIgnoreCase( "t" ) ) {
                // mutechat enabled -> disabled
                if ( MutechatManager.chatIsMuted() == false ) {
                    MutechatManager.setChatMuted( true );
                    if ( CHAT_MUTE_DISABLE != null && CHAT_MUTE_DISABLE.length() > 0 ) {
                        String msg = Utils.replaceStaffVariable( CHAT_MUTE_DISABLE, sender );
                        for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&hChatmute has been &cdisabled&h!" ) );
                    }

                }

                // mutechat disabled -> enabled
                else {
                    MutechatManager.setChatMuted( false );
                    if ( CHAT_MUTE_ENABLE != null && CHAT_MUTE_ENABLE.length() > 0 ) {
                        String msg = Utils.replaceStaffVariable( CHAT_MUTE_ENABLE, sender );
                        for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&hChatmute has been &aenabled&h!" ) );
                    }
                }
            }

            else if ( args[0].equalsIgnoreCase( "status" ) || args[0].equalsIgnoreCase( "s" ) ) {
                // mutechat is enabled
                if ( MutechatManager.chatIsMuted() ) {
                    sender.sendMessage( Utils.getColored( "&hChatmute is currently &aenabled" ) );
                }

                // mutechat is disabled
                else {
                    sender.sendMessage( Utils.getColored( "&hChatemute is currently &cdisabled" ) );
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