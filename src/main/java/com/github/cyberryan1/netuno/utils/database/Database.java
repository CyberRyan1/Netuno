package com.github.cyberryan1.netuno.utils.database;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;

import java.sql.*;
import java.util.ArrayList;

public abstract class Database {

    Netuno plugin;
    Connection connection;

    public final String PUN_TABLE_NAME = "database";
    private final String PUN_TYPE_LIST = "(id,player,staff,type,date,length,reason,active)";
    private final String PUN_UNKNOWN_LIST = "(?,?,?,?,?,?,?,?)";

    private final String NOTIF_TABLE_NAME = "notifs";
    private final String NOTIF_TYPE_LIST = "(id,player)";
    private final String NOTIF_UNKNOWN_LIST = "(?,?)";

    private final String IP_TABLE_NAME = "ip";
    private final String IP_TYPE_LIST = "(id,player,ip)";
    private final String IP_UNKNOWN_LIST = "(?,?,?)";

    public Database( Netuno instance ) {
        plugin = instance;
    }

    public abstract Connection getSqlConnection();

    public abstract void load();

    public void initialize() {
        connection = getSqlConnection();
        try {
            PreparedStatement ps = connection.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE id = ?" );
            ResultSet rs = ps.executeQuery();
            close( ps, rs );
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to retrieve connection", ex );
        }
    }

    public void close( PreparedStatement ps, ResultSet rs ) {
        try {
            if ( ps != null )
                ps.close();
            if ( rs != null )
                rs.close();
        } catch ( SQLException e ) {
            Error.close( plugin, e );
        }
    }

    //
    // Punishments Database
    //

    // Returns the ID of the punishment, if needed
    public int addPunishment( Punishment pun ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "INSERT INTO " + PUN_TABLE_NAME + " " + PUN_TYPE_LIST + " VALUES" + PUN_UNKNOWN_LIST );

            int id = getNextPunID();
            if ( id == -1 ) {
                Utils.logError( "Unable to add punishment to database" );
                return -1;
            }

            ps.setInt( 1, id );
            ps.setString( 2, pun.getPlayerUUID() );
            ps.setString( 3, pun.getStaffUUID() );
            ps.setString( 4, pun.getType() );
            ps.setString( 5, "" + pun.getDate() );
            ps.setString( 6, "" + pun.getLength() );
            ps.setString( 7, pun.getReason() );
            ps.setString( 8, pun.getActive() + "" );

