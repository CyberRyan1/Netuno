package com.github.cyberryan1.netuno.debug;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.models.time.NDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helps with debugging a cache by printing out its
 * contents to a file.
 *
 * @param <A> The key to the cache
 * @param <B> The value for the cache
 * @author Ryan
 */
public class CacheDebugPrinter<A, B> {

    private static final String DEBUG_FOLDER_NAME = "debug_folder";

    private final Map<A, B> cache = new HashMap<>();

    private PrintSpecifier<A> printerA;
    private PrintSpecifier<B> printerB;

    /**
     * @return The cache
     */
    public Map<A, B> getCache() {
        return cache;
    }

    /**
     * @param printer How the key should be printed to a
     *                file
     */
    public void setPrinterA( PrintSpecifier<A> printer ) {
        printerA = printer;
    }

    /**
     * @param printer How the value should be printed
     *                to a file
     */
    public void setPrinterB( PrintSpecifier<B> printer ) {
        printerB = printer;
    }

    /**
     * Generates a file and begins printing to that
     */
    public void printToFile() {
        File debugFolder = new File( CyberCore.getPlugin().getDataFolder(), DEBUG_FOLDER_NAME + "/" );
        if ( debugFolder.exists() == false ) debugFolder.mkdir();

        String debugFileName = DEBUG_FOLDER_NAME + "/debug_" + new NDate().getDateString().replace( " ", "_" ) + ".txt";
        File filePrintingTo = new File( debugFolder, debugFileName );
        printToFile( filePrintingTo );
    }

    /**
     * @param file A file to begin printing to
     */
    public void printToFile( File file ) {
        CyberLogUtils.logWarn( "Starting debug output to a file..." );

        if ( file.exists() == false ) {
            try {
                file.createNewFile();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        CyberLogUtils.logWarn( "Debug file successfully created at \"" + file.getAbsolutePath() + "\"" );
        CyberLogUtils.logWarn( "Starting to output to it (" + cache.size() + " entries) ..." );

        List<String> list = new ArrayList<>();
        for ( Map.Entry<A, B> entry : cache.entrySet() ) {
            String output = "\"" + printerA.print( entry.getKey() ) + "\" [\n";
            output += "\t" + printerB.print( entry.getValue() ) + "\n";
            output += "],";

            list.add( output );
        }

        try {
            final FileWriter writer = new FileWriter( file, true );
            for ( String str : list ) writer.write( str );
            writer.close();
        } catch ( IOException e ) {
            throw new RuntimeException( "An error occurred while writing debug information to the file: ", e );
        }

        CyberLogUtils.logWarn( "Debug file finished output" );
    }

    /**
     * Used to specify how a certain element
     * should be printed to a file
     *
     * @param <T> What type of element this is
     */
    public interface PrintSpecifier<T> {
        String print( T t );
    }
}