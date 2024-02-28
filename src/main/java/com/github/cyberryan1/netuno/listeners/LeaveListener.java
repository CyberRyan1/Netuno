package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.managers.DisableQuitMsg;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

public class LeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave( PlayerQuitEvent event ) {
        NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( event.getPlayer().getUniqueId().toString() );
        List<NPunishment> banPunishments = nPlayer.getPunishments().stream()
                .filter( punishment -> punishment.isActive() && punishment.getPunishmentType() == PunishmentType.BAN )
                .collect( Collectors.toList() );
        if ( banPunishments.size() >= 1 ) {
            event.setQuitMessage( null );
        }

        List<NPunishment> ipPunishments = nPlayer.getPunishments().stream()
                .filter( punishment -> punishment.isActive() && punishment.getPunishmentType() == PunishmentType.IPBAN )
                .collect( Collectors.toList() );
        if ( ipPunishments.size() >= 1 ) {
            event.setQuitMessage( null );
        }

        if ( DisableQuitMsg.containsPlayer( event.getPlayer() ) ) {
            event.setQuitMessage( null );
        }
    }
}
