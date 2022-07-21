package com.github.cyberryan1.netuno.features.cache;

import com.github.cyberryan1.netuno.utils.MsgReplaceUtils;
import com.github.cyberryan1.netuno.utils.data.PunishmentsCache;
import com.github.cyberryan1.netunoapi.helpers.ANetunoPunishment;
import com.github.cyberryan1.netunoapi.helpers.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

public class CacheEvents implements Listener {

    @EventHandler( priority = EventPriority.HIGH )
    public void onAsyncPlayerJoin( AsyncPlayerPreLoginEvent event ) {
        if ( event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED ) { return; }
        final String PLAYER_UUID = event.getUniqueId().toString();

        List<ANetunoPunishment> loadedPunishments = PunishmentsCache.getPlayerPunishments( PLAYER_UUID );
        if ( loadedPunishments.size() != 0 && PunishmentUtils.anyActive( loadedPunishments ) ) {
            ANetunoPunishment highestPun = PunishmentUtils.getHighestActive( loadedPunishments, PunishmentType.IPBAN );
            if ( highestPun == null ) { highestPun = PunishmentUtils.getHighestActive( loadedPunishments, PunishmentType.BAN ); }

            if ( highestPun != null ) {
                event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );

                final String msg = MsgReplaceUtils.getReplacedDisconnectMessage( highestPun );
                event.setKickMessage( msg );
                return;
            }
        }

        PunishmentsCache.loadPlayer( event.getUniqueId().toString() );
        loadedPunishments = PunishmentsCache.getPlayerPunishments( PLAYER_UUID );
        if ( loadedPunishments.size() == 0 ) { return; }
        if ( PunishmentUtils.anyActive( loadedPunishments ) ) {
            ANetunoPunishment highestPun = PunishmentUtils.getHighestActive( loadedPunishments, PunishmentType.IPBAN );
            if ( highestPun == null ) { highestPun = PunishmentUtils.getHighestActive( loadedPunishments, PunishmentType.BAN ); }

            if ( highestPun != null ) {
                event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );

                final String msg = MsgReplaceUtils.getReplacedDisconnectMessage( highestPun );
                event.setKickMessage( msg );
                return;
            }
        }
    }
}