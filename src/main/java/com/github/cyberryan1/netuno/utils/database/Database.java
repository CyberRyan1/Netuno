package com.github.cyberryan1.netuno.utils.database;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.classes.SingleReport;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Database {

    Netuno plugin;
    public Connection conn;

    //region FinalVariables
    public final String PUN_TABLE_NAME = "database";
    private final String PUN_TYPE_LIST = "(id,player,staff,type,date,length,reason,active)";
    private final String PUN_UNKNOWN_LIST = "(?,?,?,?,?,?,?,?)";
    private static int mostRecentPunID = -1;

    private final String NOTIF_TABLE_NAME = "notifs";
    private final String NOTIF_TYPE_LIST = "(id,player)";
    private final String NOTIF_UNKNOWN_LIST = "(?,?)";

    private final String IP_TABLE_NAME = "ip";
    private final String IP_TYPE_LIST = "(id,player,ip)";
    private final String IP_UNKNOWN_LIST = "(?,?,?)";

    private final String IP_PUN_TABLE_NAME = "ippuns";
    private final String IP_PUN_TYPE_LIST = "(id,player,staff,type,date,length,reason,active,alts)";
    private final String IP_PUN_UNKNOWN_LIST = "(?,?,?,?,?,?,?,?,?)";

    private final String NO_SIGN_NOTIFS_TABLE_NAME = "nosignnotifs";
    private final String NO_SIGN_NOTIFS_TYPE_LIST = "(player)";
    private final String NO_SIGN_NOTIFS_UNKNOWN_LIST = "(?)";

    private final String REPORTS_TABLE_NAME = "reports";
    private final String REPORTS_TYPE_LIST = "(id,target,reporter,date,reason)";
    private final String REPORTS_UNKNOWN_LIST = "(?,?,?,?,?)";

    private final String PUNISH_GUI_TABLE_NAME = "guipuns";
    private final String PUNISH_GUI_TYPE_LIST = "(id,player,type,reason)";
    private final String PUNISH_GUI_UNKNOWN_LIST = "(?,?,?,?)";

    private final String OTHER_TABLE_NAME = "other";
    private final String OTHER_TYPE_LIST = "(key,value)";
    private final String OTHER_UNKNOWN_LIST = "(?,?)";

    //endregion

    public Database( Netuno instance ) {
        plugin = instance;
    }

    public abstract Connection getSqlConnection();

    public abstract void load();

    public void initialize() {
        try {
            conn = getSqlConnection(); // do not close this one until the server is stopping
            PreparedStatement ps = conn.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE id = ?" );
            ResultSet rs = ps.executeQuery();
            ps.close();
            rs.close();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to retrieve connection", ex );
        }
    }

    public void close() {
        try {
            if ( conn != null ) { conn.close(); }
        } catch ( SQLException e ) {
            Error.close( plugin, e );
        }
    }

    //
    // Punishments Database
    //region Punishments

    // Returns the ID of the punishment, if needed
    public int addPunishment( Punishment pun ) {
        int id = getNextPunID();
        mostRecentPunID = id;
        PreparedStatement ps = null;

        try {
//        
            ps = conn.prepareStatement( "INSERT INTO " + PUN_TABLE_NAME + " " + PUN_TYPE_LIST + " VALUES" + PUN_UNKNOWN_LIST );

            if ( id == -1 ) {
                throw new SQLException();
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
            ps.close();

        } catch ( SQLException ex ) { Utils.logError( "Unable to add punishment to database" ); }

        return id;
    }

    // note: ipPuns and regular puns cannot share the same ID
    // TODO can just execute once during server startup and cache value
    private int getNextPunID() {
        int start = 1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
//        
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + PUN_TABLE_NAME );
            rs = ps.executeQuery();
            rs.next();
            start = rs.getInt( "count(*)" );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to get next available ID in punishments database" );
        }

        while ( checkPunIDExists( start ) || checkIpPunIDExists( start ) || start <= 0 ) { start++; }
        return start;
    }

    // sees if a punishment id exists
    public boolean checkPunIDExists( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean toReturn = false;

        try {
//        
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();
            rs.next();
            if ( rs.getInt( "count(*)" ) >= 1 ) { toReturn = true; }
            
            ps.close();
            rs.close();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to check if an id in the punishment database exists" );
        }

        return toReturn;
    }

    // Get a punishment from an ID
    public Punishment getPunishment( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Punishment pun = new Punishment();

        try {
//        
            ps = conn.prepareStatement( "SELECT * FROM " + PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();

            pun.setID( rs.getInt( "id" ) );
            pun.setPlayerUUID( rs.getString( "player" ) );
            pun.setStaffUUID( rs.getString( "staff" ) );
            pun.setType( rs.getString( "type" ) );
            pun.setDate( Long.parseLong( rs.getString( "date" ) ) );
            pun.setLength( Long.parseLong( rs.getString( "length" ) ) );
            pun.setReason( rs.getString( "reason" ) );
            pun.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );
            
            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        return pun;
    }

    // Search for a punishment by UUID
    public ArrayList<Punishment> getPunishment( String uuid ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Punishment> results = new ArrayList<>();

        try {
//        
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
            
            ps.close();
            rs.close();
        } catch ( SQLException e ) {
            Utils.logError( "Couldn't execute MySQL statement: ", e );
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
    // ? TODO move to punishment class
    public boolean checkActive( Punishment pun ) {
        if ( pun.getLength() == -1 ) {
            return pun.getActive();
        }

        long endingDate = pun.getDate() + pun.getLength();
        long today = Time.getCurrentTimestamp();

        if ( endingDate <= today || pun.getActive() == false ) {
            pun.setActive( false );
            if ( pun instanceof IPPunishment ) { setIPPunishmentActive( pun.getID(), false ); }
            else { setPunishmentActive( pun.getID(), false ); }
            return false;
        }

        return true;
    }

    //endregion

    //
    // Notifs Database
    //region Notifs
    public void addNotif( int punID, String playerUUID ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + NOTIF_TABLE_NAME + " " + NOTIF_TYPE_LIST + " VALUES" + NOTIF_UNKNOWN_LIST );

            ps.setInt( 1, punID );
            ps.setString( 2, playerUUID );

            ps.executeUpdate();
            ps.close();

        } catch ( SQLException ex ) { Utils.logError( "Unable to add notification to database" ); }
        
    }

    public ArrayList<Integer> searchNotifByUUID( String uuid ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> toReturn = new ArrayList<>();

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + NOTIF_TABLE_NAME + " WHERE player=?;" );

            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                toReturn.add( rs.getInt( "id" ) );
            }

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
        
        return toReturn;
    }

    public void removeNotif( int id ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + NOTIF_TABLE_NAME + " WHERE id=" + id + ";" );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    //endregion

    //
    // IP database
    //region IP
    public void addIP( String playerUUID, String ip ) {
        int id = getNextIpID();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + IP_TABLE_NAME + " " + IP_TYPE_LIST + " VALUES" + IP_UNKNOWN_LIST );

            ps.setInt( 1, id );
            ps.setString( 2, playerUUID );
            ps.setString( 3, ip );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to add ip to database" ); }

        
    }

    private int getNextIpID() {
        int toReturn = -1;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM " + IP_TABLE_NAME );
            rs.next();
            toReturn = rs.getInt( "count(*)" );

            rs.close();
            stmt.close();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to get next available ID for the IP database" );
        }

        return toReturn;
    }

    public boolean playerHasIP( String playerUUID, String ip ) {
        PreparedStatement ps = null;
        boolean toReturn = false;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + IP_TABLE_NAME + " WHERE player=?;" );

            ps.setString( 1, playerUUID );
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {
                if ( rs.getString( "ip" ).equals( ip ) ) {
                    toReturn = true;
                }
            }

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Unable to check if a player's IP address has already been saved!" ); }

        return toReturn;
    }

    public ArrayList<String> getAllAccountsOnIP( String ip ) {
        ArrayList<String> toReturn = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + IP_TABLE_NAME + " WHERE ip=?;" );
            ps.setString( 1, ip );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( rs.getString( "player" ) );
            }

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Unable to get all accounts on an IP address" ); }

        return toReturn;
    }

    public ArrayList<String> getAllIPFromPlayer( String playerUUID ) {
        ArrayList<String> toReturn = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + IP_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, playerUUID );

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                toReturn.add( rs.getString( "ip" ) );
            }

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Unable to get all IPs from a player" ); }

        return toReturn;
    }

    // returns all of the alts they have (based off what is logged in the database)
    public ArrayList<OfflinePlayer> getAllAlts( String playerUUID ) {
        ArrayList<String> ipsGoingThrough = getAllIPFromPlayer( playerUUID );
        ArrayList<String> searched = new ArrayList<>();
        ArrayList<OfflinePlayer> accounts = new ArrayList<>();

        while ( ipsGoingThrough.size() >= 1 ) {
            String current = ipsGoingThrough.remove( 0 );
            searched.add( current );

            ArrayList<String> accountsOnCurrent = getAllAccountsOnIP( current );
            for ( String uuidStr : accountsOnCurrent ) {
                OfflinePlayer offline = Bukkit.getOfflinePlayer( UUID.fromString( uuidStr ) );
                if ( accounts.contains( offline ) == false ) {
                    accounts.add( offline );
                }

                ArrayList<String> ipOfflineList = getAllIPFromPlayer( uuidStr );
                for ( String ipStr : ipOfflineList ) {
                    if ( ipsGoingThrough.contains( ipStr ) == false && searched.contains( ipStr ) == false ) {
                        ipsGoingThrough.add( ipStr );
                    }
                }
            }
        }

        return accounts;
    }

    // returns a list of all the accounts who are punished
    public ArrayList<OfflinePlayer> getPunishedAltList( String playerUUID ) {
        ArrayList<OfflinePlayer> alts = getAllAlts( playerUUID );
        ArrayList<OfflinePlayer> toReturn = new ArrayList<>();

        if ( alts.size() == 0 ) { return toReturn; }

        for ( OfflinePlayer account : alts ) {
            if ( account.getUniqueId().toString().equals( playerUUID ) == false ) {
                ArrayList<Punishment> activeMutes = getPunishment( account.getUniqueId().toString(), "mute", true );
                if ( activeMutes.size() >= 1 ) { toReturn.add( account ); }

                else {
                    ArrayList<Punishment> activeBans = getPunishment( account.getUniqueId().toString(), "ban", true );
                    if ( activeBans.size() >= 1 ) { toReturn.add( account ); }

                    else {
                        ArrayList<IPPunishment> activeIpmutes = getIPPunishment( account.getUniqueId().toString(), "ipmute", true );
                        if ( activeIpmutes.size() >= 1 ) { toReturn.add( account ); }

                        else {
                            ArrayList<IPPunishment> activeIpbans = getIPPunishment( account.getUniqueId().toString(), "ipban", true );
                            if ( activeIpbans.size() >= 1 ) { toReturn.add( account ); }
                        }
                    }
                }
            }
        }

        return toReturn;
    }

    // returns a list of all the accounts who are punished with the ACTIVE punishment type specified
    public ArrayList<OfflinePlayer> getPunishedAltsByType( String playerUUID, String punType ) {
        ArrayList<OfflinePlayer> alts = getAllAlts( playerUUID );
        ArrayList<OfflinePlayer> toReturn = new ArrayList<>();

        if ( alts.size() == 0 ) { return toReturn; }

        for ( OfflinePlayer account : alts ) {
            if ( punType.equals( "mute" ) || punType.equals( "ban" ) ) {
                ArrayList<Punishment> activePunishments = getPunishment( account.getUniqueId().toString(), punType, true );
                if ( activePunishments.size() >= 1 ) { toReturn.add( account ); }
            }

            else {
                ArrayList<IPPunishment> activePunishments = getIPPunishment( account.getUniqueId().toString(), punType, true );
                if ( activePunishments.size() >= 1 ) { toReturn.add( account ); }
            }
        }

        return toReturn;
    }

    // returns all of the alts in their respective colors
    public ArrayList<String> getPunishedColoredAltList( String playerUUID ) {
        ArrayList<OfflinePlayer> alts = getAllAlts( playerUUID );
        ArrayList<String> toReturn = new ArrayList<>();

        if ( alts.size() == 0 ) { return toReturn; }

        for ( OfflinePlayer account : alts ) {
            if ( account.getUniqueId().toString().equals( playerUUID ) == false ) { // checking that it's not the player requested
                ArrayList<Punishment> activeMutes = getPunishment( account.getUniqueId().toString(), "mute", true );
                ArrayList<Punishment> activeBans = getPunishment( account.getUniqueId().toString(), "ban", true );

                ArrayList<String> suffixs = new ArrayList<>();
                if ( activeBans.size() >= 1 ) { suffixs.add( "BANNED" ); }
                if ( activeMutes.size() >= 1 ) { suffixs.add( "MUTED" ); }

                String after = "";
                if ( suffixs.size() >= 1 ) {
                    String suffixsList[] = new String[ suffixs.size() - 1 ];
                    after = Utils.getColored( "&c[" + Utils.formatListIntoString( suffixs.toArray( suffixsList ) ) + "]" );
                }

                toReturn.add( account.getName() + " " + after );
            }
        }

        return toReturn;
    }

    //endregion

    //
    // IP-Punishments database
    //region IP-Punishments
    public int addIPPunishment( IPPunishment pun ) {
        PreparedStatement ps = null;
        int id = getNextIPPunId();
        mostRecentPunID = id;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + IP_PUN_TABLE_NAME + " " + IP_PUN_TYPE_LIST + " VALUES" + IP_PUN_UNKNOWN_LIST );

            ps.setInt( 1, id );
            ps.setString( 2, pun.getPlayerUUID() );
            ps.setString( 3, pun.getStaffUUID() );
            ps.setString( 4, pun.getType() );
            ps.setString( 5, "" + pun.getDate() );
            ps.setString( 6, "" + pun.getLength() );
            ps.setString( 7, pun.getReason() );
            ps.setString( 8, "" + pun.getActive() );
            ps.setString( 9, pun.getAltListAsString() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) {
            Utils.logError( "Unable to add ip punishment to database" );
            ex.printStackTrace();
        }

        return id;
    }

    // note: ipPuns and regular puns cannot share the same ID
    public int getNextIPPunId() {
        int start = 1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + IP_PUN_TABLE_NAME );
            rs = ps.executeQuery();
            rs.next();
            start = rs.getInt( "count(*)" );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to get next available ID in ip punishments database" ); }

        while ( checkIpPunIDExists( start ) == true || checkPunIDExists( start ) == true || start <= 0 ) { start++; }
        return start;
    }

    public boolean checkIpPunIDExists( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean toReturn = false;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + IP_PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();
            rs.next();
            if ( rs.getInt( "count(*)" ) >= 1 ) { toReturn = true; }

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to check if an id in the IP punishments database exists" ); }

        return toReturn;
    }

    // Gets an IP punishment from an ID
    public IPPunishment getIPPunishment( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        IPPunishment toReturn = null;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + IP_PUN_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();

            toReturn = new IPPunishment();
            toReturn.setID( rs.getInt( "id" ) );
            toReturn.setPlayerUUID( rs.getString( "player" ) );
            toReturn.setStaffUUID( rs.getString( "staff" ) );
            toReturn.setType( rs.getString( "type" ) );
            toReturn.setDate( Long.parseLong( rs.getString( "date" ) ) );
            toReturn.setLength( Long.parseLong( rs.getString( "length" ) ) );
            toReturn.setReason( rs.getString( "reason" ) );
            toReturn.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );
            toReturn.setAltListFromString( rs.getString( "alts" ) );

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        return toReturn;
    }

    // Search for an ippunishment by UUID
    public ArrayList<IPPunishment> getIPPunishment( String uuid ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<IPPunishment> results = new ArrayList<>();
        ArrayList<Integer> idResults = new ArrayList<>();

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + IP_PUN_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                IPPunishment pun = new IPPunishment();
                pun.setID( rs.getInt( "id" ) );
                pun.setPlayerUUID( rs.getString( "player" ) );
                pun.setStaffUUID( rs.getString( "staff" ) );
                pun.setType( rs.getString( "type" ) );
                pun.setDate( Long.parseLong( rs.getString( "date" ) ) );
                pun.setLength( Long.parseLong( rs.getString( "length" ) ) );
                pun.setReason( rs.getString( "reason" ) );
                pun.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );
                pun.setAltListFromString( rs.getString( "alts" ) );

                results.add( pun );
                idResults.add( pun.getID() );
            }

            ps = conn.prepareStatement( "SELECT * FROM " + IP_PUN_TABLE_NAME + " WHERE instr(alts, ?) > 0;" );
            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                IPPunishment pun = new IPPunishment();
                pun.setID( rs.getInt( "id" ) );

                if ( idResults.contains( pun.getID() ) == false ) {
                    pun.setPlayerUUID( rs.getString( "player" ) );
                    pun.setStaffUUID( rs.getString( "staff" ) );
                    pun.setType( rs.getString( "type" ) );
                    pun.setDate( Long.parseLong( rs.getString( "date" ) ) );
                    pun.setLength( Long.parseLong( rs.getString( "length" ) ) );
                    pun.setReason( rs.getString( "reason" ) );
                    pun.setActive( Boolean.parseBoolean( rs.getString( "active" ) ) );
                    pun.setAltListFromString( rs.getString( "alts" ) );

                    results.add( pun );
                    idResults.add( pun.getID() );
                }
            }

            ps.close();
            rs.close();

        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        return results;
    }

    // Search for an ippunishment by uuid, type, and active
    public ArrayList<IPPunishment> getIPPunishment( String uuid, String type, boolean active ) {
        ArrayList<IPPunishment> punishments = getIPPunishment( uuid );
        ArrayList<IPPunishment> toReturn = new ArrayList<>();

        for ( IPPunishment pun : punishments ) {
            if ( pun.getType().equalsIgnoreCase( type ) ) {
                if ( checkActive( pun ) == active ) {
                    toReturn.add( pun );
                }
            }
        }

        return toReturn;
    }

    // Sets whether an ippunishment is still active or not
    public void setIPPunishmentActive( int id, boolean active ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "UPDATE " + IP_PUN_TABLE_NAME + " SET active=? WHERE id=?;" );
            ps.setString( 1, active + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    // returns true if player has no sign notifs enabled, false if not
    public boolean checkPlayerNoSignNotifs( Player player ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean toReturn = false;

        try {
            ps = conn.prepareStatement( "SELECT count(*) FROM " + NO_SIGN_NOTIFS_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, player.getUniqueId().toString() );

            rs = ps.executeQuery();
            rs.next();
            if ( rs.getInt( "count(*)" ) >= 1 ) { toReturn = true; }

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to check if a player has no sign notifs enabled in the no sign notif database" ); }

        return toReturn;
    }

    //endregion

    //
    // Works for both IP punishments and regular punishments
    //region Both

    // Gets all punishments a player has
    public ArrayList<Punishment> getAllPunishments( String uuid ) {
        ArrayList<Punishment> puns = getPunishment( uuid );
        ArrayList<IPPunishment> ipPuns = getIPPunishment( uuid );

        for ( int index = 0; index < puns.size(); index++ ) {
            if ( ipPuns.size() <=0 ) { break; }
            if ( puns.get( index ).getDate() > ipPuns.get( 0 ).getDate() ) {
                puns.add( index, ipPuns.remove( 0 ) );
            }
        }
        puns.addAll( ipPuns );

        return puns;
    }

    // Returns all punishments a player has that are active
    public ArrayList<Punishment> getAllActivePunishments( String uuid ) {
        ArrayList<Punishment> toReturn = new ArrayList<>();
        for ( Punishment pun : getAllPunishments( uuid ) ) {
            if ( pun.getActive() ) {
                toReturn.add( pun );
            }
        }

        return toReturn;
    }

    // Deletes a punishment
    // Works for both regular and IP punishments
    public void deletePunishment( int id ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + PUN_TABLE_NAME + " WHERE id=?;" );
            ps.setInt( 1, id );
            ps.executeUpdate();

            ps = conn.prepareStatement( "DELETE FROM " + IP_PUN_TABLE_NAME + " WHERE id=?;" );
            ps.setInt( 1, id );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

    }

    // Sets whether a punishment is still active or not
    // Works for both regular punishments and IP punishments
    public void setPunishmentActive( int id, boolean active ) {
        PreparedStatement ps = null;

        try {
            // regular punishments
            ps = conn.prepareStatement( "UPDATE " + PUN_TABLE_NAME + " SET active=? WHERE id=?;" );
            ps.setString( 1, active + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();

            // ip punishments
            ps = conn.prepareStatement( "UPDATE " + IP_PUN_TABLE_NAME + " SET active=? WHERE id=?;" );
            ps.setString( 1, active + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    // Sets a punishment's length
    // Works for both regular punishments and IP punishments
    public void setPunishmentLength( int id, long length ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "UPDATE " + PUN_TABLE_NAME + " SET length=? WHERE id=?;" );
            ps.setString( 1, length + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps = conn.prepareStatement( "UPDATE " + IP_PUN_TABLE_NAME + " SET length=? WHERE id=?;" );
            ps.setString( 1, length + "" );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    // Sets a punishments reason
    // Works for both regular punishments and IP punishments
    public void setPunishmentReason( int id, String reason ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "UPDATE " + PUN_TABLE_NAME + " SET reason=? WHERE id=?;" );
            ps.setString( 1, reason );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps = conn.prepareStatement( "UPDATE " + IP_PUN_TABLE_NAME + " SET reason=? WHERE id=?;" );
            ps.setString( 1, reason );
            ps.setInt( 2, id );
            ps.executeUpdate();

            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    public int getMostRecentPunishmentID() { return mostRecentPunID; }

    //endregion

    //
    // Sign Notifications
    //region Sign
    public void addPlayerNoSignNotifs( Player player ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + NO_SIGN_NOTIFS_TABLE_NAME + " VALUES" + NO_SIGN_NOTIFS_UNKNOWN_LIST );

            ps.setString( 1, player.getUniqueId().toString() );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to add sign notif to database", ex ); }
    }

    public void removePlayerNoSignNotifs( Player player ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + NO_SIGN_NOTIFS_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, player.getUniqueId().toString() );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to remove sign notif from database", ex ); }
    }

    //endregion

    //
    // Reports
    //region Reports
    public int addReport( SingleReport report ) {
        int id = getReportsCount();
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + REPORTS_TABLE_NAME + " " + REPORTS_TYPE_LIST + " VALUES" + REPORTS_UNKNOWN_LIST );

            ps.setInt( 1, id );
            ps.setString( 2, report.getTarget().getUniqueId().toString() );
            ps.setString( 3, report.getReporter().getUniqueId().toString() );
            ps.setString( 4, report.getDate() + "" );
            ps.setString( 5, report.getReason() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to add report to database" ); }

        return id;
    }

    public int getReportsCount() {
        int start = 1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + REPORTS_TABLE_NAME );
            rs = ps.executeQuery();
            rs.next();
            start = rs.getInt( "count(*)" );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to get next available ID in reports database" ); }

        // idk why i check for if "start <= 0" but it cant hurt :shrug:
        while ( checkReportIDExists( start ) || start <= 0 ) { start++; }
        return start;
    }

    public boolean checkReportIDExists( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean toReturn = false;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + REPORTS_TABLE_NAME + " WHERE id=" + id + ";" );
            rs = ps.executeQuery();
            rs.next();
            if ( rs.getInt( "count(*)" ) >= 1 ) { toReturn = true; }

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to check if an id in the reports database exists" ); }

        return toReturn;
    }

    public SingleReport getReport( int id ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        SingleReport report = null;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + REPORTS_TABLE_NAME + " WHERE id = " + id + ";" );
            rs = ps.executeQuery();

            report = new SingleReport();
            report.setTarget( Bukkit.getOfflinePlayer( UUID.fromString( rs.getString( "target" ) ) ) );
            report.setReporter( Bukkit.getOfflinePlayer( UUID.fromString( rs.getString( "reporter" ) ) ) );
            report.setDate( Long.parseLong( rs.getString( "date" ) ) );
            report.setReason( rs.getString( "reason" ) );

            ps.close();
            rs.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        return report;
    }

    // Search for all reports with the target with uuid input
    public ArrayList<SingleReport> getReport( String uuid ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<SingleReport> results = new ArrayList<>();

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + REPORTS_TABLE_NAME + " WHERE target=?;" );
            ps.setString( 1, uuid );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                SingleReport report = new SingleReport();
                report.setTarget( Bukkit.getOfflinePlayer( UUID.fromString( rs.getString( "target" ) ) ) );
                report.setReporter( Bukkit.getOfflinePlayer( UUID.fromString( rs.getString( "reporter" ) ) ) );
                report.setDate( Long.parseLong( rs.getString( "date" ) ) );
                report.setReason( rs.getString( "reason" ) );

                results.add( report );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        return results;
    }

    public void removeReport( int id ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + REPORTS_TABLE_NAME + " WHERE id=" + id + ";" );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    // gets all reports where: startingIndex <= report row # <= endingIndex
    public ArrayList<SingleReport> getAllReports( int startingIndex, int endingIndex ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<SingleReport> toReturn = new ArrayList<>();

        try {
            ps = conn.prepareStatement( "SELECT row_number() OVER ( ORDER BY id ) RowNum, id FROM " + REPORTS_TABLE_NAME + ";" );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                if ( rs.getInt( "RowNum" ) >= startingIndex ) {
                    if ( rs.getInt( "RowNum" ) <= endingIndex ) {
                        idList.add( rs.getInt( 2 ) );
                    }
                    else { break; }
                }
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        for ( int id : idList ) {
            toReturn.add( getReport( id ) );
        }

        return toReturn;
    }

    public void deleteAllExpiredReports() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> toDelete = new ArrayList<>();
        final int DELETE_AFTER_INT = ConfigUtils.getInt( "reports.delete-after" );
        if ( DELETE_AFTER_INT == -1 ) { return; }
        long deleteBefore = Time.getCurrentTimestamp() - 60L * 60 * DELETE_AFTER_INT;

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + REPORTS_TABLE_NAME + ";" );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                if ( Long.parseLong( rs.getString( 4 ) ) <= deleteBefore ) { toDelete.add( rs.getInt( 1 ) ); }
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        for ( int id : toDelete ) {
            removeReport( id );
        }
    }

    //endregion

    //
    // Punish GUI Database
    //region Punish

    public void addGUIPun( OfflinePlayer target, String type, String reason, int punID ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + PUNISH_GUI_TABLE_NAME + " VALUES" + PUNISH_GUI_UNKNOWN_LIST );

            ps.setInt( 1, punID );
            ps.setString( 2, target.getUniqueId().toString() );
            ps.setString( 3, type );
            ps.setString( 4, reason.toLowerCase() );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to add GUI punishment to database" ); }
    }

    public int getGUIPunCount( OfflinePlayer target, String type, String reason ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result = 0;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + PUNISH_GUI_TABLE_NAME + " WHERE player=? AND type=? AND reason=?;" );
            ps.setString( 1, target.getUniqueId().toString() );
            ps.setString( 2, type );
            ps.setString( 3, reason.toLowerCase() );
            rs = ps.executeQuery();

            rs.next();
            result = rs.getInt( "count(*)" );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to get a GUI punishment count from the database" ); }

        return result;
    }

    public void removeGUIPun( int punID ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + PUNISH_GUI_TABLE_NAME + " WHERE id=?;" );
            ps.setInt( 1, punID );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }
    }

    public void removeAllGUIPun( OfflinePlayer target ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Integer> toRemove = new ArrayList<>();

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + PUNISH_GUI_TABLE_NAME + " WHERE player=?;" );
            ps.setString( 1, target.getUniqueId().toString() );
            rs = ps.executeQuery();

            while ( rs.next() ) {
                toRemove.add( rs.getInt( "id" ) );
            }

            ps.close();
            rs.close();
        } catch ( SQLException e ) { Utils.logError( "Couldn't execute MySQL statement: ", e ); }

        for ( int id : toRemove ) {
            removeGUIPun( id );
            deletePunishment( id );
        }
    }

    //endregion

    //
    // Other Database
    //region Other

    public void addOther( String key, String value ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "INSERT INTO " + OTHER_TABLE_NAME + " VALUES" + OTHER_UNKNOWN_LIST );

            ps.setString( 1, key );
            ps.setString( 2, value );

            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to add to the other database" ); }
    }

    public void updateOther( String key, String newValue ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "UPDATE " + OTHER_TABLE_NAME + " SET value=? WHERE key=?;" );

            ps.setString( 1, newValue );
            ps.setString( 2, key );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to update the other database" ); }
    }

    public String getOther( String key ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";

        try {
            ps = conn.prepareStatement( "SELECT * FROM " + OTHER_TABLE_NAME + " WHERE key=?;" );

            ps.setString( 1, key );
            rs = ps.executeQuery();

            rs.next();
            result = rs.getString( "value" );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to select from the other database" ); }

        return result;
    }

    public void deleteOther( String key ) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement( "DELETE FROM " + OTHER_TABLE_NAME + " WHERE key=?;" );
            ps.setString( 1, key );
            ps.executeUpdate();
            ps.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to delete from the other database" ); }
    }

    public boolean otherCheckKeyExists( String key ) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            ps = conn.prepareStatement( "SELECT COUNT(*) FROM " + OTHER_TABLE_NAME + " WHERE key=?;" );
            ps.setString( 1, key );

            rs = ps.executeQuery();
            rs.next();
            result = ( rs.getInt( 1 ) >= 1 );

            ps.close();
            rs.close();
        } catch ( SQLException ex ) { Utils.logError( "Unable to check if a key exists in the other database" ); }

        return result;
    }

    //endregion
}