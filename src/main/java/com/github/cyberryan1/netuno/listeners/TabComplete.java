package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    private List<String> argOne = getEmpty();
    private List<String> argTwo = getEmpty();
    private List<String> argThree = getEmpty();
    private final String COMMAND;

    public TabComplete( String cmd ) {
        COMMAND = cmd;
    }

    public List<String> onTabComplete( CommandSender sender, Command command, String label, String args[] ) {
        setupArgs( sender, args );
        if ( args.length == 1 ) { return argOne; }
        else if ( args.length == 2 ) { return argTwo; }
        else if ( args.length == 3 ) { return argThree; }
        return getEmpty();
    }

    private void setupArgs( CommandSender sender, String args[] ) {
        switch ( COMMAND ) {
            case "warn": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "warn.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "clearchat": {
                break;
            }
            case "kick": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "kick.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "mute": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "mute.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); argTwo = getLengths(); }
                break;
            }
            case "unmute": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "unmute.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "ban": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "ban.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); argTwo = getLengths(); }
                break;
            }
            case "unban": {
                break;
            }
            case "ipinfo": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "ipinfo.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "ipmute": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "ipmute.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); argTwo = getLengths(); }
                break;
            }
            case "unipmute": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "unipmute.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "ipban": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "ipban.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); argTwo = getLengths(); }
                break;
            }
            case "unipban": {
                break;
            }
            case "history": {
                argOne.add( "list" ); argOne.add( "edit" ); argOne.add( "reset" );
                break;
            }
            case "togglesigns": {
                argOne.add( "enable" ); argOne.add( "disable" );
                break;
            }
            case "mutechat": {
                argOne.add( "enable" ); argOne.add( "disable" ); argOne.add( "toggle" ); argOne.add( "status" );
                break;
            }
            case "report": {
                if ( YMLUtils.getConfig().getStr( "report.perm" ).equals( "" ) == false ||
                        VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "report.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "reports": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "reports.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "netuno": {
                argOne.add( "help" ); argOne.add( "reload" );
                break;
            }
            case "punish": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "punish.perm" ) ) ) { argOne = getAllOnlinePlayers( args[0] ); }
                break;
            }
            case "chatslow": {
                if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "chatslow.perm" ) ) ) { argOne.add( "get" ); argOne.add( "set" ); }
                break;
            }
        }
    }

    private List<String> getAllOnlinePlayers( String startingWith ) {
        List<String> players = new ArrayList<String>();
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            if ( p.getName().toLowerCase().startsWith( startingWith.toLowerCase() ) ) {
                players.add( p.getName() );
            }
        }
        return players;
    }

    private List<String> getLengths() {
        List<String> lengths = new ArrayList<String>();
        lengths.add( "15m" ); lengths.add( "1h" ); lengths.add( "12h" ); lengths.add( "1d" );
        lengths.add( "3d" ); lengths.add( "1w" ); lengths.add( "forever" );
        return lengths;
    }

    private List<String> getEmpty() {
        List<String> toReturn = new ArrayList<String>();
        toReturn.add( "" );
        return toReturn;
    }
}
