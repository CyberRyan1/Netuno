package com.github.cyberryan1.netuno.api.database;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.database.ReportsDatabase;
import com.github.cyberryan1.netunoapi.exceptions.ClassIncompleteException;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.models.reports.NReportData;
import com.github.cyberryan1.netunoapi.models.time.NDate;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetunoReportsDatabase implements ReportsDatabase {

    private final String TABLE_NAME = "reports";
    private final String TYPE_LIST = "(id, player, data)";
    private final String UNKNOWN_LIST = "(?, ?, ?)";

    private long EXPIRATION_TIME_SECS = -1L;

    private final List<NReport> cache = new ArrayList<>();

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
        CoreUtils.logInfo( "Initializing the reports cache..." );

        CoreUtils.logInfo( "Getting all reports from the database..." );
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();
            stmt.execute( "SELECT * FROM " + TABLE_NAME );

            ResultSet rs = stmt.getResultSet();
            while ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                NReportData data = ( NReportData ) ois.readObject();

                data.setId( rs.getInt( "id" ) );
                cache.add( ( NReport ) data );
            }

            rs.close();
            stmt.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
        CoreUtils.logInfo( "Successfully retrieved all reports from the database" );

        EXPIRATION_TIME_SECS = Settings.CACHE_EXPIRATION.integer() * 3600L; // converts hours to seconds
        CoreUtils.logInfo( "Reports expiration time set to " + Settings.CACHE_EXPIRATION.integer()
                + " hours (" + EXPIRATION_TIME_SECS + " seconds)" );

        CoreUtils.logInfo( "Reports cache successfully initialized with a size of " + cache.size() );
    }

    /**
     * Adds a report to the database, but not the cache
     * @param report The report to add
     */
    public void addReport( NReport report ) {
        checkReport( report, false );
        report.setId( getNextAvailableId() );
        cache.add( report );
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
        cache.removeIf( report -> report.getId() == id );
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
        cache.removeIf( report -> report.getPlayerUuid().equals( playerUuid ) );
    }

    /**
     * Deletes all reports that are older than the expiration time
     */
    public void deleteOldReports() {
        cache.removeIf( report -> report.getTimestamp() < NDate.getCurrentTimestamp() - EXPIRATION_TIME_SECS );
    }

    /**
     * Deletes all reports from the database, but not the cache.
     */
    public void deleteAllReports() {
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();
            stmt.execute( "DELETE FROM " + TABLE_NAME + ";" );
            stmt.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Saves all elements from the cache into the database. Note that
     * first this method deletes all entries from the database, and then
     * it inserts all elements from the cache into the database.
     */
    public void saveAll() {
        CoreUtils.logInfo( "Saving all reports in the cache to the database..." );
        deleteAllReports();

        for ( NReport report : cache ) {
            try {
                PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + " (id, player, data) VALUES (?, ?, ?);" );
                ps.setInt( 1, report.getId() );
                ps.setString( 2, report.getPlayerUuid() );
                ps.setObject( 3, ( NReportData ) report );

                ps.addBatch();
                ps.executeBatch();
                ps.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        }

        CoreUtils.logInfo( "Successfully saved " + cache.size() + " reports to the database." );
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