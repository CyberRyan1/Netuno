package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistCommand extends CyberSuperCommand {

    public WatchlistCommand( int helpOrder ) {
        super(
                "watchlist",
                Settings.WATCHLIST_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        WatchlistAddSubcommand add = new WatchlistAddSubcommand();
        new CommandHelpInfo( add, helpOrder + 1 );
        WatchlistRemoveSubcommand remove = new WatchlistRemoveSubcommand();
        new CommandHelpInfo( remove, helpOrder + 2 );
        WatchlistViewSubcommand view = new WatchlistViewSubcommand();
        new CommandHelpInfo( view, helpOrder + 3 );

        addSubCommand( add );
        addSubCommand( remove );
        addSubCommand( view );

        setDemandPermission( true );
        setMinArgLength( 1 );
    }

    @Override
    public List<String> tabComplete( SentCommand sentCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand sentCommand ) {
        return true;
    }
}

class WatchlistAddSubcommand extends CyberSubCommand {

    public WatchlistAddSubcommand() {
        super(
                "add",
                Settings.WATCHLIST_EDIT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &padd (word/regex)"
        );
        setDemandPermission( true );
        setMinArgLength( 1 );
    }


    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        String arg = subcommand.getCombinedArgs( 0 );
        if ( Netuno.CHAT_SERVICE.getWatchlist().contains( arg ) == false ) {
            Netuno.CHAT_SERVICE.getWatchlist().add( arg );
            command.respond( "&sAdded \"&p" + arg + "&s\" to the watchlist" );
        }
        else {
            command.respond( "&s\"&p" + arg + "&s\" is already in the watchlist" );
        }
        return true;
    }
}

class WatchlistRemoveSubcommand extends CyberSubCommand {

    public WatchlistRemoveSubcommand() {
        super(
                "remove",
                Settings.WATCHLIST_EDIT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &premove (word/regex)"
        );
        setDemandPermission( true );
        setMinArgLength( 1 );
    }


    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        if ( subcommand.getSubcommandArgs().length == 0 || subcommand.getArg( 0 ).length() == 0 ) return Netuno.CHAT_SERVICE.getWatchlist();
        else if ( subcommand.getSubcommandArgs().length == 1 ) return CyberCommandUtils.matchArgs( Netuno.CHAT_SERVICE.getWatchlist(), subcommand.getArg( 0 ) );
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        String arg = subcommand.getCombinedArgs( 0 );
        if ( Netuno.CHAT_SERVICE.getWatchlist().contains( arg ) ) {
            Netuno.CHAT_SERVICE.getWatchlist().remove( arg );
            command.respond( "&sRemoved \"&p" + arg + "&s\" from the watchlist" );
        }
        else {
            command.respond( "&s\"&p" + arg + "&s\" is not in the watchlist" );
        }
        return true;
    }
}

class WatchlistViewSubcommand extends CyberSubCommand {

    public WatchlistViewSubcommand() {
        super(
                "view",
                Settings.WATCHLIST_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &pview"
        );
        setDemandPermission( true );
    }


    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        subcommand.respond( "&sCurrent watchlist: " );
        subcommand.respond( "&p" + String.join( "&s || &p", Netuno.CHAT_SERVICE.getWatchlist() ) );
        return true;
    }
}