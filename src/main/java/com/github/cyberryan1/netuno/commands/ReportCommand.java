package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.report.ReportGUI;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

;

public class ReportCommand extends HelpableCommand {

    public ReportCommand( int helpOrder ) {
        super(
                "report",
                Settings.REPORT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sreport &p(player)"
        );
        register( true );

        demandPlayer( true );
        if ( super.getPermission().isBlank() == false ) { demandPermission( true ); }
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /report (player)
    public boolean execute( SentCommand command ) {
        final Player player = command.getPlayer();
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        ReportGUI gui = new ReportGUI( player, target );
        gui.open();
        return true;
    }
}
