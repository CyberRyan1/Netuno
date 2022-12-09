package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netunoapi.database.PunishmentsDatabase;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetunoPunishmentsDatabase implements PunishmentsDatabase {

    private final String TABLE_NAME = "punishments";
    private final String TYPE_LIST = "(id, player, staff, type, length, timestamp, reason, active, guipun, reference, notif)";
    private final String UNKNOWN_LIST = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Adds a punishment to the database, but not the cache
     * @param punishment The punishment to add
     */
    public void addPunishment( NPunishment punishment ) {
        punishment.ensureValid( false );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME +
                    "(player, staff, type, length, timestamp, reason, active, guipun, reference, notif) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" );

            ps.setString( 1, punishment.getPlayerUuid() ); // player
            ps.setString( 2, punishment.getStaffUuid() ); // staff
            ps.setInt( 3, punishment.getPunishmentType().getIndex() ); // type
            ps.setLong( 4, punishment.getLength() ); // length
            ps.setLong( 5, punishment.getTimestamp() ); // timestamp
            ps.setString( 6, punishment.getReason() ); // reason
            ps.setInt( 7, punishment.isActive() ? 1 : 0 ); // active
            ps.setInt( 8, punishment.isGuiPun() ? 1 : 0 ); // guipun
            ps.setInt( 9, punishment.getReferencePunId() ); // reference
            ps.setInt( 10, punishment.needsNotifSent() ? 1 : 0 ); // notif

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        NetunoPlayerCache.loadRecentlyAddedPunishment( punishment.getPlayerUuid() );
    }

    /**
     * Searches for a punishment in the database and in the cache
     * by the punishment ID.
     * @param punId The ID of the punishment to search for.
     * @return The punishment with the ID, or null if not found.
     */
    public NPunishment getPunishment( int punId ) {
        NPunishment data = NetunoPlayerCache.getCachedPunishments().stream()
                .filter( pun -> pun.getId() == punId )
                .findFirst()
                .orElse( null );
        if ( data != null ) { return data; }

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, punId );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                final int referencePunId = rs.getInt( "reference" );

                if ( referencePunId > 0 ) {
                    NPunishment originalPun = getPunishment( referencePunId );
                    data = originalPun.copy();
                    data.setId( rs.getInt( "id" ) );
                    data.setReferencePunId( referencePunId );
                    data.setPlayerUuid( rs.getString( "player" ) );
                }

                else {
                    PunishmentType type = PunishmentType.fromIndex( rs.getInt( "type" ) );
                    data = new NPunishment(
                        rs.getInt( "id" ),
                            type,
                        rs.getString( "player" ),
                        rs.getString( "staff" ),
                        rs.getLong( "length" ),
                        rs.getLong( "timestamp" ),
                        rs.getString( "reason" ),
                        rs.getInt( "active" ) == 1,
                        rs.getInt( "guipun" ) == 1,
                        referencePunId,
                        rs.getInt( "notif" ) == 1
                    );
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
     * Searches for all punishments in the database and in the cache
     * that have the given player as the player. <br>
     * <i><b>Note:</b> This will search for all punishments from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetPunishments(OfflinePlayer)} method</i>
     * @param player The {@link OfflinePlayer} to search for.
     * @return A {@link List <NPunishment>} of all punishments for the player.
     */
    public List<NPunishment> getPunishments( OfflinePlayer player ) {
        return getPunishments( player.getUniqueId().toString() );
    }

    /**
     * Searches for all punishments in the database and in the cache
     * that have the given player UUID as the player's UUID. <br>
     * <i><b>Note:</b> This will search for all punishments from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetPunishments( String )} method</i>
     * @param playerUuid The player UUID to search for.
     * @return A {@link List<NPunishment>} of all punishments for the player.
     */
    public List<NPunishment> getPunishments( String playerUuid ) {
        List<NPunishment> toReturn = NetunoPlayerCache.getOrLoad( playerUuid ).getPunishments();
        if ( toReturn.size() == 0 ) { toReturn = forceGetPunishments( playerUuid ); }
        return toReturn;
    }

    /**
     * Searches for all punishments in just the database, not in the
     * cache, that have the given player as the player. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getPunishments( OfflinePlayer )} method</i>
     * @param player The {@link OfflinePlayer} to search for.
     * @return A {@link List<NPunishment>} of all punishments for the player.
     */
    public List<NPunishment> forceGetPunishments( OfflinePlayer player ) {
        return forceGetPunishments( player.getUniqueId().toString() );
    }

    /**
     * Searches for all punishments in just the database, not in the
     * cache, that have the given player UUID as the player's UUID. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getPunishments( String )} method</i>
     * @param playerUuid The player UUID to search for.
     * @return A {@link List<NPunishment>} of all punishments for the player.
     */
    public List<NPunishment> forceGetPunishments( String playerUuid ) {
        List<NPunishment> toReturn = new ArrayList<>();

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, playerUuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                NPunishment data;
                final int referencePunId = rs.getInt( "reference" );

                if ( referencePunId > 0 ) {
                    NPunishment originalPun = getPunishment( referencePunId );
                    data = originalPun.copy();
                    data.setId( rs.getInt( "id" ) );
                    data.setReferencePunId( referencePunId );
                    data.setPlayerUuid( rs.getString( "player" ) );
                }

                else {
                    data = new NPunishment(
                            rs.getInt( "id" ),
                            PunishmentType.fromIndex( rs.getInt( "type" ) ),
                            rs.getString( "player" ),
                            rs.getString( "staff" ),
                            rs.getLong( "length" ),
                            rs.getLong( "timestamp" ),
                            rs.getString( "reason" ),
                            rs.getInt( "active" ) == 1,
                            rs.getInt( "guipun" ) == 1,
                            referencePunId,
                            rs.getInt( "notif" ) == 1
                    );
                }

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
     * Searches for all punishments in the database and in the cache
     * that have the given reference ID as the reference ID. <br>
     * <i><b>Note:</b> This will search for all punishments from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetPunishmentsFromReference( int )} method</i>
     * @param referenceId The reference ID to search for
     * @return A {@link List<NPunishment>} of all punishments for the reference ID.
     */
    public List<NPunishment> getPunishmentsFromReference( int referenceId ) {
        List<NPunishment> toReturn = NetunoPlayerCache.getCachedPunishments().stream()
                .filter( pun -> pun.getReferencePunId() == referenceId )
                .collect( Collectors.toList() );
        if ( toReturn.size() == 0 ) { toReturn = forceGetPunishmentsFromReference( referenceId ); }
        return toReturn;
    }

    /**
     * Searches for all punishments in the database, not in the
     * cache, that have the given reference ID as the reference ID. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getPunishmentsFromReference( int )} method</i>
     * @param referenceId The reference ID to search for
     * @return A {@link List<NPunishment>} of all punishments for the reference ID.
     */
    public List<NPunishment> forceGetPunishmentsFromReference( int referenceId ) {
        List<NPunishment> toReturn = new ArrayList<>();
        if ( referenceId < 0 ) { return toReturn; }
        final NPunishment ORIGINAL_PUN = getPunishment( referenceId );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE reference = ?;" );
            ps.setInt( 1, referenceId );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                NPunishment data = ORIGINAL_PUN.copy();
                data.setId( rs.getInt( "id" ) );
                data.setReferencePunId( referenceId );
                data.setPlayerUuid( rs.getString( "player" ) );

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
     * Updates the given punishment in the database and cache
     * @param newData The updated punishment data
     */
    public void updatePunishment( NPunishment newData ) {
        newData.ensureValid( true );

        if ( newData.getPunishmentType().isIpPunishment() ) {
            final List<NPunishment> allReferences = forceGetPunishmentsFromReference( newData.getReferencePunId() );
            final NPunishment original = getPunishment( newData.getId() );
            allReferences.add( original );

            for ( NPunishment ref : allReferences ) {
                NPunishment newPun = newData.copy();
                newPun.setId( ref.getId() );
                newPun.setPlayerUuid( ref.getPlayerUuid() );
                if ( original.getId() == ref.getId() ) { newPun.setReferencePunId( -1 ); }

                NetunoPlayerCache.getOrLoad( newPun.getPlayerUuid() ).addPunishment( newPun );
                executePunishmentDatabaseUpdate( newPun );
            }
        }

        else {
            NetunoPlayerCache.getOrLoad( newData.getPlayerUuid() ).addPunishment( newData );
            executePunishmentDatabaseUpdate( newData );
        }
    }

    private void executePunishmentDatabaseUpdate( NPunishment newData ) {
        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "UPDATE " + TABLE_NAME +
                    " SET player = ?, staff = ?, length = ?, timestamp = ?, reason = ?, active = ?, " +
                    "guipun = ?, reference = ?, notif = ? WHERE id = ?;" );
            ps.setString( 1, newData.getPlayerUuid() );
            ps.setString( 2, newData.getStaffUuid() );
            ps.setLong( 3, newData.getLength() );
            ps.setLong( 4, newData.getTimestamp() );
            ps.setString( 5, newData.getReason() );
            ps.setInt( 6, newData.isActive() ? 1 : 0 );
            ps.setInt( 7, newData.isGuiPun() ? 1 : 0 );
            ps.setInt( 8, newData.getReferencePunId() );
            ps.setInt( 9, newData.needsNotifSent() ? 1 : 0 );

            ps.setInt( 10, newData.getId() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Deletes the punishment with the given ID from
     * the database and cache
     * @param punId The ID of the punishment to delete
     */
    public void removePunishment( int punId ) {
        NPunishment storedPun = NetunoPlayerCache.getCachedPunishments().stream()
                .filter( pun -> pun.getId() == punId )
                .findFirst()
                .orElse( null );
        NetunoPlayerCache.getOrLoad( storedPun.getPlayerUuid() ).getPunishments().remove( storedPun );

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
    public void removePunishments( OfflinePlayer player ) {
        removePunishments( player.getUniqueId().toString() );
    }

    /**
     * Deletes all punishments for the given player UUID
     * from the database and cache
     * @param playerUuid The player UUID to delete punishments for
     */
    public void removePunishments( String playerUuid ) {
        NetunoPlayerCache.getOrLoad( playerUuid ).getPunishments().clear();

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
    public void removePunishments( int referenceId ) {
        NetunoPlayerCache.getCachedPunishments().stream()
                .filter( pun -> pun.getReferencePunId() == referenceId )
                .forEach( pun -> {
                    NetunoPlayerCache.getOrLoad( pun.getPlayerUuid() ).getPunishments().remove( pun );
                } );

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
    public int getRecentlyInsertedId() {
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
}