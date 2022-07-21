package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetunoCmd extends BaseCommand {

    private final String COMMAND_ORDER[] = { "help", "punish", "warn", "clearchat", "kick", "mute", "unmute", "history", "ban", "unban",
            "ipinfo", "ipmute", "unipmute", "ipban", "unipban", "report", "reports", "togglesigns", "reload" };

    public NetunoCmd() {
        super( "netuno", YMLUtils.getConfig().getStr( "general.staff-perm" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 0 || args[0].length() == 0 ) {
                List<String> toReturn = new ArrayList<>();
                Collections.addAll( toReturn, "reload", "help" );
                return toReturn;
            }
            else if ( "RELOAD".startsWith( args[0].toUpperCase()  ) ) {
                List<String> toReturn = new ArrayList<>();
                Collections.addAll( toReturn, "reload" );
                return toReturn;
            }
            else if ( "HELP".startsWith( args[0].toUpperCase() ) ) {
                List<String> toReturn = new ArrayList<>();
                Collections.addAll( toReturn, "help" );
                return toReturn;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
            if ( args[0].equalsIgnoreCase( "reload" ) ) {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "general.reload-perm" ) ) ) {
                    sender.sendMessage( Utils.getColored( "&7Attempting to reload &6Netuno&7..." ) );
                    Utils.logInfo( "Attempting to reload Netuno..." );

                    YMLUtils.getConfig().getYMLManager().reloadConfig();
                    YMLUtils.getConfig().getYMLManager().updateConfig();

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
                    catch ( NumberFormatException e ) { sender.sendMessage( Utils.getColored( "&hInvalid page number!" ) ); }

                    if ( page >= 0 && page < Math.ceil( COMMAND_ORDER.length / 6.0 ) ) {
                        sendHelpMessage( sender, page * 6, page * 6 + 6 );
                    }

                    else {
                        sender.sendMessage( Utils.getColored( "&hInvalid page number!" ) );
                    }
                }

                else {
                    sendHelpMessage( sender, 0, 6 );
                }
            }

            else {
                sendHelpMessage( sender, 0, 6 );
            }
        }

        else {
            sendHelpMessage( sender, 0, 6 );
        }

        return true;
    }

    private void sendHelpMessage( CommandSender sender, int start, int end ) {
        String toSend = "\n";
        for ( int index = start; index < end; index++ ) {
            toSend += CommandErrors.getCommandUsage( COMMAND_ORDER[index] ) + "\n";
        }
        toSend += "\n";
        Utils.sendAnyMsg( sender, toSend );
    }
}
