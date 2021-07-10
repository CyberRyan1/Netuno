package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NetunoCmd implements CommandExecutor {

    private final String COMMAND_ORDER[] = { "help", "warn", "clearchat", "kick", "mute", "unmute", "history", "ban", "unban",
            "ipinfo", "ipmute", "unipmute", "ipban", "unipban", "report", "reports", "togglesigns", "reload" };

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "general.staff-perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
            if ( args[0].equalsIgnoreCase( "reload" ) ) {
                if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "general.reload-perm" ) ) ) {
                    sender.sendMessage( Utils.getColored( "&7Attempting to reload &6Netuno&7..." ) );
                    Utils.logInfo( "Attempting to reload Netuno..." );

                    if ( ConfigUtils.getConfigManager().getConfigFile().exists() == false || ConfigUtils.getConfigManager().getConfigFile() == null ) {
                        Utils.logInfo( "Found no config file, recreating it..." );
                        ConfigUtils.getConfigManager().saveDefaultConfig();
                    }

                    ConfigUtils.getConfigManager().reloadConfig();

                    sender.sendMessage( Utils.getColored( "&7Successfully reloaded &6Netuno" ) );
                    Utils.logInfo( "Successfully reloaded Netuno and its files" );
                }

                else {
                    CommandErrors.sendInvalidPerms( sender );
                }
            }

            else if ( args[0].equalsIgnoreCase( "help" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {
                    int page = 0;
                    try { page = Integer.parseInt( args[1] ) - 1; }
                    catch ( NumberFormatException e ) { sender.sendMessage( Utils.getColored( "&7Invalid page number!" ) ); }

                    if ( page >= 0 && page < Math.ceil( COMMAND_ORDER.length / 6.0 ) ) {
                        String toSend = "\n";
                        for ( int index = page * 6; index < page * 6 + 6; index++ ) {
                            toSend += CommandErrors.getCommandUsage( COMMAND_ORDER[index] ) + "\n";
                        }
                        toSend += "\n";
                        Utils.sendAnyMsg( sender, toSend );
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&7Invalid page number!" ) );
                    }
                }

                else {
                    String toSend = "\n";
                    for ( int index = 0; index < 6; index++ ) {
                        toSend += CommandErrors.getCommandUsage( COMMAND_ORDER[index] ) + "\n";
                    }
                    toSend += "\n";
                    Utils.sendAnyMsg( sender, toSend );
                }
            }

            else {
                String toSend = "\n";
                for ( int index = 0; index < 6; index++ ) {
                    toSend += CommandErrors.getCommandUsage( COMMAND_ORDER[index] ) + "\n";
                }
                toSend += Utils.getColored( "\n" );
                Utils.sendAnyMsg( sender, toSend );
            }
        }

        else {
            String toSend = "\n";
            for ( int index = 0; index < 6; index++ ) {
                toSend += CommandErrors.getCommandUsage( COMMAND_ORDER[index] ) + "\n";
            }
            toSend += "\n";
            Utils.sendAnyMsg( sender, toSend );
        }

        return true;
    }
}
