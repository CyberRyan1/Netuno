package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberSubcommand;
import com.github.cyberryan1.cybercore.helpers.command.CyberSupercommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class HistorySupercommand extends CyberSupercommand {

    public HistorySupercommand() {
        super( 
                "history",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null 
        );
        register( true );

        addSubcommand( new HistoryListSubcommand() );
        addSubcommand( new HistoryEditSubcommand() );
        addSubcommand( new HistoryResetSubcommand() );

        demandPermission( true );
        setMinArgs( 2 );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        final List<CyberSubcommand> ALLOWED_SUBCOMMANDS = getSubcommandsForPlayer( sender );
        if ( ALLOWED_SUBCOMMANDS.size() == 0 ) { return List.of(); }
        final List<String> ALLOWED_SUBCOMMAND_LABELS = ALLOWED_SUBCOMMANDS.stream()
                .map( CyberSubcommand::getName )
                .collect( Collectors.toList() );

        if ( args.length == 0 || args[0].length() == 0 ) { return ALLOWED_SUBCOMMAND_LABELS; }
        if ( args.length == 1 ) { return matchArgs( ALLOWED_SUBCOMMAND_LABELS, args[0] ); }

        for ( CyberSubcommand subcmd : ALLOWED_SUBCOMMANDS ) {
            if ( subcmd.getName().equalsIgnoreCase( args[0] ) ) {
                return subcmd.onTabComplete( sender, args );
            }
        }

        return List.of();
    }

    @Override
    // /history list (player)
    // /history edit (pun ID)
    // /history reset (player)
    public boolean execute( CommandSender sender, String args[] ) {

        for ( CyberSubcommand subcmd : getSubcommandsForPlayer( sender ) ) {
            if ( subcmd.getName().equalsIgnoreCase( args[0] ) ) {
                subcmd.runExecute( sender, args );
                return true;
            }
        }

        super.sendUsage( sender );
        return true;
    }
}