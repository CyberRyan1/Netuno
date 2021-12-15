package com.github.cyberryan1.netuno.managers;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class DisableQuitMsg {

    private static List<OfflinePlayer> players = new ArrayList<>();

    public static void addPlayer( OfflinePlayer player ) {
        players.add( player );
    }

    public static boolean containsPlayer( OfflinePlayer player ) {
        return players.contains( player );
    }

    public static void removePlayer( OfflinePlayer player ) {
        players.remove( player );
    }
}