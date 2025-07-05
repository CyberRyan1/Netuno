package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.models.ApiStaff;
import com.github.cyberryan1.netuno.api.services.*;
import com.github.cyberryan1.netuno.database.SettingsDatabase;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import com.github.cyberryan1.netuno.models.NetunoPlayer;
import com.github.cyberryan1.netuno.models.NetunoPunishment;
import com.github.cyberryan1.netuno.models.NetunoStaff;
import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * An implementation of the {@link ApiNetunoService} interface
 *
 * @author Ryan
 */
public class NetunoService implements ApiNetunoService {

    public static final CacheDebugPrinter.PrintSpecifier<NetunoPlayer> DEBUG_PRINTER_NETUNOPLAYER = player -> {
        String output = "\tPunishments (" + player.getPunishments().size() + " total):\n";
        for ( ApiPunishment aPun : player.getPunishments() ) {
            NetunoPunishment p = ( NetunoPunishment ) aPun;

            output += "\t\tPunishment #" + p.getId() + "\n";
            output += "\t\t\tPlayer = " + p.getPlayer().getName() + " (UUID \"" + p.getPlayerUuid().toString() + "\")\n";
            final String staffName = p.getStaffUuid() == ApiPunishment.CONSOLE_UUID ? "console" : p.getStaff().getName();
            final String staffUuid = p.getStaffUuid() == ApiPunishment.CONSOLE_UUID ? "console" : p.getStaffUuid().toString();
            output += "\t\t\tStaff = " + staffName + " (UUID \"" + staffUuid + "\")\n";
            output += "\t\t\tPunishment Type = " + p.getType().name().toUpperCase() + "\n";
            output += "\t\t\tTimestamp = " + p.getTimestamp() + "\n";
            output += "\t\t\tReason = \"" + p.getReason() + "\"\n";
            output += "\t\t\tActive = " + ( p.isActive() ? "TRUE" : "FALSE" ) + "\n";
            output += "\t\t\tReference ID = " + p.getReferenceId() + "\n";
            output += "\t\t\tGUI Punishment = " + ( p.isGuiPun() ? "YES" : "NO" ) + "\n";
            output += "\t\t\tNotification Sent = " + ( p.isNotifSent() ? "YES" : "NO" ) + "\n";
            output += "\t\t\tExecuted = " + ( p.isExecuted() ? "YES" : "NO" ) + "\n";
        }

        return output;
    };

    private final PlayerLoginLogoutCache<NetunoPlayer> PLAYER_CACHE = new PlayerLoginLogoutCache<>();
    private final PlayerLoginLogoutCache<NetunoStaff> STAFF_CACHE = new PlayerLoginLogoutCache<>();

    private final PunishmentService PUNISHMENT_SERVICE;
    private final AltService ALT_SERVICE;
    private final ChatService CHAT_SERVICE;
    private final ReportService REPORT_SERVICE;

    /**
     * Note that almost nothing should be done in the
     * constructor, but instead be done in the
     * {@link #initialize()} method
     */
    public NetunoService( PunishmentService punishmentService, AltService altService, ChatService chatService, ReportService reportService ) {
        this.PUNISHMENT_SERVICE = punishmentService;
        this.ALT_SERVICE = altService;
        this.CHAT_SERVICE = chatService;
        this.REPORT_SERVICE = reportService;
    }

    /**
     * Initializes this service
     */
    public void initialize() {
        this.PLAYER_CACHE.setLoginScript( event -> Optional.of( new NetunoPlayer( event.getUniqueId() ) ) );

        this.STAFF_CACHE.setLoginScript( event -> Optional.of( new NetunoStaff( event.getUniqueId() ) ) );
        this.STAFF_CACHE.setDataValidityScript( uuid -> CyberVaultUtils.hasPerms( Bukkit.getOfflinePlayer( uuid ), Settings.STAFF_PERMISSION.string() ) );
        this.STAFF_CACHE.setLogoutScript( event -> {
            getStaff( event.getPlayer() ).thenAccept( staff -> {
                String settingName = SettingsDatabase.NAME_SIGN_NOTIF_STATUS.replace( "%uuid%", event.getPlayer().getUniqueId().toString() );
                SettingsDatabase.saveSetting( settingName, staff.getSignNotificationStatus() + "" );
            } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
        } );

        this.ALT_SERVICE.initialize();
        this.REPORT_SERVICE.initialize();

        // Loading all online players
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            getPlayer( p );
            if ( p.hasPermission( Settings.STAFF_PERMISSION.string() ) ) {
                getStaff( p );
            }
        }
    }

    /**
     * Closes this service
     */
    public void close() {
    }

    /**
     * @return The {@link ApiPunishmentService}
     */
    @Override
    public ApiPunishmentService getPunishmentService() {
        return this.PUNISHMENT_SERVICE;
    }

    /**
     * @return The {@link ApiAltService} instance
     */
    @Override
    public ApiAltService getAltService() {
        return this.ALT_SERVICE;
    }

    /**
     * @return The {@link ApiChatService} instance
     */
    @Override
    public ApiChatService getChatService() {
        return this.CHAT_SERVICE;
    }

    /**
     * @return The {@link ApiReportService} instance
     */
    @Override
    public ApiReportService getReportService() {
        return this.REPORT_SERVICE;
    }

