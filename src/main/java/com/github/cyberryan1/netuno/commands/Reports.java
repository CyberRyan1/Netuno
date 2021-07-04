package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.guis.report.StaffReportsGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reports implements CommandExecutor {

    @Override
    // /reports
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
        StaffReportsGUI gui = new StaffReportsGUI( staff, 1 );
        gui.openInventory( staff );

        return true;
    }
}