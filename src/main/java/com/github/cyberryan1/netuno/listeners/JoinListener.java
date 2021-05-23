package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {

        // * IMPORTANT * Should be the last thing checked in this event, if anything more is going to be added
        // Checking if the player has been punished while they were offline
        // Gives them a notification about it if they were
        if ( DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ).size() > 0 ) {
            for ( int id : DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ) ) {
                Punishment pun = DATA.getPunishment( id );

                String staff = "CONSOLE";
                if ( pun.getStaffUUID().equals( "CONSOLE" ) == false ) {
                    staff = Bukkit.getOfflinePlayer( pun.getStaffUUID() ).getName();
                }

                // TODO add something as a check for the length

                String notif = ConfigUtils.getColoredStrFromList( "warn.message" );
                notif = ConfigUtils.replaceAllVariables( notif, staff, event.getPlayer().getName(), "", pun.getReason() );
                event.getPlayer().sendMessage( notif );
                if ( notif.charAt( notif.length() - 1 ) == '\n' ) {
                    event.getPlayer().sendMessage( "" );
                }

                DATA.removeNotif( id );
            }
        }
    }
}
