package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.database.ReportsDatabase;
import com.github.cyberryan1.netunoapi.exceptions.ClassIncompleteException;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetunoReportsDatabase implements ReportsDatabase {

    private final String TABLE_NAME = "reports";
    private final String TYPE_LIST = "(id, player, reporter, timestamp, reason)";
    private final String UNKNOWN_LIST = "(?, ?, ?, ?, ?)";

    private long EXPIRATION_TIME_SECS = -1L;
    private int SAVE_EVERY = 50;

    private final List<NReport> cache = new ArrayList<>();
    private final List<NReport> newlyCreatedReports = new ArrayList<>();
    private final List<NReport> newlyDeletedReports = new ArrayList<>();

    /**
     * @return The cache of reports.
     */
    public List<NReport> getCache() {
        return cache;
    }

    /**
     * Initializes the cache
     */
    public void initializeCache() {
        CoreUtils.logInfo( "[REPORTS CACHE] Initializing the reports cache..." );

        CoreUtils.logInfo( "[REPORTS CACHE] Getting all reports from the database..." );
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();
            stmt.execute( "SELECT * FROM " + TABLE_NAME );

            ResultSet rs = stmt.getResultSet();
            while ( rs.next() ) {
                NReport data = new NReport(
                        rs.getInt( "id" ),
                        rs.getString( "player" ),
                        rs.getString( "reporter" ),
                        rs.getLong( "timestamp" ),
                        rs.getString( "reason" )
                );
                cache.add( ( NReport ) data );
            }

            rs.close();
            stmt.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
        CoreUtils.logInfo( "[REPORTS CACHE] Successfully retrieved all reports from the database" );

        reloadSettings();

        CoreUtils.logInfo( "[REPORTS CACHE] Reports cache successfully initialized with a size of " + cache.size() );
    }

    public void reloadSettings() {
        // Setting for how long before a report is automatically deleted
        EXPIRATION_TIME_SECS = Settings.REPORT_EXPIRE_TIME.integer() * 3600L; // converts hours to seconds
        if ( EXPIRATION_TIME_SECS > 0 ) {
            CoreUtils.logInfo( "[REPORTS CACHE] Reports expiration time set to " + Settings.REPORT_EXPIRE_TIME.integer()
                    + " hours (" + EXPIRATION_TIME_SECS + " seconds)" );
        }
        else {
            EXPIRATION_TIME_SECS = 172800L; // 48 hours
            CoreUtils.logError( "[REPORTS CACHE] The value for the setting \"reports.delete-after\" must be greater than 0" );
            CoreUtils.logError( "[REPORTS CACHE] Defaulting to expiring reports after 48 hours" );
        }

        // Setting for how many edits to the cache before the edits are saved to the database
        SAVE_EVERY = Settings.CACHE_REPORTS_SAVE_EVERY.integer();
        if ( SAVE_EVERY > 0 ) {
            CoreUtils.logInfo( "[REPORTS CACHE] New and deleted reports will be saved every " + SAVE_EVERY + " report creations/deletions" );
        }
        else {
            SAVE_EVERY = 50;
            CoreUtils.logError( "[REPORTS CACHE] The value for the setting \"database.cache.reports.save-every\" must be greater than zero" );
            CoreUtils.logError( "[REPORTS CACHE] Defaulting to saving every " + SAVE_EVERY + " report creations and deletions" );
        }
    }

    /**
     * Adds a report to the database, but not the cache
     * @param report The report to add
     */
    public void addReport( NReport report ) {
        checkReport( report, false );
        report.setId( getNextAvailableId() );
        cache.add( report );
        newlyCreatedReports.add( report );
        checkNeedsSave();
    }

    /**
     * Searches for a report in the database and in the cache
     * by the report ID
     * @param id The ID of the report to search for
     * @return The report if found, null otherwise
     */
    public NReport getReport( int id ) {
        deleteOldReports();
        return cache.stream()
                .filter( report -> report.getId() == id )
                .findFirst()
                .orElse( null );
    }

    /**
     * Searches for all reports in the database and in the cache
     * that have the given player has the player. <br>
     * @param player The player to search for
     * @return A {@link List <NReport>} of all reports for the player
     */
    public List<NReport> getReports( OfflinePlayer player ) {
        return getReports( player.getUniqueId().toString() );
    }

    /**
     * Searches for all reports in the database and in the cache
     * that have the given player UUID has the player. <br>
     * @param playerUuid The player UUID to search for
     * @return A {@link List<NReport>} of all reports for the player
     */
    public List<NReport> getReports( String playerUuid ) {
        deleteOldReports();
        return cache.stream()
                .filter( report -> report.getPlayerUuid().equals( playerUuid ) )
                .collect( Collectors.toList() );
    }

    /**
     * Deletes a report with the given id.
     * @param id The id of the report to delete.
     */
    public void deleteReport( int id ) {
        final NReport report = cache.stream()
                .filter( r -> r.getId() == id )
                .findFirst()
                .orElse( null );
        if ( report == null ) { return; }

        cache.remove( report );
        newlyDeletedReports.add( report );
        checkNeedsSave();
    }

    /**
     * Deletes all reports of a player
     * @param player The player to delete reports for
     */
    public void deleteReports( OfflinePlayer player ) {
        deleteReports( player.getUniqueId().toString() );
    }

    /**
     * Deletes all reports of a player
     * @param playerUuid The UUID of the player to delete reports for
     */
    public void deleteReports( String playerUuid ) {
        List<NReport> toRemove = cache.stream()
                .filter( report -> report.getPlayerUuid().equals( playerUuid ) )
                .collect( Collectors.toList() );
        cache.removeAll( toRemove );
        newlyDeletedReports.addAll( toRemove );
        checkNeedsSave();
    }

    /**
     * Deletes all reports that are older than the expiration time
     */
    public void deleteOldReports() {
        List<NReport> toRemove = cache.stream()
                .filter( report -> report.getTimestamp() < TimeUtils.getCurrentTimestamp() - EXPIRATION_TIME_SECS )
                .collect( Collectors.toList() );
        cache.removeAll( toRemove );
        newlyDeletedReports.addAll( toRemove );
        checkNeedsSave();
    }

    /**
     * Saves all recently created reports to the database and
     * deletes all recently deleted reports from the database.
     */
    public void saveAllReportEdits() {
        CoreUtils.logInfo( "[REPORTS CACHE] Saving all recently created and recently deleted reports to the database..." );

        for ( NReport report : newlyCreatedReports ) {
            try {
                PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + " "  +
                        TYPE_LIST + " VALUES " + UNKNOWN_LIST );
                ps.setInt( 1, report.getId() );
                ps.setString( 2, report.getPlayerUuid() );
                ps.setString( 3, report.getReporterUuid() );
                ps.setLong( 4, report.getTimestamp() );
                ps.setString( 5, report.getReason() );

                ps.addBatch();
                ps.executeBatch();
                ps.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        }
        CoreUtils.logInfo( "[REPORTS CACHE] Successfully saved " + newlyCreatedReports.size() + " reports to the database" );
        newlyCreatedReports.clear();

        for ( NReport report : newlyDeletedReports ) {
            try {
                PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE id = ?" );
                ps.setInt( 1, report.getId() );
                ps.execute();
                ps.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        }
        CoreUtils.logInfo( "[REPORTS CACHE] Successfully deleted " + newlyDeletedReports.size() + " reports from the database" );
        newlyDeletedReports.clear();
    }

    private void checkNeedsSave() {
        if ( newlyCreatedReports.size() + newlyDeletedReports.size() >= SAVE_EVERY ) {
            saveAllReportEdits();
        }
    }

    /**
     * @return The next available group id
     */
    private int getNextAvailableId() {
        deleteOldReports();
        int toReturn = cache.size() + 1;
        boolean continueWhile = true;

        while ( continueWhile ) {
            final int x = toReturn;
            if ( cache.stream().anyMatch( report -> report.getId() == x ) ) { toReturn++; }
            else { continueWhile = false; }
        }

        return toReturn;
    }

    /**
     * Checks if a report is completely filled with the correct data
     * @param report The report to check
     * @param shouldBeValidId If the punishment ID should be above 0 (true) or less than or equal to 0 (false)
     * @throws ClassIncompleteException If the report is incomplete
     */
    private void checkReport( NReport report, boolean shouldBeValidId ) {
        if ( shouldBeValidId && report.getId() <= 0 ) { throw new ClassIncompleteException( "Report incomplete: report ID must be greater than zero" ); }
        if ( shouldBeValidId == false && report.getId() > 0 ) { throw new ClassIncompleteException( "Report incomplete: report ID must be less than or equal to zero" ); }
        if ( report.getPlayerUuid() == null ) { throw new ClassIncompleteException( "Report incomplete: player UUID cannot be null" ); }
        if ( report.getReporterUuid() == null ) { throw new ClassIncompleteException( "Report incomplete: reporter UUID cannot be null" ); }
        if ( report.getTimestamp() <= 0 ) { throw new ClassIncompleteException( "Report incomplete: timestamp must be greater than zero" ); }
        if ( report.getReason() == null ) { throw new ClassIncompleteException( "Report incomplete: reason cannot be null" ); }
    }
}