package com.github.cyberryan1.netuno.api.models.players;

import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.players.NPlayerLoader;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        return load( player.getUniqueId().toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param player The player to load
     * @return The loaded user
     */
    public NPlayer load( Player player ) {
        return load( player.getUniqueId().toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param uuid The uuid to load
     * @return The loaded user
     */
    public NPlayer load( UUID uuid ) {
        return load( uuid.toString() );
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise loads them into the cache and returns it.
     * @param uuid The uuid to load
     * @return The loaded user
     */
    public NPlayer load( String uuid ) {
        NetunoPlayer toReturn = get( uuid );
        if ( toReturn != null ) { return toReturn; }
        toReturn = new NetunoPlayer( uuid );
        cache.add( toReturn );
        return toReturn;
    }

    /**
     * Gets a loaded user from the cache if they are present,
     * otherwise returns null.
     * @param uuid The uuid to get
     * @return The loaded user, or null if not found
     */
    public static NetunoPlayer get( String uuid ) {
        return cache.stream()
                .filter( player -> player.getPlayer().getUniqueId().toString().equals( uuid ) )
                .findFirst()
                .orElse( null );
    }
}