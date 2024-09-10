package com.github.cyberryan1.netuno.models.helpers;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used to manage cached data for players by loading the data for a player
 * when they log in (active data) and marking the data as inactive when the
 * player quits the server. After a customizable period of time, inactive
 * data is removed from the cache.
 *
 * @param <T> The type of data to cache
 * @author Ryan
 */
public class PlayerLoginLogoutCache<T> {

    // Settings
    private static final long DEFAULT_REMOVAL_DELAY = 1000L * 60 * 15; // 15 minutes, expressed in milliseconds
    private static final long AUTOMATIC_REMOVAL_DELAY = 1000L * 60; // 1 minute, expressed in milliseconds

    // Static variables
    private static boolean eventListenerRegistered = false;
    private static List<PlayerLoginLogoutCache> ALL_CACHES = new ArrayList<>();

    // Class variables
    private final ConcurrentHashMap<UUID, CacheData<T>> CACHE = new ConcurrentHashMap<>();
    private final BukkitTask REMOVAL_TASK;

    private long removalDelay = DEFAULT_REMOVAL_DELAY; // 15 minutes default, unit is milliseconds
    private LoginScriptExecutor<T> loginScript = null;
    private LogoutScriptExecutor logoutScript = null;
    private GenericScriptExecutor<T> updateScript = null;
    private GenericScriptExecutor<T> removalScript = null;

    /**
     * Adds this cache to the list of all caches, which is used in
     * the {@link PlayerLoginLogoutCacheListener} class. Additionally,
     * if that listener class is not yet registered as an event listener
     * with Bukkit, this will register it. Finally, this will start a
     * Bukkit task that will run the {@link #expireInactiveAndOldData()}
     * method every {@link #AUTOMATIC_REMOVAL_DELAY} milliseconds
     */
    public PlayerLoginLogoutCache() {
        ALL_CACHES.add( this );

        if ( eventListenerRegistered == false ) {
            CyberCore.getPlugin().getServer().getPluginManager().registerEvents( new PlayerLoginLogoutCacheListener(), CyberCore.getPlugin() );
            eventListenerRegistered = true;
        }

        REMOVAL_TASK = Bukkit.getScheduler().runTaskTimerAsynchronously( CyberCore.getPlugin(),
                this::expireInactiveAndOldData, AUTOMATIC_REMOVAL_DELAY, AUTOMATIC_REMOVAL_DELAY );
    }

    /**
     * <b>IMPORTANT!</b> You should only change this when
     * the server is starting up. Changing this while players
     * are online may cause issues.
     *
     * @param script The script to run when a player logs in.
     *               The output of this script is what will be
     *               added to the cache. If you don't want
     *               anything to be added to the cache, you can
     *               return an empty optional. If you don't want
     *               a login script to be ran, set this to null.
     *               Note that this will be ran async because it
     *               is triggered via the
     *               {@link AsyncPlayerPreLoginEvent} event.
     */
    public void setLoginScript( LoginScriptExecutor<T> script ) {
        this.loginScript = script;
    }

    /**
     * <b>IMPORTANT!</b> You should only change this when
     * the server is starting up. Changing this while players
     * are online may cause issues.
     *
     * @param script The script to run when a player who was
     *               in the cache logs out. If you don't want
     *               a logout script to be ran, set this
     *               to null. Note that this will NOT be ran
     *               async
     */
    public void setLogoutScript( LogoutScriptExecutor script ) {
        this.logoutScript = script;
    }

    /**
     * @param script The script that runs when a piece of cached
     *               data is updated via the
     *               {@link #updateData(UUID, Object)} method. If
     *               you don't want an update script to be ran,
     *               set this to null. Note that this will NOT
     *               be ran async
     */
    public void setUpdateScript( GenericScriptExecutor<T> script ) {
        this.updateScript = script;
    }

    /**
     * @param script The script that runs when a piece of cached
     *               data is removed because it is both (a) marked
     *               as {@link CacheDataState#INACTIVE} and (b)
     *               was last accessed at a time greater than
     *               {@link #getRemovalDelay()} milliseconds ago.
     *               If you don't want a removal script to be
     *               ran, set this to null. Note that this will
     *               NOT be ran async
     */
    public void setRemovalScript( GenericScriptExecutor<T> script ) {
        this.removalScript = script;
    }

