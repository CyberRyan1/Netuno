package com.github.cyberryan1.netuno.utils.database;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;

import java.sql.*;

public abstract class Database {

    Netuno plugin;
    Connection connection;
    public String table = "database";
    public int tokens = 0;

    private final String TYPELIST = "(id,player,staff,type,date,length,reason)";
    private final String UNKNOWNLIST = "(?,?,?,?,?,?,?)";

    public Database( Netuno instance ) {
        plugin = instance;
    }

    public abstract Connection getSqlConnection();

    public abstract void load();

    public void initialize() {
        connection = getSqlConnection();
        try {
            PreparedStatement ps = connection.prepareStatement( "SELECT * FROM " + table + " WHERE id = ?" );
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to retrieve connection", ex );
        }
    }

    public void addPunishment( Punishment pun ) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "INSERT INTO " + table + " " + TYPELIST + " VALUES" + UNKNOWNLIST );

            int id = getNextID();
            if ( id == -1 ) {
                Utils.logError( "Unabled to add punishment to database" );
                return;
            }

            ps.setInt( 1, id );
            ps.setString( 2, pun.getPlayerUUID() );
            ps.setString( 3, pun.getStaffUUID() );
            ps.setString( 4, pun.getType() );
            ps.setString( 5, "" + pun.getDate() );
            ps.setString( 6, "" + pun.getLength() );
            ps.setString( 7, pun.getReason() );

            ps.executeUpdate();
            return;

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
    }

    private int getNextID() {
        try {
            Connection conn = getSqlConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM " + table );
            rs.next();
            return rs.getInt("count(*)");
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to get next available ID in database" );
        }
        return -1;
    }

//    public Punishment getPunishment( int id ) {
//        if ( checkIDExists( id ) ) {
//            Connection conn = null;
//            PreparedStatement ps = null;
//            ResultSet rs = null;
//
//            try {
//                conn = getSqlConnection();
//                ps = conn.prepareStatement( "SELECT * FROM " + table + " WHERE id = " + id + ";" );
//
//                rs = ps.executeQuery();
//                while ( rs.next() ) {
//                    Punishment next = rs.getObject( "punishment", Punishment.class );
//                    if ( next.getID() == id ) {
//                        return next;
//                    }
//                }
//            } catch ( SQLException e ) {
//                e.printStackTrace();
//                Utils.logError( "Couldn't execute MySQL statement: ", e );
//        //        Utils.logError( Errors.sqlConnectionExecute(), e );
//            } finally {
//                try {
//                    if ( ps != null )
//                        ps.close();
//                    if ( conn != null )
//                        conn.close();
//                } catch ( SQLException e ) {
//                    Utils.logError( Errors.sqlConnectionClose(), e );
//                }
//            }
//        }
//
//        return null;
//    }
//
//
//
//    public void addPunishment( Punishment pun ) {
//        int id = pun.getID();
//        Connection conn = null;
//        PreparedStatement ps = null;
//
//        try {
//            conn = getSqlConnection();
//            ps = conn.prepareStatement( "INSERT INTO " + table + " (id,history) VALUES(?,?)" );
//
//            ps.setInt( 1, id );
//            ps.setObject( 2, pun );
//            //ps.setBytes( 2, pun.toByteArray() );
//            ps.executeUpdate();
//            return;
//        } catch ( SQLException e ) {
//            Utils.logError( Errors.sqlConnectionExecute() );
//        } finally {
//            try {
//                if ( ps != null )
//                    ps.close();
//                if ( conn != null )
//                    conn.close();
//            } catch ( SQLException e ) {
//                Utils.logError( Errors.sqlConnectionExecute(), e );
//            }
//        }
//
//        return;
//    }

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

    // sees if a punishment id exists
    private boolean checkIDExists( int id ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSqlConnection();
            ps = conn.prepareStatement( "SELECT * FROM " + table + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();
        } catch ( SQLException ignore ) {}
        finally {
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

    // Convert a byte array to a Punishment object
//    private Punishment getPunishmentFromByteArray( byte[] data ) {
//        try {
//            ByteArrayInputStream baip = new ByteArrayInputStream( data );
//            ObjectInputStream ois = new ObjectInputStream( baip );
//            Punishment pun = ( Punishment ) ois.readObject();
//            return pun;
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        } catch ( ClassNotFoundException e ) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}
