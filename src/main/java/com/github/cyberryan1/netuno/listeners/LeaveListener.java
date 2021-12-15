package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.managers.DisableQuitMsg;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class LeaveListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerLeave( PlayerQuitEvent event ) {
        ArrayList<Punishment> banPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "ban", true );
        if ( banPunishments.size() >= 1 ) {
            event.setQuitMessage( null );
        }

        ArrayList<IPPunishment> ipPunishments = DATA.getIPPunishment( event.getPlayer().getUniqueId().toString(), "ipban", true );
        if ( ipPunishments.size() >= 1 ) {
            event.setQuitMessage( null );
        }

        if ( DisableQuitMsg.containsPlayer( event.getPlayer() ) ) {
            event.setQuitMessage( null );
        }
    }
}