    /**
     * @return The script to run when a player logs in. Null if
     * there is no login script that is ran. Note that this WILL
     * be ran async because it is triggered via the
     * {@link AsyncPlayerPreLoginEvent} event.
     */
    LoginScriptExecutor<T> getLoginScript() {
        return this.loginScript;
    }

    /**
     * @return The script to run when a player who was in this
     * cache logs out. Null if there is no logout script that
     * is ran. Note that this will NOT be ran async
     */
    LogoutScriptExecutor getLogoutScript() {
        return this.logoutScript;
    }

    /**
     * @return The script to run when a piece of data is updated
     * via the {@link #updateData(UUID, Object)} method. Null if
     * there is no update script that is ran. Note that this will
     * NOT be ran async
     */
    GenericScriptExecutor<T> getUpdateScript() {
        return this.updateScript;
    }

    /**
     * @return The script to run when a piece of data is removed
     * because it was inactive and was old. Null if there is no
     * removal script that is ran. Note that this will NOT be
     * ran async
     */
    GenericScriptExecutor<T> getRemovalScript() {
        return this.removalScript;
    }

    /**
     * Called when a player logs in to the server. If
     * {@link #getLoginScript()} is not null, the provided
     * event is passed to the login script's
     * {@link LoginScriptExecutor#login(AsyncPlayerPreLoginEvent)}
     * method. If this returns a non-empty optional,
     * the contents of that optional are added to the cache.
     * Otherwise, nothing is added to the cache <br><br>
     *
     * <i>Note: if the player is already in the cache, their
     * cached data is removed first, and then the above steps
     * are executed</i>
     *
     * @param event The {@link AsyncPlayerPreLoginEvent} event
     */
    private void executeLogin( AsyncPlayerPreLoginEvent event ) {
        if ( this.CACHE.containsKey( event.getUniqueId() ) ) {
            this.CACHE.remove( event.getUniqueId() );
        }

        if ( this.loginScript == null ) return;

        Optional<T> dataRetrieved = this.loginScript.login( event );
        if ( dataRetrieved.isEmpty() ) return;

        CacheData<T> cacheData = new CacheData<>( dataRetrieved.get() );
        cacheData.setState( CacheDataState.ACTIVE );
        this.CACHE.put( event.getUniqueId(), cacheData );
    }

    /**
     * Called when a player exits the server. If the player
     * provided is not entered within this cache, nothing
     * happens. Otherwise, the logout script's
     * {@link LogoutScriptExecutor#logout(PlayerQuitEvent)}
     * method is ran, provided {@link #getLogoutScript()}
     * is not null. Additionally, the state of the player's
     * cached data is set to {@link CacheDataState#INACTIVE}.
     * If {@link #getRemovalDelay()} is less than or equal to
     * zero, the data is immediately removed from this cache.
     *
     * @param event The {@link PlayerQuitEvent} event
     */
    private void executeLogout( PlayerQuitEvent event ) {
        // If the player who logged out isn't in the cache, we will not continue with
        //      running this method
        if ( this.CACHE.containsKey( event.getPlayer().getUniqueId() ) == false ) return;

        if ( this.logoutScript != null ) {
            this.logoutScript.logout( event );
        }

        CacheData<T> cacheData = this.CACHE.get( event.getPlayer().getUniqueId() );
        // Setting the recently accessed timestamp to the current time
        cacheData.recentlyAccessedUpdate();
        // Setting the state of the cached data as inactive
        cacheData.setState( CacheDataState.INACTIVE );

        // If the removal delay is less than or equal to zero, we remove the data immediately
        if ( this.removalDelay <= 0 ) {
            this.CACHE.remove( event.getPlayer().getUniqueId() );
        }
    }

