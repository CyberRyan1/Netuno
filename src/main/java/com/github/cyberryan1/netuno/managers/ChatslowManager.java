package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatslowManager {

    private static final String DATA_KEY = "chatslow-amount";

    private static int slow;
    private static String TABLE_NAME;

    public ChatslowManager() {
        Bukkit.getScheduler().runTaskAsynchronously( CyberCore.getPlugin(), () -> {
            try {
                PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager()
                        .getConn().prepareStatement( "INSERT INTO random(key, value) VALUES(?, ?) ON DUPLICATE KEY UPDATE key = ?;" );
                ps.setString( 1, DATA_KEY );
                ps.setString( 2, Settings.CHATSLOW_DEFAULT_VALUE.integer() + "" );
                ps.setString( 3, DATA_KEY );

                ps.addBatch();
                ps.executeBatch();
                ps.close();

                ps = ApiNetuno.getInstance().getConnectionManager()
                        .getConn().prepareStatement( "SELECT * FROM random WHERE key = ?;" );
                ps.setString( 1, DATA_KEY );

                ResultSet rs = ps.executeQuery();
                if ( rs.next() ) {
                    slow = Integer.parseInt( rs.getString( "value" ) );
                }

                ps.close();
                rs.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        } );
    }

    public static int getSlow() { return slow; }

    public static void setSlow( int newSlow ) {
        slow = newSlow;

        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager()
                    .getConn().prepareStatement( "UPDATE random SET value = ? WHERE key = ?;" );
            ps.setString( 1, slow + "" );
            ps.setString( 2, DATA_KEY );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}
