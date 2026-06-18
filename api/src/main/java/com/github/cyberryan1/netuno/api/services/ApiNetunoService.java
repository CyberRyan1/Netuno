package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.events.NetunoEventDispatcher;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiStaff;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
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
     * @return The {@link ApiChatService} instance
     */
    ApiChatService getChatService();

    /**
     * @return The {@link ApiReportService} instance
     */
    ApiReportService getReportService();

    /**
     * @return The {@link NetunoEventDispatcher} instance
     */
    NetunoEventDispatcher getEventDispatcher();

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
     * Gets the provided player as an API player right now. This
     * should only be ran if the player is online, as it should
     * be guaranteed that the player is cached
     * @param player A player
     * @return The player and all of their Netuno data
     */
    Optional<ApiPlayer> getPlayerNow( Player player );

    /**
     * @param player A staff member
     * @return The staff member and all of their Netuno data
     */
    CompletableFuture<ApiStaff> getStaff( OfflinePlayer player );

    /**
     * @param uuid A staff member's uuid
     * @return The staff member and all of their Netuno data
     */
    CompletableFuture<ApiStaff> getStaff( UUID uuid );
}