    /**
     * @param player A player
     * @return The player and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiPlayer> getPlayer( OfflinePlayer player ) {
        return getPlayer( player.getUniqueId() );
    }

    /**
     * @param uuid A player's uuid
     * @return The player and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiPlayer> getPlayer( UUID uuid ) {
        if ( this.PLAYER_CACHE.containsPlayer( uuid ) ) {
            this.PLAYER_CACHE.updateDataState( uuid );
            return CompletableFuture.completedFuture( this.PLAYER_CACHE.getData( uuid ).get() );
        }

        return CompletableFuture.supplyAsync( () -> {
            NetunoPlayer toReturn = new NetunoPlayer( uuid );
            final boolean playerOnline = Bukkit.getPlayer( uuid ) != null;

            // We know the cache does not contain the player from the
            //      check above

            // If the cache doesn't contain the player but they are
            //      online, add their data as active
            if ( playerOnline )
                this.PLAYER_CACHE.insertActiveData( uuid, toReturn );
            // If the cache doesn't contain the player and they are
            //      offline, add their data as inactive
            else
                this.PLAYER_CACHE.insertInactiveData( uuid, toReturn );

            return toReturn;
        } );
    }

    /**
     * Gets the provided player as an API player right now. This
     * should only be ran if the player is online, as it should
     * be guaranteed that the player is cached
     * @param player A player
     * @return The player and all of their Netuno data
     */
    @Override
    public Optional<ApiPlayer> getPlayerNow( Player player ) {
        if ( this.PLAYER_CACHE.containsPlayer( player.getUniqueId() ) ) {
            return Optional.of( this.PLAYER_CACHE.getData( player.getUniqueId() ).get() );
        }
        return Optional.empty();
    }

    /**
     * @param player A staff member
     * @return The staff member and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiStaff> getStaff( OfflinePlayer player ) {
        return getStaff( player.getUniqueId() );
    }

    /**
     * @param uuid A staff member's uuid
     * @return The staff member and all of their Netuno data
     */
    @Override
    public CompletableFuture<ApiStaff> getStaff( UUID uuid ) {
        if ( this.STAFF_CACHE.containsPlayer( uuid ) ) {
            this.STAFF_CACHE.updateDataState( uuid );
            return CompletableFuture.completedFuture( this.STAFF_CACHE.getData( uuid ).get() );
        }

        return CompletableFuture.supplyAsync( () -> {
            NetunoStaff toReturn = new NetunoStaff( uuid );
            final boolean playerOnline = Bukkit.getPlayer( uuid ) != null;

            // We know the cache does not contain the player from the
            //      check above

            // If the cache doesn't contain the player but they are
            //      online, add their data as active
            if ( playerOnline )
                this.STAFF_CACHE.insertActiveData( uuid, toReturn );
                // If the cache doesn't contain the player and they are
                //      offline, add their data as inactive
            else
                this.STAFF_CACHE.insertInactiveData( uuid, toReturn );

            return toReturn;
        } );
    }

    /**
     * @return All {@link NetunoStaff} instances stored
     */
    public List<NetunoStaff> getAllStaff() {
        List<NetunoStaff> toReturn = new ArrayList<>();
        for ( UUID uuid : STAFF_CACHE.getKeySet() ) {
            STAFF_CACHE.getData( uuid ).ifPresent( toReturn::add );
        }
        return toReturn;
    }

    /**
     * @param uuid A player's UUID
     * @return True if the provided UUID is cached, false otherwise
     */
    public boolean containsPlayer( UUID uuid ) {
        return this.PLAYER_CACHE.containsPlayer( uuid );
    }

    /**
     * @return Access the player cache. Note that this should
     *         rarely be used for editing and instead mainly used
     *         for reading of the data provided
     * @deprecated Please use other methods provided within this
     * class
     */
    public PlayerLoginLogoutCache<NetunoPlayer> getPlayerCache() {
        return this.PLAYER_CACHE;
    }

    /**
     * @return A list of type {@link NetunoPlayer} of all the players
     * who are cached. Does NOT refresh the last access timestamp
     * for each of the returned players
     */
    public List<NetunoPlayer> getAll() {
        List<NetunoPlayer> toReturn = new ArrayList<>();
        for ( UUID uuid : this.PLAYER_CACHE.getKeySet() ) {
            toReturn.add( this.PLAYER_CACHE.getData( uuid ).get() ); // since the uuid is in the cache, this will never be null
        }

        return toReturn;
    }

    /**
     * @param predicate A predicate with the argument being of
     *                  type UUID
     * @return A list of type {@link NetunoPlayer} of all the players
     * who are cached and satisfy the given predicate. Also
     * refreshes the last access timestamp for each of the
     * returned players
     */
    public List<NetunoPlayer> getAllThatSatisfy( Predicate<? super NetunoPlayer> predicate ) {
        List<NetunoPlayer> toReturn = new ArrayList<>();
        this.PLAYER_CACHE.getKeySet()
                .forEach( uuid -> {
                    NetunoPlayer player = this.PLAYER_CACHE.getData( uuid ).get(); // since the uuid is in the cache, this will never be null
                    if ( predicate.test( player ) == false ) return;
                    toReturn.add( player );
                    this.PLAYER_CACHE.refreshLastAccessTimestamp( uuid );
                } );
        return toReturn;
    }
}