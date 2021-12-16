package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.VaultUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Clearchat extends BaseCommand {

    public Clearchat() {
        super( "clearchat", ConfigUtils.getStr( "clearchat.perm" ),  ConfigUtils.getColoredStr( "general.perm-denied-msg" ), getColorizedStr( "&8/&uclearchat" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        return null;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "clearchat.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        String broadcast = ConfigUtils.getColoredStr( "clearchat.broadcast" );
        broadcast = ConfigUtils.replaceStaffVariable( broadcast, sender );

        boolean sendStaffBroadcast = ConfigUtils.getStr( "clearchat.staff-broadcast" ).equals( "" ) == false;
        String staffBroadcast = ConfigUtils.getColoredStr( "clearchat.staff-broadcast" );
        staffBroadcast = ConfigUtils.replaceStaffVariable( staffBroadcast, sender );

        // clears ~860 lines of chat
        String clear = "";
        for ( int x = 0; x < 20; x++ ) {
            clear += "\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n";
        }

        for ( Player p : Bukkit.getOnlinePlayers() ) {
            if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) == false ) {
                p.sendMessage( clear );
                p.sendMessage( broadcast );
            }
            else {
                if ( ConfigUtils.getBool( "clearchat.staff-bypass" ) == false ) {
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