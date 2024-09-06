package com.github.cyberryan1.netuno.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to manage the IPs a player has joined with and manages if the list held by this instance matches what is held in the
 * database
 *
 * @author Ryan
 */
public class PlayerIpsRecord /*implements ApiPlayerIpList*/ {

    // test
    private final UUID uuid;
    private final List<String> ipList;
    private boolean dataMatchesDatabase;

    public PlayerIpsRecord( UUID uuid, boolean dataMatchesDatabase, List<String> ipList ) {
        this.uuid = uuid;
        this.ipList = ipList;
        this.dataMatchesDatabase = dataMatchesDatabase;
    }

    public PlayerIpsRecord( UUID uuid, boolean dataMatchesDatabase ) {
        this( uuid, dataMatchesDatabase, new ArrayList<>() );
    }

    /**
     * @return The UUID of the player represented
     */
//    @Override
    public UUID getPlayer() {
        return this.uuid;
    }

    /**
     * @return A list of IPs this player has joined the server with
     */
//    @Override
    public List<String> getIps() {
        return this.ipList;
    }

    /**
     * Adds the given IP to this list and marks this
     * instance as not up to date with the database
     * @param ip The IP to add
     */
    public void addIp( String ip ) {
        addIp( ip, false );
    }

    /**
     * Adds the given IP to this list and marks this
     * instance as either up to date or not up to date
     * with the database, depending on what is passed
     * @param ip The IP to add
     * @param upToDate Whether this instance should
     *                 remained marked as up to date
     *                 with the database (true) or
     *                 not (false)
     */
    public void addIp( String ip, boolean upToDate ) {
        ipList.add( ip );
        this.dataMatchesDatabase = upToDate;
    }

    /**
     * @return True if the data regarding the list of IPs the player has joined with in this
     *         instance matches the data stored in the database, false otherwise
     */
//    @Override
    public boolean isUpdatedInDatabase() {
        return this.dataMatchesDatabase;
    }

    public void setDataMatchesDatabase( boolean b ) {
        this.dataMatchesDatabase = b;
    }

}