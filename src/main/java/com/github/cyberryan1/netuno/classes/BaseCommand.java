package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private static final String PRIMARY_COLOR = YMLUtils.getConfig().getColoredStr( "general.primary-color" );
    private static final String SECONDARY_COLOR = YMLUtils.getConfig().getColoredStr( "general.secondary-color" );

    // returns true if the sender is a player, false if not
    public static boolean demandPlayer( CommandSender sender ) {
        if ( sender instanceof Player ) { return true; }

        sender.sendMessage( YMLUtils.getConfig().getColoredStr( "general.player-only-msg" ) );
        return false;
    }

    // formats a string with the primary and secondary colors, as desired
    // use the &y for the primary color and &u for the secondary
    public static String getColorizedStr( String str ) {
        str = str.replaceAll( "&y", PRIMARY_COLOR ).replaceAll( "&u", SECONDARY_COLOR );
        return Utils.getColored( str );
    }

    // returns all online player's names
    public static List<String> getAllOnlinePlayerNames() {
        List<String> toReturn = new ArrayList<>();
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            toReturn.add( p.getName() );
        }

        return toReturn;
    }

    // returns all online players with the name starting with the input
    public static List<String> matchOnlinePlayers( String startsWith ) {
        List<String> toReturn = new ArrayList<>();
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            if ( p.getName().toUpperCase().startsWith( startsWith.toUpperCase() ) ) {
                toReturn.add( p.getName() );
            }
        }

        return toReturn;
    }

    // returns an List<String> of all the strings inputted
    public static List<String> addAllToList( String ... strings ) {
        List<String> toReturn = new ArrayList<String>();
        Collections.addAll( toReturn, strings );
        return toReturn;
    }

    protected String label;
    protected String permission;
    protected String permissionMsg;
    protected String usage;

    public BaseCommand( String label, String permission, String permissionMsg, String usage ) {
        this.label = label;
        this.permission = permission;
        this.permissionMsg = permissionMsg;
        this.usage = usage;
    }

    public BaseCommand( String label, String permission ) {
        this.label = label;
        this.permission = permission;
        this.permissionMsg = YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" );
        this.usage = null;
    }

    public BaseCommand( String label, String permission, String usage ) {
        this.label = label;
        this.permission = permission;
        this.permissionMsg = YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" );
        this.usage = usage;
    }

    // will be done in the individual class, depending on the need
    public abstract List<String> onTabComplete( CommandSender sender, Command command, String label, String args[] );

    @Override
    // will also be done in the individual class as the contents of this depends on the need of the command
    public abstract boolean onCommand( CommandSender sender, Command command, String label, String args[] );

    // can be @Override if needed
    public boolean permissionsAllowed( CommandSender sender ) {
        if ( permission == null ) { return true; }
        return VaultUtils.hasPerms( sender, permission );
    }

    public void sendPermissionMsg( CommandSender sender ) {
        sender.sendMessage( permissionMsg );
    }

    public void sendUsage( CommandSender sender ) {
        sender.sendMessage( usage );
    }

    public void sendInvalidPlayerArg( CommandSender sender, String input ) {
        sender.sendMessage( getColorizedStr( "&uCould not find the player &y\"" + input + "&y\"" ) );
    }
}