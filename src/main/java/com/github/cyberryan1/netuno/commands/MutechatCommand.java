package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

;

public class MutechatCommand extends HelpableCommand {
    
    private final String CHAT_MUTE_ENABLE;
    private final String CHAT_MUTE_DISABLE;

    public MutechatCommand( int helpOrder ) {
        super(
                "mutechat",
                Settings.MUTECHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&smutechat &p[toggle/enable/disable/status]"
        );
        register( true );

        demandPermission( true );
        setRunAsync( true );
        setMinArgLength( 0 );
        setArgType( 0, ArgType.STRING );
        setStringArgOptions( 0, List.of( "toggle", "enable", "disable", "status" ) );
        CHAT_MUTE_ENABLE = Utils.getCombinedString( Settings.MUTECHAT_CHAT_ENABLE_BROADCAST.coloredStringlist() );
        CHAT_MUTE_DISABLE = Utils.getCombinedString( Settings.MUTECHAT_CHAT_DISABLE_BROADCAST.coloredStringlist() );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /mutechat [toggle/enable/disable/status]
    public boolean execute( SentCommand command ) {

        if ( command.getArgs().length == 0 || command.getArg( 0 ).equalsIgnoreCase( "toggle" ) || command.getArg( 0 ).equalsIgnoreCase( "t" ) ) {
            // mutechat enabled -> disabled
            if ( MutechatManager.chatIsMuted() == false ) {
                MutechatManager.setChatMuted( true );
                if ( CHAT_MUTE_DISABLE != null && CHAT_MUTE_DISABLE.length() > 0 ) {
                    String msg = Utils.replaceStaffVariable( CHAT_MUTE_DISABLE, command.getSender() );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute has been &cdisabled" );
                }

            }

            // mutechat disabled -> enabled
            else {
                MutechatManager.setChatMuted( false );
                if ( CHAT_MUTE_ENABLE != null && CHAT_MUTE_ENABLE.length() > 0 ) {
                    String msg = Utils.replaceStaffVariable( CHAT_MUTE_ENABLE, command.getSender() );
                    for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }

                else {
                    CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute has been &aenabled" );
                }
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "enable" ) || command.getArg( 0 ).equalsIgnoreCase( "e" ) ) {
            if ( MutechatManager.chatIsMuted() ) {
                CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute is already enabled" );
                return true;
            }

            MutechatManager.setChatMuted( false );
            if ( CHAT_MUTE_ENABLE != null && CHAT_MUTE_ENABLE.length() > 0 ) {
                String msg = Utils.replaceStaffVariable( CHAT_MUTE_ENABLE, command.getSender() );
                for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                    Utils.sendAnyMsg( p, msg );
                }
            }

            else {
                CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute has been &aenabled" );
            }

        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "disable" ) || command.getArg( 0 ).equalsIgnoreCase( "d" ) ) {
            if ( MutechatManager.chatIsMuted() == false ) {
                CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute is already disabled" );
                return true;
            }

            MutechatManager.setChatMuted( true );
            if ( CHAT_MUTE_DISABLE != null && CHAT_MUTE_DISABLE.length() > 0 ) {
                String msg = Utils.replaceStaffVariable( CHAT_MUTE_DISABLE, command.getSender() );
                for ( Player p : Bukkit.getServer().getOnlinePlayers() ) {
                    Utils.sendAnyMsg( p, msg );
                }
            }

            else {
                CyberMsgUtils.sendMsg( command.getSender(),"&sChatemute has been &cdisabled" );
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "status" ) || command.getArg( 0 ).equalsIgnoreCase( "s" ) ) {
            // mutechat is enabled
            if ( MutechatManager.chatIsMuted() ) {
                CyberMsgUtils.sendMsg( command.getSender(), "&sChatmute is currently &aenabled" );
            }

            // mutechat is disabled
            else {
                CyberMsgUtils.sendMsg( command.getSender(),"&sChatemute is currently &cdisabled" );
            }
        }

        return true;
    }

}