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

    /**
     * Converts the timestamp length into an unformulated length string. <br>
     * Example: 3,600,000 = "1h", 259,200,000 = "3d", etc
     * @param timestamp The timestamp
     * @return The unformulated length string
     */
    public String timestampToUnformulatedLength( long timestamp ) {
        if ( timestamp == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return "forever";
        long seconds = timestamp / 1000;
        long minutes = ( seconds / 60 ) % 60;
        long hours = ( seconds / 3600 ) % 24;
        long days = ( seconds / 86400 ) % 7;
        long weeks = ( seconds / 604800 );

        if ( weeks > 0 ) { return weeks + "w"; }
        if ( days > 0 ) { return days + "d"; }
        if ( hours > 0 ) { return hours + "h"; }
        if ( minutes > 0 ) { return minutes + "m"; }
        return seconds + "s";
    }

    /**
     * Converts the timestamp length into a full formatted length string. <br>
     * Example: 3,600,000 = "1 hour", 259,203,000 = "3 days and 3 seconds",
     * 3,729,000 = "1 hour, 2 minutes, and 9 seconds", 108,201,000 = "1 day, 6 hours, 3 minutes, and 21 seconds"
     * @param timestamp The timestamp
     * @param limit The maximum number of units to include in the string (set to -1 for no limit)
     * @return The formatted length string
     */
    public String timestampToFormulatedLength( long timestamp, int limit ) {
        if ( timestamp == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return "forever";
        long seconds = timestamp / 1000;
        long minutes = ( seconds / 60 ) % 60;
        long hours = ( seconds / 3600 ) % 24;
        long days = ( seconds / 86400 ) % 7;
        long weeks = ( seconds / 604800 );

        List<String> elements = new ArrayList<>();
        if ( weeks > 0 ) {
            String e = weeks + " week";
            if ( weeks > 1 ) { e += "s"; }
            elements.add( e );
        }

        if ( days > 0 ) {
            String e = days + " day";
            if ( days > 1 ) { e += "s"; }
            elements.add( e );
        }

        if ( hours > 0 ) {
            String e = hours + " hour";
            if ( hours > 1 ) { e += "s"; }
            elements.add( e );
        }

        if ( minutes > 0 ) {
            String e = minutes + " minute";
            if ( minutes > 1 ) { e += "s"; }
            elements.add( e );
        }

        if ( seconds > 0 ) {
            String e = seconds + " second";
            if ( seconds > 1 ) { e += "s"; }
            elements.add( e );
        }

        final List<String> finalElements = new ArrayList<>();
        if ( limit > 0 ) {
            for ( int i = 0; i < limit; i++ ) {
                if ( i >= elements.size() ) { break; }
                finalElements.add( elements.get( i ) );
            }
        }
        else {
            finalElements.addAll( elements );
        }

        if ( finalElements.size() == 0 ) { return "0 seconds"; }
        else if ( finalElements.size() == 1 ) { return finalElements.get( 0 ); }
        else if ( finalElements.size() == 2 ) { return finalElements.get( 0 ) + " and " + finalElements.get( 1 ); }
        else {
            String toReturn = "";
            for ( int i = 0; i < finalElements.size(); i++ ) {
                if ( i == finalElements.size() - 1 ) { toReturn += " and "; }
                else if ( i > 0 ) { toReturn += ", "; }
                toReturn += finalElements.get( i );
            }
            return toReturn;
        }
    }
}