package com.github.cyberryan1.netuno.api.models;

import java.util.List;
import java.util.UUID;

// TODO javadoc
public interface ApiPlayerIpList {

    /**
     * @return The UUID of the player represented
     */
    UUID getPlayer();

    /**
     * @return A list of IPs this player has joined
     * the server with
     */
    List<String> getIps();

    /**
     * Adds the given IP to this list and marks this
     * instance as not up to date with the database
     * @param ip The IP to add
     */
    void addIp( String ip );

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
    void addIp( String ip, boolean upToDate );

    /**
     * @return True if the data regarding the list of IPs
     * the player has joined with in this instance matches
     * the data stored in the database, false otherwise
     */
    boolean isUpdatedInDatabase();
}