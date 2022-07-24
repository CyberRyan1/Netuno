package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chatslow extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public Chatslow() {
        super( "chatslow", YMLUtils.getConfig().getStr( "chatslow.perm" ), YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ), null );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 1 ) {
                if ( args[0].length() == 0 ) {
                    List<String> toReturn = new ArrayList<>();
                    Collections.addAll( toReturn, "get", "set" );
                    return toReturn;
                }

                if ( "GET".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add( "get" );
                    return toReturn;
                }

                else if ( "SET".startsWith( args[0].toUpperCase() ) ) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add( "set" );
                    return toReturn;
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    // /chatslow get
    // /chatslow set (amount)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "chatslow.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            // /chatslow get
            if ( args[0].equalsIgnoreCase( "get" ) ) {
                sender.sendMessage( Utils.getColored( "&hThe chatslow is currently &g" + ChatslowManager.getSlow() ) + " seconds" );
            }

            // /chatslow set (amount)
            else if ( args[0].equalsIgnoreCase( "set" ) ) {
                if ( Utils.isOutOfBounds( args, 1 ) == false ) {
                    int newSlow;
                    try {
                        newSlow = Integer.parseInt( args[1] );
                    } catch ( NumberFormatException e ) {
                        CommandErrors.sendCommandUsage( sender, "chatslow-set" );
                        return true;
                    }

                    if ( newSlow < 0 ) {
                        sender.sendMessage( Utils.getColored( "&hThe chatslow must be a positive integer or zero" ) );
                        return true;
                    }

                    ChatslowManager.setSlow( newSlow );
                    sender.sendMessage( Utils.getColored( "&hThe chatslow has been set to &g" + ChatslowManager.getSlow() + " seconds" ) );

                    if ( YMLUtils.getConfig().getStr( "chatslow.broadcast" ).equals( "" ) == false ) {
                        Bukkit.broadcastMessage( YMLUtils.getConfig().getColoredStr( "chatslow.broadcast" ).replace( "[AMOUNT]", newSlow + "" ) );
                    }
                }

                else {
                    CommandErrors.sendCommandUsage( sender, "chatslow-set" );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "chatslow" );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "chatslow" );
        }

        return true;
    }
}