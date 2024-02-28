package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.models.commands.HelpableSubCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryListSubCommand extends HelpableSubCommand {

    public HistoryListSubCommand( int helpOrder ) {
        super(
                "list",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &plist (player)"
        );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );

        HistoryListGUI gui = new HistoryListGUI( target, player, 1 );
        gui.open();
        return true;
    }
}