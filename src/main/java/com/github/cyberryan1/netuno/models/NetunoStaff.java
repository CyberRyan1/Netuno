package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiStaff;
import com.github.cyberryan1.netuno.database.SettingsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a staff member and all of their data related to
 * Netuno. This implementation allows managing staff settings and
 * preferences
 *
 * @author Ryan
 */
public class NetunoStaff implements ApiStaff {

    private final UUID uuid;

    private boolean signNotificationStatus = true;

    /**
     * Creates a new NetunoStaff instance for the given UUID
     *
     * @param uuid The UUID of the staff member
     */
    public NetunoStaff( UUID uuid ) {
        this.uuid = uuid;
        reloadData();
    }

    /**
     * Creates a new NetunoStaff instance for the given player
     *
     * @param player The player to create the staff instance for
     */
    public NetunoStaff( OfflinePlayer player ) {
        this( player.getUniqueId() );
    }

    /**
     * Reloads the staff member's data from the database,
     * specifically their sign notification status
     */
    public void reloadData() {
        String settingName = SettingsDatabase.NAME_SIGN_NOTIF_STATUS.replace( "%uuid%", uuid.toString() );
        Optional<String> optional = SettingsDatabase.getSetting( settingName );
        if ( optional.isPresent() ) signNotificationStatus = Boolean.parseBoolean( optional.get() );
        else signNotificationStatus = true;
    }

    /**
     * @return The UUID of the staff represented
     */
    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return The staff represented
     */
    @Override
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer( this.uuid );
    }

    /**
     * @param status Whether this staff member should have sign
     *               notifs enabled (true) or not (false)
     */
    @Override
    public void setSignNotificationStatus( boolean status ) {
        this.signNotificationStatus = status;
        CompletableFuture.runAsync( () -> {
            SettingsDatabase.saveSetting( uuid.toString() + "@sign", String.valueOf( status ) );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    /**
     * @return Whether this staff member has sign notifs enabled
     *         (true) or not (false)
     */
    @Override
    public boolean getSignNotificationStatus() {
        return this.signNotificationStatus;
    }
}