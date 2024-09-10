package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.services.ApiAltService;
import com.github.cyberryan1.netuno.api.services.ApiNetunoService;
import com.github.cyberryan1.netuno.api.services.ApiPunishmentService;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import com.github.cyberryan1.netuno.models.NPlayer;
import com.github.cyberryan1.netuno.models.Punishment;
import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * An implementation of the {@link ApiNetunoService} interface
 *
 * @author Ryan
 */
public class NetunoService implements ApiNetunoService {

    public static final CacheDebugPrinter.PrintSpecifier<NPlayer> DEBUG_PRINTER_NPLAYER = player -> {
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
    private final PunishmentService PUNISHMENT_SERVICE;
    private final AltService ALT_SERVICE;

    /**
     * Note that almost nothing should be done in the
     * constructor, but instead be done in the
     * {@link #initialize()} method
     */
    public NetunoService( PunishmentService punishmentService, AltService altService ) {
        this.PUNISHMENT_SERVICE = punishmentService;
        this.ALT_SERVICE = altService;
    }

    /**
     * Initializes this service
     */
    public void initialize() {
        this.PLAYER_CACHE.setLoginScript( event -> Optional.of( new NPlayer( event.getUniqueId() ) ) );
        this.ALT_SERVICE.initialize();
    }

    /**
     * Closes this service
     */
    public void close() {
    }

    /**
     * @return The {@link ApiPunishmentService}
     */
    @Override
    public ApiPunishmentService getPunishmentService() {
        return this.PUNISHMENT_SERVICE;
    }

    /**
     * @return The {@link ApiAltService} instance
     */
    @Override
    public ApiAltService getAltService() {
        return this.ALT_SERVICE;
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

                // If the cache doesn't contain the player but they are
                //      online, add their data as active
                if ( playerOnline )
                    this.PLAYER_CACHE.insertActiveData( uuid, toReturn );
                // If the cache doesn't contain the player and they are
                //      offline, add their data as inactive
                else
                    this.PLAYER_CACHE.insertInactiveData( uuid, toReturn );
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
     * @param uuid A player's UUID
     * @return True if the provided UUID is cached, false otherwise
     */
    public boolean containsPlayer( UUID uuid ) {
        return this.PLAYER_CACHE.containsPlayer( uuid );
    }

    /**
     * @return Access the player cache. Note that this should
     *         rarely be used for editing and instead mainly used
     *         for reading of the data provided
     * @deprecated Please use other methods provided within this
     * class
     */
    public PlayerLoginLogoutCache<NPlayer> getPlayerCache() {
        return this.PLAYER_CACHE;
    }

    /**
     * @return A list of type {@link NPlayer} of all the players
     * who are cached. Does NOT refresh the last access timestamp
     * for each of the returned players
     */
    public List<NPlayer> getAll() {
        List<NPlayer> toReturn = new ArrayList<>();
        for ( UUID uuid : this.PLAYER_CACHE.getKeySet() ) {
            getPlayer( uuid ).thenAccept( player -> toReturn.add( ( NPlayer ) player ) );
            this.PLAYER_CACHE.refreshLastAccessTimestamp( uuid );
        }

        return toReturn;
    }

    /**
     * @param predicate A predicate with the argument being of
     *                  type UUID
     * @return A list of type {@link NPlayer} of all the players
     * who are cached and satisfy the given predicate. Also
     * refreshes the last access timestamp for each of the
     * returned players
     */
    public List<NPlayer> getAllThatSatisfy( Predicate<? super NPlayer> predicate ) {
        List<NPlayer> toReturn = new ArrayList<>();
        this.PLAYER_CACHE.getKeySet()
                .forEach( uuid -> {
                    getPlayer( uuid ).thenAccept( player -> {
                        if ( predicate.test( ( NPlayer ) player ) == false ) return;

                        toReturn.add( ( NPlayer ) player );
                        this.PLAYER_CACHE.refreshLastAccessTimestamp( uuid );
                    } );
                } );
        return toReturn;
    }
}