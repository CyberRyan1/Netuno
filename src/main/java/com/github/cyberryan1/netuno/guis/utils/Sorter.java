package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;

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
}