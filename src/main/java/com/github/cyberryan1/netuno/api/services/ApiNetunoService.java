package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
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
     * @return The {@link ApiPunishmentService}
     */
    ApiPunishmentService getPunishmentService();

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

    /**
     * @return A list of all players who have ever
     * joined the server which maps to a list of
     * all the IPs they have joined the server
     * with
     */
    Map<UUID, List<String>> getAllPlayersJoinedIps();
}