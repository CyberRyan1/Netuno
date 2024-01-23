package com.github.cyberryan1.netuno.apimplement.models.alts.redo;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netunoapi.models.alts.AltInfoLoader;
import com.github.cyberryan1.netunoapi.models.alts.UuidIpRecord;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class NetunoAltInfoLoader implements AltInfoLoader {

    private static final int MAX_DEPTH = 2;

    public void initialize() {}

    public void initEntry( UUID uuid, String ip ) {
        Set<UuidIpRecord> entries = ApiNetuno.getData().getIpHistoryDatabase().queryByIp( ip );
        if ( entries.stream().anyMatch( entry -> entry.getUuid().equals( uuid ) && entry.getIp().equals( ip ) ) == false ) {
            ApiNetuno.getData().getIpHistoryDatabase().save( new UuidIpRecord( uuid, ip, false ) );
        }
    }

    public Set<UUID> queryAccounts( String ip ) {
        Set<UuidIpRecord> entries = ApiNetuno.getData().getIpHistoryDatabase().queryByIp( ip );

        for ( int i = 0; i < MAX_DEPTH; i++ ) {
            Set<String> ipList = entries.stream()
                    .map( UuidIpRecord::getIp )
                    .collect( Collectors.toSet() );
            Set<UuidIpRecord> newAlts = ApiNetuno.getData().getIpHistoryDatabase().queryByMultipleIps( ipList );
            if ( newAlts.isEmpty() ) { break; }

            entries.addAll( newAlts );
        }

        return entries.stream()
                .map( UuidIpRecord::getUuid )
                .collect( Collectors.toSet() );
    }
}