package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class UnbanCommand extends CyberCommand {

    public UnbanCommand() {
        super(
                "unban",
                Settings.UNBAN_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sunban &p(player)"
        );
        register( true );

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
    public boolean execute( CommandSender sender, String args[] ) {
        final NetunoPlayer target = NetunoPlayerCache.getOrLoad( Bukkit.getOfflinePlayer( args[0] ).getUniqueId().toString() );

        final List<NPunishment> punishments = target.getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.BAN && pun.isActive() )
                .collect( Collectors.toList() );

        if ( punishments.size() >= 1 ) {
            NetunoPrePunishment pun = new NetunoPrePunishment();
            pun.setPlayer( target.getPlayer() );
            pun.setPunishmentType( PunishmentType.UNBAN );
            pun.setTimestamp( TimeUtils.getCurrentTimestamp() );
            pun.setReason( "" );
            pun.setLength( 0 );

            pun.setStaffUuid( "CONSOLE" );
            if ( sender instanceof Player ) { pun.setStaff( ( Player ) sender ); }

            pun.executePunishment();
        }

        else {
            CommandErrors.sendNoPunishments( sender, target.getPlayer().getName(), "ban" );
        }

        return true;
    }
}
