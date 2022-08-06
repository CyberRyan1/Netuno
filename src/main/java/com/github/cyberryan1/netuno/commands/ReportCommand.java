package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.guis.report.ReportGUI;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportCommand extends CyberCommand {

    public ReportCommand() {
        super(
                "report",
                Settings.REPORT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sreport &p(player)"
        );
        register( true );

        demandPlayer( true );
        if ( super.getPermission().isBlank() == false ) { demandPermission( true ); }
        setMinArgs( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /report (player)
    public boolean execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        ReportGUI gui = new ReportGUI( player, target );
        gui.open();
        return true;
    }
}
