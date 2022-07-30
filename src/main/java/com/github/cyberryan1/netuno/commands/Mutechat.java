package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Mutechat extends CyberCommand {

    private final String CHAT_MUTE_ENABLE;
    private final String CHAT_MUTE_DISABLE;

    public Mutechat() {
        super(
                "mutechat",
                Settings.MUTECHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&smutechat &p[toggle/enable/disable/status]"
        );
        register( true );

        demandPermission( true );
        setAsync( true );
        CHAT_MUTE_ENABLE = Utils.getCombinedString( Settings.MUTECHAT_CHAT_ENABLE_BROADCAST.coloredStringlist() );
        CHAT_MUTE_DISABLE = Utils.getCombinedString( Settings.MUTECHAT_CHAT_DISABLE_BROADCAST.coloredStringlist() );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            List<String> suggestions = List.of( "toggle", "enable", "disable", "status" );
            if ( args.length == 0 || args[0].length() == 0 ) { return suggestions; }
            if ( args.length == 1 ) { return matchArgs( suggestions, args[0] ); }
        }

        return List.of();
    }

    @Override
    // /mutechat [toggle/enable/disable/status]
    public boolean execute( CommandSender sender, String args[] ) {

        if ( args.length == 0 || args[0].equalsIgnoreCase( "toggle" ) || args[0].equalsIgnoreCase( "t" ) ) {
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
                    CoreUtils.sendMsg( sender, "&sChatmute has been &cdisabled" );
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
                    CoreUtils.sendMsg( sender, "&sChatmute has been &aenabled" );
                }
            }
        }

        else if ( args[0].equalsIgnoreCase( "enable" ) || args[0].equalsIgnoreCase( "e" ) ) {
            if ( MutechatManager.chatIsMuted() ) {
                CoreUtils.sendMsg( sender, "&sChatmute is already enabled" );
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
                CoreUtils.sendMsg( sender, "&sChatmute has been &aenabled" );
            }

        }

        else if ( args[0].equalsIgnoreCase( "disable" ) || args[0].equalsIgnoreCase( "d" ) ) {
            if ( MutechatManager.chatIsMuted() == false ) {
                CoreUtils.sendMsg( sender, "&sChatmute is already disabled" );
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
                CoreUtils.sendMsg( sender,"&sChatemute has been &cdisabled" );
            }
        }

        else if ( args[0].equalsIgnoreCase( "status" ) || args[0].equalsIgnoreCase( "s" ) ) {
            // mutechat is enabled
            if ( MutechatManager.chatIsMuted() ) {
                CoreUtils.sendMsg( sender, "&sChatmute is currently &aenabled" );
            }

            // mutechat is disabled
            else {
                CoreUtils.sendMsg( sender,"&sChatemute is currently &cdisabled" );
            }
        }

        else {
            sendUsage( sender );
        }

        return true;
    }

}