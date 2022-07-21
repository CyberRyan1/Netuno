package com.github.cyberryan1.netuno.utils.data;

import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.helpers.ANetunoPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PunishmentsCache {

    private static List<ANetunoPunishment> punishments = new ArrayList<>();
    private static int editCount = 0;

    public static void initializeCache() {
        editCount = 0;
        punishments.clear();

        // ? Going to test if we don't need to load all punishments from the database
        // ? Going to see if we can load a player's cache when they join the server instead
//        try {
//            Statement stmt = Database.getConn().createStatement();
//            stmt.execute( "SELECT * FROM " + TableNames.PUNISHMENTS );
//
//            ResultSet rs = stmt.getResultSet();
//            while ( rs.next() ) {
//                byte bytes[] = ( byte[] ) rs.getObject( "data" );
//                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
//                ObjectInputStream oip = new ObjectInputStream( bais );
//                ANetunoPunishment punishment = ( ANetunoPunishment ) oip.readObject();
//                punishment.setId( rs.getInt( "id" ) );
//                punishments.add( punishment );
//            }
//
//            stmt.close();
//            rs.close();
//        } catch ( SQLException | IOException | ClassNotFoundException e ) {
//            throw new RuntimeException( e );
//        }
    }

    public static void saveCache() {
        for ( ANetunoPunishment pun : punishments ) {
            try {
                PreparedStatement ps = Database.getConn().prepareStatement( "INSERT INTO " + TableNames.PUNISHMENTS
                        + "(id, data) VALUES(?, ?) ON DUPLICATE KEY UPDATE data=?;" );
                ps.setInt( 1, pun.getId() );
                ps.setObject( 2, pun );
                ps.setObject( 3, pun );

                ps.addBatch();
                ps.executeBatch();
                ps.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    public static void loadPlayer( String playerUuid ) {
        List<ANetunoPunishment> toAdd = new ArrayList<>();
        try {
            PreparedStatement ps = Database.getConn().prepareStatement( "SELECT * FROM " + TableNames.PUNISHMENTS + " WHERE playerUuid = ?;" );
            ps.setString( 1, playerUuid );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                byte bytes[] = ( byte[] ) rs.getObject( "data" );
                ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
                ObjectInputStream oip = new ObjectInputStream( bais );
                ANetunoPunishment punishment = ( ANetunoPunishment ) oip.readObject();
                punishment.setId( rs.getInt( "id" ) );
                toAdd.add( punishment );
            }
        } catch ( SQLException | IOException | ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        toAdd.forEach( pun -> {
            punishments.removeIf( p -> p.getId() == pun.getId() );
            punishments.add( pun );
        } );
    }

    public static void loadPlayer( OfflinePlayer player ) {
        loadPlayer( player.getUniqueId().toString() );
    }

    public static void loadOnlinePlayers() {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            loadPlayer( player.getUniqueId().toString() );
        }
    }

    public static List<ANetunoPunishment> getPunishments() {
        return punishments;
    }

    public static void addPunishment( ANetunoPunishment pun ) {
        punishments.add( pun );
        addEditCount();
    }

    public static ANetunoPunishment getPunishment( int punId ) {
        for ( ANetunoPunishment pun : punishments ) {
            if ( pun.getId() == punId ) {
                return pun;
            }
        }
        return null;
    }

    public static List<ANetunoPunishment> getPlayerPunishments( OfflinePlayer player ) {
        return getPlayerPunishments( player.getUniqueId().toString() );
    }

    public static List<ANetunoPunishment> getPlayerPunishments( String playerUuid ) {
        return punishments.stream()
                .filter( ( pun ) -> ( pun.getPlayerUuid().equals( playerUuid ) ) )
                .collect( Collectors.toList() );
    }

    public static void removePunishment( int punId ) {
        for ( ANetunoPunishment pun : punishments ) {
            if ( pun.getId() == punId ) {
                punishments.remove( pun );
                addEditCount();
                return;
            }
        }
    }

    public static void removePunishment( ANetunoPunishment pun ) {
        removePunishment( pun.getId() );
    }

    private static void addEditCount() {
        editCount++;
        if ( editCount >= Settings.CACHE_EDITS_BEFORE_SAVE.integer() ) {
            saveCache();
            editCount = 0;
        }
    }
}