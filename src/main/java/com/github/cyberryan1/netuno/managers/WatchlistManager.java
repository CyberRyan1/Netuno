package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WatchlistManager {
    private static final String WORDS_LIST_KEY = "watchlist_words_list";
    private static final String PATTERNS_LIST_KEY = "watchlist_patterns_list";
    private static final String DELIMITER = " ";

    private static final List<String> wordsList = new ArrayList<>();
    private static final List<String> patternList = new ArrayList<>();

    public static void initialize() {
        Bukkit.getScheduler().runTaskAsynchronously( CyberCore.getPlugin(), () -> {
            try {
                PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager()
                        .getConn().prepareStatement( "SELECT * FROM random WHERE k = ?;" );
                ps.setString( 1, WORDS_LIST_KEY );

                ResultSet rs = ps.executeQuery();
                if ( rs.next() ) {
                    wordsList.addAll( List.of( rs.getString( "v" ).split( DELIMITER ) ) );
                }
                else { firstTimeInit(); }

                ps.close();
                rs.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }

            try {
                PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager()
                        .getConn().prepareStatement( "SELECT * FROM random WHERE k = ?;" );
                ps.setString( 1, PATTERNS_LIST_KEY );

                ResultSet rs = ps.executeQuery();
                if ( rs.next() ) {
                    patternList.addAll( List.of( rs.getString( "v" ).split( DELIMITER ) ) );
                }

                ps.close();
                rs.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }

            wordsList.removeIf( word -> word.isEmpty() || word.isBlank() );
            patternList.removeIf( pattern -> pattern.isEmpty() || pattern.isBlank() );
        } );
    }

    private static void firstTimeInit() {
        try {
            PreparedStatement ps1 = ApiNetuno.getInstance().getConn().getConn().prepareStatement( "INSERT INTO random (k, v) VALUES(?, ?);" );
            ps1.setString( 1, WORDS_LIST_KEY );
            ps1.setString( 2, "" );

            ps1.addBatch();
            ps1.executeBatch();
            ps1.close();

            PreparedStatement ps2 = ApiNetuno.getInstance().getConn().getConn().prepareStatement( "INSERT INTO random (k, v) VALUES(?, ?);" );
            ps2.setString( 1, PATTERNS_LIST_KEY );
            ps2.setString( 2, "" );

            ps2.addBatch();
            ps2.executeBatch();
            ps2.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void save() {
        String words = String.join( DELIMITER, wordsList );
        String patterns = String.join( DELIMITER, patternList );
        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager().getConn()
                    .prepareStatement( "UPDATE random SET v = ? WHERE k = ?;" );
            ps.setString( 1, words );
            ps.setString( 2, WORDS_LIST_KEY );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        try {
            PreparedStatement ps = ApiNetuno.getInstance().getConnectionManager().getConn()
                    .prepareStatement( "UPDATE random SET v = ? WHERE k = ?;" );
            ps.setString( 1, patterns );
            ps.setString( 2, PATTERNS_LIST_KEY );

            ps.addBatch();
            ps.executeBatch();
            ps.close();
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }

    public static List<String> getWordsList() {
        return wordsList;
    }

    public static List<String> getPatternsList() {
        return patternList;
    }
}