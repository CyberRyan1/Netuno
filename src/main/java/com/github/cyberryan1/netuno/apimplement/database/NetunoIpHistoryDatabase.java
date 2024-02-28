package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.IpHistoryDatabase;
import com.github.cyberryan1.netunoapi.models.alts.UuidIpRecord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NetunoIpHistoryDatabase implements IpHistoryDatabase {

    public void initialize() {}

    public void shutdown() {}

    public void save( UuidIpRecord tempUuidIpEntry ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO ip(ip, uuid) VALUES(?,?);" );
            ps.setString( 1, tempUuidIpEntry.getIp() );
            ps.setString( 2, tempUuidIpEntry.getUuid().toString() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public Set<UuidIpRecord> queryByUuid( UUID uuid ) {
        Set<UuidIpRecord> entries = new HashSet<>();
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE uuid LIKE ?;" );
            ps.setString( 1, uuid.toString() );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new UuidIpRecord( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public Set<UuidIpRecord> queryByIp( String ip ) {
        Set<UuidIpRecord> entries = new HashSet<>();
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE ip LIKE ?;" );
            ps.setString( 1, ip );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new UuidIpRecord( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public Set<UuidIpRecord> queryByMultipleIps( Set<String> ipList ) {
        Set<UuidIpRecord> entries = new HashSet<>();
        String ipListStr = String.join( ",", ipList );
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE ip IN (?)" );
            ps.setString( 1, ipListStr );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new UuidIpRecord( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public void deleteEntry( UuidIpRecord tempUuidIpEntry ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM ip WHERE uuid = ? AND ip = ?;" );
            ps.setString( 1, tempUuidIpEntry.getUuid().toString() );
            ps.setString( 2, tempUuidIpEntry.getIp() );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}
