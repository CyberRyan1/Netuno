package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.guis.ipinfo.AltsListGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IpInfoCommand extends CyberCommand {

    public IpInfoCommand() {
        super(
                "ipinfo",
                Settings.IPINFO_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sipinfo &p(player)"
        );
        register( true );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /ipinfo (player)
    public boolean execute( SentCommand command ) {
        final Player player = command.getPlayer();
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        // want the target to have joined before
        if ( target.hasPlayedBefore() || target.isOnline() ) {
            if ( CyberVaultUtils.hasPerms( target, YMLUtils.getConfig().getStr( "ipinfo.exempt-perm" ) )
                    && CyberVaultUtils.hasPerms( player, YMLUtils.getConfig().getStr( "general.all-perms" ) ) == false ) {
                CommandErrors.sendPlayerExempt( player, target.getName() );
                return true;
            }

            AltsListGUI altsGui = new AltsListGUI( player, target, 1 );
            altsGui.open();
        }

        else {
            CommandErrors.sendPlayerNeverJoined( command.getSender(), target.getName() );
        }

        return true;
    }
}
