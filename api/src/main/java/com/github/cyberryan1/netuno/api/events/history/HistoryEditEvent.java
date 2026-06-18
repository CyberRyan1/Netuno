package com.github.cyberryan1.netuno.api.events.history;

import com.github.cyberryan1.netuno.api.events.NetunoEvent;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import org.bukkit.entity.Player;

/**
 * This event is fired when a punishment is edited in a player's history.
 *
 * @author Ryan
 */
public class HistoryEditEvent implements NetunoEvent {

    private final ApiPunishment previousPunishment;
    private final ApiPunishment newPunishment;
    private final Player player;

    public HistoryEditEvent( ApiPunishment previousPunishment, ApiPunishment newPunishment, Player player ) {
        this.previousPunishment = previousPunishment;
        this.newPunishment = newPunishment;
        this.player = player;
    }

    /**
     * @return The {@link ApiPunishment} before the edit
     */
    public ApiPunishment getOldPunishment() {
        return previousPunishment;
    }

    /**
     * @return The {@link ApiPunishment} after the edit
     */
    public ApiPunishment getNewPunishment() {
        return newPunishment;
    }

    /**
     * @return The {@link Player} who is editing the punishment
     */
    public Player getPlayer() {
        return player;
    }
}