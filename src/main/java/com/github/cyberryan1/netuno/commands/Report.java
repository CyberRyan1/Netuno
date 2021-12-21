package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.guis.report.ReportGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Report extends BaseCommand {

    public Report() {
        super( "report", ConfigUtils.getStr( "report.perm" ), getColorizedStr( "&8/&ureport &y(player)") );
    }

    @Override
    public boolean permissionsAllowed( CommandSender sender ) {
        if ( super.permission.equals( "" ) ) { return true; }
        else { return VaultUtils.hasPerms( sender, super.permission ); }
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
    // /report (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( ConfigUtils.getStr( "report.perm" ).equals( "" ) == false
                && VaultUtils.hasPerms( sender, ConfigUtils.getStr( "report.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[0] );
            if ( target != null ) {
                Player player = ( Player ) sender;
                ReportGUI gui = new ReportGUI( player, target );
                gui.openInventory( player );
            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "report" );
        }

        return true;
    }
}
