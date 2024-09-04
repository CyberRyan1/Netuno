package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.database.PunishmentsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a player and all of their respective Netuno data
 *
 * @author Ryan
 */
public class NPlayer implements ApiPlayer {

    private final UUID uuid;

    private List<ApiPunishment> loadedPunishments = new ArrayList<>();

    /**
     * Note that after this constructor is finished,
     * {@link #reloadData()} is called. You may want to
     * run this constructor async to avoid lag
     * @param uuid The UUID of the player
     */
    public NPlayer( UUID uuid ) {
        this.uuid = uuid;

        reloadData();
    }

    /**
     * Note that after this constructor is finished,
     * {@link #reloadData()} is called. You may want to
     * run this constructor async to avoid lag
     * @param player The player
     */
    public NPlayer( OfflinePlayer player ) {
        this( player.getUniqueId() );
    }

    /**
     * @return The UUID of the player represented
     */
    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return The player represented
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer( this.uuid );
    }

    /**
     * Reloads the data for this player. Should be
     * ran async to avoid lag.
     */
    public void reloadData() {
        // Loading punishments
        this.loadedPunishments.clear();
        this.loadedPunishments.addAll( PunishmentsDatabase.getPunishments( uuid.toString() ) );
    }

    /**
     * @return List of all punishments of this player
     */
    @Override
    public List<ApiPunishment> getPunishments() {
        return this.loadedPunishments;
    }

    /**
     * @return List of all active punishments of this player
     */
    @Override
    public List<ApiPunishment> getActivePunishments() {
        return this.loadedPunishments.stream().filter( ApiPunishment::isActive ).collect( Collectors.toList() );
    }
}