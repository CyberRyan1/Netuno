package com.github.cyberryan1.netuno.models;

import com.github.cyberryan1.netuno.api.models.ApiPlayerIpList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO javadoc
public class PlayerIpList implements ApiPlayerIpList {

    private final UUID uuid;
    private final List<String> ipList;
    private boolean dataMatchesDatabase;

    public PlayerIpList( UUID uuid, boolean dataMatchesDatabase, List<String> ipList ) {
        this.uuid = uuid;
        this.ipList = ipList;
        this.dataMatchesDatabase = dataMatchesDatabase;
    }

    public PlayerIpList( UUID uuid, boolean dataMatchesDatabase ) {
        this( uuid, dataMatchesDatabase, new ArrayList<>() );
    }

    /**
     * @return The UUID of the player represented
     */
    @Override
    public UUID getPlayer() {
        return this.uuid;
    }

    /**
     * @return A list of IPs this player has joined the server with
     */
    @Override
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
    @Override
    public boolean isUpdatedInDatabase() {
        return this.dataMatchesDatabase;
    }

    public void setDataMatchesDatabase( boolean b ) {
        this.dataMatchesDatabase = b;
    }

}