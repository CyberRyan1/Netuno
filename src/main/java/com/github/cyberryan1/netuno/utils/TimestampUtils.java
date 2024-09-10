package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.api.models.ApiPunishment;

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

    /**
     * Checks if a given unformulated length is valid.
     * @param unformulatedLength The unformulated length to check (i.e. "3m" or "12h")
     * @return True if the length is valid, false otherwise.
     */
    public static boolean isAllowableLength( String unformulatedLength ) {
        if ( unformulatedLength == null || unformulatedLength.length() <= 1 ) { return false; }
        if ( unformulatedLength.equalsIgnoreCase( "forever" ) ) { return true; }

        char unit = unformulatedLength.charAt( unformulatedLength.length() - 1 );
        if ( unit != 'w' && unit != 'd' && unit != 'h' && unit != 'm' && unit != 's' ) { return false; }

        int amount;
        try {
            amount = Integer.parseInt( unformulatedLength.substring( 0, unformulatedLength.length() - 1 ) );
        } catch ( NumberFormatException ex ) {
            return false;
        }

        return amount > 0;
    }

    /**
     * Converts the unformulated length format into a
     * timestamp. <br>
     * Example: "1h" = 3,600,000 milliseconds, "1d" = 86,400,000
     * milliseconds, etc
     * @param unformulatedLength The unformulated string to convert
     * @return The timestamp. Returns {@link ApiPunishment#PERMANENT_PUNISHMENT_LENGTH}
     * if it is a permanent punishment length
     */
    public static long getTimestampFromUnformulatedLength( String unformulatedLength ) {
        if ( unformulatedLength.equalsIgnoreCase( "forever" ) ) return ApiPunishment.PERMANENT_PUNISHMENT_LENGTH;

        String amount = unformulatedLength.substring( 0, unformulatedLength.length() - 1 );
        char unit = unformulatedLength.charAt( unformulatedLength.length() - 1 );
        return 1000L * switch ( unit ) {
            case 'w' -> Long.parseLong( amount ) * 604800;
            case 'd' -> Long.parseLong( amount ) * 86400;
            case 'h' -> Long.parseLong( amount ) * 3600;
            case 'm' -> Long.parseLong( amount ) * 60;
            case 's' -> Long.parseLong( amount );
            default -> -1;
        };
    }

    /**
     * Scales a duration by a given scale (e.g. 2x, 3x, etc)
     * and the given count.
     * @param start The duration to scale
     * @param scale The scale to use
     * @param count The count to use
     * @return The scaled duration
     */
    public static long getScaledDuration( long start, int scale, int count ) {
        if ( start == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return ApiPunishment.PERMANENT_PUNISHMENT_LENGTH;
        return 1000L * ( long ) ( start * Math.pow( ( scale * 1F ), ( count - 1 ) ) );
    }
}