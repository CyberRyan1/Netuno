package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class ChatListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerChatEvent( AsyncPlayerChatEvent event ) {
        ArrayList<Punishment> allPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActive = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() == true && pun.getType().equalsIgnoreCase( "mute" ) ) {
                hadActive = true;
                break;
            }
        }

        ArrayList<Punishment> punishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true );
        if ( punishments.size() >= 1 ) {
            event.setCancelled( true );
            Punishment first = punishments.get( 0 );
            Utils.sendDeniedMsg( event.getPlayer(), first );
        }

        else if ( hadActive ) {
            if ( ConfigUtils.checkListNotEmpty( "mute.expire" ) ) {
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "mute.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "mute.expire-staff" ) ) {
                String msg = ConfigUtils.getColoredStrFromList( "mute.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg.replace( "[TARGET]", event.getPlayer().getName() ) );
                    }
                }
            }
        }
    }
}
