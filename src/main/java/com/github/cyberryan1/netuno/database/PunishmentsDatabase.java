package com.github.cyberryan1.netuno.database;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.Punishment;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentsDatabase {

    private static final String TABLE_NAME = "punishments";
    private static final String TYPE_LIST = "(id, player, staff, type, length, timestamp, reason, active, guipun, reference, notif)";
    private static final String UNKNOWN_LIST = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Adds a punishment to the database. After this is completed,
     * the punishment passed to this method is updated to have
     * the correct punishment ID
     * @param punishment The punishment to add
     */
    public static void addPunishment( Punishment punishment ) {
        punishment.ensureValid( false );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME +
                    "(player, staff, type, length, timestamp, reason, active, guipun, reference, notif) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" );

            ps.setString( 1, punishment.getPlayerUuid().toString() ); // player
            ps.setString( 2, punishment.getStaffUuid().toString() ); // staff
            ps.setInt( 3, punishment.getType().getIndex() ); // type
            ps.setLong( 4, punishment.getLength() / 1000L ); // length -- We store timestamp and length in seconds, but the class uses them in milliseconds
            ps.setLong( 5, punishment.getTimestamp() / 1000L ); // timestamp
            ps.setString( 6, punishment.getReason() ); // reason
            ps.setInt( 7, punishment.isActive() ? 1 : 0 ); // active
            ps.setInt( 8, punishment.isGuiPun() ? 1 : 0 ); // guipun
            ps.setInt( 9, punishment.getReferenceId() ); // reference
            ps.setInt( 10, punishment.isNotifSent() ? 1 : 0 ); // notif

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        punishment.setId( getRecentlyInsertedId() );
    }

    /**
     * Searches for a punishment in the database by the punishment ID.
     * @param punId The ID of the punishment to search for.
     * @return The punishment with the ID, or null if not found.
     */
    public static Punishment getPunishment( int punId ) {
        Punishment data = null;

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, punId );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                final int referencePunId = rs.getInt( "reference" );

                if ( referencePunId != ApiPunishment.DEFAULT_REFERENCE_ID ) {
                    Punishment originalPun = getPunishment( referencePunId );
                    data = ( Punishment ) originalPun.copy();
                    data.setId( rs.getInt( "id" ) );
                    data.setReferenceId( referencePunId );
                    data.setPlayer( UUID.fromString( rs.getString( "player" ) ) );
                }

                else {
                    ApiPunishment.PunType type = ApiPunishment.PunType.fromIndex( rs.getInt( "type" ) );
                    data = processResultSetIntoPunishment( rs );
                    data.setType( type );
                    data.setReferenceId( referencePunId );
//                    data = new Punishment(
//                            rs.getInt( "id" ),
//                            rs.getString( "player" ),
//                            rs.getString( "staff" ),
//                            type,
//                            rs.getLong( "timestamp" ),
//                            rs.getLong( "length" ),
//                            rs.getString( "reason" ),
//                            rs.getInt( "active" ) == 1,
//                            referencePunId,
//                            rs.getInt( "guipun" ) == 1,
//                            rs.getInt( "notif" ) == 1,
//                            true
//                    );
                }
            }

            rs.close();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return data;
    }

    /**
     * Searches for all punishments in the database that have the given
     * player as the player.
     * @param player The {@link OfflinePlayer} to search for.
     * @return A {@link List <Punishment>} of all punishments for the player.
     */
    public static List<Punishment> getPunishments( OfflinePlayer player ) {
        return getPunishments( player.getUniqueId().toString() );
    }

    /**
     * Searches for all punishments in just the database that have the
     * given player UUID as the player's UUID. <br>
     * @param playerUuid The player UUID to search for.
     * @return A {@link List<Punishment>} of all punishments for the player.
     */
    public static List<Punishment> getPunishments( String playerUuid ) {
        List<Punishment> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, playerUuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                Punishment data = null;
                final int referencePunId = rs.getInt( "reference" );

                if ( referencePunId != ApiPunishment.DEFAULT_REFERENCE_ID ) {
                    Punishment originalPun = getPunishment( referencePunId );

                    // If the original punishment is null, remove this punishment
                    //      from the database
                    if ( originalPun == null ) {
                        removePunishment( rs.getInt( "id" ) );
                    }

                    if ( originalPun != null ) {
                        data = getPunishment( rs.getInt( "id" ) );
//                        data = ( Punishment ) originalPun.copy();
//                        data.setId( rs.getInt( "id" ) );
//                        data.setReferenceId( referencePunId );
//                        data.setPlayer( UUID.fromString( rs.getString( "player" ) ) );
                    }
                }

                else {
                    data = processResultSetIntoPunishment( rs );
                    data.setReferenceId( referencePunId );
//                    data = new Punishment(
//                            rs.getInt( "id" ),
//                            rs.getString( "player" ),
//                            rs.getString( "staff" ),
//                            ApiPunishment.PunType.fromIndex( rs.getInt( "type" ) ),
//                            rs.getLong( "timestamp" ),
//                            rs.getLong( "length" ),
//                            rs.getString( "reason" ),
//                            rs.getInt( "active" ) == 1,
//                            referencePunId,
//                            rs.getInt( "guipun" ) == 1,
//                            rs.getInt( "notif" ) == 1,
//                            true
//                    );
                }

                if ( data != null ) { toReturn.add( data ); }
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Searches for all punishments in the database that have the given
     * reference ID as the reference ID. Note that the list returned by
     * this method will NOT include the original punishment
     * @param referenceId The reference ID to search for
     * @return A {@link List<Punishment>} of all punishments for the reference ID.
     */
    public static List<Punishment> getPunishmentsFromReference( int referenceId ) {
        List<Punishment> toReturn = new ArrayList<>();
        if ( referenceId < 0 ) { return toReturn; }
//        final Punishment ORIGINAL_PUN = getPunishment( referenceId );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE reference = ?;" );
            ps.setInt( 1, referenceId );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                Punishment data = processResultSetIntoPunishment( rs );
                data.setReferenceId( referenceId );

//                Punishment data = ( Punishment ) ORIGINAL_PUN.copy();
//                data.setId( rs.getInt( "id" ) );
//                data.setReferenceId( referenceId );
//                data.setPlayer( UUID.fromString( rs.getString( "player" ) ) );

                toReturn.add( data );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Updates the given punishment in the database. If the given
     * punishment is an IP punishment, this also updates the other
     * punishments that reference it
     * @param newData The updated punishment data
     */
    public static void updatePunishment( Punishment newData ) {
        newData.ensureValid( true );

        if ( newData.getType().isIpPunishment() ) {
            final List<Punishment> allReferences = getPunishmentsFromReference( newData.getReferenceId() );
            final Punishment original = getPunishment( newData.getId() );
            allReferences.add( original );

            for ( Punishment ref : allReferences ) {
                Punishment newPun = ( Punishment ) newData.copy();
                newPun.setId( ref.getId() );
                newPun.setPlayer( ref.getPlayerUuid() );
                if ( original.getId() == ref.getId() ) { newPun.setReferenceId( ApiPunishment.DEFAULT_REFERENCE_ID ); }

                updatePunishmentSingularly( newPun );
            }
        }

        else {
            updatePunishmentSingularly( newData );
        }
    }

    /**
     * Updates a singular punishment within the database
     * @param newData The updated punishment data
     */
    private static void updatePunishmentSingularly( ApiPunishment newData ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "UPDATE " + TABLE_NAME +
                    " SET player = ?, staff = ?, length = ?, timestamp = ?, reason = ?, active = ?, " +
                    "guipun = ?, reference = ?, notif = ? WHERE id = ?;" );
            ps.setString( 1, newData.getPlayerUuid().toString() );
            ps.setString( 2, newData.getStaffUuid().toString() );
            ps.setLong( 3, newData.getLength() / 1000L );
            ps.setLong( 4, newData.getTimestamp() / 1000L );
            ps.setString( 5, newData.getReason() );
            ps.setInt( 6, newData.isActive() ? 1 : 0 );
            ps.setInt( 7, newData.isGuiPun() ? 1 : 0 );
            ps.setInt( 8, newData.getReferenceId() );
            ps.setInt( 9, newData.isNotifSent() ? 1 : 0 );

            ps.setInt( 10, newData.getId() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Deletes the punishment with the given ID from
     * the database
     * @param punId The ID of the punishment to delete
     */
    public static void removePunishment( int punId ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE id = ?" );
            ps.setInt( 1, punId );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Deletes all punishments for the given player
     * from the database and cache
     * @param player The player to delete punishments for
     */
    public static void removePunishments( OfflinePlayer player ) {
        removePunishments( player.getUniqueId().toString() );
    }

    /**
     * Deletes all punishments for the given player UUID
     * from the database and cache
     * @param playerUuid The player UUID to delete punishments for
     */
    public static void removePunishments( String playerUuid ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE player = ?" );
            ps.setString( 1, playerUuid );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Deletes all punishments that have the given reference ID
     * @param referenceId The reference ID to delete punishments for
     */
    public static void removePunishmentsWithReferenceId( int referenceId ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "DELETE FROM " + TABLE_NAME + " WHERE reference = ?" );
            ps.setInt( 1, referenceId );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @return The id of the most recently added punishment
     */
    public static int getRecentlyInsertedId() {
        int toReturn = -1;
        try {
            Statement stmt = ConnectionManager.CONN.createStatement();

            if ( ConnectionManager.IS_SQL ) { stmt.execute( "SELECT ID AS lastId FROM " + TABLE_NAME + " WHERE ID = @@Identity;" ); }
            else { stmt.execute( "SELECT last_insert_rowid() AS lastId FROM " + TABLE_NAME + ";" ); }

            ResultSet rs = stmt.getResultSet();
            rs.next();
            if ( rs.isAfterLast() == false ) { toReturn = rs.getInt( "lastId" ); }
            stmt.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    private static Punishment processResultSetIntoPunishment( ResultSet rs ) throws SQLException {
        return new Punishment(
                rs.getInt( "id" ),
                rs.getString( "player" ),
                rs.getString( "staff" ),
                ApiPunishment.PunType.fromIndex( rs.getInt( "type" ) ),
                rs.getLong( "timestamp" ) * 1000L, // We store timestamp and length in seconds,
                rs.getLong( "length" ) * 1000L,             // but the class uses them in milliseconds
                rs.getString( "reason" ),
                rs.getInt( "active" ) == 1,
                rs.getInt( "reference" ),
                rs.getInt( "guipun" ) == 1,
                rs.getInt( "notif" ) == 1,
                true
        );
    }
}