package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

;

public class UnIpmuteCommand extends HelpableCommand {

    public UnIpmuteCommand( int helpOrder ) {
        super(
                helpOrder,
                "unipmute",
                Settings.UNIPMUTE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sunipmute &p(player)",
                "&sUnmutes all known accounts of a player"
        );
        register( true );

        demandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        final NetunoPlayer target = NetunoPlayerCache.getOrLoad( command.getOfflinePlayerAtArg( 0 ).getUniqueId().toString() );

        final List<NPunishment> punishments = target.getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.IPMUTE && pun.isActive() )
                .collect( Collectors.toList() );

        if ( punishments.size() >= 1 ) {
            NetunoPrePunishment prePun = new NetunoPrePunishment();
            prePun.getPunishment().setPlayer( target.getPlayer() );
            prePun.getPunishment().setPunishmentType( PunishmentType.UNIPMUTE );
            prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
            prePun.getPunishment().setReason( "" );
            prePun.getPunishment().setLength( 0 );

            prePun.getPunishment().setStaffUuid( "CONSOLE" );
            if ( command.getSender() instanceof Player ) { prePun.getPunishment().setStaff( ( Player ) command.getSender() ); }

            prePun.executePunishment();
        }

        else {
            CommandErrors.sendNoPunishments( command.getSender(), target.getPlayer().getName(), "ipmute" );
        }

        return true;
    }
}