    /**
     * Inserts data into the cache for the given player. The data inserted
     * will be marked as {@link CacheDataState#ACTIVE}. If the player already
     * exists within the cache, nothing will happen. Additionally, if the
     * provided player is not online, an error will be thrown.
     *
     * @param uuid The UUID of the player this data is for
     * @param t The data to be cached
     */
    public void insertActiveData( UUID uuid, T t ) {
        if ( this.CACHE.containsKey( uuid ) ) return;
        if ( Bukkit.getPlayer( uuid ) == null ) throw new IllegalArgumentException( "Player with uuid \"" + uuid.toString()
                + "\" is not online, so you cannot insert data marked as active for them" );

        CacheData<T> cacheData = new CacheData<>( t );
        cacheData.setState( CacheDataState.ACTIVE );
        this.CACHE.put( uuid, cacheData );
    }

    /**
     * A few remarks about this method: <br>
     * <ol type="I">
     *     <li>Firstly, if {@link #getRemovalDelay()} is less than or equal
     *     to zero, this method will do nothing</li>
     *     <li>Secondly, this method will error if the player provided is
     *     already in the cache AND the state of their cached date is
     *     {@link CacheDataState#ACTIVE}, as we do not want to replace active
     *     data with what is considered to be inactive data</li>
     *     <li>If none of the above is true, then the given data will be
     *     cached for the player and marked as {@link CacheDataState#INACTIVE}</li>
     * </ol>
     *
     * @param uuid The UUID of the player this data is for
     * @param t The data to be cached
     */
    public void insertInactiveData( UUID uuid, T t ) {
        // If the removal delay is less than or equal to zero, we can
        //      just ignore the inactive data
        if ( this.removalDelay <= 0 ) return;

        // If the player is already in the cache, we need to do some
        //      more checks to see if the data provided to this method
        //      can be loaded to the cache as inactive
        if ( this.CACHE.containsKey( uuid ) ) {
            final CacheData<T> cacheData = this.CACHE.get( uuid );

            // If the player is currently online, then their data cannot
            //      be cached as inactive
            // If the data for the player is currently active, we cannot
            //      update their data as inactive
            if ( cacheData.getState() == CacheDataState.ACTIVE ) throw new IllegalArgumentException( "Cannot cache data for player with uuid \""
                    + uuid.toString() + "\" as inactive because the player already has active cache data" );

            // If the player is offline, we just update their cached data
            //      to be what was provided to this method
            cacheData.updateData( t );
            // Note: there shouldn't be any need to call the
            //      CacheData#recentlyAccessedUpdate() method after the
            //      line above, but I am writing this here as a reminder
            //      just in case
        }

        else {
            CacheData<T> cacheData = new CacheData<>( t );
            cacheData.setState( CacheDataState.INACTIVE );
            this.CACHE.put( uuid, cacheData );
        }
    }

    /**
     * @return How many milliseconds to wait between when
     * a cached data is marked as {@link CacheDataState#INACTIVE}
     * and when it is removed from the cache
     */
    public long getRemovalDelay() {
        return this.removalDelay;
    }

    /**
     * @param removalDelay How many milliseconds to wait between
     *                     when a cached data is marked as
     *                     {@link CacheDataState#INACTIVE} and
     *                     when it is removed from the cache
     */
    public void setRemovalDelay( long removalDelay ) {
        this.removalDelay = removalDelay;
    }

    /**
     * @param uuid A player's UUID
     * @return True if any data for the player is within this
     * cache, false otherwise
     */
    public boolean containsPlayer( UUID uuid ) {
        return this.CACHE.containsKey( uuid );
    }

    /**
     * @return A list of all the keys
     */
    public List<UUID> getKeySet() {
        return new ArrayList<>( this.CACHE.keySet() );
    }

    /**
     * @param uuid A player's UUID
     * @return The data associated with the provided player
     * that is cached here. Returns an empty optional if the
     * player is not contained within this cache
     */
    public Optional<T> getData( UUID uuid ) {
        CacheData<T> data = this.CACHE.get( uuid );
        if ( data == null ) return Optional.empty();
        return Optional.of( data.accessData() );
    }

    /**
     * Used to update the data associated with a player. If
     * {@link #getUpdateScript()} is not null, then it will
     * also be ran
     *
     * @param uuid A player's UUID
     * @param t The new data
     */
    public void updateData( UUID uuid, T t ) {
        CacheData<T> data = this.CACHE.get( uuid );
        if ( data == null ) throw new IllegalArgumentException( "UUID \"" + uuid.toString() + "\" is not in the cache" );

        data.updateData( t );
        if ( this.updateScript != null ) this.updateScript.execute( uuid, t );
    }

