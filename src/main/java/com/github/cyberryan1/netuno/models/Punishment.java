package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Represents a punishment given to a player
 *
 * @author Ryan
 */
public class Punishment implements ApiPunishment {

    // Data that is stored in the database
    private int id;
    private UUID playerUuid;
    private UUID staffUuid;
    private PunType punType;
    private long timestamp;
    private long length;
    private String reason;
    private boolean isActive;
    private int referenceId;
    private boolean isGuiPun;
    private boolean isNotifSent;

    // Other data
    private boolean isExecuted;

    public Punishment( int id, UUID playerUuid, UUID staffUuid, PunType punType,
                       long timestamp, long length, String reason, boolean isActive,
                       int referenceId, boolean isGuiPun, boolean isNotifSent, boolean isExecuted ) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.staffUuid = staffUuid;
        this.punType = punType;
        this.timestamp = timestamp;
        this.length = length;
        this.reason = reason;
        this.isActive = isActive;
        this.referenceId = referenceId;
        this.isGuiPun = isGuiPun;
        this.isNotifSent = isNotifSent;
        this.isExecuted = isExecuted;
    }

    public Punishment( int id, String playerUuid, String staffUuid, PunType punType,
                       long timestamp, long length, String reason, boolean isActive,
                       int referenceId, boolean isGuiPun, boolean isNotifSent, boolean isExecuted ) {
        this(
                id, UUID.fromString( playerUuid ), UUID.fromString( staffUuid ), punType, timestamp, length,
                reason, isActive, referenceId, isGuiPun, isNotifSent, isExecuted
        );
    }

    public Punishment( UUID playerUuid, UUID staffUuid, PunType punType,
                       long timestamp, long length, String reason, boolean isActive,
                       int referenceId, boolean isGuiPun, boolean isNotifSent, boolean isExecuted ) {
        this(
                DEFAULT_ID, playerUuid, staffUuid, punType, timestamp, length, reason, isActive,
                referenceId, isGuiPun, isNotifSent, isExecuted
        );
    }

    public Punishment( String playerUuid, String staffUuid, PunType punType,
                       long timestamp, long length, String reason, boolean isActive,
                       int referenceId, boolean isGuiPun, boolean isNotifSent, boolean isExecuted ) {
        this(
                DEFAULT_ID, UUID.fromString( playerUuid ), UUID.fromString( staffUuid ), punType, timestamp, length,
                reason, isActive, referenceId, isGuiPun, isNotifSent, isExecuted
        );
    }

    public Punishment() {
        this(
                DEFAULT_ID, ( UUID ) null, null, null,
                -1L, -1L, null, false, DEFAULT_REFERENCE_ID,
                false, false, false
        );
    }

    /**
     * @return the ID of the punishment. If the ID
     * doesn't exist yet, set this to {@link ApiPunishment#DEFAULT_ID}
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * @param id the ID of the punishment
     */
    @Override
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * @return UUID of the player who is punished
     */
    @Override
    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    /**
     * @return The player who is punished
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer( this.playerUuid );
    }

    /**
     * @param player UUID of the player who is punished
     */
    @Override
    public void setPlayer( UUID player ) {
        this.playerUuid = player;
    }

    /**
     * @return UUID of the staff who did this punishment. Null if executed by console
     */
    @Override
    public UUID getStaffUuid() {
        return this.staffUuid;
    }

    /**
     * @return The staff who executed this punishment. Null
     * if executed by console.
     */
    public OfflinePlayer getStaff() {
        if ( this.staffUuid == null ) return null;
        return Bukkit.getOfflinePlayer( this.staffUuid );
    }

    /**
     * @param uuid UUID of the staff who did this punishment. Can be null if executed by console
     */
    @Override
    public void setStaff( UUID uuid ) {
        this.staffUuid = uuid;
    }

    /**
     * @return The type of this punishment
     */
    @Override
    public PunType getType() {
        return this.punType;
    }

    /**
     * @param type The type of this punishment
     */
    @Override
    public void setType( PunType type ) {
        this.punType = type;
    }

    /**
     * @return Timestamp, in milliseconds, of when this punishment was executed
     */
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp Timestamp, in milliseconds, of when this punishment was executed
     */
    @Override
    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    /**
     * @return The length of the punishment, in milliseconds
     */
    @Override
    public long getLength() {
        return this.length;
    }

    /**
     * @param length The length of this punishment, in milliseconds
     */
    @Override
    public void setLength( long length ) {
        this.length = length;
    }

    /**
     * @return Returns how long the remaining duration of this
     *         punishment is. If {@link #isActive()} is false,
     *         returns 0
     */
    @Override
    public long getDurationRemaining() {
        if ( isActive() == false ) return 0;
        long durationSinceTimestamp = TimestampUtils.getTimeSince( this.timestamp );
        return this.length - durationSinceTimestamp;
    }

    /**
     * @return The reason for this punishment
     */
    @Override
    public String getReason() {
        return this.reason;
    }

    /**
     * @param reason The reason for this punishment
     */
    @Override
    public void setReason( String reason ) {
        this.reason = reason;
    }

    /**
     * This will check if the punishment is active or not. If the
     * punishment has been set to inactive, returns false. Otherwise,
     * this will check if the punishment has expired yet or not. If the
     * punishment has expired, the punishment is set as inactive and false
     * is returned. Otherwise returns true<br><br>
     *
     * Note: if the punishment is not active, it can NOT be set back to active.
     *
     * @return True if this punishment is active, false otherwise
     */
    @Override
    public boolean isActive() {
        if ( this.isActive ) {
            if ( this.length == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return true;
            if ( TimestampUtils.timestampHasExpired( this.timestamp, this.length ) ) {
                this.isActive = false;
                return false;
            }

            return true;
        }
        return this.isActive;
    }

    /**
     * Note: if the punishment is not active, it can NOT be set back to active.
     *
     * @param active True if this punishment is active, false otherwise
     */
    @Override
    public void setActive( boolean active ) {
        if ( this.isActive && active == false ) throw new RuntimeException( "A punishment that is currently inactive cannot be set back as active" );
        if ( active && this.punType.hasNoLength() ) throw new RuntimeException( "A punishment with no length cannot be set as active" );
        this.isActive = active;
    }

    /**
     * @return The ID of the reference punishment associated with this punishment, if there is one.
     *         Otherwise, returns {@link ApiPunishment#DEFAULT_REFERENCE_ID}
     */
    @Override
    public int getReferenceId() {
        return this.referenceId;
    }

    /**
     * @param referenceId The ID of the reference punishment associated with this punishment, if
     *                    there is one. Otherwise, set this to {@link ApiPunishment#DEFAULT_REFERENCE_ID}
     */
    @Override
    public void setReferenceId( int referenceId ) {
        this.referenceId = referenceId;
    }

    /**
     * @return True if this punishment was executed via the punishment GUI, false otherwise
     */
    @Override
    public boolean isGuiPun() {
        return this.isGuiPun;
    }

    /**
     * @param guiPun True if this punishment was executed through the punishment GUI, false
     *               otherwise
     */
    @Override
    public void setGuiPun( boolean guiPun ) {
        this.isGuiPun = guiPun;
    }

    /**
     * @return True if a notification has been sent to the player about this punishment, false
     *         otherwise
     */
    @Override
    public boolean isNotifSent() {
        return this.isNotifSent;
    }

    /**
     * If the player is online and a notification has not yet been sent to them, this will send the
     * notification to them. After this, a notification cannot be sent to them again. <br> <br>
     *
     * Note: a notification should only be sent for punishments that occur when the player is offline,
     * such as a mute or warn. This should not be done for things like kicks or bans
     */
    @Override
    public void sendNotification() {
        // TODO
    }

    /**
     * @return true if the punishment has been executed, false otherwise
     */
    @Override
    public boolean isExecuted() {
        return this.isExecuted;
    }

    /**
     * Executes this punishment, provided it has not already been executed. Note that after doing
     * this, some data of this punishment will no longer be editable
     */
    @Override
    public void execute() {
        // TODO
        if ( this.isExecuted ) throw new RuntimeException( "This punishment has already been executed" );

        this.isExecuted = true;
    }

    /**
     * @return A copy of this punishment
     */
    public ApiPunishment copy() {
        return new Punishment(
                this.id,
                this.playerUuid,
                this.staffUuid,
                this.punType,
                this.timestamp,
                this.length,
                this.reason,
                this.isActive,
                this.referenceId,
                this.isGuiPun,
                this.isNotifSent,
                false // note that isExecuted will always be set back to false, unsure if we should do this or not
        );
    }

    @Override
    public String toString() {
        return "Punishment{" +
                "id=" + id +
                ", playerUuid=" + playerUuid +
                ", staffUuid=" + staffUuid +
                ", punType=" + punType +
                ", timestamp=" + timestamp +
                ", length=" + length +
                ", reason='" + reason + '\'' +
                ", isActive=" + isActive +
                ", referenceId=" + referenceId +
                ", isGuiPun=" + isGuiPun +
                ", isNotifSent=" + isNotifSent +
                ", isExecuted=" + isExecuted +
                '}';
    }
}