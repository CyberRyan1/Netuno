package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.api.database.SignNotifs;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Togglesigns extends CyberCommand {

    public Togglesigns() {
        super(
                "togglesigns",
                Settings.SIGN_NOTIFS_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&stogglesigns &p[enable/disable]"
        );
        register( true );

        demandPermission( true );
        demandPlayer( true );
        setAsync( true );
    }


    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            List<String> suggestions = List.of( "enable", "disable" );
            if ( args.length == 0 || args[0].length() == 0 ) { return suggestions; }
            else if ( args.length == 1 ) { return matchArgs( suggestions, args[0] ); }
        }

        return List.of();
    }

    @Override
    // /togglesigns
    // /togglesigns [enable/disable]
    public boolean execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final boolean noSignNotifs = SignNotifs.playerHasNoSignNotifs( player.getUniqueId().toString() ); // true = no sign notifs is enabled

        if ( args.length == 0 ) {
            // currently has sign notifs enabled
            if ( noSignNotifs == false ) {
                SignNotifs.addPlayerNoSignNotifs( player.getUniqueId().toString() );
                CoreUtils.sendMsg( player, "&cDisabled &ssign notifications" );
            }

            // currently has sign notifs disabled
            else {
                SignNotifs.removePlayerNoSignNotifs( player.getUniqueId().toString() );
                CoreUtils.sendMsg( player, "&aEnabled &ssign notifications" );
            }
        }

        else if ( args[0].equalsIgnoreCase( "enable" ) ) {
            if ( noSignNotifs == false ) {
                CoreUtils.sendMsg( player, "&sSign notifications are already enabled" );
            }

            else {
                SignNotifs.removePlayerNoSignNotifs( player.getUniqueId().toString() );
                CoreUtils.sendMsg( player, "&aEnabled &ssign notifications" );
            }
        }

        else if ( args[0].equalsIgnoreCase( "disable" ) ) {
            if ( noSignNotifs ) {
                CoreUtils.sendMsg( player, "&sSign notifications are already disabled" );
            }

            else {
                SignNotifs.addPlayerNoSignNotifs( player.getUniqueId().toString() );
                CoreUtils.sendMsg( player, "&cDisabled &ssign notifications" );
            }
        }

        else {
            sendUsage( player );
        }

        return true;
    }
}
