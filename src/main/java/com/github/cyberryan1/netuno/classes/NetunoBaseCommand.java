package com.github.cyberryan1.netuno.classes;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class NetunoBaseCommand extends CyberCommand {

    //
    // Constructors
    //

    public NetunoBaseCommand( String name, String permission, String permissionMsg, String usage ) {
        super(
                name,
                permission,
                Utils.getColored( permissionMsg ),
                Utils.getColored( usage )
        );
    }

    public NetunoBaseCommand( String name, String permission, String usage ) {
        super(
                name,
                permission,
                YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ),
                Utils.getColored( usage )
        );
    }

    public NetunoBaseCommand( String name, String usage ) {
        super(
                name,
                null,
                YMLUtils.getConfig().getColoredStr( "general.perm-denied-msg" ),
                Utils.getColored( usage )
        );
    }

    //
    // Main Methods
    //

    @Override
    public abstract List<String> tabComplete( CommandSender sender, String args[] );

    @Override
    public abstract boolean execute( CommandSender sender, String args[] );

    //
    // Overrides
    //

    @Override
    public void sendInvalidPlayerArg( CommandSender sender, String name ) {
        sender.sendMessage( Utils.getColored( "&sCould not find a player with the name &p" + name ) );
    }

    @Override
    public void sendInvalidIntegerArg( CommandSender sender, String arg ) {
        sender.sendMessage( "&sInvalid integer &p" + arg );
    }

    @Override
    public void sendInvalidDoubleArg( CommandSender sender, String arg ) {
        sender.sendMessage( "&sInvalid number &p" + arg );
    }
}