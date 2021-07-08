package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.guis.report.StaffAllReportsGUI;
import com.github.cyberryan1.netuno.guis.report.StaffPlayerReportsGUI;
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

public class Reports implements CommandExecutor {

    @Override
    // /reports [player]
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "reports.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }

        Player staff = ( Player ) sender;

        // /reports
        if ( Utils.isOutOfBounds( args, 0 ) ) {
            StaffAllReportsGUI gui = new StaffAllReportsGUI( staff, 1 );
            gui.openInventory( staff );
        }

        // /reports [player]
        else {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            if ( target != null ) {
                StaffPlayerReportsGUI gui = new StaffPlayerReportsGUI( staff, target );
                gui.openInventory( staff );
            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

        }

        return true;
    }
}