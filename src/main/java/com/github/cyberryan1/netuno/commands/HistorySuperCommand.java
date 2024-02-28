package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.models.commands.HelpableSuperCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class HistorySuperCommand extends HelpableSuperCommand {

    public HistorySuperCommand( int helpOrder ) {
        super( 
                "history",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null 
        );
        register( true );

        addSubCommand( new HistoryListSubCommand( helpOrder + 10 ) );
        addSubCommand( new HistoryEditSubCommand( helpOrder + 20 ) );
        addSubCommand( new HistoryResetSubCommand( helpOrder + 30 ) );

        demandPermission( true );
        setMinArgLength( 2 );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /history list (player)
    // /history edit (pun ID)
    // /history reset (player)
    public boolean execute( SentCommand command ) {
        return true;
    }
}