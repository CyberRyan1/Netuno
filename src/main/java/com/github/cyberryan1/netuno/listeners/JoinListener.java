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
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {

        ArrayList<Punishment> allPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActiveBan = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() == true && pun.getType().equalsIgnoreCase( "ban" ) ) {
                hadActiveBan = true;
                break;
            }
        }

        ArrayList<Punishment> banPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "ban", true );
        if ( banPunishments.size() >= 1 ) {
            event.setJoinMessage( null );

            long highestExpire = banPunishments.get( 0 ).getExpirationDate();
            Punishment highest = banPunishments.get( 0 );
            for ( int index = 1; index < banPunishments.size(); index++ ) {
                if ( banPunishments.get( index ).getExpirationDate() > highestExpire ) {
                    highest = banPunishments.get( index );
                }
            }

            event.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables( ConfigUtils.getColoredStrFromList( "ban.attempt" ), highest ) );
        }

        else if ( hadActiveBan ) {
            if ( ConfigUtils.checkListNotEmpty( "ban.expire" ) ) {
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "ban.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "ban.expire-staff" ) ) {
                String msg = ConfigUtils.getColoredStrFromList( "ban.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg.replace( "[TARGET]", event.getPlayer().getName() ) );
                    }
                }
            }
        }

        // * IMPORTANT * Should be the last thing checked in this event, if anything more is going to be added
        // Checking if the player has been punished while they were offline
        // Gives them a notification about it if they were
        Bukkit.getScheduler().runTaskLater( Utils.getPlugin(), () -> {
            if ( DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ).size() > 0 ) {
                for ( int id : DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ) ) {
                    Punishment pun = DATA.getPunishment( id );
                    Utils.sendPunishmentMsg( event.getPlayer(), pun );
                    DATA.removeNotif( id );
                }
            }
        }, 60L );
    }
}
