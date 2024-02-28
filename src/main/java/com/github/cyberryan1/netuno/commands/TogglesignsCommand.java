package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.apimplement.database.SignNotifs;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.entity.Player;

import java.util.List;

;

public class TogglesignsCommand extends HelpableCommand {

    public TogglesignsCommand( int helpOrder ) {
        super(
                "togglesigns",
                Settings.SIGN_NOTIFS_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&stogglesigns &p[enable/disable]"
        );
        register( true );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 0 );
        setArgType( 0, ArgType.STRING );
        setStringArgOptions( 0, List.of( "enable", "disable" ) );
        setRunAsync( true );
    }


    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /togglesigns
    // /togglesigns [enable/disable]
    public boolean execute( SentCommand command ) {
        final Player player = command.getPlayer();
        final boolean noSignNotifs = SignNotifs.playerHasNoSignNotifs( player.getUniqueId().toString() ); // true = no sign notifs is enabled

        if ( command.getArgs().length == 0 ) {
            // currently has sign notifs enabled
            if ( noSignNotifs == false ) {
                SignNotifs.addPlayerNoSignNotifs( player.getUniqueId().toString() );
                CyberMsgUtils.sendMsg( player, "&cDisabled &ssign notifications" );
            }

            // currently has sign notifs disabled
            else {
                SignNotifs.removePlayerNoSignNotifs( player.getUniqueId().toString() );
                CyberMsgUtils.sendMsg( player, "&aEnabled &ssign notifications" );
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "enable" ) ) {
            if ( noSignNotifs == false ) {
                CyberMsgUtils.sendMsg( player, "&sSign notifications are already enabled" );
            }

            else {
                SignNotifs.removePlayerNoSignNotifs( player.getUniqueId().toString() );
                CyberMsgUtils.sendMsg( player, "&aEnabled &ssign notifications" );
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "disable" ) ) {
            if ( noSignNotifs ) {
                CyberMsgUtils.sendMsg( player, "&sSign notifications are already disabled" );
            }

            else {
                SignNotifs.addPlayerNoSignNotifs( player.getUniqueId().toString() );
                CyberMsgUtils.sendMsg( player, "&cDisabled &ssign notifications" );
            }
        }

        return true;
    }
}