    /**
     * Removes all cached data related to the provided UUID
     * from this cache. If {@link #getRemovalScript()} is not
     * null, then it will also be ran. This will only work if
     * the provided UUID exists within this cache, otherwise
     * an error will occur
     *
     * @param uuid A player's UUID
     */
    public void clearData( UUID uuid ) {
        if ( this.CACHE.containsKey( uuid ) == false ) throw new IllegalArgumentException(
                "Cannot find entry associated with uuid \"" + uuid.toString() + "\"" );

        if ( this.removalScript != null ) this.removalScript.execute( uuid, this.CACHE.get( uuid ).accessData() );
        this.CACHE.remove( uuid );
    }

    /**
     * Goes through the cache and expires any data that is
     * (a) marked as {@link CacheDataState#INACTIVE}, and (b)
     * was last accessed more than the time specified by
     * {@link #getRemovalDelay()} ago. Items that meat this
     * criteria are removed via the {@link #clearData(UUID)}
     * method. <br><br>
     *
     * <b>Note:</b> this should typically never be used by
     * anything else except for the automatic removal task
     */
    private void expireInactiveAndOldData() {
        for ( UUID uuid : this.CACHE.keySet() ) {
            CacheData<T> data = this.CACHE.get( uuid );
            if ( data.getState() == CacheDataState.INACTIVE && data.getLastAccessTimestamp() <= System.currentTimeMillis() - removalDelay ) {
                clearData( uuid );
            }
        }
    }

    /**
     * @param uuid A player's UUID
     * @return True if the data stored for the provided player
     * is active, false otherwise
     */
    public boolean playersDataIsActive( UUID uuid ) {
        if ( this.CACHE.containsKey( uuid ) == false ) throw new IllegalArgumentException( "Player with uuid \""
                + uuid + "\" does not exist within this cache" );
        return this.CACHE.get( uuid ).getState() == CacheDataState.ACTIVE;
    }

    /**
     * Checks if the provided player is online or not and
     * updates the state of their cached data accordingly
     *
     * @param uuid The player's UUID
     */
    public void updateDataState( UUID uuid ) {
        if ( this.CACHE.containsKey( uuid ) == false ) throw new IllegalArgumentException( "Player with uuid \""
                + uuid + "\" does not exist within this cache" );

        final CacheData<T> data = this.CACHE.get( uuid );
        final boolean playerOnline = Bukkit.getPlayer( uuid ) != null;
        if ( playerOnline ) data.setState( CacheDataState.ACTIVE );
        else data.setState( CacheDataState.INACTIVE );
    }

    /**
     * Updates the recently accessed timestamp of the data
     * associated with the provided UUID to the current timestamp
     * @param uuid A UUID
     */
    public void refreshLastAccessTimestamp( UUID uuid ) {
        if ( this.CACHE.containsKey( uuid ) == false ) throw new IllegalArgumentException( "Player with uuid \""
                + uuid + "\" does not exist within this cache" );

        this.CACHE.get( uuid ).recentlyAccessedUpdate();
    }

    /**
     * Outputs the contents of this cache to a file to
     * help with debugging
     *
     * @param printerB The way the data will be printed to
     *                 the file
     */
    public void printDebugInfo( CacheDebugPrinter.PrintSpecifier<T> printerB ) {
        CacheDebugPrinter<UUID, T> debugger = new CacheDebugPrinter<>();

        for ( UUID uuid : this.CACHE.keySet() ) {
            debugger.getCache().put( uuid, this.CACHE.get( uuid ).accessDataWithoutUpdate() );
        }

        debugger.setPrinterA( UUID::toString );
        debugger.setPrinterB( printerB );

        debugger.printToFile();
    }

    /**
     * Used to hold a piece of cached data, along with other
     * data associated with it, including the last timestamp
     * it was accessed and the state of this data
     *
     * @param <H> The type of data being stored
     * @author Ryan
     */
    private class CacheData<H> {

        private H h;
        private long lastAccessTimestamp; // in milliseconds
        private CacheDataState state;

