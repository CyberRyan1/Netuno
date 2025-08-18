package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.report.ViewAllReportsGui;
import com.github.cyberryan1.netuno.guis.report.ViewPlayerReportsGui;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class ViewReportsCommand extends CyberCommand {

    public ViewReportsCommand( int helpOrder ) {
        super(
                "viewreports",
                Settings.VIEW_REPORT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&sviewreports &p[player]"
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        setDemandPlayer( true );
        setMinArgLength( 0 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        if ( command.getArgs().length == 0 ) {
            ViewAllReportsGui gui = new ViewAllReportsGui( command.getPlayer() );
            gui.open();
        }

        else {
            ViewPlayerReportsGui gui = new ViewPlayerReportsGui( command.getPlayer(), command.getOfflinePlayerAtArg( 0 ) );
            gui.open();
        }

        return true;
    }
}