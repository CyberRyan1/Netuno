package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberSubcommand;
import com.github.cyberryan1.cybercore.helpers.command.SubcommandStatus;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryListSubcommand extends CyberSubcommand {

    public HistoryListSubcommand() {
        super(
                "list",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &plist (player)"
        );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgs( 2 );
        setArgType( 1, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String args[] ) {
        return List.of();
    }

    @Override
    public SubcommandStatus execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[1] );

        HistoryListGUI gui = new HistoryListGUI( target, player, 1 );
        gui.open();
        return SubcommandStatus.NORMAL;
    }
}