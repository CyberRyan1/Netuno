package com.github.cyberryan1.netuno.guis.punish.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class OpenGui {

    private final Player staff;
    private final OfflinePlayer target;
    private boolean silent = false;
    private boolean cancelDelete = false;

    public OpenGui( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;
    }

    public Player getStaff() {
        return staff;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    public boolean isSilent() {
        return silent;
    }

    public boolean isCancelDelete() { return cancelDelete; }

    public void setSilent( boolean silent ) {
        this.silent = silent;
    }

    public void setCancelDelete( boolean cancelDelete ) { this.cancelDelete = cancelDelete; }
}