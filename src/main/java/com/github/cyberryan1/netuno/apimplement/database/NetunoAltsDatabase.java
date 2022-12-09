package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.AltsDatabase;
import com.github.cyberryan1.netunoapi.models.alts.NAltEntry;
import com.github.cyberryan1.netunoapi.models.alts.NAltGroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NetunoAltsDatabase implements AltsDatabase {

    private final String TABLE_NAME = "ip_history";
    private final String TYPE_LIST = "(id, uuid, ip, group_id)";
    private final String UNKNOWN_LIST = "(?, ?, ?, ?)";

    private int nextGroupId = 0;

    public void initialize() {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM random WHERE k = 'next_alt_group_id'" );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                this.nextGroupId = Integer.parseInt( rs.getString( "v" ) );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public void save() {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM random WHERE k = 'next_alt_group_id'" );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                PreparedStatement ps2 = ConnectionManager.CONN.prepareStatement( "UPDATE random SET v = ? WHERE k = 'next_alt_group_id'" );
                ps2.setString( 1, String.valueOf( this.nextGroupId ) );
                ps2.executeUpdate();
                ps2.close();
            }

            else {
                PreparedStatement ps2 = ConnectionManager.CONN.prepareStatement( "INSERT INTO random (k, v) VALUES ('next_alt_group_id', ?)" );
                ps2.setString( 1, String.valueOf( this.nextGroupId ) );
                ps2.executeUpdate();
                ps2.close();
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public List<String> queryIps( String uuid ) {
        List<String> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE uuid = ?" );
            ps.setString( 1, uuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( rs.getString( "ip" ) );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    public List<UUID> queryUuids( String ip ) {
        List<UUID> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE ip = ?" );
            ps.setString( 1, ip );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( UUID.fromString( rs.getString( "uuid" ) ) );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    public Optional<NAltGroup> queryGroup( int groupId ) {
        NAltGroup toReturn = null;

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE group_id = ?" );
            ps.setInt( 1, groupId );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                if ( toReturn == null ) {
                    toReturn = new NAltGroup( groupId );
                }

                toReturn.addEntry( new NAltEntry(
                        rs.getString( "uuid" ),
                        rs.getString( "ip" ),
                        groupId )
                );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return Optional.ofNullable( toReturn );
    }

    public List<NAltEntry> queryAllEntries() {
        List<NAltEntry> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( new NAltEntry(
                        rs.getString( "uuid" ),
                        rs.getString( "ip" ),
                        rs.getInt( "group_id" )
                ) );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    public void saveNewEntry( NAltEntry entry ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + " (uuid, ip, group_id) VALUES (?,?,?)" );
            ps.setString( 1, entry.getUuid() );
            ps.setString( 2, entry.getIp() );
            ps.setInt( 3, entry.getGroupId() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public void updateEntryGroupId( NAltEntry entry, int newGroupId ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "UPDATE " + TABLE_NAME + " SET group_id = ? WHERE uuid = ? AND ip = ?" );
            ps.setInt( 1, newGroupId );
            ps.setString( 2, entry.getUuid() );
            ps.setString( 3, entry.getIp() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public void deleteEntry( NAltEntry entry ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE uuid = ? AND ip = ? AND group_id = ?" );
            ps.setString( 1, entry.getUuid() );
            ps.setString( 2, entry.getIp() );
            ps.setInt( 3, entry.getGroupId() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public int getNextGroupId() {
        nextGroupId++;
        return nextGroupId;
    }
}