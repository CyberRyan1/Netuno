package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Warn implements CommandExecutor {

    @Override
    // /warn (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "warn.perm" ) ) ) {

        }

        else {
            sender.sendMessage( ConfigUtils.getColoredStr( "general.perm-denied-msg" ) );
        }

        return true;
    }
}
