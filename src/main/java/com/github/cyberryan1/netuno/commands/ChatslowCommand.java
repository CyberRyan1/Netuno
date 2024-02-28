package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

;

public class ChatslowCommand extends HelpableCommand {

    public ChatslowCommand( int helpOrder ) {
        super(
                helpOrder,
                "chatslow",
                Settings.CHATSLOW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );

        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.STRING );
        setStringArgOptions( 0, List.of( "get", "set" ) );
        setArgType( 1, ArgType.INTEGER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /chatslow get
    // /chatslow set (amount)
    public boolean execute( SentCommand command ) {

        // /chatslow get
        if ( command.getArg( 0 ).equalsIgnoreCase( "get" ) ) {
            CyberMsgUtils.sendMsg( command.getSender(), "&sThe chatslow is currently &p" + ChatslowManager.getSlow() + " seconds" );
        }

        // /chatslow set (amount)
        else if ( command.getArg( 0 ).equalsIgnoreCase( "set" ) ) {
            int newSlow = command.getIntegerAtArg( 1 );
            if ( newSlow < 0 ) {
                CyberMsgUtils.sendMsg( command.getSender(), "&sThe chatslow must be greater than or equal to zero" );
                return true;
            }

            ChatslowManager.setSlow( newSlow );
            CyberMsgUtils.sendMsg( command.getSender(), "&sThe chatslow has been set to &p" + ChatslowManager.getSlow() + " seconds" );

            if ( Settings.CHATSLOW_BROADCAST.string().isBlank() == false ) {
                Bukkit.broadcastMessage( CyberColorUtils.getColored(
                        Settings.CHATSLOW_BROADCAST.coloredString().replace( "[AMOUNT]", newSlow + "" ) ) );
            }
        }

        return true;
    }

    @Override
    public void sendUsage( CommandSender sender ) {
        CyberMsgUtils.sendMsg( sender,
                "&8",
                "&8/&schatslow &pget",
                "&8/&schatslow &pset (amount)",
                "&8"
        );
    }
}