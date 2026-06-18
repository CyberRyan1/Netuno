package com.github.cyberryan1.netuno.api.events.history;

import com.github.cyberryan1.netuno.api.events.NetunoEvent;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import org.bukkit.entity.Player;

/**
 * This event is fired when a punishment is deleted from a player's history.
 *
 * @author Ryan
 */
public class HistoryDeleteEvent implements NetunoEvent {

    private final ApiPunishment punishment;
    private final Player player;

    public HistoryDeleteEvent( ApiPunishment punishment, Player player ) {
        this.punishment = punishment;
        this.player = player;
    }

    /**
     * @return The {@link ApiPunishment} being deleted
     */
    public ApiPunishment getPunishment() {
        return punishment;
    }

    /**
     * @return The {@link Player} who is deleting the punishment
     */
    public Player getPlayer() {
        return player;
    }
}