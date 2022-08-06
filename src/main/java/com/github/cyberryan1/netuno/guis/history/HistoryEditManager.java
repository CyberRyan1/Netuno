package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;

public class HistoryEditManager implements Listener {

    private static Map<String, HistoryEditGUI> editing = new HashMap<>();

    public static void addEditing( Player player, HistoryEditGUI gui ) {
        editing.put( player.getUniqueId().toString(), gui );
    }

    public static void removeEditing( Player player ) {
        editing.remove( player.getUniqueId().toString() );
    }

    public boolean isEditing( Player player ) {
        return editing.containsKey( player.getUniqueId().toString() );
    }

    public boolean isEditingReason( Player player ) {
        if ( isEditing( player ) == false ) { return false; }
        return editing.get( player.getUniqueId().toString() ).isEditingReason();
    }

    public boolean isEditingLength( Player player ) {
        if ( isEditing( player ) == false ) { return false; }
        return editing.get( player.getUniqueId().toString() ).isEditingLength();
    }

    @EventHandler( priority = EventPriority.LOW )
    public void onAsyncPlayerChat( AsyncPlayerChatEvent event ) {
        if ( isEditing( event.getPlayer() ) == false ) { return; }
        if ( isEditingReason( event.getPlayer() ) ) {
            event.setCancelled( true );
            editing.get( event.getPlayer().getUniqueId().toString() ).onReasonEditInput( event.getMessage() );
        }
        if ( isEditingLength( event.getPlayer() ) ) {
            event.setCancelled( true );
            editing.get( event.getPlayer().getUniqueId().toString() ).onLengthEditInput( event.getMessage() );
        }
    }

    @EventHandler( priority = EventPriority.LOW )
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
        if ( isEditing( event.getPlayer() ) == false ) { return; }
        HistoryEditGUI gui = editing.get( event.getPlayer().getUniqueId().toString() );
        gui.setEditingLength( false );
        gui.setEditingReason( false );
        CoreUtils.sendMsg( event.getPlayer(), "&sThe punishment edit has been cacelled" );
        removeEditing( event.getPlayer() );
    }
}