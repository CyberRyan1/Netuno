package com.github.cyberryan1.netuno.apimplement.models.punishments;

import com.github.cyberryan1.netunoapi.exceptions.ClassIncompleteException;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class NetunoPunishment implements NPunishment {

    private int id = -1;
    private PunishmentType punishmentType = null;
    private String playerUuid = null;
    private String staffUuid = null;
    private long length = 0;
    private long timestamp = -1;
    private String reason = null;
    private boolean active = false;
    private boolean guiPun = false;
    private int referencePunId = -100;
    private boolean needsNotifSent = false;

    public NetunoPunishment( int id, PunishmentType punishmentType, String playerUuid, String staffUuid,
                             long length, long timestamp, String reason, boolean active,
                             boolean guiPun, int referencePunId, boolean needsNotifSent ) {
        this.id = id;
        this.punishmentType = punishmentType;
        this.playerUuid = playerUuid;
        this.staffUuid = staffUuid;
        this.length = length;
        this.timestamp = timestamp;
        this.reason = reason;
        this.active = active;
        this.guiPun = guiPun;
        this.referencePunId = referencePunId;
        this.needsNotifSent = needsNotifSent;
    }

    public NetunoPunishment() {}

    //
    // Main Methods
    //

    /**
     * @return The amount of seconds remaining in the punishment. Returns
     * 0 if the punishment has no length or if the punishment is not active.
     * Returns -1 if the punishment length is permanent.
     */
    public long getSecondsRemaining() {
        if ( this.punishmentType.hasNoLength() || dataIsActive() == false ) { return 0; }
        if ( this.length == -1 ) { return -1; }
        long remain = this.length - ( TimeUtils.getCurrentTimestamp() - this.timestamp );
        if ( remain < 0 ) { return 0; }
        return remain;
    }

    /**
     * @return False if the punishment has been designated as inactive in
     * the database, if the punishment has no length, or if the punishment
     * has expired. True otherwise.
     */
    public boolean isActive() {
        long secondsRemain = getSecondsRemaining();
        return secondsRemain != 0;
    }

    /**
     * @return The amount of seconds remaining in the punishment as a
     * {@link NDuration} object.
     */
    public NDuration getLengthRemaining() {
        long remain = getSecondsRemaining();
        if ( remain == -1 ) { return new NDuration( true ); }
        return new NDuration( remain );
    }

    /**
     * @return The length of the punishment as a {@link NDuration} object.
     */
    public NDuration getTimeLength() {
        if ( this.length == -1 ) { return new NDuration( true ); }
        return new NDuration( this.length );
    }

    /**
     * @return The player as an {@link OfflinePlayer}
     */
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer( UUID.fromString( this.playerUuid ) );
    }

    /**
     * @return The staff as an {@link OfflinePlayer}
     */
    public OfflinePlayer getStaff() {
        return Bukkit.getOfflinePlayer( UUID.fromString( this.staffUuid ) );
    }

    /**
     * Checks if the punishment is completely filled with the correct information
     * @param requireValidId If the punishment id should be above 0 (true) or less than or equal to 0 (false)
     * @throws ClassIncompleteException If the punishment is incomplete
     */
    public void ensureValid( boolean requireValidId ) {
        if ( requireValidId && this.id <= 0 ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment ID must be greater than zero" ); }
        if ( requireValidId == false && this.id > 0 ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment ID must be less than or equal to zero" ); }
        if ( this.punishmentType == null ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment type cannot be null" ); }
        if ( this.playerUuid == null ) { throw new ClassIncompleteException( "Punishment incomplete: Player UUID cannot be null" ); }
        if ( this.staffUuid == null ) { throw new ClassIncompleteException( "Punishment incomplete: Staff UUID cannot be null" ); }
        if ( this.length <= 0 && this.punishmentType.hasNoLength() == false && this.length != -1 ) { throw new ClassIncompleteException( "Punishment incomplete: Length must be greater than zero seconds" ); }
        if ( this.timestamp <= 0 ) { throw new ClassIncompleteException( "Punishment incomplete: Timestamp must be greater than zero" ); }
        if ( this.reason == null ) { throw new ClassIncompleteException( "Punishment incomplete: Reason cannot be null" ); }
        if ( this.punishmentType.isIpPunishment() && this.referencePunId < -1 ) { throw new ClassIncompleteException( "Punishment incomplete: Reference Punishment ID must be greater than zero for IP punishments" ); }
    }

    /**
     * @return A copy of this punishment
     */
    public NPunishment copy() {
        NPunishment clone = new NetunoPunishment();
        clone.setId( this.id );
        clone.setPunishmentType( this.punishmentType );
        clone.setPlayerUuid( this.playerUuid );
        clone.setStaffUuid( this.staffUuid );
        clone.setLength( this.length );
        clone.setTimestamp( this.timestamp );
        clone.setReason( this.reason );
        clone.setReferencePunId( this.referencePunId );
        clone.setGuiPun( this.guiPun );
        clone.setActive( this.active );
        clone.setNeedsNotifSent( this.needsNotifSent );
        return clone;
    }

    //
    // Getters & Setters
    //

    /**
     * @return The ID of the punishment.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The type of this punishment.
     */
    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    /**
     * @return The UUID of the player who was punished.
     */
    public String getPlayerUuid() {
        return playerUuid;
    }

    /**
     * @return The UUID of the staff member who punished the player.
     * May be "CONSOLE" to represent console punishments.
     */
    public String getStaffUuid() {
        return staffUuid;
    }

    /**
     * @return The length of the punishment in seconds. May be -1 to
     * represent permanent punishments or punishments with no length,
     * such as warns, kicks, unmutes, etc.
     */
    public long getLength() {
        return length;
    }

    /**
     * @return The timestamp of the punishment.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return The reason for the punishment.
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return Whether the punishment is active (true) or not (false)
     */
    public boolean dataIsActive() {
        return active;
    }

    /**
     * @return Whether the punishment was executed via the punishment
     * GUI (true) or not (false)
     */
    public boolean isGuiPun() {
        return guiPun;
    }

    /**
     * @return The ID of the punishment that this punishment is a
     * reference to. Should only be set if the punishment is an
     * IP punishment, like an IP ban, IP mute, etc. Otherwise, it
     * should be -100. If the punishment is the base punishment,
     * this should be -1.
     */
    public int getReferencePunId() {
        return referencePunId;
    }

    /**
     * @return Whether the notification has been sent for this
     * punishment (true) or not (false). Should only be set to true
     * if the punishment type is a warning.
     */
    public boolean needsNotifSent() {
        return needsNotifSent;
    }

    /**
     * @param id The ID of the punishment. Must be greater than 0.
     */
    public void setId( int id ) {
        this.id = id;
    }

    /**
     * @param punishmentType The type of punishment.
     */
    public void setPunishmentType( PunishmentType punishmentType ) {
        this.punishmentType = punishmentType;
    }

    /**
     * @param playerUuid The UUID of the player who was punished.
     */
    public void setPlayerUuid( String playerUuid ) {
        this.playerUuid = playerUuid;
    }

    /**
     * @param player The player who was punished.
     */
    public void setPlayer( OfflinePlayer player ) {
        this.playerUuid = player.getUniqueId().toString();
    }

    /**
     * @param staffUuid The UUID of the staff member who punished
     *                  the player. May be "CONSOLE" to represent
     *                  console punishments.
     */
    public void setStaffUuid( String staffUuid ) {
        this.staffUuid = staffUuid;
    }

    /**
     * @param staff The staff member who punished the player.
     */
    public void setStaff( OfflinePlayer staff ) {
        this.staffUuid = staff.getUniqueId().toString();
    }

    /**
     * @param length The length of the punishment in seconds.
     *               May be -1 to represent permanent punishments
     *               or punishments with no length, such as warns,
     *               kicks, unmutes, etc.
     */
    public void setLength( long length ) {
        this.length = length;
    }

    /**
     * @param timestamp The timestamp of the punishment.
     */
    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    /**
     * @param reason The reason for the punishment.
     */
    public void setReason( String reason ) {
        this.reason = reason;
    }

    /**
     * @param active Whether the punishment is active (true) or
     *               not (false).
     */
    public void setActive( boolean active ) {
        this.active = active;
    }

    /**
     * @param guiPun Whether the punishment was executed via the
     *              punishment GUI (true) or not (false)
     */
    public void setGuiPun( boolean guiPun ) {
        this.guiPun = guiPun;
    }

    /**
     * @param referencePunId The ID of the punishment that this
     *                        punishment is a reference to. Should
     *                        only be set if the punishment is an
     *                        IP punishment, like an IP ban, IP
     *                        mute, etc. If this punishment is the
     *                        original IP punishment, this should be
     *                        -1. Otherwise, this should be -100.
     */
    public void setReferencePunId( int referencePunId ) {
        this.referencePunId = referencePunId;
    }

    /**
     * @param needsNotifSent Whether the notification has been sent
     *                       for this punishment (true) or not
     *                       (false). Should only be set to true if
     *                       the punishment type is a warning.
     */
    public void setNeedsNotifSent( boolean needsNotifSent ) {
        this.needsNotifSent = needsNotifSent;
    }

    /**
     * I hope this is obvious.
     */
    @Override
    public String toString() {
        return "NPunishment{" + "id=" + id +
                ", punishmentType=" + punishmentType +
                ", playerUuid=" + playerUuid +
                ", staffUuid=" + staffUuid +
                ", length=" + length +
                ", timestamp=" + timestamp +
                ", reason=" + reason +
                ", guiPun=" + guiPun +
                ", referencePunId=" + referencePunId +
                ", needsNotifSent=" + needsNotifSent +
                '}';
    }
}