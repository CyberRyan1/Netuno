package com.github.cyberryan1.netuno.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to manage the IPs a player has joined with
 *
 * @author Ryan
 */
public class PlayerIpsRecord {

    private final UUID uuid;
    private final List<String> ipList;

    public PlayerIpsRecord( UUID uuid, List<String> ipList ) {
        this.uuid = uuid;
        this.ipList = ipList;
    }

    public PlayerIpsRecord( UUID uuid ) {
        this( uuid, new ArrayList<>() );
    }

    /**
     * @return The UUID of the player represented
     */
    public UUID getPlayer() {
        return this.uuid;
    }

    /**
     * @return A list of IPs this player has joined the server
     *         with
     */
    public List<String> getIps() {
        return this.ipList;
    }

    /**
     * @param ip An IP address
     * @return True if this record contains the provided IP
     *         address, false otherwise.
     */
    public boolean containsIp( String ip ) {
        return this.ipList.contains( ip );
    }

    /**
     * Adds the given IP to this list
     *
     * @param ip The IP to add
     */
    public void addIp( String ip ) {
        ipList.add( ip );
    }
}