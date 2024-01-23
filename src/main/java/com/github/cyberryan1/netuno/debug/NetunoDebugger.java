package com.github.cyberryan1.netuno.debug;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.models.time.NDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetunoDebugger {

    private static final String FOLDER_NAME = "debug_logs";

    private final DebugInfo debugInfo;
    private final String debugFileName;
    private File debugFile = null;

    public NetunoDebugger( DebugInfo debugInfo ) {
        this.debugInfo = debugInfo;

        this.debugFileName = FOLDER_NAME + "/debug_" + new NDate().getDateString().replace( " ", "_" ) + ".txt";
        this.initFile();
    }

    private void initFile() {
        final File debugFolder = new File( CyberCore.getPlugin().getDataFolder(), FOLDER_NAME + "/" );
        if ( debugFolder.exists() == false ) { debugFolder.mkdir(); }

        debugFile = new File( CyberCore.getPlugin().getDataFolder(), this.debugFileName );
        if ( debugFile.exists() ) { throw new RuntimeException( "Debug file \"" + this.debugFileName + "\" already exists!" ); }
        try {
            debugFile.createNewFile();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public void start() {
        if ( debugFile == null ) { throw new NullPointerException( "Debug file has not been initialized/created and is null" ); }

        CyberLogUtils.logWarn( "Started debug logging with level " + this.debugInfo.name().toLowerCase() + " to file " + this.debugFileName );

        switch ( this.debugInfo ) {
            case CACHED_PLAYERS -> {
                writeCachedPlayers();
            }
            case CACHED_ALTS -> {
                writeCachedAlts();
            }
            case CACHED_REPORTS -> {
                writeCachedReports();
            }
            case ALL -> {
                writeCachedPlayers();
                writeCachedAlts();
                writeCachedReports();
            }
        }

        CyberLogUtils.logWarn( "Finished debug logging to file " + this.debugFileName );
    }

    private void writeCachedPlayers() {
        final List<String> msgs = new ArrayList<>();
        //                            UUID
        //                                 Alt Group ID (null if not found)
        //                                        List of punishment IDs
        final String stringFormat = "%-36s|%-12s|%s\n";

        msgs.add( "----------------------------------------\n" );
        msgs.add( "DEBUG TYPE :: Cached Players -\n" );
        msgs.add( "\n" );
        msgs.add( String.format( stringFormat, "UUID", "Punishment IDs" ) );

        for ( NetunoPlayer np : NetunoPlayerCache.getCache() ) {
            final String uuid = np.getPlayer().getUniqueId().toString();
            final String punishmentIds = np.getPunishments().stream()
                    .map( pun -> pun.getId() + "" ).collect( Collectors.joining( ", " ) );

            msgs.add( String.format( stringFormat, uuid, punishmentIds ) );
        }

        msgs.add( "\n" );
        msgs.add( "----------------------------------------\n" );

        write( msgs );
    }

    private void writeCachedAlts() {
        final List<String> msgs = new ArrayList<>();
        //                           Alt Group ID
        //                                 List of UUIDs in alt group
        //                                    List of IPs in alt group
        final String stringFormat = "%-12s|%s|%s\n";

        msgs.add( "----------------------------------------\n" );
        msgs.add( "DEBUG TYPE :: Cached Alts -\n" );
        msgs.add( "\n" );
        msgs.add( String.format( stringFormat, "Alt Group", "UUIDs", "IPs" ) );

        msgs.add( "ERROR - OUT OF DATE" );
        msgs.add( "ERROR - OUT OF DATE" );
        msgs.add( "ERROR - OUT OF DATE" );
        msgs.add( "ERROR - OUT OF DATE" );
//        for ( NAltGroup group : NetunoAltsCache.cache ) {
//            final String altGroupId = group.getGroupId() + "";
//            final String uuids = group.getUuids().stream().map( UUID::toString ).collect( Collectors.joining( ", " ) );
//            final String ips = String.join( ", ", group.getIps() );
//
//            msgs.add( String.format( stringFormat, altGroupId, uuids, ips ) );
//        }

        msgs.add( "\n" );
        msgs.add( "----------------------------------------\n" );

        write( msgs );
    }

    private void writeCachedReports() {
        final List<String> msgs = new ArrayList<>();
        //                           Report ID
        //                                 Player UUID
        //                                    Reporter UUID
        //                                             Timestamp
        //                                                   Reason
        final String stringFormat = "%-12s|%-36s|%-36s|%-10s|%s\n";

        msgs.add( "----------------------------------------\n" );
        msgs.add( "DEBUG TYPE :: Cached Reports -\n" );
        msgs.add( "\n" );
        msgs.add( String.format( stringFormat, "Report ID", "Player UUID", "Reporter UUID", "Timestamp", "Reason" ) );

        ApiNetuno.getData().getNetunoReports().deleteOldReports();
        for ( NReport report : ApiNetuno.getData().getNetunoReports().getCache() ) {
            final String id = report.getId() + "";
            final String playerUuid = report.getPlayerUuid();
            final String reporterUuid = report.getReporterUuid();
            final String timestamp = report.getTimestamp() + "";
            final String reason = report.getReason();

            msgs.add( String.format( stringFormat, id, playerUuid, reporterUuid, timestamp, reason ) );
        }

        msgs.add( "\n" );
        msgs.add( "----------------------------------------\n" );

        write( msgs );
    }

    private void write( List<String> msgs ) {
        if ( debugFile == null ) { return; }

        try {
            final FileWriter writer = new FileWriter( this.debugFile, true );
            for ( String str : msgs ) {
                writer.write( str );
            }
            writer.close();
        } catch ( IOException e ) {
            throw new RuntimeException( "An error occurred while writing debug information to the file: ", e );
        }
    }
}