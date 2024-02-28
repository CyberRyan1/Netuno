package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.models.commands.HelpableSubCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistAddSubCommand extends HelpableSubCommand {

    public WatchlistAddSubCommand( int helpOrder ) {
        super(
                helpOrder,
                "add",
                Settings.WATCHLIST_EDIT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &padd (word/regex) (entry)",
                "&sAdds a word/regex patterns to the watchlist"
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
            if ( WatchlistManager.getPatternsList().contains( subCommand.getArg( 1 ) ) ) {
                CyberMsgUtils.sendMsg( command.getPlayer(), "&7That pattern is already on the regex list" );
                return true;
            }
            WatchlistManager.getPatternsList().add( subCommand.getArg( 1 ) );
            regexOrWords = "regex";
        }
        else {
            if ( WatchlistManager.getWordsList().contains( subCommand.getArg( 1 ) ) ) {
                CyberMsgUtils.sendMsg( command.getPlayer(), "&7That word is already on the normal list" );
                return true;
            }
            WatchlistManager.getWordsList().add( subCommand.getArg( 1 ) );
        }

        CyberMsgUtils.sendMsg( command.getPlayer(), "&sAdded \"&p" + subCommand.getArg( 1 ) + "&s\" to the " + regexOrWords + " list" );
        return true;
    }
}