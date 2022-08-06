package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.guis.report.StaffAllReportsGUI;
import com.github.cyberryan1.netuno.guis.report.StaffPlayerReportsGUI;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
        setMinArgs( 0 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /reports [player]
    public boolean execute( CommandSender sender, String args[] ) {
        final Player staff = ( Player ) sender;

        // /reports
        if ( args.length == 0 ) {
            StaffAllReportsGUI gui = new StaffAllReportsGUI( staff, 1 );
            gui.open();
        }

        // /reports [player]
        else {
            final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            StaffPlayerReportsGUI gui = new StaffPlayerReportsGUI( staff, target );
            gui.open();
        }

        return true;
    }
}