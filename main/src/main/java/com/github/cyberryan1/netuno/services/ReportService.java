package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import com.github.cyberryan1.netuno.api.services.ApiReportService;
import com.github.cyberryan1.netuno.database.ReportsDatabase;
import com.github.cyberryan1.netuno.guis.report.ReportUtils;
import com.github.cyberryan1.netuno.models.NetunoReport;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Used to get information about reports and to delete them.
 * The API does not support report creation- that will be only
 * done internally by Netuno. <br>
 * This is an implementation of {@link ApiReportService}
 *
 * @author Ryan
 */
public class ReportService implements ApiReportService {

    // how long before a report expires
    public static long REPORT_EXPIRE_TIME_MILLIS;

    private final Map<UUID, List<ApiReport>> CACHE = new HashMap<>();
    // how often to check for expired reports
    private final long CHECK_INTERVAL_TICKS = 20L * 60 * 5; // 5 minutes
    
    private BukkitTask task = null;

    /**
     * Initializes the report service
     */
    public void initialize() {
        updateReportExpireTimeMillis();

        CyberLogUtils.logWarn( "Attempting to query all entries in the reports database" );
        CyberLogUtils.logWarn( "Lag may occur!" );
        List<ApiReport> reports = ReportsDatabase.getAllReports();
        CyberLogUtils.logWarn( "Successfully queried " + reports.size() + " entries from the reports database" );
        
        // Loading all reports from the database into the cache
        for ( ApiReport report : reports ) {
            if ( CACHE.containsKey( report.getPlayer() ) ) {
                CACHE.get( report.getPlayer() ).add( report );
            }
            else {
                List<ApiReport> toAdd = new ArrayList<>();
                toAdd.add( report );
                CACHE.put( report.getPlayer(), toAdd );
            }
        }

        // Loading all online players
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            getReportsAgainst( p );
        }

        task = Bukkit.getScheduler().runTaskTimerAsynchronously( CyberCore.getPlugin(), this::deleteAllExpiredReports, 100L, CHECK_INTERVAL_TICKS );
        ReportUtils.updateAvailableReasons();
    }

    /**
     * Updates the {@link #REPORT_EXPIRE_TIME_MILLIS} to match
     * the value given by {@link Settings#VIEW_REPORT_EXPIRE_TIME_HOURS}
     */
    public void updateReportExpireTimeMillis() {
        // note: Settings.REPORT_EXPIRE_TIME is given in hours
        REPORT_EXPIRE_TIME_MILLIS = 1000L * 60L * 60L * Settings.VIEW_REPORT_EXPIRE_TIME_HOURS.integer();
                                //                60 minutes per hour
                                //          60 seconds per minute
                                //  1000ms per second
    }

    /**
     * Adds the provided report to the database and the cache,
     * if needed
     * @param report The report
     */
    public void addReport( NetunoReport report ) {
        report.ensureValid( false );
        if ( CACHE.containsKey( report.getPlayer() ) ) {
            CACHE.get( report.getPlayer() ).add( report );
        }
        else if ( Bukkit.getOfflinePlayer( report.getPlayer() ).isOnline() ) {
            List<ApiReport> toAdd = new ArrayList<>();
            toAdd.add( report );
            CACHE.put( report.getPlayer(), toAdd );
        }

        CompletableFuture.runAsync( () -> ReportsDatabase.addReport( report ) ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    /**
     * Gets a report by its unique identifier
     *
     * @param id The ID of the report to get
     * @return The report with the specified ID
     */
    @Override
    public Optional<ApiReport> getReport( int id ) {
        for ( List<ApiReport> reports : CACHE.values() ) {
            for ( ApiReport report : reports ) {
                if ( report.getId() == id ) {
                    return Optional.of( report );
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets all reports that have been made against the specified
     * player
     *
     * @param player The player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    @Override
    public List<ApiReport> getReportsAgainst( OfflinePlayer player ) {
        return getReportsAgainst( player.getUniqueId() );
    }

    /**
     * Gets all reports that have been made against the specified
     * player
     *
     * @param uuid UUID of the player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    @Override
    public List<ApiReport> getReportsAgainst( UUID uuid ) {
        return CACHE.getOrDefault( uuid, new ArrayList<>() );
    }

    /**
     * Gets all reports that have been made by the specified
     * player
     *
     * @param player The player to get reports by
     * @return List of reports that have been made by the player
     */
    @Override
    public List<ApiReport> getReportsBy( OfflinePlayer player ) {
        return getReportsBy( player.getUniqueId() );
    }

    /**
     * Gets all reports that have been made by the specified
     * player
     *
     * @param uuid UUID of the player to get reports by
     * @return List of reports that have been made by the player
     */
    @Override
    public List<ApiReport> getReportsBy( UUID uuid ) {
        List<ApiReport> toReturn = new ArrayList<>();
        for ( List<ApiReport> reports : CACHE.values() ) {
            for ( ApiReport report : reports ) {
                if ( report.getReportAuthor().equals( uuid ) ) {
                    toReturn.add( report );
                }
            }
        }
        return toReturn;
    }

    /**
     * Deletes the specified report
     *
     * @param report The report to delete
     */
    @Override
    public void deleteReport( ApiReport report ) {
        CompletableFuture.runAsync( () -> ReportsDatabase.deleteReport( report.getId() ) ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );

        for ( List<ApiReport> reports : CACHE.values() ) {
            for ( ApiReport r : reports ) {
                if ( report.getId() == r.getId() ) {
                    reports.remove( r );

                    // checking if the player this report was against now has zero reports
                    // if so, we can remove them from the cache
                    if ( CACHE.get( r.getPlayer() ).isEmpty() ) {
                        CACHE.remove( r.getPlayer() );
                    }

                    return;
                }
            }
        }
    }

    /**
     * Deletes the report with the specified ID
     *
     * @param id The ID of the report to delete
     */
    @Override
    public void deleteReport( int id ) {
        Optional<ApiReport> report = getReport( id );
        report.ifPresent( this::deleteReport );
    }

    /**
     * Gets all reports that are currently in the cache
     *
     * @return List of all reports in the cache
     */
    public List<ApiReport> getAllReports() {
        List<ApiReport> toReturn = new ArrayList<>();
        for ( List<ApiReport> reports : CACHE.values() ) {
            toReturn.addAll( reports );
        }
        return toReturn;
    }

    /**
     * @return The cache
     */
    public Map<UUID, List<ApiReport>> getCache() {
        return CACHE;
    }
    
    /**
     * Iterates through all reports and deletes the ones
     * that are expired (meaning they were made more than
     * {@link #REPORT_EXPIRE_TIME_MILLIS} milliseconds ago)
     */
    private void deleteAllExpiredReports() {
        int count = 0;
        for ( List<ApiReport> reports : CACHE.values() ) {
            for ( int i = reports.size() - 1; i >= 0; i-- ) {
                ApiReport r = reports.get( i );
                if ( TimestampUtils.timestampHasExpired( r.getReportDate(), REPORT_EXPIRE_TIME_MILLIS ) ) {
                    reports.remove( i );
                    count++;
                }
            }
        }
        CyberLogUtils.logInfo( "Successfully deleted " + count + " expired reports" );
    }
}