package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.models.commands.HelpableSuperCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistCommand extends HelpableSuperCommand {

    public WatchlistCommand( int helpOrder ) {
        super(
                "watchlist",
                Settings.WATCHLIST_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );

        addSubCommand( new WatchlistViewSubCommand( helpOrder + 10 ) );
        addSubCommand( new WatchlistAddSubCommand( helpOrder + 20 ) );
        addSubCommand( new WatchlistRemoveSubCommand( helpOrder + 30 ) );

        demandPermission( true );
    }

    public List<String> tabComplete( SentCommand sentCommand ) {
        return List.of();
    }

    public boolean execute( SentCommand sentCommand ) {
        return true;
    }
}
