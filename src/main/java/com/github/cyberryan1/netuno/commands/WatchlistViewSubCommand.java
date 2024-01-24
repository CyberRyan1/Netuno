package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class WatchlistViewSubCommand extends CyberSubCommand {

    public WatchlistViewSubCommand() {
        super(
                "view",
                Settings.WATCHLIST_VIEW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&swatchlist &pview (regex/word)"
        );

        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.STRING );
        setStringArgOptions( 0, List.of( "regex", "word" ) );
    }

    public List<String> tabComplete( SentCommand sentCommand, SentSubCommand subCommand ) {
        return List.of();
    }

    public boolean execute( SentCommand sentCommand, SentSubCommand subCommand ) {
        if ( subCommand.getArg( 0 ).equalsIgnoreCase( "regex" ) ) {
            String regex = String.join( "&7\"&f, \"&p", WatchlistManager.getPatternsList() );
            CyberMsgUtils.sendMsg( sentCommand.getPlayer(), "&7Current regex items: " + regex );
        }
        else {
            String normal = String.join( "&7\"&f, \"&p", WatchlistManager.getWordsList() );
            CyberMsgUtils.sendMsg( sentCommand.getPlayer(), "&7Current regular items: " + normal );
        }

        return true;
    }
}
