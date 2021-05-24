package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class ChatListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerChatEvent( AsyncPlayerChatEvent event ) {
        ArrayList<Punishment> punishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true );
        if ( punishments.size() >= 1 ) {
            event.setCancelled( true );
            Punishment first = punishments.get( 0 );
            Utils.sendDeniedMsg( event.getPlayer(), first );
        }
    }
}
