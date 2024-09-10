package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.PrettyStringLibrary;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * Only works for IP punishments
     *
     * @return True if {@link #getReferenceId()} is equal to
     *         {@link #DEFAULT_REFERENCE_ID}, false otherwise
     */
    @Override
    public boolean isOriginalPunishment() {
        return getReferenceId() == DEFAULT_REFERENCE_ID;
    }

    /**
     * If the player is online and a notification has not yet been sent to them, this will send the
     * notification to them. After this, a notification cannot be sent to them again.
     */
    @Override
    public void sendNotification() {
        OfflinePlayer player = getPlayer();
        if ( player.isOnline() == false )
            throw new RuntimeException( "Player " + player.getName() + " (uuid " + player.getUniqueId().toString() + ") is not online" );
        Settings settingToFill = PunishmentLibrary.getSettingForMessageType( getType(), PunishmentLibrary.MessageSetting.JOIN_NOTIFICATION );
        Component message = fillSettingMessage( settingToFill );
        player.getPlayer().sendMessage( message );
        this.isNotifSent = true;
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
     * @param silent True to execute this punishment silently,
     *               false otherwise
     */
    @Override
    public void execute( boolean silent ) {
        // TODO
        if ( this.isExecuted ) throw new RuntimeException( "This punishment has already been executed" );

        this.isExecuted = true;
    }

    /**
     * Replaces all the config variables within the provided
     * setting and returns it <br> <br>
     * <p>
     * List of config variables:
     * <ul>
     *     <li><code>[STAFF]</code> Staff who executed the punishment</li>
     *     <li><code>[TARGET]</code> The player who was punished</li>
     *     <li><code>[LENGTH]</code> The length of the punishment</li>
     *     <li><code>[REMAIN]</code> How long until the punishment expires</li>
     *     <li><code>[REASON]</code> The reason of the punishment</li>
     *     <li><code>[ACCOUNTS]</code> A list of all known alt accounts of the target player</li>
     *
     * </ul>
     *
     * @param setting The {@link Settings} to use
     * @return The filled component
     */
    public Component fillSettingMessage( Settings setting ) {
        String msg = setting.string();

        Map<String, String> replacements = new HashMap<>();
        replacements.put( "[STAFF]", getStaff().getName() );
        replacements.put( "[TARGET]", getPlayer().getName() );
        replacements.put( "[LENGTH]", TimestampUtils.durationToString( getLength() ) );
        replacements.put( "[REMAIN]", TimestampUtils.durationToString( getDurationRemaining() ) );
        replacements.put( "[REASON]", getReason() );
        for ( Map.Entry<String, String> entry : replacements.entrySet() ) {
            msg = msg.replace( entry.getKey(), entry.getValue() );
        }

        // Since accounts might be resource intensive, we do it
        //      separately from the others
        if ( msg.contains( "[ACCOUNTS]" ) ) {
            List<String> altNames = Netuno.ALT_SERVICE.getAlts( getPlayerUuid() ).stream()
                    .map( altUuid -> Bukkit.getOfflinePlayer( altUuid ).getName() )
                    .collect( Collectors.toList() );
            // Only going to allow a maximum of three list elements
            String replacement = PrettyStringLibrary.getNonOxfordCommaListWithRemainder( altNames, 3 );
            msg = msg.replace( "[ACCOUNTS]", replacement );
        }

        return MiniMessage.miniMessage().deserialize( msg );
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