package com.github.cyberryan1.netuno.api.models;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Used to represent a staff member and all of their data
 * related to Netuno
 *
 * @author Ryan
 */
public interface ApiStaff {

    /**
     * @return The UUID of the staff represented
     */
    UUID getUuid();

    /**
     * @return The staff represented
     */
    OfflinePlayer getPlayer();

    /**
     * @param status Whether this staff member should have sign
     *               notifs enabled (true) or not (false)
     */
    void setSignNotificationStatus( boolean status );

    /**
     * @return Whether this staff member has sign notifs
     * enabled (true) or not (false)
     */
    boolean getSignNotificationStatus();
}