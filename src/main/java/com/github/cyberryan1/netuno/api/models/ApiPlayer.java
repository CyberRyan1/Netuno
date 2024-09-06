package com.github.cyberryan1.netuno.api.models;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

/**
 * Used to represent a player and all of their data
 * related to Netuno
 *
 * @author Ryan
 */
public interface ApiPlayer {

    /**
     * @return The UUID of the player represented
     */
    UUID getUuid();

    /**
     * @return The player represented
     */
    OfflinePlayer getPlayer();

    /**
     * Reloads the data for this player. Should be
     * ran async to avoid lag.
     */
    void reloadData();

    /**
     * Alias to {@link com.github.cyberryan1.netuno.api.services.ApiAltService#getAlts(UUID)}
     * @return A list of known accounts that this player has
     * joined the server with
     */
    List<UUID> getAlts();

    /**
     * @return List of all punishments of this player
     */
    List<ApiPunishment> getPunishments();

    /**
     * @return List of all active punishments of this player
     */
    List<ApiPunishment> getActivePunishments();
}