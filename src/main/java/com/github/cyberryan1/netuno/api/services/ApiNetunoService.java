package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to manage players' data and access other services
 * provided by Netuno
 *
 * @author Ryan
 */
public interface ApiNetunoService {

    /**
     * @return The {@link ApiPunishmentService} instance
     */
    ApiPunishmentService getPunishmentService();

    /**
     * @return The {@link ApiAltService} instance
     */
    ApiAltService getAltService();

    /**
     * @param player A player
     * @return The player and all of their Netuno data
     */
    CompletableFuture<ApiPlayer> getPlayer( OfflinePlayer player );

    /**
     * @param uuid A player's uuid
     * @return The player and all of their Netuno data
     */
    CompletableFuture<ApiPlayer> getPlayer( UUID uuid );
}