package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.services.ApiNetunoService;
import com.github.cyberryan1.netuno.api.services.ApiPunishmentService;
import com.github.cyberryan1.netuno.database.IpListDatabase;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private final List<PlayerIpsRecord> ALL_PLAYERS_JOINED_IPS = new ArrayList<>();
    private final PunishmentService PUNISHMENT_SERVICE;

    /**
     * Note that almost nothing should be done in the
     * constructor, but instead be done in the
     * {@link #initialize()} method
     */
    public NetunoService( PunishmentService punishmentService ) {
        this.PUNISHMENT_SERVICE = punishmentService;
    }

    /**
     * Initializes this service
     */
    public void initialize() {
        this.PLAYER_CACHE.setLoginScript( event -> Optional.of( new NPlayer( event.getUniqueId() ) ) );

        Map<UUID, List<String>> storedRows = IpListDatabase.getAllEntries();
        for ( Map.Entry<UUID, List<String>> entry : storedRows.entrySet() ) {
            this.ALL_PLAYERS_JOINED_IPS.add( new PlayerIpsRecord( entry.getKey(), true, entry.getValue() ) );
        }
    }

    /**
     * @return The {@link ApiPunishmentService}
     */
    @Override
    public ApiPunishmentService getPunishmentService() {
        return this.PUNISHMENT_SERVICE;
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
                if ( playerOnline )
                    this.PLAYER_CACHE.insertActiveData( uuid, toReturn );
                    // If the cache doesn't contain the player but they are
                    //      online, add their data as active
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
     * @return Access the player cache. Note that this should
     *         rarely be used for editing and instead mainly used
     *         for reading of the data provided
     */
    public PlayerLoginLogoutCache<NPlayer> getPlayerCache() {
        return this.PLAYER_CACHE;
    }

    /**
     * @return A list of all players who have ever joined the
     *         server which maps to a list of all the IPs they
     *         have joined the server with
     */
    @Override
    public Map<UUID, List<String>> getAllPlayersJoinedIps() {
        Map<UUID, List<String>> toReturn = new HashMap<>();

        for ( PlayerIpsRecord pil : this.ALL_PLAYERS_JOINED_IPS ) {
            toReturn.put( pil.getPlayer(), pil.getIps() );
        }

        return toReturn;
    }
}