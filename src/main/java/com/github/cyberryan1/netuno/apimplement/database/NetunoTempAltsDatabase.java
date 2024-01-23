package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.TempAltsDatabase;
import com.github.cyberryan1.netunoapi.models.alts.TempUuidIpEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NetunoTempAltsDatabase implements TempAltsDatabase {

    public void initialize() {}

    public void shutdown() {}

    public void save( TempUuidIpEntry tempUuidIpEntry ) {
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

    public Set<TempUuidIpEntry> queryByUuid( UUID uuid ) {
        Set<TempUuidIpEntry> entries = new HashSet<>();
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE uuid LIKE ?;" );
            ps.setString( 1, uuid.toString() );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new TempUuidIpEntry( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public Set<TempUuidIpEntry> queryByIp( String ip ) {
        Set<TempUuidIpEntry> entries = new HashSet<>();
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE ip LIKE ?;" );
            ps.setString( 1, ip );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new TempUuidIpEntry( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public Set<TempUuidIpEntry> queryByMultipleIps( Set<String> ipList ) {
        Set<TempUuidIpEntry> entries = new HashSet<>();
        String ipListStr = String.join( ",", ipList );
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip WHERE ip IN (?)" );
            ps.setString( 1, ipListStr );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                entries.add( new TempUuidIpEntry( UUID.fromString( rs.getString( "uuid" ) ),
                        rs.getString( "ip" ),
                        true ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return entries;
    }

    public void deleteEntry( TempUuidIpEntry tempUuidIpEntry ) {
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

//    public void initialize() {} // I don't think there is anything to do here
//
//    public void shutdown() {
//        // TODO
//    }

//    public void save( TempIpEntry tempAltEntry ) {
//        String uuidList = "";
//        for ( UUID u : tempAltEntry.getUuids() ) {
//            uuidList += u.toString() + ",";
//        }
//        uuidList = uuidList.substring( 0, uuidList.length() - 1 );
//
//        try {
//            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO ip_uuids_list(ip, uuid_list)" +
//                    " VALUES(?,?) ON DUPLICATE KEY UPDATE uuid_list = ?;" );
//            ps.setString( 1, tempAltEntry.getIp() );
//            ps.setString( 2, uuidList );
//            ps.setString( 3, uuidList );
//        } catch ( SQLException e ) {
//            throw new RuntimeException( e );
//        }
//
//    }
//
//    public Set<TempIpEntry> queryByUuid( UUID uuid ) {
//        Set<TempIpEntry> entries = new HashSet<>();
//        try {
//            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip_uuids_list WHERE " +
//                    "uuid_list LIKE ?;" );
//            ps.setString( 1, uuid.toString() );
//
//            ResultSet rs = ps.executeQuery();
//            while ( rs.next() ) {
//                TempIpEntry entry = new TempIpEntry( rs.getString( "ip" ), true );
//                final String uuidStrList = rs.getString( "uuid_list" );
//                for ( String str : uuidStrList.split( "," ) ) {
//                    entry.getUuids().add( UUID.fromString( str ) );
//                }
//
//                entries.add( entry );
//            }
//        } catch ( SQLException e ) {
//            throw new RuntimeException( e );
//        }
//
//        return entries;
//    }
//
//    public Optional<TempIpEntry> queryByIp( String ip ) {
//        Optional<TempIpEntry> toReturn = Optional.empty();
//        try {
//            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM ip_uuids_list WHERE " +
//                    "ip LIKE ?;" );
//            ps.setString( 1, ip );
//
//            ResultSet rs = ps.executeQuery();
//            if ( rs.next() ) {
//                TempIpEntry entry = new TempIpEntry( ip, true );
//                for ( String str : rs.getString( "uuid_list" ).split( "," ) ) {
//                    entry.getUuids().add( UUID.fromString( str ) );
//                }
//                toReturn = Optional.of( entry );
//            }
//        } catch ( SQLException e ) {
//            throw new RuntimeException( e );
//        }
//
//        return toReturn;
//    }
//
//    public void deleteEntry( TempIpEntry tempAltEntry ) {
//        // TODO
//    }
}
