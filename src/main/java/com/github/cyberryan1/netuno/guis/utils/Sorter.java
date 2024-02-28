package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

import java.util.List;

public class Sorter {

    public static List<NPunishment> sortPuns( List<NPunishment> puns, SortBy sort ) {
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