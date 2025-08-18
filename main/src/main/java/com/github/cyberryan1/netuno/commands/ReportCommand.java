package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.report.ReportGui;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class ReportCommand extends CyberCommand {

    public ReportCommand( int helpOrder ) {
        super(
                "report",
                Settings.REPORT_PERMISSION.string().isBlank() ? null : Settings.REPORT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&sreport &p(player)"
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        ReportGui gui = new ReportGui( command.getPlayer(), command.getOfflinePlayerAtArg( 0 ) );
        gui.open();
        return true;
    }
}