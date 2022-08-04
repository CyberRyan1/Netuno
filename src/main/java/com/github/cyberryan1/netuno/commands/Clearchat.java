package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Clearchat extends CyberCommand {

    public Clearchat() {
        super(
                "clearchat",
                Settings.CLEARCHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&sclearchat"
        );
        register( false );

        setDemandPermission( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    public boolean execute( CommandSender sender, String args[] ) {

        String broadcast = Settings.CLEARCHAT_BROADCAST.coloredString();
        broadcast = Utils.replaceStaffVariable( broadcast, sender );

        String staffBroadcast = Settings.CLEARCHAT_STAFF_BROADCAST.coloredString();
        staffBroadcast = Utils.replaceStaffVariable( staffBroadcast, sender );
        boolean sendStaffBroadcast = staffBroadcast.isBlank() == false;
        boolean staffBypass = Settings.CLEARCHAT_STAFF_BYPASS.bool();

        // clears ~860 lines of chat
        String clear = "";
        for ( int x = 0; x < 20; x++ ) {
            clear += "\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n";
        }

        for ( Player p : Bukkit.getOnlinePlayers() ) {
            if ( VaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) == false ) {
                p.sendMessage( clear );
                p.sendMessage( broadcast );
            }
            else {
                if ( staffBypass == false ) {
                    p.sendMessage( clear );
                }

                if ( sendStaffBroadcast ) {
                    p.sendMessage( staffBroadcast );
                }
                else {
                    p.sendMessage( broadcast );
                }
            }
        }

        return true;
    }
}