package com.github.cyberryan1.netuno.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to help manage timestamps
 *
 * @author Ryan
 */
public class TimestampUtils {

    private static final long SECONDS = 1000L;
    private static final long MINUTES = SECONDS * 60;
    private static final long HOURS = MINUTES * 60;
    private static final long DAYS = HOURS * 24;
    private static final long WEEKS = DAYS * 7;

    /**
     * @return The current timestamp, in milliseconds
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * @param timestamp A timestamp
     * @return The duration of time that has passed since
     * the provided timestamp
     */
    public static long getTimeSince( long timestamp ) {
        return getCurrentTimestamp() - timestamp;
    }

    /**
     * Adds the provided timestamp to the provided duration
     * and returns true if that sum is before the current time,
     * false otherwise
     * @param timestamp The timestamp
     * @param duration The duration
     * @return True if the timestamp has expired, false otherwise
     */
    public static boolean timestampHasExpired( long timestamp, long duration ) {
        return getTimeSince( timestamp ) > duration;
    }

    // TODO javadoc
    /**
     *
     * @param duration
     * @return
     */
    public static String durationToString( long duration ) {
        return durationToString( duration, 5 );
    }

    // TODO javadoc
    /**
     *
     * @param duration
     * @param maxUnits
     * @return
     */
    public static String durationToString( long duration, int maxUnits ) {
        List<Long> amount = new ArrayList<>();
        amount.add( duration / WEEKS );
        duration %= WEEKS;
        amount.add( duration / DAYS );
        duration %= DAYS;
        amount.add( duration / HOURS );
        duration %= HOURS;
        amount.add( duration / MINUTES );
        duration %= MINUTES;
        amount.add( duration );

        String output = "";
        int unitCount = 0;
        for ( int index = 0; index < maxUnits; index++ ) {
            long value = amount.get( index );
            if ( value == 0 ) continue;

            // TODO continue from here
            String unitType = switch ( index ) {
                case 1 -> "week";
                case 2 -> "day";
                case 3 -> "hour";
                case 4 -> "minute";
                case 5 -> "second";
                default -> null;
            };
            if ( value > 1 ) unitType += "s";

            output += value + " " + unitType + ", ";
            unitCount++;
            if ( unitCount >= maxUnits ) break;
        }

        int lastIndex = output.lastIndexOf( ", " );
        if ( lastIndex >= 0 ) {
            String temp = output.substring( 0, lastIndex - 1 );
            temp += ", and ";
            temp += output.substring( lastIndex + ", ".length() );
            output = temp;
        }

        return output;
    }
}