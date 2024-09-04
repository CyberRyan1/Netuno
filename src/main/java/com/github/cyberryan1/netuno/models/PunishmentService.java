package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.services.ApiPunishmentService;
import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PunishmentService implements ApiPunishmentService {

    /**
     * @param id A punishment ID
     * @return The punishment with the given id, empty otherwise
     */
    @Override
    public CompletableFuture<Optional<ApiPunishment>> getPunishment( int id ) {
        return CompletableFuture.supplyAsync( () -> Optional.ofNullable( PunishmentsDatabase.getPunishment( id ) ) );
    }

    /**
     * @param uuid A player's uuid
     * @return A list of punishments the given player has
     */
    @Override
    public CompletableFuture<List<ApiPunishment>> getPunishments( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> PunishmentsDatabase.getPunishments( uuid.toString() ).stream()
                .map( pun -> ( ApiPunishment ) pun )
                .collect( Collectors.toList() )
        );
    }

    /**
     * @param player A player
     * @return A list of punishments the given player has
     */
    @Override
    public CompletableFuture<List<ApiPunishment>> getPunishments( OfflinePlayer player ) {
        return getPunishments( player.getUniqueId() );
    }

    @Override
    public PunishmentBuilder punishmentBuilder() {
        return new PunBuilder();
    }

    static class PunBuilder implements PunishmentBuilder {

        private Punishment punishment;

        public PunBuilder() {
            punishment = new Punishment();
        }

        @Override
        public PunishmentBuilder setPlayer( UUID uuid ) {
            punishment.setPlayer( uuid );
            return this;
        }

        @Override
        public PunishmentBuilder setPlayer( OfflinePlayer player ) {
            punishment.setPlayer( player.getUniqueId() );
            return this;
        }

        @Override
        public PunishmentBuilder setStaff( UUID uuid ) {
            punishment.setStaff( uuid );
            return this;
        }

        @Override
        public PunishmentBuilder setStaff( OfflinePlayer player ) {
            punishment.setStaff( player.getUniqueId() );
            return this;
        }

        @Override
        public PunishmentBuilder setType( ApiPunishment.PunType type ) {
            punishment.setType( type );
            return this;
        }

        @Override
        public PunishmentBuilder setLength( long length ) {
            punishment.setLength( length );
            return this;
        }

        @Override
        public PunishmentBuilder setReason( String reason ) {
            punishment.setReason( reason );
            return this;
        }

        @Override
        public ApiPunishment build() {
            return punishment;
        }
    }
}
