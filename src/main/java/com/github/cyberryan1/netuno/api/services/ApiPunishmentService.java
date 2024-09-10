package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to query for punishments, create punishments, and more
 *
 * @author Ryan
 */
public interface ApiPunishmentService {

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
     * @return A list of punishments that have the given
     *         reference ID
     */
    CompletableFuture<List<ApiPunishment>> getPunishmentsByReferenceId( int referenceId );

    /**
     * Updates the provided punishment within the database with
     * any new data
     *
     * @param punishment The punishment
     */
    void updatePunishment( ApiPunishment punishment );

    /**
     * @param punishment A punishment to remove from the database
     *                   and from any caches
     */
    void deletePunishment( ApiPunishment punishment );

    /**
     * @param id The ID of a punishment to remove from the
     *           database and from any caches
     */
    void deletePunishment( int id );

    PunishmentBuilder punishmentBuilder();

    interface PunishmentBuilder {

        PunishmentBuilder setPlayer( UUID uuid );

        PunishmentBuilder setPlayer( OfflinePlayer player );

        PunishmentBuilder setStaff( UUID uuid );

        PunishmentBuilder setStaff( OfflinePlayer player );

        PunishmentBuilder setType( ApiPunishment.PunType type );

        PunishmentBuilder setLength( long length );

        PunishmentBuilder setReason( String reason );

        PunishmentBuilder markAsGuiPunishment( boolean isGuiPunishment );

        ApiPunishment build();
    }
}