package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class ToggleSignsCommand extends CyberCommand {

    public ToggleSignsCommand( int helpOrder ) {
        super(
                "togglesigns",
                Settings.SIGN_NOTIFS_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/togglesigns"
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        demandPermission( true );
        demandPlayer( true );
    }


    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        Netuno.SERVICE.getStaff( command.getPlayer() ).thenAccept( staff -> {
            // sign notifs are enabled, want to disable them
            if ( staff.getSignNotificationStatus() ) {
                staff.setSignNotificationStatus( false );
                command.respond( "&sSign notifications have been &pdisabled" );
            }

            // sign notifs are disabled, want to enable them
            else {
                staff.setSignNotificationStatus( true );
                command.respond( "&sSign notifications have been &penabled" );
            }
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
        return true;
    }
}