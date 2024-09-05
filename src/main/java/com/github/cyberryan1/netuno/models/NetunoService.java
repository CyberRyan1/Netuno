package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.services.ApiNetunoService;
import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link ApiNetunoService} interface
 *
 * @author Ryan
 */
public class NetunoService implements ApiNetunoService {

    private static final CacheDebugPrinter.PrintSpecifier<NPlayer> DEBUG_PRINTER_NPLAYER = player -> {
        String output = "\tPunishments (" + player.getPunishments().size() + " total):\n";
        for ( ApiPunishment aPun : player.getPunishments() ) {
            Punishment p = ( Punishment ) aPun;

            output += "\t\tPunishment #" + p.getId() + "\n";
            output += "\t\t\tPlayer = " + p.getPlayer().getName() + " (UUID \"" + p.getPlayerUuid().toString() + "\")\n";
            output += "\t\t\tStaff =" + p.getStaff().getName() + " (UUID \"" + p.getStaffUuid().toString() + "\")\n";
            output += "\t\t\tPunishment Type = " + p.getType().name().toUpperCase() + "\n";
            output += "\t\t\tTimestamp = " + p.getTimestamp() + "\n";
            output += "\t\t\tReason = \"" + p.getReason() + "\"\n";
            output += "\t\t\tActive = " + ( p.isActive() ? "TRUE" : "FALSE" ) + "\n";
            output += "\t\t\tReference ID = " + p.getReferenceId() + "\n";
            output += "\t\t\tGUI Punishment = " + ( p.isGuiPun() ? "YES" : "NO" ) + "\n";
            output += "\t\t\tNotification Sent = " + ( p.isNotifSent() ? "YES" : "NO" ) + "\n";
            output += "\t\t\tExecuted = " + ( p.isExecuted() ? "YES" : "NO" ) + "\n";
        }

        return output;
    };

    private final PlayerLoginLogoutCache<NPlayer> PLAYER_CACHE = new PlayerLoginLogoutCache<>();

    /**
     * Note that almost nothing should be done in the
     * constructor, but instead be done in the
     * {@link #initialize()} method
     */
    public NetunoService() {}

    /**
     * Initializes this service
     */
    public void initialize() {
        this.PLAYER_CACHE.setLoginScript( event -> Optional.of( new NPlayer( event.getUniqueId() ) ) );
    }

    /**
     * @param player A player
     * @return The player and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiPlayer> getPlayer( OfflinePlayer player ) {
        return getPlayer( player.getUniqueId() );
    }

    /**
     * @param uuid A player's uuid
     * @return The player and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiPlayer> getPlayer( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            NPlayer toReturn = new NPlayer( uuid );
            final boolean playerOnline = Bukkit.getPlayer( uuid ) != null;

            if ( this.PLAYER_CACHE.containsPlayer( uuid ) == false ) {
                // If the cache doesn't contain the player and they are
                //      offline, add their data as inactive
                if ( playerOnline ) this.PLAYER_CACHE.insertActiveData( uuid, toReturn );
                // If the cache doesn't contain the player but they are
                //      online, add their data as active
                else this.PLAYER_CACHE.insertInactiveData( uuid, toReturn );
            }
            else {
                // If the cache does contain the player, we just update
                //      the state of their cached data
                this.PLAYER_CACHE.updateDataState( uuid );
                // We change our to return to be what is currently in the cache
                toReturn = this.PLAYER_CACHE.getData( uuid ).get();
            }

            return toReturn;
        } );
    }

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
            if ( pun.getId() == id ) return CompletableFuture.supplyAsync( () -> Optional.of( pun ) );
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
        if ( toReturn.isEmpty() == false ) return CompletableFuture.supplyAsync( () -> toReturn );

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
     * @return A list of punishments that have the given reference ID
     */
    public CompletableFuture<List<ApiPunishment>> getPunishmentsByReferenceId( int referenceId ) {
        return CompletableFuture.supplyAsync( () ->
                convertPunishmentsToApiPunishments( PunishmentsDatabase.getPunishmentsFromReference( referenceId ) ) );
    }

    /**
     * @return A list of the punishments of all cached players
     */
    public List<Punishment> getAllCachedPunishments() {
        List<Punishment> toReturn = new ArrayList<>();

        for ( UUID uuid : PLAYER_CACHE.getKeySet() ) {
            // No need to check if the optional is empty since we are
            //      iterating through all the keyed UUIDs
            NPlayer player = this.PLAYER_CACHE.getData( uuid ).get();
            toReturn.addAll( player.getPunishments().stream()
                    .map( pun -> ( Punishment ) pun )
                    .collect( Collectors.toList() )
            );
        }

        return toReturn;
    }

    /**
     * Used to output debug information about the cache
     * to a file
     */
    public void startCacheDebugPrinter() {
        PLAYER_CACHE.printDebugInfo( DEBUG_PRINTER_NPLAYER );
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

    private List<ApiPunishment> convertPunishmentsToApiPunishments( List<Punishment> list ) {
        return list.stream()
                .map( p -> ( ApiPunishment ) p )
                .collect( Collectors.toList() );
    }
}