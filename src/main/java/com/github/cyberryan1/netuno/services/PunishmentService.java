package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.services.ApiPunishmentService;
import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
import com.github.cyberryan1.netuno.models.NPlayer;
import com.github.cyberryan1.netuno.models.Punishment;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A service to manage punishments by letting others create them,
 * query for punishments, and modify punishments.
 *
 * @author Ryan
 */
public class PunishmentService implements ApiPunishmentService {

    /**
     * Searches through a cache of punishments first. If nothing
     * is found, then queries the database.
     *
     * @param id A punishment ID
     * @return The punishment with the given id, empty otherwise
     */
    @Override
    public CompletableFuture<Optional<ApiPunishment>> getPunishment( int id ) {
        for ( Punishment pun : getAllCachedPunishments() ) {
            if ( pun.getId() == id )
                return CompletableFuture.supplyAsync( () -> Optional.of( pun ) );
        }
        return CompletableFuture.supplyAsync( () -> Optional.ofNullable( PunishmentsDatabase.getPunishment( id ) ) );
    }

    /**
     * Searches through a cache of punishments first. If nothing
     * is found, then queries the database.
     *
     * @param uuid A player's uuid
     * @return A list of punishments the given player has
     */
    @Override
    public CompletableFuture<List<ApiPunishment>> getPunishments( UUID uuid ) {
        List<ApiPunishment> toReturn = getAllCachedPunishments().stream()
                .filter( pun -> pun.getPlayerUuid().equals( uuid ) )
                .map( pun -> ( ApiPunishment ) pun )
                .collect( Collectors.toList() );
        if ( toReturn.isEmpty() == false )
            return CompletableFuture.supplyAsync( () -> toReturn );

        return CompletableFuture.supplyAsync( () ->
                convertPunishmentsToApiPunishments( PunishmentsDatabase.getPunishments( uuid.toString() ) ) );
    }

    /**
     * Searches through a cache of punishments first. If nothing
     * is found, then queries the database.
     *
     * @param player A player
     * @return A list of punishments the given player has
     */
    @Override
    public CompletableFuture<List<ApiPunishment>> getPunishments( OfflinePlayer player ) {
        return getPunishments( player.getUniqueId() );
    }

    /**
     * This will always have to query the database, as searching
     * a cache can mean that some elements will drop out of the
     * cache while others remain within it, causing a nightmare
     *
     * @param referenceId A reference ID
     * @return A list of punishments that have the given
     *         reference ID
     */
    public CompletableFuture<List<ApiPunishment>> getPunishmentsByReferenceId( int referenceId ) {
        return CompletableFuture.supplyAsync( () ->
                convertPunishmentsToApiPunishments( PunishmentsDatabase.getPunishmentsFromReference( referenceId ) ) );
    }

    /**
     * Creates the provided punishment in the database. Also adds
     * the provided punishment to any players loaded in {@link NetunoService}.
     * @param pun The punishment
     */
    public void createPunishment( Punishment pun ) {
        PunishmentsDatabase.addPunishment( pun );

        // Adding the punishment to any players in NetunoService
        if ( Netuno.SERVICE.containsPlayer( pun.getPlayerUuid() ) == false ) return;
        Netuno.SERVICE.getPlayer( pun.getPlayerUuid() ).thenAccept( player -> {
            if ( player.getPunishments().contains( pun ) ) return;
            player.getPunishments().add( pun );
        } );
    }

    /**
     * Updates the provided punishment within the database with
     * any new data
     *
     * @param punishment The punishment
     */
    @Override
    public void updatePunishment( ApiPunishment punishment ) {
        PunishmentsDatabase.updatePunishment( ( Punishment ) punishment );
    }

    /**
     * @param punishment A punishment to remove from the database
     *                   and any caches. Also removes any
     *                   punishments with a reference ID equal to
     *                   the ID of this punishment
     */
    @Override
    public void deletePunishment( ApiPunishment punishment ) {
        deletePunishment( punishment.getId() );
    }

    /**
     * @param id The ID of a punishment to remove from the
     *           database and any caches. Also removes any
     *           punishments with a reference ID equal to the
     *           provided ID
     */
    @Override
    public void deletePunishment( int id ) {
        // Removing the punishment from the database
        PunishmentsDatabase.removePunishment( id );
        // Removing all reference punishments from the database
        PunishmentsDatabase.removePunishmentsWithReferenceId( id );

        // Removing the punishment from all cached players
        for ( NPlayer player : Netuno.SERVICE.getAll() ) {
            for ( int index = player.getPunishments().size() - 1; index >= 0; index-- ) {
                ApiPunishment current = player.getPunishments().get( index );
                if ( current.getId() == id || current.getReferenceId() == id ) {
                    player.getPunishments().remove( current );
                }
            }
        }
    }

    /**
     * @return A list of the punishments of all cached players
     */
    public List<Punishment> getAllCachedPunishments() {
        List<Punishment> toReturn = new ArrayList<>();
        for ( NPlayer player : Netuno.SERVICE.getAll() )
            toReturn.addAll( player.getPunishments().stream()
                    .map( pun -> ( Punishment ) pun )
                    .collect( Collectors.toList() ) );

        return toReturn;
    }

    /**
     * Used to output debug information about the cache to a
     * file
     */
    public void startCacheDebugPrinter() {
        Netuno.SERVICE.getPlayerCache().printDebugInfo( NetunoService.DEBUG_PRINTER_NPLAYER );
    }

    @Override
    public PunishmentBuilder punishmentBuilder() {
        return new PunBuilder();
    }

    public static class PunBuilder implements PunishmentBuilder {

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
        public PunishmentBuilder markAsGuiPunishment( boolean isGuiPunishment ) {
            punishment.setGuiPun( isGuiPunishment );
            return this;
        }

        @Override
        public ApiPunishment build() {
            return punishment;
        }
    }

    private List<ApiPunishment> convertPunishmentsToApiPunishments( List<Punishment> list ) {
        return list.stream()
                .map( p -> ( ApiPunishment ) p )
                .collect( Collectors.toList() );
    }
}