package com.github.cyberryan1.netuno.api.models;

import com.github.cyberryan1.netunoapi.exceptions.ClassIncompleteException;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Used to represent a punishment and all of its data. Note
 * that this punishment may not be executed
 *
 * @author Ryan
 */
public interface ApiPunishment {

    int DEFAULT_ID = -1;
    int DEFAULT_REFERENCE_ID = -1;
    long PERMANENT_PUNISHMENT_LENGTH = Long.MAX_VALUE;

    /**
     * @return the ID of the punishment
     */
    int getId();

    /**
     * @param id the ID of the punishment
     */
    void setId( int id );

    /**
     * @return UUID of the player who is punished
     */
    UUID getPlayerUuid();

    /**
     * @return The player who is punished
     */
    OfflinePlayer getPlayer();

    /**
     * @param player UUID of the player who is punished
     */
    void setPlayer( UUID player );

    /**
     * @return UUID of the staff who did this punishment.
     *         Null if executed by console
     */
    UUID getStaffUuid();

    /**
     * @return The staff who executed this punishment. Null
     * if executed by console.
     */
    OfflinePlayer getStaff();

    /**
     * @param uuid UUID of the staff who did this punishment.
     *             Can be null if executed by console
     */
    void setStaff( UUID uuid );

    /**
     * @return The type of this punishment
     */
    PunType getType();

    /**
     * @param type The type of this punishment
     */
    void setType( PunType type );

    /**
     * @return Timestamp, in milliseconds, of when this
     *         punishment was executed
     */
    long getTimestamp();

    /**
     * @param timestamp Timestamp, in milliseconds, of
     *                  when this punishment was executed
     */
    void setTimestamp( long timestamp );

    /**
     * @return The length of the punishment, in milliseconds
     */
    long getLength();

    /**
     * @param length The length of this punishment, in
     *               milliseconds
     */
    void setLength( long length );

    /**
     * @return Returns how long the remaining duration of this
     *         punishment is. If {@link #isActive()} is false,
     *         returns 0
     */
    long getDurationRemaining();

    /**
     * @return The reason for this punishment
     */
    String getReason();

    /**
     * @param reason The reason for this punishment
     */
    void setReason( String reason );

    /**
     * Note: if the punishment is not active, it can
     * NOT be set back to active.
     * @return True if this punishment is active,
     *         false otherwise
     */
    boolean isActive();

    /**
     * Note: if the punishment is not active, it can
     * NOT be set back to active.
     * @param active True if this punishment is active,
     *               false otherwise
     */
    void setActive( boolean active );

    /**
     * @return The ID of the reference punishment
     *         associated with this punishment, if
     *         there is one. Otherwise, returns -1
     */
    int getReferenceId();

    /**
     * @param referenceId The ID of the reference
     *                    punishment associated with
     *                    this punishment, if there
     *                    is one. Otherwise, set this
     *                    to -1
     */
    void setReferenceId( int referenceId );

    /**
     * @return True if this punishment was executed via
     * the punishment GUI, false otherwise
     */
    boolean isGuiPun();

    /**
     * @param guiPun True if this punishment was executed
     *               through the punishment GUI, false
     *               otherwise
     */
    void setGuiPun( boolean guiPun );

    /**
     * @return True if a notification has been sent to the
     * player about this punishment, false otherwise
     */
    boolean isNotifSent();

    /**
     * Only works for IP punishments
     * @return True if {@link #getReferenceId()} is equal to
     * {@link #DEFAULT_REFERENCE_ID}, false otherwise
     */
    boolean isOriginalPunishment();

    /**
     * If the player is online and a notification has not
     * yet been sent to them, this will send the notification
     * to them. After this, a notification cannot be sent to
     * them again.
     */
    void sendNotification();

    /**
     *
     * @return true if the punishment has been executed, false otherwise
     */
    boolean isExecuted();

    /**
     * Executes this punishment, provided it has not already
     * been executed.
     * @param silent True to execute this punishment silently,
     *               false otherwise
     */
    void execute( boolean silent);

    /**
     * Checks if the punishment is completely filled with the correct information
     * @param requireValidId If the punishment id should be above 0 (true) or less than or equal to 0 (false)
     * @throws ClassIncompleteException If the punishment is incomplete
     */
    default void ensureValid( boolean requireValidId ) {
        if ( requireValidId && this.getId() <= 0 ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment ID must be greater than zero" ); }
        if ( requireValidId == false && this.getId() > 0 && this.getId() != DEFAULT_ID ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment ID must be less than or equal to zero" ); }
        if ( this.getType() == null ) { throw new ClassIncompleteException( "Punishment incomplete: Punishment type cannot be null" ); }
        if ( this.getPlayerUuid() == null ) { throw new ClassIncompleteException( "Punishment incomplete: Player UUID cannot be null" ); }
        if ( this.getStaffUuid() == null ) { throw new ClassIncompleteException( "Punishment incomplete: Staff UUID cannot be null" ); }
        if ( this.getLength() <= 0 && this.getType().hasNoLength() == false && this.getLength() != -1 ) { throw new ClassIncompleteException( "Punishment incomplete: Length must be greater than zero seconds" ); }
        if ( this.getTimestamp() <= 0 ) { throw new ClassIncompleteException( "Punishment incomplete: Timestamp must be greater than zero" ); }
        if ( this.getReason() == null ) { throw new ClassIncompleteException( "Punishment incomplete: Reason cannot be null" ); }
        if ( this.getType().isIpPunishment() && this.getReferenceId() < -1 ) { throw new ClassIncompleteException( "Punishment incomplete: Reference Punishment ID must be greater than zero for IP punishments" ); }
    }

    /**
     * @return A copy of this punishment
     */
    ApiPunishment copy();

    /**
     * Represents each of the different punishment types
     *
     * @author Ryan
     */
    enum PunType {
        WARN ( 0, true, false,false ),
        KICK ( 1, true, false, false ),
        MUTE ( 2, false, false, false ),
        UNMUTE ( 3, true, true, false ),
        BAN ( 4, false, false, false ),
        UNBAN ( 5, true, true, false ),
        IPMUTE ( 6, false, false, true ),
        UNIPMUTE ( 7, true, true, true ),
        IPBAN ( 8, false, false, true ),
        UNIPBAN ( 9, true, true, true );

        private final int index;
        private final boolean hasNoLength;
        private final boolean hasNoReason;
        private final boolean ipPunishment;
        PunType( int index, boolean hasNoLength, boolean hasNoReason, boolean ipPunishment ) {
            this.index = index;
            this.hasNoLength = hasNoLength;
            this.hasNoReason = hasNoReason;
            this.ipPunishment = ipPunishment;
        }

        public int getIndex() { return index; }

        /**
         * @return True if this is a punishment type that can
         * not have a length (i.e. a warn or an unban),
         * false otherwise
         */
        public boolean hasNoLength() {
            return hasNoLength;
        }

        /**
         * @return True if this is a punishment type that can
         * not have a reason (i.e. an unban or unmute),
         * false otherwise
         */
        public boolean hasNoReason() {
            return hasNoReason;
        }

        /**
         * @return True if this is an IP punishment,
         * false otherwise
         */
        public boolean isIpPunishment() {
            return ipPunishment;
        }

        //
        // Static Methods
        //

        public static PunType fromString( String str ) {
            for ( PunType type : values() ) {
                if ( type.name().equalsIgnoreCase( str ) ) {
                    return type;
                }
            }
            return null;
        }

        public static PunType fromIndex( int index ) {
            for ( PunType type : values() ) {
                if ( type.getIndex() == index ) {
                    return type;
                }
            }
            return null;
        }
    }
}