            ps.executeUpdate();
            return id;

        } catch ( SQLException ex ) {
            Utils.logError( "Unable to add punishment to database" );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionExecute(), e );
            }
        }

        return -1;
    }

    private int getNextPunID() {
        try {
            Connection conn = getSqlConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM " + PUN_TABLE_NAME );
            rs.next();
            return rs.getInt( "count(*)" );
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to get next available ID in database" );
        }
        return -1;
    }

    // sees if a punishment id exists
    // TODO won't work bad code lmao
    private boolean checkPunIDExists( int id ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();
        } catch ( SQLException ignore ) {
        } finally {
            try {
                if ( ps != null && conn != null ) {
                    ps.close();
                    rs.close();
                    return true;
                }
                if ( ps != null )
                    ps.close();
                if ( conn != null )
                    conn.close();
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }

        return false;
    }

    // Get a punishment from an ID
    public Punishment getPunishment( int id ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();

            Punishment pun = new Punishment();
            pun.setID( rs.getInt( "id" ) );
            pun.setPlayerUUID( rs.getString( "player" ) );
            pun.setStaffUUID( rs.getString( "staff" ) );
            pun.setType( rs.getString( "type" ) );
            pun.setDate( Long.parseLong( rs.getString( "date" ) ) );
            pun.setLength( Long.parseLong( rs.getString( "length" ) ) );
            pun.setReason( rs.getString( "reason" ) );
            pun.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );

            return pun;

        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }

        return null;
    }

    // Search for a punishment by UUID
    public ArrayList<Punishment> getPunishment( String uuid ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Punishment> results = new ArrayList<>();

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                Punishment pun = new Punishment();
                pun.setID( rs.getInt( "id" ) );
                pun.setPlayerUUID( rs.getString( "player" ) );
                pun.setStaffUUID( rs.getString( "staff" ) );
                pun.setType( rs.getString( "type" ) );
                pun.setDate( Long.parseLong( rs.getString( "date" ) ) );
                pun.setLength( Long.parseLong( rs.getString( "length" ) ) );
                pun.setReason( rs.getString( "reason" ) );
                pun.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );

                results.add( pun );
            }
        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }

        return results;
    }

    // Gets punishments of a certain type that are active
    public ArrayList<Punishment> getPunishment( String uuid, String type, boolean active ) {
        ArrayList<Punishment> punishments = getPunishment( uuid );
        ArrayList<Punishment> toReturn = new ArrayList<>();

        for ( Punishment pun : punishments ) {
            if ( pun.getType().equalsIgnoreCase( type ) ) {
                if ( checkActive( pun ) == active ) {
                    toReturn.add( pun );
                }
            }
        }

        return toReturn;
    }

    // Checks if a punishment is active or not
    // true = still active, false = not active
    public boolean checkActive( Punishment pun ) {
        if ( pun.getLength() == -1 ) {
            return true;
        }

        long endingDate = pun.getDate() + pun.getLength();
        long today = Time.getCurrentTimestamp();

        if ( endingDate <= today || pun.getActive() == false ) {
            pun.setActive( false );
            setPunishmentActive( pun.getID(), false );
            return false;
        }

        return true;
    }

    // Sets whether a punishment is still active or not
    public void setPunishmentActive( int id, boolean active ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "UPDATE " + PUN_TABLE_NAME + " SET active=? WHERE id=?;" );
            ps.setString( 1, active + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();
        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }
    }

    //
    // Notifs Database
    //
    public void addNotif( int punID, String playerUUID ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "INSERT INTO " + NOTIF_TABLE_NAME + " " + NOTIF_TYPE_LIST + " VALUES" + NOTIF_UNKNOWN_LIST );

            ps.setInt( 1, punID );
            ps.setString( 2, playerUUID );

            ps.executeUpdate();
            return;

        } catch ( SQLException ex ) {
            Utils.logError( "Unable to add notification to database" );
        }
    }

    public ArrayList<Integer> searchNotifByUUID( String uuid ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> toReturn = new ArrayList<>();

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + NOTIF_TABLE_NAME + " WHERE player=?;" );

            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                toReturn.add( rs.getInt( "id" ) );
            }

            return toReturn;
        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }

        return null;
    }

    public void removeNotif( int id ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "DELETE FROM " + NOTIF_TABLE_NAME + " WHERE id=" + id + ";" );
            ps.executeUpdate();
        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
        } finally {
            try {
                if ( ps != null ) {
                    ps.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }
    }

    //
    // IP database
    //
    public void addIP( String playerUUID, String ip ) {
        int id = getNextIpID();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "INSERT INTO " + IP_TABLE_NAME + " " + IP_TYPE_LIST + " VALUES" + IP_UNKNOWN_LIST );

            ps.setInt( 1, id );
            ps.setString( 2, playerUUID );
            ps.setString( 3, ip );

            ps.executeUpdate();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to add ip to database" );
        } finally {
            try {
                if ( conn != null ) { conn.close(); }
                if ( ps != null ) { ps.close(); }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }
    }

    private int getNextIpID() {
        Connection conn = getSqlConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM " + IP_TABLE_NAME );
            rs.next();
            return rs.getInt( "count(*)" );
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to get next available ID for the IP database" );
        } finally {
            try {
                if ( conn != null ) { conn.close(); }
            } catch ( SQLException e ) {
                Utils.logError( Errors.sqlConnectionClose(), e );
            }
        }

        return -1;
    }

    public boolean playerHasIP( String playerUUID, String ip ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + IP_TABLE_NAME + " WHERE player=?;" );

            ps.setString( 1, playerUUID );
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {
                if ( rs.getString( "ip" ).equals( ip ) ) {
                    return true;
                }
            }

            return false;
        } catch ( SQLException e ) {
            Utils.logError( "Unable to check if a player's IP address has already been saved!" );
        } finally {
            try {
                if ( conn != null ) {
                    conn.close();
                }
                if ( ps != null ) {
                    ps.close();
                }
            } catch ( SQLException ex ) {
                Utils.logError( Errors.sqlConnectionClose(), ex );
            }
        }

        return false;
    }
}
































