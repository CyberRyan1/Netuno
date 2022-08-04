package com.github.cyberryan1.netuno.api.database;

import com.github.cyberryan1.netunoapi.database.PunishmentsDatabase;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishmentData;
import com.github.cyberryan1.netunoapi.utils.ExpiringCache;
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

public class NetunoPunishmentsDatabase implements PunishmentsDatabase {

    private final String TABLE_NAME = "punishments";
    private final String TYPE_LIST = "(id, player, data, guipun, reference)";
    private final String UNKNOWN_LIST = "(?, ?, ?, ?, ?)";

    private final ExpiringCache<NPunishment> cache = new ExpiringCache<>();

    /**
     * @return The cache of punishments.
     */
    public ExpiringCache<NPunishment> getCache() {
        return cache;
    }

    /**
     * Adds a punishment to the database, but not the cache
     * @param punishment The punishment to add
     */
    public void addPunishment( NPunishment punishment ) {
        punishment.ensureValid( false );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO " + TABLE_NAME + "(player, data, guipun, reference) VALUES(?, ?, ?, ?);" );
            ps.setString( 1, punishment.getPlayerUuid() );
            ps.setObject( 2, ( NPunishmentData ) punishment );
            ps.setString( 3, punishment.isGuiPun() + "" );
            ps.setInt( 4, punishment.getReferencePunId() );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Searches for a punishment in the database and in the cache
     * by the punishment ID.
     * @param punId The ID of the punishment to search for.
     * @return The punishment with the ID, or null if not found.
     */
    public NPunishment getPunishment( int punId ) {
        NPunishmentData data = cache.searchForOne( p -> p.getId() == punId );
        if ( data != null ) { return ( NPunishment ) data; }

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE id = ?;" );
            ps.setInt( 1, punId );

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                data = ( NPunishmentData ) ois.readObject();
                data.setId( rs.getInt( "id" ) );
                cache.add( ( NPunishment ) data );
            }

            rs.close();
            ps.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        return ( data == null ) ? ( null ) : ( ( NPunishment ) data );
    }

    /**
     * Searches for all punishments in the database and in the cache
     * that have the given player as the player. <br>
     * <i><b>Note:</b> This will search for all punishments from the
     * cache first, and if none are found, then the database will
     * be searched. If you want to search just the database, then
     * use the {@link #forceGetPunishments(OfflinePlayer)} method</i>
     * @param player The {@link OfflinePlayer} to search for.
     * @return A {@link List < NPunishment >} of all punishments for the player.
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
     * @return A {@link List< NPunishment >} of all punishments for the player.
     */
    public List<NPunishment> getPunishments( String playerUuid ) {
        List<NPunishment> toReturn = cache.searchForMany( p -> p.getPlayerUuid().equals( playerUuid ) );
        if ( toReturn.size() == 0 ) { toReturn = forceGetPunishments( playerUuid ); }
        return toReturn;
    }

    /**
     * Searches for all punishments in just the database, not in the
     * cache, that have the given player as the player. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getPunishments( OfflinePlayer )} method</i>
     * @param player The {@link OfflinePlayer} to search for.
     * @return A {@link List< NPunishment >} of all punishments for the player.
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
     * @return A {@link List< NPunishment >} of all punishments for the player.
     */
    public List<NPunishment> forceGetPunishments( String playerUuid ) {
        List<NPunishment> toReturn = new ArrayList<>();
        cache.removeAllWhere( p -> p.getPlayerUuid().equals( playerUuid ) );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE player = ?;" );
            ps.setString( 1, playerUuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                NPunishmentData data = ( NPunishmentData ) ois.readObject();
                data.setId( rs.getInt( "id" ) );
                cache.add( ( NPunishment ) data );
                toReturn.add( ( NPunishment ) data );
            }

            ps.close();
            rs.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
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
        List<NPunishment> toReturn = cache.searchForMany( pun -> pun.getReferencePunId() == referenceId );
        if ( toReturn.size() == 0 ) { toReturn = forceGetPunishmentsFromReference( referenceId ); }
        return toReturn;
    }

    /**
     * Searches for all punishments in the database, not in the
     * cache, that have the given reference ID as the reference ID. <br>
     * <i>If you want to search the cache first then the database,
     * use the {@link #getPunishmentsFromReference( int )} method</i>
     * @param referenceId The reference ID to search for
     * @return A {@link List< NPunishment >} of all punishments for the reference ID.
     */
    public List<NPunishment> forceGetPunishmentsFromReference( int referenceId ) {
        List<NPunishment> toReturn = new ArrayList<>();
        cache.removeAllWhere( pun -> pun.getReferencePunId() == referenceId );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "SELECT * FROM " + TABLE_NAME + " WHERE reference = ?;" );
            ps.setInt( 1, referenceId );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream ois = new ObjectInputStream( bais );
                NPunishmentData data = ( NPunishmentData ) ois.readObject();
                data.setId( rs.getInt( "id" ) );
                cache.add( ( NPunishment ) data );
                toReturn.add( ( NPunishment ) data );
            }

            ps.close();
            rs.close();
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
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
        cache.removeAllWhere( p -> p.getId() == newData.getId() );
        cache.add( newData );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "UPDATE " + TABLE_NAME +
                    " SET player = ?, data = ?, guipun = ?, reference = ? WHERE id = ?" );
            ps.setString( 1, newData.getPlayerUuid() );
            ps.setObject( 2, ( NPunishmentData ) newData );
            ps.setString( 3, newData.isGuiPun() + "" );
            ps.setInt( 4, newData.getReferencePunId() );
            ps.setInt( 5, newData.getId() );

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
        cache.removeAllWhere( p -> p.getId() == punId );

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
        cache.removeAllWhere( p -> p.getPlayerUuid().equals( playerUuid ) );

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
        cache.removeAllWhere( p -> p.getReferencePunId() == referenceId );

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