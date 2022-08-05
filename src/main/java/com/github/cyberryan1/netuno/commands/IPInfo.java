package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.guis.ipinfo.NewAltsListGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IPInfo extends CyberCommand {

    public IPInfo() {
        super(
                "ipinfo",
                Settings.IPINFO_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sipinfo &p(player)"
        );
        register( true );

        demandPlayer( true );
        demandPermission( true );
        setMinArgs( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /ipinfo (player)
    public boolean execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        // want the target to have joined before
        if ( target.hasPlayedBefore() || target.isOnline() ) {
            if ( VaultUtils.hasPerms( target, YMLUtils.getConfig().getStr( "ipinfo.exempt-perm" ) )
                    && VaultUtils.hasPerms( player, YMLUtils.getConfig().getStr( "general.all-perms" ) ) == false ) {
                CommandErrors.sendPlayerExempt( player, target.getName() );
                return true;
            }

            NewAltsListGUI altsGui = new NewAltsListGUI( player, target, 1 );
            altsGui.open();
        }

        else {
            CommandErrors.sendPlayerNeverJoined( sender, args[0] );
        }

        return true;
    }
}
