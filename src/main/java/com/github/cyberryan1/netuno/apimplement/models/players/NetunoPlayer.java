package com.github.cyberryan1.netuno.apimplement.models.players;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.alts.NAltGroup;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public class NetunoPlayer implements NPlayer {

    private OfflinePlayer player;
    private List<NPunishment> punishments;

    public NetunoPlayer( OfflinePlayer player ) {
        this.player = player;
        updatePunishments();
    }

    public NetunoPlayer( UUID uuid ) {
        this.player = Bukkit.getOfflinePlayer( uuid );
        updatePunishments();
    }

    public NetunoPlayer( String uuid ) {
        this.player = Bukkit.getOfflinePlayer( UUID.fromString( uuid ) );
        updatePunishments();
    }

    /**
     * @return The {@link OfflinePlayer} of this player.
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * @return All punishments that this player has.
     */
    public List<NPunishment> getPunishments() {
        return punishments;
    }

    /**
     * Updates the punishments list for this player.
     */
    public void updatePunishments() {
        this.punishments = ApiNetuno.getData().getPun().forceGetPunishments( this.player.getUniqueId().toString() );
    }

    /**
     * Adds a punishment to the punishments list of this player.
     * @param punishment The punishment to add.
     */
    public void addPunishment( NPunishment punishment ) {
        this.punishments.removeIf( pun -> pun.getId() == punishment.getId() );
        this.punishments.add( punishment );
    }

    /**
     * @return The {@link NAltGroup} this player belongs to.
     * Will return null if the player hasn't joined the server
     * before or if their alt group cannot be found.
     */
    public NAltGroup getAltGroup() {
        return ApiNetuno.getInstance().getAltLoader().search( this.player.getUniqueId() ).orElse( null );
    }
}