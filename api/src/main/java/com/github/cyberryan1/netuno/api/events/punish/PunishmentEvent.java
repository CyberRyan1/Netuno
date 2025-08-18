package com.github.cyberryan1.netuno.api.events.punish;

import com.github.cyberryan1.netuno.api.events.NetunoEvent;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;

/**
 * This event is fired when a player is punished.
 *
 * @author Ryan
 */
public class PunishmentEvent implements NetunoEvent {

    private final ApiPunishment punishment;

    public PunishmentEvent( ApiPunishment punishment ) {
        this.punishment = punishment;
    }

    /**
     * @return The {@link ApiPunishment} executed
     */
    public ApiPunishment getPunishment() {
        return punishment;
    }
}