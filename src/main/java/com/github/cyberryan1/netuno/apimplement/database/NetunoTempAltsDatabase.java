package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.TempAltsDatabase;
import com.github.cyberryan1.netunoapi.models.alts.TempAltGroup;

import java.util.Optional;
import java.util.UUID;

public class NetunoTempAltsDatabase implements TempAltsDatabase {
    public void initialize() {

    }

    public void shutdown() {

    }

    public void save() {

    }

    public void save( TempAltGroup tempAltGroup ) {

    }

    public Optional<TempAltGroup> queryByUuid( UUID uuid ) {
        return Optional.empty();
    }

    public Optional<TempAltGroup> queryByIp( String s ) {
        return Optional.empty();
    }

    public void deleteGroup( TempAltGroup tempAltGroup ) {

    }

    public int getNextGroupId() {
        return 0;
    }
}
