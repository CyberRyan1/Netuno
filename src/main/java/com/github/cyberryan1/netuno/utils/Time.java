package com.github.cyberryan1.netuno.utils;

public class Time {

    public static long getCurrentTimestamp() { return System.currentTimeMillis() / 1000; }

    // supported units: s, m, h, d, w
    public static String getFormattedLength( String len ) {
        int amount = Integer.parseInt( len.substring( 0, len.length() - 1 ) );
        char unit = len.charAt( len.length() - 1 );
        String betterUnit;

        if ( unit == 'w' ) { betterUnit = "week"; }
        else if ( unit == 'd' ) { betterUnit = "day"; }
        else if ( unit == 'h' ) { betterUnit = "hour"; }
        else if ( unit == 'm' ) { betterUnit = "month"; }
        else { betterUnit = "second"; }

        if ( amount > 1 ) {
            betterUnit += "s";
        }

        return amount + " " + betterUnit;
    }
}
