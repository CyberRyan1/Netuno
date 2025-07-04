package com.github.cyberryan1.netuno.api.services;

import com.github.cyberryan1.netuno.api.models.ApiReport;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to get information about reports and to delete them.
 * The API does not support report creation- that will be only
 * done internally by Netuno
 * 
 * @author Ryan
 */
public interface ApiReportService {

    /**
     * Gets a report by its unique identifier
     *
     * @param id The ID of the report to get
     * @return The report with the specified ID
     */
    CompletableFuture<Optional<ApiReport>> getReport( int id );
    
    /**
     * Gets all reports that have been made against the specified
     * player
     *
     * @param player The player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    CompletableFuture<List<ApiReport>> getReportsAgainst( OfflinePlayer player );

    /**
     * Gets all reports that have been made against the specified
     * player
     *
     * @param uuid UUID of the player to get reports against
     * @return List of reports that have been made against the
     *         player
     */
    CompletableFuture<List<ApiReport>> getReportsAgainst( UUID uuid );

    /**
     * Gets all reports that have been made by the specified
     * player
     *
     * @param player The player to get reports by
     * @return List of reports that have been made by the player
     */
    CompletableFuture<List<ApiReport>> getReportsBy( OfflinePlayer player );

    /**
     * Gets all reports that have been made by the specified
     * player
     *
     * @param uuid UUID of the player to get reports by
     * @return List of reports that have been made by the player
     */
    CompletableFuture<List<ApiReport>> getReportsBy( UUID uuid );

    /**
     * Deletes the specified report
     *
     * @param report The report to delete
     */
    void deleteReport( ApiReport report );

    /**
     * Deletes the report with the specified ID
     *
     * @param id The ID of the report to delete
     */
    void deleteReport( int id );
}