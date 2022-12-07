package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.report.StaffAllReportsGUI;
import com.github.cyberryan1.netuno.guis.report.StaffPlayerReportsGUI;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportsCommand extends CyberCommand {

    public ReportsCommand() {
        super(
                "reports",
                Settings.REPORT_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sreports &p[player]"
        );
        register( true );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgLength( 0 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /reports [player]
    public boolean execute( SentCommand command ) {
        final Player staff = command.getPlayer();

        // /reports
        if ( command.getArgs().length == 0 ) {
            StaffAllReportsGUI gui = new StaffAllReportsGUI( staff, 1 );
            gui.open();
        }

        // /reports [player]
        else {
            final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );
            StaffPlayerReportsGUI gui = new StaffPlayerReportsGUI( staff, target );
            gui.open();
        }

        return true;
    }
}