package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignNotifs {

    public static void addPlayerNoSignNotifs( String playerUuid ) {
        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConn().getConn().prepareStatement( "INSERT INTO random(k, v) VALUES(?, ?);" );
            ps.setString( 1, "no-sign-notifs" );
            ps.setString( 2, playerUuid );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void removePlayerNoSignNotifs( String playerUuid ) {
        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConn().getConn().prepareStatement( "DELETE FROM random WHERE k = ? AND v = ?;" );
            ps.setString( 1, "no-sign-notifs" );
            ps.setString( 2, playerUuid );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public static boolean playerHasNoSignNotifs( String playerUuid ) {
        boolean toReturn = false;
        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConn().getConn().prepareStatement( "SELECT * FROM random WHERE k = ? AND v = ?;" );
            ps.setString( 1, "no-sign-notifs" );
            ps.setString( 2, playerUuid );

            toReturn = ps.executeQuery().next();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
        return toReturn;
    }
}