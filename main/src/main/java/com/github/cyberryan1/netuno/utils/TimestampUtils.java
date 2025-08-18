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
        if ( duration == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return "Forever";
        List<String> strings = new ArrayList<>();

        long weeks = duration / WEEKS;
        duration = duration - ( weeks * WEEKS );
        if ( weeks != 0 ) strings.add( weeks + " week" + ( ( weeks == 1 ) ? "" : "s" ) );

        long days = duration / DAYS;
        duration = duration - ( days * DAYS );
        if ( days != 0 ) strings.add( days + " day" + ( ( days == 1 ) ? "" : "s" ) );

        long hours = duration / HOURS;
        duration = duration - ( hours * HOURS );
        if ( hours != 0 ) strings.add( hours + " hour" + ( ( hours == 1 ) ? "" : "s" ) );

        long minutes = duration / MINUTES;
        duration = duration - ( minutes * MINUTES );
        if ( minutes != 0 ) strings.add( minutes + " minute" + ( ( minutes == 1 ) ? "" : "s" ) );

        long seconds = duration / SECONDS;
        if ( seconds != 0 || strings.size() == 0 ) strings.add( seconds + " second" + ( ( seconds == 1 ) ? "" : "s" ) );

        return PrettyStringLibrary.getNonOxfordCommaList( strings, maxUnits );
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
     * @param start The duration to scale, in milliseconds
     * @param scale The scale to use
     * @param count The count to use
     * @return The scaled duration, in milliseconds
     */
    public static long getScaledDuration( long start, int scale, int count ) {
        if ( start == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return ApiPunishment.PERMANENT_PUNISHMENT_LENGTH;
        return ( long ) ( start * Math.pow( ( scale * 1F ), ( count - 1 ) ) );
    }

    /**
     * Converts the timestamp length into an unformulated length string. <br>
     * Example: 3,600,000 = "1h", 259,200,000 = "3d", etc
     * @param timestamp The timestamp
     * @return The unformulated length string
     */
    public static String timestampToUnformulatedLength( long timestamp ) {
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
    public static String timestampToFormulatedLength( long timestamp, int limit ) {
        if ( timestamp == ApiPunishment.PERMANENT_PUNISHMENT_LENGTH ) return "forever";

        final long SECOND = 1000;
        final long MINUTE = 60 * SECOND;
        final long HOUR = 60 * MINUTE;
        final long DAY = 24 * HOUR;

        // Time units and their names
        long[] units = {DAY, HOUR, MINUTE, SECOND};
        String[] unitNames = {"day", "hour", "minute", "second"};

        List<String> parts = new ArrayList<>();

        for (int i = 0; i < units.length; i++) {
            long unitValue = units[i];
            if (timestamp >= unitValue) {
                long amount = timestamp / unitValue;
                timestamp %= unitValue;

                parts.add(amount + " " + unitNames[i] + (amount > 1 ? "s" : ""));
            }
        }

        // Apply limit if specified
        if (limit > 0 && parts.size() > limit) {
            parts = parts.subList(0, limit);
        }

        // Format final string with commas and "and"
        if (parts.size() == 1) {
            return parts.get(0);
        } else if (parts.size() == 2) {
            return parts.get(0) + " and " + parts.get(1);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                if (i > 0) sb.append(i == parts.size() - 1 ? ", and " : ", ");
                sb.append(parts.get(i));
            }
            return sb.toString();
        }
    }
}