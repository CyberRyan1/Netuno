package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to manage player's alt accounts, store a cache from the
 * IP database, and provide access to look ups from that cache
 *
 * @author Ryan
 */
public interface ApiAltService {

    /**
     * @param uuid The player's UUID
     * @return A list of all other accounts the provided player
     *         has joined the server with. This list will NOT
     *         contain the provided player
     */
    List<UUID> getAlts( UUID uuid );

    /**
     * @param player The player
     * @return A list of all other accounts the provided player
     *         has joined the server with. This list will NOT
     *         contain the provided player
     */
    List<UUID> getAlts( OfflinePlayer player );

    /**
     * Searches for other accounts the provided player has joined
     * the server with. Note that this method is different from
     * the other <code>getAlts()</code> methods as it will load
     * the alt accounts into {@link ApiNetunoService}'s cache and
     * return the {@link ApiPlayer} associated with each alt.
     * <br><br>
     *
     * <b>IMPORTANT</b> This may cause lag. Recommended to run
     * this async
     *
     * @param player The player
     * @return A list of {@link ApiPlayer}
     */
    CompletableFuture<List<ApiPlayer>> getAlts( ApiPlayer player );

    /**
     * @param ip An IP
     * @return A list of all records from the that contain the
     *         provided IP
     */
    List<UUID> getPlayersWithIp( String ip );

    /**
     * @param uuid A player's UUID
     * @return An list of all the IPs the provided player has
     *         joined the server with. If the player has never
     *         joined the server before, an empty list is
     *         returned
     */
    List<String> getPlayerIps( UUID uuid );

    /**
     * @return A list of all players who have ever joined the
     *         server which maps to a list of all the IPs they
     *         have joined the server with
     */
    Map<UUID, List<String>> getAllPlayersJoinedIps();
}