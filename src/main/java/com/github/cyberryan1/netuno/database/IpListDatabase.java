package com.github.cyberryan1.netuno.database;

import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class IpListDatabase {

    private static final String TABLE_NAME = "ip";
    private static final String TYPE_LIST = "(inde, ip, uuid)";
    private static final String UNKNOWN_LIST = "(?, ?, ?)";

    /**
     * Queries all rows from the IP List database and returns
     * them
     *
     * @return A map of each player and the IPs they have joined
     *         the server with
     */
    public static Map<UUID, List<String>> getAllEntries() {
        CyberLogUtils.logWarn( "Attempting to query all entries in the IP list database" );
        CyberLogUtils.logWarn( "Lag may occur!" );

        Map<UUID, List<String>> entries = new HashMap<>();
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME );
            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                List<String> ips = entries.getOrDefault( uuid, new ArrayList<>() );
                ips.add( rs.getString( "ip" ) );
                entries.put( uuid, ips );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        CyberLogUtils.logWarn( "Finished querying all entries from the IP list database" );
        return entries;
    }

    /**
     * Saves an entry to the database
     *
     * @param uuid The UUID of the player
     * @param ip   The IP address
     */
    public static void saveEntry( UUID uuid, String ip ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(ip, uuid) VALUES(?,?);" );
            ps.setString( 1, ip );
            ps.setString( 2, uuid.toString() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Saves multiple entries in bulk to the database
     *
     * @param entries A map of UUIDs to Strings, where the key is
     *                the player's UUID and the value is the IP
     *                address
     */
    public static void saveBulkEntries( Map<UUID, String> entries ) {
        // Important: apparently you are supposed to set rewriteBatchedStatements=true
        //      in the actual database settings
        try {
            final boolean PRIOR_AUTOCOMMIT_STATUS = ConnectionManager.CONN.getAutoCommit();
            ConnectionManager.CONN.setAutoCommit( false );
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(ip, uuid) VALUES(?,?);" );

            for ( Map.Entry<UUID, String> entry : entries.entrySet() ) {
                ps.setString( 1, entry.getKey().toString() );
                ps.setString( 2, entry.getValue() );
                ps.addBatch();
            }

            int numUpdates[] = ps.executeBatch();
            for ( int index = 0; index < numUpdates.length; index++ ) {
                if ( numUpdates[index] == PreparedStatement.SUCCESS_NO_INFO ) {
                    CyberLogUtils.logWarn( "Execution " + index + ": unknown number of rows updated" );
                }
                else {
                    CyberLogUtils.logInfo( "Execution " + index + " successful: " + numUpdates[index] + " rows updated" );
                }
            }

            ConnectionManager.CONN.commit();

            ConnectionManager.CONN.setAutoCommit( PRIOR_AUTOCOMMIT_STATUS );
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}