package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.guis.ipinfo.AltsListGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class IPInfo extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public IPInfo() {
        super( "ipinfo", YMLUtils.getConfig().getStr( "ipinfo.perm" ), YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ), getColorizedStr( "&8/&uipinfo &y(player)" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 0 || args[0].length() == 0 ) {
                return getAllOnlinePlayerNames();
            }
            else if ( args.length == 1 ) {
                return matchOnlinePlayers( args[0] );
            }
        }
        return Collections.emptyList();
    }

    @Override
    // /ipinfo (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "ipinfo.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }
        Player staff = ( Player ) sender;

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            // want the target to have joined before
            if ( target.hasPlayedBefore() || target.isOnline() ) {
                if ( VaultUtils.hasPerms( target, YMLUtils.getConfig().getStr( "ipinfo.exempt-perm" ) )
                        && VaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "general.all-perms" ) ) == false ) {
                    CommandErrors.sendPlayerExempt( staff, target.getName() );
                    return true;
                }

                AltsListGUI gui = new AltsListGUI( target, staff, 1 );
                gui.openInventory( staff );
                Utils.getPlugin().getServer().getPluginManager().registerEvents( gui, Utils.getPlugin() );
            }

            else {
                CommandErrors.sendPlayerNeverJoined( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "ipinfo" );
        }

        return true;
    }
}
