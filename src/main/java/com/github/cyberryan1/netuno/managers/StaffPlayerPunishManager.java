package com.github.cyberryan1.netuno.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

// Clarification: this class is mainly to manage the punishing list
// This helps prevent double-punishments via the GUI

public class StaffPlayerPunishManager {

    private final static HashMap<Player, OfflinePlayer> punishing = new HashMap<>();

    public static Player getWhoPunishingTarget( OfflinePlayer target ) {
        if ( punishing.containsValue( target ) == false ) { return null; }
        for ( Player p : punishing.keySet() ) {
            if ( punishing.get( p ).equals( target ) ) {
                return p;
            }
        }

        return null;
    }

    public static OfflinePlayer getWhoStaffPunishing( Player staff ) {
        return punishing.get( staff );
    }

    public static void addStaffTarget( Player staff, OfflinePlayer target ) {
        punishing.put( staff, target );
    }

    public static void removeStaff( Player staff ) {
        punishing.remove( staff );
    }
}
