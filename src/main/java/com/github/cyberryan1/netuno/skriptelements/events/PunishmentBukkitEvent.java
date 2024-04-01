package com.github.cyberryan1.netuno.skriptelements.events;

import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PunishmentBukkitEvent extends Event {

    private final NPunishment punishment;

    public PunishmentBukkitEvent( NPunishment punishment ) {
        this.punishment = punishment;
        CyberLogUtils.logError( "PunishmentBukkitEvent constructor" );
    }

    public NPunishment getPunishment() { return punishment; }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}