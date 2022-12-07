package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class HistorySupercommand extends CyberSuperCommand {

    public HistorySupercommand() {
        super( 
                "history",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null 
        );
        register( true );

        addSubCommand( new HistoryListSubcommand() );
        addSubCommand( new HistoryEditSubcommand() );
        addSubCommand( new HistoryResetSubcommand() );

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