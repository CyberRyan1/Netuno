package com.github.cyberryan1.netuno.models.libraries;

import com.github.cyberryan1.netuno.models.Punishment;

import java.util.List;

/**
 * A library for punishments
 *
 * @author Ryan
 */
public class PunishmentLibrary {

    /**
     * @param list A list of punishments
     * @return The punishment with the highest duration remaining
     *         from the provided list
     */
    public static Punishment getPunishmentWithHighestDurationRemaining( List<Punishment> list ) {
        Punishment highest = list.get( 0 );
        for ( int index = 1; index < list.size(); index++ ) {
            if ( highest.getDurationRemaining() < list.get( index ).getDurationRemaining() ) {
                highest = list.get( index );
            }
        }
        return highest;
    }
}