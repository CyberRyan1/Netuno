package com.github.cyberryan1.netuno.services;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import com.github.cyberryan1.netuno.api.services.ApiReportService;
import com.github.cyberryan1.netuno.database.ReportsDatabase;
import com.github.cyberryan1.netuno.models.helpers.PlayerLoginLogoutCache;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    private final PlayerLoginLogoutCache<List<ApiReport>> REPORT_CACHE = new PlayerLoginLogoutCache<>();

    public static long REPORT_EXPIRE_TIME_MILLIS;

    public ReportService() {}

    /**
     * Initializes the report service
     */
    public void initialize() {
        updateReportExpireTimeMillis();

        this.REPORT_CACHE.setLoginScript( event -> Optional.of( ReportsDatabase.getReportsAgainst( event.getUniqueId() ) ) );
        this.REPORT_CACHE.setDataValidityScript( uuid -> {
            // in this, we are going to iterate through all of the reports of the provided player and delete any that are expired
            // a report is expired if it was created more than ReportService.REPORT_EXPIRE_TIME_MILLIS milliseconds ago
            this.REPORT_CACHE.getDataSilently( uuid ).ifPresent( reports -> {
                for ( int index = reports.size() - 1; index >= 0; index-- ) {
                    ApiReport report = reports.get( index );
                    if ( TimestampUtils.timestampHasExpired( report.getReportDate(), ReportService.REPORT_EXPIRE_TIME_MILLIS ) ) {
                        ReportsDatabase.deleteReport( report.getId() );
                        reports.remove( index );
                    }
                }
            } );

            // we are only using this for the above, we will always return true
            return true;
        } );

        // Loading all online players
        for ( Player p : Bukkit.getOnlinePlayers() ) {
            getReportsAgainst( p );
        }
    }

    /**
     * Updates the {@link #REPORT_EXPIRE_TIME_MILLIS} to match
     * the value given by {@link Settings#REPORT_EXPIRE_TIME_HOURS}
     */
    public void updateReportExpireTimeMillis() {
        // note: Settings.REPORT_EXPIRE_TIME is given in hours
        REPORT_EXPIRE_TIME_MILLIS = 1000L * 60L * 60L * Settings.REPORT_EXPIRE_TIME_HOURS.integer();
                                //                60 minutes per hour
                                //          60 seconds per minute
                                //  1000ms per second
    }

    /**
     * Gets a report by its unique identifier. Any results are
     * <b>not</b> added to the cache. <br>
     * Searches through a cache of reports first. If nothing is
     * found, then queries the database.
     *
     * @param id The ID of the report to get
     * @return The report with the specified ID
     */
    @Override
    public CompletableFuture<Optional<ApiReport>> getReport( int id ) {
        for ( ApiReport report : getAllCachedReports() ) {
            if ( report.getId() == id ) {
                return CompletableFuture.completedFuture( Optional.of( report ) );
            }
        }
        return CompletableFuture.supplyAsync( () -> ReportsDatabase.getReport( id ) );
    }

    /**
     * Gets all reports that have been made against the specified
     * player and adds their data to the cache, as needed. <br>
     * Searches through a cache of reports first. If nothing is
     * found, then queries the database.
     *
     * @param player The player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    @Override
    public CompletableFuture<List<ApiReport>> getReportsAgainst( OfflinePlayer player ) {
        return getReportsAgainst( player.getUniqueId() );
    }

    /**
     * Gets all reports that have been made against the specified
     * player and adds their data to the cache, if needed. <br>
     * Searches through a cache of reports first. If nothing is
     * found, then queries the database.
     *
     * @param uuid UUID of the player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    @Override
    public CompletableFuture<List<ApiReport>> getReportsAgainst( UUID uuid ) {
        if ( this.REPORT_CACHE.containsPlayer( uuid ) ) {
            return CompletableFuture.completedFuture( this.REPORT_CACHE.getData( uuid ).get() ); // since their uuid is in the cache, this will never be null
        }

        return CompletableFuture.supplyAsync( () -> {
            List<ApiReport> toReturn = ReportsDatabase.getReportsAgainst( uuid );
            // from above check, we know the player is not in the cache
            // therefore, we add their data as inactive
            this.REPORT_CACHE.insertInactiveData( uuid, toReturn );
            return toReturn;
        } );
    }

    /**
     * Gets all reports that have been made by the specified
     * player. Any results are <b>not</b> added to the cache. <br>
     * This <b>always</b> queries the database, as not all
     * reports authored by the provided player may not be in
     * the cache at once.
     *
     * @param player The player to get reports by
     * @return List of reports that have been made by the player
     */
    @Override
    public CompletableFuture<List<ApiReport>> getReportsBy( OfflinePlayer player ) {
        return getReportsBy( player.getUniqueId() );
    }

    /**
     * Gets all reports that have been made by the specified
     * player. Any results are <b>not</b> added to the cache. <br>
     * This <b>always</b> queries the database, as not all
     * reports authored by the provided player may not be in
     * the cache at once.
     *
     * @param uuid UUID of the player to get reports by
     * @return List of reports that have been made by the player
     */
    @Override
    public CompletableFuture<List<ApiReport>> getReportsBy( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> ReportsDatabase.getReportsBy( uuid ) );
    }

    /**
     * Deletes the specified report from the database and the
     * cache.
     *
     * @param report The report to delete
     */
    @Override
    public void deleteReport( ApiReport report ) {
        this.REPORT_CACHE.getDataSilently( report.getPlayer() ).ifPresent( reports -> {
            for ( ApiReport r : reports ) {
                if ( report.getId() == r.getId() ) {
                    reports.remove( r );
                    return;
                }
            }
        } );

        CompletableFuture.runAsync( () -> ReportsDatabase.deleteReport( report.getId() ) ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    /**
     * Deletes the report with the specified ID from the database
     * and the cache.
     *
     * @param id The ID of the report to delete
     */
    @Override
    public void deleteReport( int id ) {
        getReport( id ).thenAccept( report -> {
            report.ifPresent( this::deleteReport );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    /**
     * @return A list of {@link ApiReport} of all the reports
     *         that are cached. Does NOT refresh the last access
     *         timestamp for each of the returned reports
     */
    public List<ApiReport> getAllCachedReports() {
        List<ApiReport> toReturn = new ArrayList<>();
        for ( UUID uuid : this.REPORT_CACHE.getKeySet() ) {
            toReturn.addAll( this.REPORT_CACHE.getDataSilently( uuid ).get() ); // since the uuid is in the cache, this will never be null
        }

        return toReturn;
    }
}