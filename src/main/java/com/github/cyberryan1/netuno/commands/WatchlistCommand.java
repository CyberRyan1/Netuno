package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistCommand extends CyberSuperCommand {

    public WatchlistCommand() {
        super(
                "watchlist",
                Settings.WATCHLIST_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );

        addSubCommand( new WatchlistViewSubCommand() );
        addSubCommand( new WatchlistAddSubCommand() );
        addSubCommand( new WatchlistRemoveSubCommand() );

        demandPermission( true );
    }

    public List<String> tabComplete( SentCommand sentCommand ) {
        return List.of();
    }

    public boolean execute( SentCommand sentCommand ) {
        return true;
    }
}
