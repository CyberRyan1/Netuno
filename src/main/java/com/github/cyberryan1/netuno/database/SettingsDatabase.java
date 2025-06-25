package com.github.cyberryan1.netuno.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Helper class for working with the settings database table
 *
 * @author Ryan
 */
public class SettingsDatabase {

    private static final String TABLE_NAME = "settings";
    private static final String TYPE_LIST = "(number, name, data)";
    private static final String UNKNOWN_LIST = "(?, ?, ?)";

    // settings names
    //                                           replace %uuid% with the player's uuid
    public static final String NAME_SIGN_NOTIF_STATUS = "%uuid%@sign"; // sign notification status for the player with the UUID
    public static final String NAME_CHAT_SLOW = "chat-slow"; // chat slow, in seconds
    public static final String NAME_CHAT_DISABLED = "chat-disabled"; // chat disabled (true) or chat enabled (false)

    /**
     * Saves a setting to the database. If the setting already
     * exists, its data will be updated.
     *
     * @param name The name of the setting to save
     * @param data The data to save for this setting
     * @throws RuntimeException if there is an SQL error while
     *                          saving the setting
     */
    public static void saveSetting( String name, String data ) {
        String sql = "INSERT INTO " + TABLE_NAME + " " + TYPE_LIST + " VALUES " + UNKNOWN_LIST +
                " ON DUPLICATE KEY UPDATE data = ?";
        try ( PreparedStatement stmt = ConnectionManager.CONN.prepareStatement( sql ) ) {
            stmt.setInt( 1, 0 );
            stmt.setString( 2, name );
            stmt.setString( 3, data );
            stmt.setString( 4, data );
            stmt.executeUpdate();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
    
    /**
     * Retrieves a setting from the database by its name
     *
     * @param name The name of the setting to retrieve
     * @return Optional containing the setting's data if found,
     *         empty Optional if not found
     * @throws RuntimeException if there is an SQL error while
     *                          retrieving the setting
     */
    public static Optional<String> getSetting( String name ) {
        Optional<String> toReturn = Optional.empty();
        
        String sql = "SELECT data FROM " + TABLE_NAME + " WHERE name = ?";
        try ( java.sql.PreparedStatement stmt = ConnectionManager.CONN.prepareStatement( sql ) ) {
            stmt.setString( 1, name );
            java.sql.ResultSet results = stmt.executeQuery();

            if ( results.next() ) {
                toReturn = Optional.of( results.getString( "data" ) );
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return toReturn;
    }

    /**
     * Deletes a setting from the database by its name
     *
     * @param name The name of the setting to delete
     */
    public static void deleteSetting( String name ) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE name = ?";
        try ( java.sql.PreparedStatement stmt = ConnectionManager.CONN.prepareStatement( sql ) ) {
            stmt.setString( 1, name );
            stmt.executeUpdate();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}
