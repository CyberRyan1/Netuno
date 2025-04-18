package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.history.HistoryListGui;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommand extends CyberSuperCommand {

    public HistoryCommand( int helpOrder ) {
        super(
                "history",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
            null
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        HistoryListSubcommand list = new HistoryListSubcommand();
        new CommandHelpInfo( list, helpOrder + 1 );
        addSubCommand( list );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 2 );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        return true;
    }
}

class HistoryListSubcommand extends CyberSubCommand {

    public HistoryListSubcommand() {
        super(
                "list",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &plist (player)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );

    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );

        HistoryListGui gui = new HistoryListGui( player, target );
        gui.open();
        return true;
    }
}