package com.github.cyberryan1.netuno.api.database;

import com.github.cyberryan1.netunoapi.database.ReportsDatabase;
import com.github.cyberryan1.netunoapi.exceptions.ClassIncompleteException;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.models.reports.NReportData;
import com.github.cyberryan1.netunoapi.utils.ExpiringCache;
import org.bukkit.OfflinePlayer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NetunoReportsDatabase implements ReportsDatabase {

    private final String TABLE_NAME = "reports";
    private final String TYPE_LIST = "(id, player, data)";
    private final String UNKNOWN_LIST = "(?, ?, ?)";

    private final ExpiringCache<NReport> cache = new ExpiringCache<>();

    /**
     * @return The cache of reports.
     */
    public ExpiringCache<NReport> getCache() {
        return cache;
    }

    /**
     * Adds a report to the database, but not the cache
     * @param report The report to add
     */
    public void addReport( NReport report ) {
        checkReport( report, false );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(player, data) VALUES(?, ?);" );
            ps.setString( 1, report.getPlayerUuid() );
            ps.setObject( 2, ( NReportData ) report );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Searches for a report in the database and in the cache
     * by the report ID
     * @param id The ID of the report to search for
     * @return The report if found, null otherwise
     */
    public NReport getReport( int id ) {
        NReportData data = cache.searchForOne( r -> r.getId() == id );
        if ( data != null ) { return ( NReport ) data; }

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, id );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                data = ( NReportData ) ois.readObject();
                data.setId( rs.getInt( "id" ) );
                cache.add( ( NReport ) data );
            }

            ps.close();
            rs.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        return ( data == null ) ? ( null ) : ( ( NReport ) data );
    }

    /**
     * Searches for all reports in the database and in the cache
     * that have the given player has the player. <br>
     * <i><b>Note:</b> This will search for all reports from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetReports(OfflinePlayer)} method</i>
     * @param player The player to search for
     * @return A {@link List < NReport >} of all reports for the player
     */
    public List<NReport> getReports( OfflinePlayer player ) {
        return getReports( player.getUniqueId().toString() );
    }

    /**
     * Searches for all reports in the database and in the cache
     * that have the given player UUID has the player. <br>
     * <i><b>Note:</b> This will search for all reports from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetReports( String )} method</i>
     * @param playerUuid The player UUID to search for
     * @return A {@link List< NReport >} of all reports for the player
     */
    public List<NReport> getReports( String playerUuid ) {
        List<NReport> toReturn = cache.searchForMany( r -> r.getPlayerUuid().equals( playerUuid ) );
        if ( toReturn.size() == 0 ) { toReturn = forceGetReports( playerUuid ); }
        return toReturn;
    }

    /**
     * Searches for all reports in just the database, not in the
     * cache, that have the given player as the player. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getReports( String )} method</i>
     * @param player The {@link OfflinePlayer} to search for
     * @return A {@link List< NReport >} of all reports for the player
     */
    public List<NReport> forceGetReports( OfflinePlayer player ) {
        return forceGetReports( player.getUniqueId().toString() );
    }

    /**
     * Searches for all reports in just the database, not in the
     * cache, that have the given player UUID as the player's UUID. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getReports( String )} method</i>
     * @param playerUuid The player UUID to search for
     * @return A {@link List< NReport >} of all reports for the player
     */
    public List<NReport> forceGetReports( String playerUuid ) {
        List<NReport> toReturn = new ArrayList<>();
        cache.removeAllWhere( r -> r.getPlayerUuid().equals( playerUuid ) );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, playerUuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                NReportData data = ( NReportData ) ois.readObject();
                data.setId( rs.getInt( "id" ) );
                toReturn.add( ( NReport ) data );
                cache.add( ( NReport ) data );
            }

            ps.close();
            rs.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Deletes a report with the given id.
     * @param id The id of the report to delete.
     */
    public void deleteReport( int id ) {
        cache.removeAllWhere( r -> r.getId() == id );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, id );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
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
        cache.removeAllWhere( r -> r.getPlayerUuid().equals( playerUuid ) );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, playerUuid );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
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