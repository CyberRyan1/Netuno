package com.github.cyberryan1.netuno.database;

import com.github.cyberryan1.netuno.api.models.ApiReport;
import com.github.cyberryan1.netuno.models.NetunoReport;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper class for working with the reports database table
 *
 * @author Ryan
 */
public class ReportsDatabase {

    private static final String TABLE_NAME = "reports";
    private static final String TYPE_LIST = "(id, player, author, timestamp, reasons)";
    private static final String UNKNOWN_LIST = "(?, ?, ?, ?, ?)";

    /**
     * Adds a new report to the database
     *
     * @param report The report to add to the database
     */
    public static void addReport( NetunoReport report ) {
        report.ensureValid( false );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME +
                    "(player, author, timestamp, reasons) " +
                    "VALUES(?, ?, ?, ?)" );

            ps.setString( 1, report.getPlayer().toString() );
            ps.setString( 2, report.getReportAuthor().toString() );
            ps.setLong( 3, toDatabaseTimestamp( report.getReportDate() ) );
            ps.setString( 4, report.getReasonsString() );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        report.setId( getRecentlyInsertedId() );
    }

    /**
     * Gets a report from the database by its ID
     *
     * @param id The ID of the report to get
     * @return Optional containing the report if found, empty
     *         otherwise
     */
    public static Optional<ApiReport> getReport( int id ) {
        NetunoReport toReturn = null;

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, id );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                toReturn = processResultSetIntoReport( rs );
            }

            rs.close();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return Optional.ofNullable( toReturn );
    }

    /**
     * Gets all reports made against a specific player
     *
     * @param target UUID of the player to get reports against
     * @return List of all reports made against the specified
     *         player
     */
    public static List<ApiReport> getReportsAgainst( UUID target ) {
        List<ApiReport> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, target.toString() );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( processResultSetIntoReport( rs ) );
            }

            rs.close();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Gets all reports made against a specific player
     *
     * @param target Player to get reports against
     * @return List of all reports made against the specified
     *         player
     */
    public static List<ApiReport> getReportsAgainst( OfflinePlayer target ) {
        return getReportsAgainst( target.getUniqueId() );
    }

    /**
     * Gets all reports made by a specific player
     *
     * @param author UUID of the player who made the reports
     * @return List of all reports made by the specified player
     */
    public static List<ApiReport> getReportsBy( UUID author ) {
        List<ApiReport> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE author = ?;" );
            ps.setString( 1, author.toString() );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( processResultSetIntoReport( rs ) );
            }

            rs.close();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Gets all reports made by a specific player
     *
     * @param author Player who made the reports
     * @return List of all reports made by the specified player
     */
    public static List<ApiReport> getReportsBy( OfflinePlayer author ) {
        return getReportsBy( author.getUniqueId() );
    }

    /**
     * Deletes a report from the database
     *
     * @param id The ID of the report to delete
     */
    public static void deleteReport( int id ) {
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
     * Deletes all reports made against a specific player
     *
     * @param target UUID of the player whose reports should be
     *               deleted
     */
    public static void deleteReportsAgainst( UUID target ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, target.toString() );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @return The id of the most recently added punishment
     */
    private static int getRecentlyInsertedId() {
        int toReturn = -1;
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();

            if ( ConnectionManager.IS_SQL ) {
                stmt.execute( "SELECT ID AS lastId FROM " + TABLE_NAME + " WHERE ID = @@Identity;" );
            }
            else {
                stmt.execute( "SELECT last_insert_rowid() AS lastId FROM " + TABLE_NAME + ";" );
            }

            ResultSet rs = stmt.getResultSet();
            rs.next();
            if ( rs.isAfterLast() == false ) {
                toReturn = rs.getInt( "lastId" );
            }
            stmt.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    private static NetunoReport processResultSetIntoReport( ResultSet rs ) throws SQLException {
        return new NetunoReport(
                rs.getInt( "id" ),
                UUID.fromString( rs.getString( "player" ) ),
                rs.getString( "reasons" ), // NetunoReport constructor can split the reasons list automatically
                UUID.fromString( rs.getString( "author" ) ),
                fromDatabaseTimestamp( rs.getLong( "timestamp" ) )
        );
    }

    /**
     * Converts a timestamp from milliseconds to seconds for
     * database storage. The database stores timestamps in
     * seconds while the {@link NetunoReport} class uses
     * milliseconds.
     *
     * @param milliseconds The timestamp in milliseconds from the
     *                     {@link NetunoReport} class
     * @return The timestamp converted to seconds for database
     *         storage
     */
    private static long toDatabaseTimestamp( long milliseconds ) {
        return milliseconds / 1000L;
    }

    /**
     * Converts a timestamp from seconds to milliseconds for use
     * in {@link NetunoReport}. The database stores timestamps in
     * seconds while the {@link NetunoReport} class uses
     * milliseconds.
     *
     * @param seconds The timestamp in seconds from the database
     * @return The timestamp converted to milliseconds for use in
     *         {@link NetunoReport} class
     */
    private static long fromDatabaseTimestamp( long seconds ) {
        return seconds * 1000L;
    }
}