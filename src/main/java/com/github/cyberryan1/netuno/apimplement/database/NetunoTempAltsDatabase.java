package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.TempAltsDatabase;
import com.github.cyberryan1.netunoapi.models.alts.TempAltGroup;
import com.github.cyberryan1.netunoapi.models.alts.TempIpEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class NetunoTempAltsDatabase implements TempAltsDatabase {

    public void initialize() {}

    public void shutdown() {
        // TODO
    }

    public void save() {
        // ignore?
    }

    public void save( TempIpEntry tempAltEntry ) {
        String uuidList = "";
        for ( UUID u : tempAltEntry.getUuids() ) {
            uuidList += u.toString() + ",";
        }
        uuidList = uuidList.substring( 0, uuidList.length() - 1 );

        try {
            PreparedStatement ps = ConnectionManager.CONN.prepareStatement( "INSERT INTO ip_uuids_list(ip, uuid_list)" +
                    " VALUES(?,?) ON DUPLICATE KEY UPDATE uuid_list = ?;" );
            ps.setString( 1, tempAltEntry.getIp() );
            ps.setString( 2, uuidList );
            ps.setString( 3, uuidList );
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

    }

    public Set<TempIpEntry> queryByUuid( UUID uuid ) {
    }

    public Set<TempAltGroup> queryByIp( String ip ) {
    }

    public void deleteGroup( TempIpEntry tempAltEntry ) {

    }
}
