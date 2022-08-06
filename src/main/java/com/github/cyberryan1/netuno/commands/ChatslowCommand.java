package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChatslowCommand extends CyberCommand {

    public ChatslowCommand() {
        super(
                "chatslow",
                Settings.CHATSLOW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );

        setDemandPermission( true );
        setMinArgs( 1 );
        setArgType( 1, ArgType.INTEGER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            List<String> options = List.of( "get", "set" );
            if ( args.length == 0 || args[0].length() == 0 ) { return options; }
            else if ( args.length == 1 ) { return matchArgs( options, args[0] ); }
        }

        return List.of();
    }

    @Override
    // /chatslow get
    // /chatslow set (amount)
    public boolean execute( CommandSender sender, String args[] ) {

        // /chatslow get
        if ( args[0].equalsIgnoreCase( "get" ) ) {
            CoreUtils.sendMsg( sender, "&sThe chatslow is currently &p" + ChatslowManager.getSlow() + " seconds" );
        }

        // /chatslow set (amount)
        else if ( args[0].equalsIgnoreCase( "set" ) ) {
            if ( args.length >= 2 ) {
                int newSlow = Integer.parseInt( args[1] );

                if ( newSlow < 0 ) {
                    CoreUtils.sendMsg( sender, "&sThe chatslow must be greater than or equal to zero" );
                    return true;
                }

                ChatslowManager.setSlow( newSlow );
                CoreUtils.sendMsg( sender, "&sThe chatslow has been set to &p" + ChatslowManager.getSlow() + " seconds" );

                if ( Settings.CHATSLOW_BROADCAST.string().isBlank() == false ) {
                    Bukkit.broadcastMessage( CoreUtils.getColored( Settings.CHATSLOW_BROADCAST.coloredString().replace( "[AMOUNT]", newSlow + "" ) ) );
                }
            }

            else {
                sendUsage( sender );
            }
        }

        return true;
    }

    @Override
    public void sendUsage( CommandSender sender ) {
        CoreUtils.sendMsg( sender,
                "&8",
                "&8/&schatslow &pget",
                "&8/&schatslow &pset (amount)",
                "&8"
        );
    }
}