package com.github.cyberryan1.netuno.skriptelements.events.bukkitevents;

import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NetunoPunishmentBukkitEvent extends Event {

    private final NPunishment punishment;

    public NetunoPunishmentBukkitEvent( NPunishment punishment ) {
        this.punishment = punishment;
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