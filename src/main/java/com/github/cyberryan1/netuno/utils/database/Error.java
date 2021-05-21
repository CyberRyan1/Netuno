package com.github.cyberryan1.netuno.utils.database;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.Utils;

public class Error {

    public static void execute( Netuno plugin, Exception e ) {
        Utils.logError( "Couldn't execute MySQL statement: ", e );
    }

    public static void close( Netuno plugin, Exception e ) {
        Utils.logError( "Failed to close MySQL connection: ", e );
    }
}
