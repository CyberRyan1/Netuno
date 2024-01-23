package com.github.cyberryan1.netuno.apimplement.models.players;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.alts.TempUuidIpEntry;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    public Set<UUID> getAltAccounts() {
        String ip = "";
        if ( getPlayer().isOnline() ) { ip = getPlayer().getPlayer().getAddress().getAddress().getHostAddress(); }
        else {
            List<TempUuidIpEntry> entries = new ArrayList<>( ApiNetuno.getData().getTempAltsDatabase().queryByUuid( player.getUniqueId() ) );
            ip = entries.get( 0 ).getIp();
        }
        return ApiNetuno.getInstance().getAltCache().queryAccounts( ip );
    }

    /**
     * Adds a punishment to the punishments list of this player.
     * @param punishment The punishment to add.
     */
    public void addPunishment( NPunishment punishment ) {
        this.punishments.removeIf( pun -> pun.getId() == punishment.getId() );
        this.punishments.add( punishment );
    }
}