        /**
         * @param h The data to be stored
         */
        public CacheData( H h ) {
            this.h = h;
            recentlyAccessedUpdate();
        }

        /**
         * Accesses the data and updates its last access
         * timestamp to the current time. <br>
         * <b>Note:</b> if you want to update this data,
         * you should be using {@link #updateData(Object)},
         * as this will ensure the last access timestamp
         * is correct
         *
         * @return The data held by this instance
         */
        public H accessData() {
            recentlyAccessedUpdate();
            return this.h;
        }

        /**
         * Accesses the data but does NOT update its last
         * access timestamp to the current time. This should
         * rarely be used
         *
         * @return The data held by this instance
         */
        public H accessDataWithoutUpdate() {
            return this.h;
        }

        /**
         * Updates the data held by in this instance
         * and updates its last access timestamp to the
         * current time
         *
         * @param h The updated data
         */
        public void updateData( H h ) {
            recentlyAccessedUpdate();
            this.h = h;
        }

        /**
         * @return The state of this data
         */
        public CacheDataState getState() {
            return this.state;
        }

        /**
         * @param state The new state of this data
         */
        public void setState( CacheDataState state ) {
            this.state = state;
        }

        /**
         * @return The timestamp, in milliseconds, of
         * when this data was last accessed
         */
        public long getLastAccessTimestamp() {
            return this.lastAccessTimestamp;
        }

        /**
         * Updates the last access timestamp to the
         * current time
         */
        private void recentlyAccessedUpdate() {
            this.lastAccessTimestamp = System.currentTimeMillis();
        }
    }

    /**
     * Holds different states a piece of cached data can
     * be in.
     *
     * @author Ryan
     */
    private enum CacheDataState {
        /**
         * The state when the player associated with this
         * piece of data is online. The state should rarely
         * be used when the associated player is not online
         */
        ACTIVE,

        /**
         * The state when the player associated with this
         * piece of data is offline, and typically means that
         * it will be removed from the cache soon. This state
         * should never be used when the associated player is
         * online
         */
        INACTIVE
    }

    /**
     * The script that is executed when a player logs on to the
     * server. The output of this script is what is added to the
     * cache
     *
     * @param <E> The type of data this script will output
     * @author Ryan
     */
    public interface LoginScriptExecutor<E> {
        /**
         *
         * @param event The {@link AsyncPlayerPreLoginEvent} event
         * @return What is to be added to the cache
         */
        Optional<E> login( AsyncPlayerPreLoginEvent event );
    }

    /**
     * The script that is executed when a player who was in the
     * cache logs off the server
     *
     * @author Ryan
     */
    public interface LogoutScriptExecutor {
        /**
         * Note: this doesn't return anything as nothing should be
         * added to the cache when a player logs out
         * @param event The {@link PlayerQuitEvent}
         */
        void logout( PlayerQuitEvent event );
    }

//    /**
//     * The script that is executed when a piece of data is removed
//     * from the cache because it is both (a) marked as
//     * {@link CacheDataState#INACTIVE} and (b) was last accessed
//     * at a time longer than {@link #getRemovalDelay()} milliseconds
//     * ago
//     *
//     * @author Ryan
//     */
//    public interface RemovalScriptExecutor {
//
//        void execute();
//    }

    /**
     * A generic script that is ran when needed
     *
     * @author Ryan
     */
    public interface GenericScriptExecutor<A> {

        /**
         * @param uuid The player's UUID
         * @param data The data associated with the player
         */
        void execute( UUID uuid, A data );
    }

    /**
     * Handles login and logout events for the cache
     *
     * @author Ryan
     */
    private static class PlayerLoginLogoutCacheListener implements Listener {

        @EventHandler( priority = EventPriority.HIGHEST )
        public void onPlayerLogin( AsyncPlayerPreLoginEvent event ) {
            ALL_CACHES.forEach( c -> c.executeLogin( event ) );
        }

        @EventHandler( priority = EventPriority.HIGHEST )
        public void onPlayerQuit( PlayerQuitEvent event ) {
            ALL_CACHES.forEach( c -> c.executeLogout( event ) );
        }
    }
}