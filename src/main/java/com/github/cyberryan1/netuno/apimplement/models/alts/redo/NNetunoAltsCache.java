package com.github.cyberryan1.netuno.apimplement.models.alts.redo;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.models.alts.TempAltCache;
import com.github.cyberryan1.netunoapi.models.alts.TempAltGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NNetunoAltsCache implements TempAltCache {

    private final Cache<Integer, TempAltGroup> CACHE = Caffeine.newBuilder()
            .maximumSize( 400 )
            .expireAfterAccess( 10, TimeUnit.MINUTES )
            .build();

    public void initialize() {
        CyberLogUtils.logInfo( "[ALTS CACHE] Initializing the alts cache..." );

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            loadPlayer( player.getUniqueId(), player.getAddress().getAddress().getHostAddress() );
        }

        CyberLogUtils.logInfo( "[ALTS CACHE] Loaded a total of " + CACHE.estimatedSize() + " alt groups" );
    }

    public void loadPlayer( UUID uuid, String ip ) {

    }

    public Optional<TempAltGroup> searchByUuid( UUID uuid ) {
        return Optional.empty();
    }

    public Optional<TempAltGroup> searchByIp( String ip ) {
        return Optional.empty();
    }

    public void save() {

    }
}