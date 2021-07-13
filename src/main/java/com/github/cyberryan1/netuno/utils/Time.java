package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.netuno.classes.Punishment;

import java.sql.Timestamp;
import java.time.Duration;

public class Time {

    // returns number of seconds since Jan 1st, 1970
    public static long getCurrentTimestamp() { return System.currentTimeMillis() / 1000; }

    public static String getDateFromTimestamp( long stamp ) {
        Timestamp date = new Timestamp( stamp * 1000 );
        return date.toGMTString();
    }

    // gets the timestamp of a length
    // returns -1 if the length is forever
    public static long getTimestampFromLength( String len ) {
        if ( len.equalsIgnoreCase( "forever" ) ) { return -1; }

        Duration dur = Duration.ZERO;
        char unit = len.charAt( len.length() - 1 );
        int amount = Integer.parseInt( len.substring( 0, len.length() - 1 ) );

        if ( unit == 'w' ) { dur = Duration.ofDays( 7L * amount ); }
        else if ( unit == 'd' ) { dur = Duration.ofDays( ( long ) amount ); }
        else if ( unit == 'h' ) { dur = Duration.ofHours( ( long ) amount ); }
        else if ( unit == 'm' ) { dur = Duration.ofMinutes( ( long ) amount ); }
        else { dur = Duration.ofSeconds( ( long ) amount ); }

        return dur.getSeconds();
    }

    // supported units: s, m, h, d, w, and forever
    public static String getFormattedLength( String len ) {
        if ( len.equalsIgnoreCase( "forever" ) ) { return "Forever"; }

        int amount = Integer.parseInt( len.substring( 0, len.length() - 1 ) );
        char unit = len.charAt( len.length() - 1 );
        String betterUnit;

        if ( unit == 'w' ) { betterUnit = "week"; }
        else if ( unit == 'd' ) { betterUnit = "day"; }
        else if ( unit == 'h' ) { betterUnit = "hour"; }
        else if ( unit == 'm' ) { betterUnit = "minute"; }
        else { betterUnit = "second"; }

        if ( amount > 1 ) {
            betterUnit += "s";
        }

        return amount + " " + betterUnit;
    }

    public static boolean isAllowableLength( String len ) {
        if ( len.equalsIgnoreCase( "forever" ) ) { return true; }

        char unit = len.charAt( len.length() - 1 );
        if ( unit != 'w' && unit != 'd' && unit != 'h' && unit != 'm' && unit != 's' ) { return false; }

        int amount;
        try {
            amount = Integer.parseInt( len.substring( 0, len.length() - 1 ) );
        } catch ( NumberFormatException ex ) {
            return false;
        }

        if ( amount <= 0 ) { return false; }

        return true;
    }

    public static String getLengthFromTimestamp( long len ) {
        if ( len == -1 ) { return "Forever"; }

        int min = ( int ) ( len / 60 );
        int hour = min / 60;
        int day = hour / 24;
        int week = day / 7;

        long secondsUsed = 0;
        if ( week != 0 ) {
            secondsUsed += ( week * 7L * 24 * 3600 );
        }
        else if ( day != 0 ) {
            secondsUsed += ( day * 24L * 3600 );
        }
        else if ( hour != 0 ) {
            secondsUsed += ( hour * 3600L );
        }
        else if ( min != 0 ) {
            secondsUsed += ( min * 60L );
        }
        else {
            secondsUsed += len % 60;
        }

        long secondsRemain = len - secondsUsed;
        int secondSec = ( int ) ( secondsRemain % 60 );
        int secondMin = ( int ) ( secondsRemain / 60 );
        int secondHour = secondMin / 60;
        int secondDay = secondHour / 24;

        // // //
        if ( min == 0 && hour == 0 && day == 0 && week == 0 ) { return getFormattedLength( len + "s" ); }
        else if ( hour == 0 && day == 0 && week == 0 ) {
            if ( secondSec % 60 == 0 ) { return getFormattedLength( min + "m" ); }
            return getFormattedLength( min + "m" ) + " and " + getFormattedLength( secondSec + "s" );
        }
        else if ( day == 0 && week == 0 ) {
            if ( secondMin == 0 ) { return getFormattedLength( hour + "h" ); }
            return getFormattedLength( hour + "h" ) + " and " + getFormattedLength( secondMin + "m" );
        }
        else if ( week == 0 ) {
            if ( secondHour == 0 ) { return getFormattedLength( day + "d" ); }
            return getFormattedLength( day + "d" ) + " and " + getFormattedLength( secondHour + "h" );
        }
        else {
            if ( secondDay == 0 ) { return getFormattedLength( week + "w" ); }
            return getFormattedLength( week + "w" ) + " and " + getFormattedLength( secondDay + "d" );
        }
    }

    public static String getLengthRemaining( Punishment pun ) {
        if ( pun.getLength() == -1 ) { return "Never"; }
        return getLengthFromTimestamp( pun.getDate() + pun.getLength() - getCurrentTimestamp() );
    }

    public static String getScaledTime( String startingTime, int punTotal ) {
        String startingAmount = startingTime;
        char units[] = { 's', 'm', 'h', 'd', 'w' };
        for ( char c : units ) {
            startingAmount = startingAmount.replace( c + "", "" );
        }
        int amount = Integer.parseInt( startingAmount );
        amount = ( int ) ( amount * ( Math.pow( 2.0, ( punTotal - 1 ) ) ) );

        return "" + amount + startingTime.charAt( startingTime.length() - 1 );
    }
}
