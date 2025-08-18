//package com.github.cyberryan1.netuno.models.helpers;
//
//import com.github.cyberryan1.cybercore.spigot.CyberCore;
//import org.bukkit.Bukkit;
//import org.bukkit.scheduler.BukkitTask;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.function.Consumer;
//import java.util.function.Predicate;
//
///**
// * A cache implementation that evicts entries based on
// * specified criteria and time-based access patterns. The
// * cache leverages Bukkit's asynchronous scheduler to
// * periodically check and remove entries that either match
// * the eviction criteria or have exceeded their timeout
// * period.<br><br>
// *
// * The cache maintains thread safety using ConcurrentHashMap
// * and provides automatic cleanup of stale entries. When an
// * entry is evicted, a specified callback is executed to
// * handle any necessary cleanup operations.<br><br>
// *
// * The eviction process runs on a separate thread to avoid
// * impacting server performance. Both time-based eviction and
// * custom criteria-based eviction are supported
// * simultaneously. Time-based evictions will not activate the
// * eviction callback- only custom criteria-based evictions
// * will.
// *
// * @param <K> The type of keys maintained by this cache
// * @param <V> The type of values maintained by this cache
// */
//public class CriteriaCache<K, V> {
//
//    /**
//     * Represents an entry in the cache that holds a value and
//     * tracks its last access time. The entry is used internally
//     * by the CriteriaCache to manage cached values and determine
//     * when they should be evicted.
//     *
//     * @param <V> The type of value stored in this cache entry
//     */
//    private static class CacheEntry<V> {
//        final V value;
//        volatile long lastAccessTime;
//
//        CacheEntry( V value ) {
//            this.value = value;
//            this.lastAccessTime = System.currentTimeMillis();
//        }
//    }
//
//    private final ConcurrentMap<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
//    private final Predicate<V> evictionCriteria;
//    private final Consumer<V> onEvict;
//    private final BukkitTask scheduler;
//    private final long timeoutMs;
//
//    /**
//     * Constructs a new CriteriaCache with the specified eviction
//     * parameters.
//     *
//     * @param evictionCriteria   The predicate used to determine
//     *                           if an entry should be evicted
//     * @param onEvict            The consumer to execute when an
//     *                           entry is evicted due to it
//     *                           meeting the evicitionCriteria
//     *                           <i>(note: this will <b>NOT</b>
//     *                           run if an entry is evicted due
//     *                           to a timeout)</i>
//     * @param checkIntervalTicks The interval in ticks between
//     *                           eviction checks
//     * @param timeoutSeconds     The time in seconds after which
//     *                           an entry is considered for
//     *                           eviction
//     */
//    public CriteriaCache( Predicate<V> evictionCriteria, Consumer<V> onEvict, long checkIntervalTicks, long timeoutSeconds ) {
//        this.evictionCriteria = evictionCriteria;
//        this.onEvict = onEvict;
//        this.timeoutMs = timeoutSeconds * 1000;
//        this.scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously( CyberCore.getPlugin(), this::evictIfNecessary, checkIntervalTicks, checkIntervalTicks );
//    }
//
//    /**
//     * Adds an entry to the cache.
//     *
//     * @param key   The key to store the value under
//     * @param value The value to store
//     */
//    public void put( K key, V value ) {
//        map.put( key, new CacheEntry<>( value ) );
//    }
//
//    /**
//     * Retrieves an Optional containing the value associated
//     * with the given key in the cache.
//     *
//     * @param key The key whose associated value is to be
//     *            returned
//     * @return An Optional containing the value associated with
//     *         the specified key, or an empty Optional if no
//     *         value is present
//     */
//    public Optional<V> get( K key ) {
//        CacheEntry<V> entry = map.get( key );
//        if ( entry != null ) {
//            entry.lastAccessTime = System.currentTimeMillis();
//            return Optional.of( entry.value );
//        }
//        return Optional.empty();
//    }
//
//    /**
//     * Returns a Set view of the keys contained in this cache.
//     *
//     * @return A Set view of the keys contained in this cache
//     */
//    public Set<K> getKeys() {
//        return map.keySet();
//    }
//
//
//    /**
//     * Returns true if this cache contains a mapping for the
//     * specified key.
//     *
//     * @param key The key whose presence in this cache is to be
//     *            tested
//     * @return true if this cache contains a mapping for the
//     *         specified key, false otherwise
//     */
//    public boolean containsKey( K key ) {
//        return map.containsKey( key );
//    }
//
//    private void evictIfNecessary() {
//        long now = System.currentTimeMillis();
//        for ( Map.Entry<K, CacheEntry<V>> entry : map.entrySet() ) {
//            if ( now - entry.getValue().lastAccessTime > timeoutMs ) {
//                map.remove( entry.getKey() );
//            }
//            else if ( evictionCriteria.test( entry.getValue().value ) ) {
//                map.remove( entry.getKey() );
//                onEvict.accept( entry.getValue().value );
//            }
//        }
//    }
//}
