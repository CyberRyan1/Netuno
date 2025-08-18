package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.guis.alts.AltsGui;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IpinfoCommand extends CyberCommand {

    public IpinfoCommand( int helpOrder ) {
        super(
                "ipinfo",
                Settings.IPINFO_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&sipinfo &p(player)"
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        final Player player = command.getPlayer();
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        // want the target to have joined before
        if ( target.hasPlayedBefore() || target.isOnline() ) {
            if ( CyberVaultUtils.hasPerms( target, Settings.IPINFO_EXEMPT_PERMISSION.string()  )
                    && CyberVaultUtils.hasPerms( player, Settings.ALL_PERMISSIONS.string() ) == false ) {
                CommandErrors.sendPlayerExempt( player, target.getName() );
                return true;
            }

            AltsGui altsGui = new AltsGui( player, target, 1 );
            altsGui.open();
        }

        else {
            CommandErrors.sendPlayerNeverJoined( command.getSender(), target.getName() );
        }

        return true;
    }
}