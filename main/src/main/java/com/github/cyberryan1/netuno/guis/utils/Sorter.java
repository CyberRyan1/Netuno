package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * A utility class for sorting lists
 *
 * @author Ryan
 */
public class Sorter {

    /**
     * Sorts a punishment list according to a provided sort order
     *
     * @param puns The punishment list to sort
     * @param sort The sort order
     * @return The sorted punishment list
     */
    public static List<ApiPunishment> sortPuns( List<ApiPunishment> puns, SortBy sort ) {
        if ( puns.size() <= 1 ) { return puns; }

        switch ( sort ) {
            case FIRST_DATE:
                puns.sort( ( a, b ) -> ( int ) ( a.getTimestamp() - b.getTimestamp() ) );
                break;
            case LAST_DATE:
                puns.sort( ( a, b ) -> ( int ) ( b.getTimestamp() - a.getTimestamp() ) );
                break;
            case FIRST_ACTIVE:
                puns.sort( ( a, b ) -> {
                    if ( a.isActive() == b.isActive() ) {
                        return ( int ) ( a.getTimestamp() - b.getTimestamp() );
                    } else {
                        return a.isActive() ? -1 : 1;
                    }
                } );
                break;
            case LAST_ACTIVE:
                puns.sort( ( a, b ) -> {
                    if ( a.isActive() == b.isActive() ) {
                        return ( int ) ( b.getTimestamp() - a.getTimestamp() );
                    } else {
                        return a.isActive() ? 1 : -1;
                    }
                } );
                break;
        }
        return puns;
    }

    /**
     * Sorts a report list according to a provided sort order
     *
     * @param reports The report list to sort
     * @param sort The sort order
     * @return The sorted report list
     */
    public static List<ApiReport> sortReports( List<ApiReport> reports, SortBy sort ) {
        if ( reports.size() <= 1 ) { return reports; }

        switch ( sort ) {
            case FIRST_DATE:
                reports.sort( ( a, b ) -> ( int ) ( a.getReportDate() - b.getReportDate() ) );
                break;
            case LAST_DATE:
                reports.sort( ( a, b ) -> ( int ) ( b.getReportDate() - a.getReportDate() ) );
                break;
            case ONLINE:
                reports.sort( ( a, b ) -> {
                    OfflinePlayer aPlayer = Bukkit.getOfflinePlayer( a.getPlayer() );
                    OfflinePlayer bPlayer = Bukkit.getOfflinePlayer( b.getPlayer() );

                    if ( aPlayer.isOnline() == bPlayer.isOnline() ) {
                        return aPlayer.getName().compareToIgnoreCase( bPlayer.getName() );
                    }
                    return aPlayer.isOnline() ? -1 : 1;
                } );
                break;
            case OFFLINE:
                reports.sort( ( a, b ) -> {
                    OfflinePlayer aPlayer = Bukkit.getOfflinePlayer( a.getPlayer() );
                    OfflinePlayer bPlayer = Bukkit.getOfflinePlayer( b.getPlayer() );

                    if ( aPlayer.isOnline() == bPlayer.isOnline() ) {
                        return aPlayer.getName().compareToIgnoreCase( bPlayer.getName() );
                    }
                    return aPlayer.isOnline() ? 1 : -1;
                } );
                break;
            case ALPHABETICAL:
                reports.sort( ( a, b ) -> {
                    String aName = Bukkit.getOfflinePlayer( a.getPlayer() ).getName();
                    String bName = Bukkit.getOfflinePlayer( b.getPlayer() ).getName();
                    return aName.compareToIgnoreCase( bName );
                } );
                break;
        }
        return reports;
    }
}