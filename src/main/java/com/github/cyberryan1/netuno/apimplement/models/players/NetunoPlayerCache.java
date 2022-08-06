package com.github.cyberryan1.netuno.apimplement.models.players;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.players.NPlayerLoader;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NetunoPlayerCache implements NPlayerLoader {

    private static List<NetunoPlayer> cache = new ArrayList<>();

    /**
     * Initializes all online players into the cache.
     */
    public void initialize() {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            load( player );
        }
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param player The player to load
     * @return The loaded user
     */
    public NPlayer load( OfflinePlayer player ) {
        return getOrLoad( player.getUniqueId().toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param player The player to load
     * @return The loaded user
     */
    public NPlayer load( Player player ) {
        return getOrLoad( player.getUniqueId().toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param uuid The uuid to load
     * @return The loaded user
     */
    public NPlayer load( UUID uuid ) {
        return getOrLoad( uuid.toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param uuid The uuid to load
     * @return The loaded user
     */
    public NPlayer load( String uuid ) {
        return getOrLoad( uuid );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param uuid The uuid to get
     * @return The loaded user, or null if not found
     */
    public static NetunoPlayer getOrLoad( String uuid ) {
        NetunoPlayer toReturn = cache.stream()
                .filter( player -> player.getPlayer().getUniqueId().toString().equals( uuid ) )
                .findFirst()
                .orElse( null );
        if ( toReturn != null ) { return toReturn; }
        toReturn = new NetunoPlayer( uuid );
        cache.add( toReturn );
        return toReturn;
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param player The player to get
     * @return The loaded user, or null if not found
     */
    public static NetunoPlayer getOrLoad( OfflinePlayer player ) {
        return getOrLoad( player.getUniqueId().toString() );
    }

    /**
     * Forcefully loads a player into the cache and returns
     * them.
     * @param uuid The uuid to get
     * @return The loaded user, or null if not found
     */
    public static NetunoPlayer forceLoad( String uuid ) {
        cache.removeIf( player -> player.getPlayer().getUniqueId().toString().equals( uuid ) );
        NetunoPlayer toReturn = new NetunoPlayer( uuid );
        cache.add( toReturn );
        return toReturn;
    }

    /**
     * Forcefully loads a player into the cache and returns
     * them.
     * @param player The player to get
     * @return The loaded user, or null if not found
     */
    public static NetunoPlayer forceLoad( OfflinePlayer player ) {
        return forceLoad( player.getUniqueId().toString() );
    }

    /**
     * Loads the most recent punishment that was added to the
     * database, checks if the punishment is for the player
     * provided, and adds the punishment to the player.
     * @param uuid The uuid to add the most recent punishment to
     */
    public static void loadRecentlyAddedPunishment( String uuid ) {
        NetunoPlayer player = searchForOne( netunoPlayer -> netunoPlayer.getPlayer().getUniqueId().toString().equals( uuid ) );
        if ( player == null ) { forceLoad( uuid ); return; }

        int recentlyInsertedId = ApiNetuno.getData().getNetunoPuns().getRecentlyInsertedId();
        NPunishment recentlyInserted = ApiNetuno.getData().getNetunoPuns().getPunishment( recentlyInsertedId );
        if ( recentlyInserted.getPlayerUuid().equals( player.getPlayer().getUniqueId().toString() ) ) {
            player.addPunishment( recentlyInserted );
        }
    }

    /**
     * @return The cache of loaded players.
     */
    public static List<NetunoPlayer> getCache() {
        return cache;
    }

    /**
     * @return All the punishments of all loaded players.
     */
    public static List<NPunishment> getCachedPunishments() {
        return cache.stream()
                .flatMap( player -> player.getPunishments().stream() )
                .collect( Collectors.toList() );
    }

    /**
     * Gets the first element in the cache that satisfies the given predicate.
     * @param predicate The predicate to check with
     * @return The first unexpired element that satisfies the predicate, null if none were found
     */
    public static NetunoPlayer searchForOne( Predicate<? super NetunoPlayer> predicate ) {
        return cache.stream()
                .filter( predicate )
                .findFirst()
                .orElse( null );
    }

    /**
     * Gets all elements in the cache that satisfy the given predicate.
     * @param predicate The predicate to check with
     * @return A list of all unexpired elements that satisfy the predicate
     */
    public static List<NetunoPlayer> searchForMany( Predicate<? super NetunoPlayer> predicate ) {
        return cache.stream()
                .filter( predicate )
                .collect( Collectors.toList() );
    }
}