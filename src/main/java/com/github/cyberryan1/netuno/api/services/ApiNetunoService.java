package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to manage players' data and to retrieve, create, and
 * edit punishments
 *
 * @author Ryan
 */
public interface ApiNetunoService {

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
     * @param id A punishment ID
     * @return The punishment with the given id, empty otherwise
     */
    CompletableFuture<Optional<ApiPunishment>> getPunishment( int id );

    /**
     * @param uuid A player's uuid
     * @return A list of punishments the given player has
     */
    CompletableFuture<List<ApiPunishment>> getPunishments( UUID uuid );

    /**
     * @param player A player
     * @return A list of punishments the given player has
     */
    CompletableFuture<List<ApiPunishment>> getPunishments( OfflinePlayer player );

    /**
     * @param referenceId A reference ID
     * @return A list of punishments that have the given reference ID
     */
    CompletableFuture<List<ApiPunishment>> getPunishmentsByReferenceId( int referenceId );

    PunishmentBuilder punishmentBuilder();

    interface PunishmentBuilder {

        PunishmentBuilder setPlayer( UUID uuid );

        PunishmentBuilder setPlayer( OfflinePlayer player );

        PunishmentBuilder setStaff( UUID uuid );

        PunishmentBuilder setStaff( OfflinePlayer player );

        PunishmentBuilder setType( ApiPunishment.PunType type );

        PunishmentBuilder setLength( long length );

        PunishmentBuilder setReason( String reason );

        ApiPunishment build();
    }

}
