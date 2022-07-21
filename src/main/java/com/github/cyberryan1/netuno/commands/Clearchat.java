package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Clearchat extends BaseCommand {

    public Clearchat() {
        super( "clearchat", YMLUtils.getConfig().getStr( "clearchat.perm" ),  YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ), getColorizedStr( "&8/&uclearchat" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "clearchat.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        String broadcast = YMLUtils.getConfig().getColoredStr( "clearchat.broadcast" );
        broadcast = Utils.replaceStaffVariable( broadcast, sender );

        boolean sendStaffBroadcast = YMLUtils.getConfig().getStr( "clearchat.staff-broadcast" ).equals( "" ) == false;
        String staffBroadcast = YMLUtils.getConfig().getColoredStr( "clearchat.staff-broadcast" );
        staffBroadcast = Utils.replaceStaffVariable( staffBroadcast, sender );

        // clears ~860 lines of chat
        String clear = "";
        for ( int x = 0; x < 20; x++ ) {
            clear += "\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n";
        }

        for ( Player p : Bukkit.getOnlinePlayers() ) {
            if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) == false ) {
                p.sendMessage( clear );
                p.sendMessage( broadcast );
            }
            else {
                if ( YMLUtils.getConfig().getBool( "clearchat.staff-bypass" ) == false ) {
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

    private void clearchat( Player p ) {
        for ( int x = 0; x < 750; x++ ) {
            p.sendMessage( "\n" );
        }
    }
}