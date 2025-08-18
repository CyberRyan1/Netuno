package com.github.cyberryan1.netuno.api.models;

import java.util.List;
import java.util.UUID;

/**
 * Represents a report made against a player
 * 
 * @author Ryan
 */
public interface ApiReport {

    /**
     * Gets the unique identifier of this report
     *
     * @return The ID of this report
     */
    int getId();

    /**
     * Gets the UUID of the reported player
     *
     * @return The UUID of the player that was reported
     */
    UUID getPlayer();

    /**
     * Gets the reasons for this report
     *
     * @return List of reasons why this player was reported
     */
    List<String> getReasons();

    /**
     * Gets the UUID of the player who made this report
     *
     * @return The UUID of the player that made the report
     */
    UUID getReportAuthor();

    /**
     * Gets the timestamp of when this report was made
     *
     * @return Timestamp of when this report was made
     */
    long getReportDate();
}