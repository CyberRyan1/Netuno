package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.models.commands.HelpableSubCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistRemoveSubCommand extends HelpableSubCommand {

    public WatchlistRemoveSubCommand( int helpOrder ) {
        super(
                helpOrder,
                "remove",
                Settings.WATCHLIST_EDIT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &premove (word/regex) (entry)"
        );

        setDemandPermission( true );
        setMinArgLength( 2 );
        setArgType( 0, ArgType.STRING );
        setStringArgOptions( 0, List.of( "regex", "word" ) );
    }

    public List<String> tabComplete( SentCommand sentCommand, SentSubCommand sentSubCommand ) {
        return List.of();
    }

    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        String regexOrWords = "words";
        if ( subCommand.getArg( 0 ).equalsIgnoreCase( "regex" ) ) {
            WatchlistManager.getPatternsList().remove( subCommand.getArg( 1 ) );
            regexOrWords = "regex";
        }
        else {
            WatchlistManager.getWordsList().remove( subCommand.getArg( 1 ) );
        }

        CyberMsgUtils.sendMsg( command.getPlayer(), "&sRemoved \"&p" + subCommand.getArg( 1 ) + "&s\" from the " + regexOrWords + " list" );
        return true;
    }
}