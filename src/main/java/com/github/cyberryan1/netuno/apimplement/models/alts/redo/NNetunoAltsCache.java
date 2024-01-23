package com.github.cyberryan1.netuno.apimplement.models.alts.redo;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.alts.TempAltCache;
import com.github.cyberryan1.netunoapi.models.alts.TempUuidIpEntry;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class NNetunoAltsCache implements TempAltCache {

    private static final int MAX_DEPTH = 2;

    public void initialize() {}

    public void initEntry( UUID uuid, String ip ) {
        Set<TempUuidIpEntry> entries = ApiNetuno.getData().getTempAltsDatabase().queryByIp( ip );
        if ( entries.stream().anyMatch( entry -> entry.getUuid().equals( uuid ) && entry.getIp().equals( ip ) ) == false ) {
            ApiNetuno.getData().getTempAltsDatabase().save( new TempUuidIpEntry( uuid, ip, false ) );
        }
    }

    public Set<UUID> queryAccounts( String ip ) {
        Set<TempUuidIpEntry> entries = ApiNetuno.getData().getTempAltsDatabase().queryByIp( ip );

        for ( int i = 0; i < MAX_DEPTH; i++ ) {
            Set<String> ipList = entries.stream()
                    .map( TempUuidIpEntry::getIp )
                    .collect( Collectors.toSet() );
            Set<TempUuidIpEntry> newAlts = ApiNetuno.getData().getTempAltsDatabase().queryByMultipleIps( ipList );
            if ( newAlts.size() == 0 ) { break; }

            entries.addAll( newAlts );
        }

        return entries.stream()
                .map( TempUuidIpEntry::getUuid )
                .collect( Collectors.toSet() );
    }


//    //                  IP
//    //                          Set of UUID's that have joined on this IP
//    private final Cache<String, Set<UUID>> CACHE = Caffeine.newBuilder()
//            .maximumSize( 400 )
//            .expireAfterAccess( 10, TimeUnit.MINUTES )
//            .build();
//
////    private final Multigraph<AltGraphNode, AltGraphEdge> GRAPH = new Multigraph<>( AltGraphEdge.class );
//
//    public void initialize() {
//        CyberLogUtils.logInfo( "[ALTS CACHE] Initializing the alts cache..." );
//
//        for ( Player player : Bukkit.getOnlinePlayers() ) {
//            loadPlayer( player.getUniqueId(), player.getAddress().getAddress().getHostAddress() );
//        }
//
//        //CyberLogUtils.logInfo( "[ALTS CACHE] Loaded a total of " + CACHE.estimatedSize() + " alt groups" );
//    }
//
//    public void loadPlayer( UUID uuid, String ip ) {
//        Optional<Set<UUID>> optionalUuidList = Optional.ofNullable( CACHE.getIfPresent( ip ) );
//
//        if ( optionalUuidList.isPresent() ) {
//            Set<UUID> uuids = optionalUuidList.get();
//        }
//
//        else {
//
//        }
//    }
//
//    private void loadIpIntoCache( String ip ) {
//        ApiNetuno.getData().getTempAltsDatabase().queryByIp( ip ).ifPresent( ipEntry -> {
//            CACHE.put( ip, ipEntry.getUuids() );
//        } );
//    }
//
////    private void loadPlayerFromCache( UUID uuid, String ip ) {
////        // TODO
////    }
////
////    private void loadPlayerFromDatabase( UUID uuid, String ip ) {
////        // TODO
////    }
//
//    public Optional<TempAltGroup> searchByUuid( UUID uuid ) {
//        // TODO
//    }
//
//    public Optional<TempAltGroup> searchByIp( String ip ) {
//        // TODO
//    }
//
//    public void save() {
//
//    }
}