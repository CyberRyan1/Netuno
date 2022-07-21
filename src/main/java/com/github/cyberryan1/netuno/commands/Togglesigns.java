package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Togglesigns extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public Togglesigns() {
        super( "reports", YMLUtils.getConfig().getStr( "signs.notifs-perm" ), getColorizedStr( "&8/&ureports &y[player]" ) );
    }


    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 0 || args[0].length() == 0 ) {
                return addAllToList( "enable", "disable" );
            }
            else if ( "ENABLE".startsWith( args[0].toUpperCase() ) ) {
                return addAllToList( "enable" );
            }
            else if ( "DISABLE".startsWith( args[0].toUpperCase() ) ) {
                return addAllToList( "disable" );
            }
        }

        return Collections.emptyList();
    }

    @Override
    // /togglesigns
    // /togglesigns [enable/disable]
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "signs.notifs-perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }
        Player player = ( Player ) sender;

        // /togglesigns
        if ( Utils.isOutOfBounds( args, 0 ) ) {
            // currently has sign notifs enabled
            if ( DATA.checkPlayerNoSignNotifs( player ) == false ) {
                DATA.addPlayerNoSignNotifs( player );
                player.sendMessage( Utils.getColored( "&cDisabled&h sign notifications" ) );
            }

            // currently has sign notifs disabled
            else {
                DATA.removePlayerNoSignNotifs( player );
                player.sendMessage( Utils.getColored( "&aEnabled&h sign notifications" ) );
            }
        }

        // /togglesigns [enable/disable]
        else {
            if ( args[0].equalsIgnoreCase( "enable" ) ) {
                if ( DATA.checkPlayerNoSignNotifs( player ) ) {
                    DATA.removePlayerNoSignNotifs( player );
                    player.sendMessage( Utils.getColored( "&aEnabled&h sign notifications" ) );
                }

                else {
                    player.sendMessage( Utils.getColored( "&hSign notifications are already &aenabled" ) );
                }

            }

            else if ( args[0].equalsIgnoreCase( "disable" ) ) {
                if ( DATA.checkPlayerNoSignNotifs( player ) == false ) {
                    DATA.addPlayerNoSignNotifs( player );
                    player.sendMessage( Utils.getColored( "&cDisabled&h sign notifications" ) );
                }

                else {
                    player.sendMessage( Utils.getColored( "&hSign notifications are already &cdisabled" ) );
                }
            }

            else {
                CommandErrors.sendCommandUsage( player, "togglesigns" );
            }
        }

        return true;
    }
}
