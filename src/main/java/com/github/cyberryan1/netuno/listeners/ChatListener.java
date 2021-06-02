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
        boolean hadActiveMute = false;
        boolean hadActiveIpMute = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() == true ) {
                if ( pun.getType().equalsIgnoreCase( "mute" ) ) {
                    hadActiveMute = true;
                }

                else if ( pun.getType().equalsIgnoreCase( "ipmute" ) ) {
                    hadActiveIpMute = true;
                }

            }
        }

        ArrayList<Punishment> ipmutePuns = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "ipmute", true );
        if ( ipmutePuns.size() >= 1 ) {
            event.setCancelled( true );

            long highestExpire = ipmutePuns.get( 0 ).getExpirationDate();
            Punishment highest = ipmutePuns.get( 0 );
            for ( int index = 1; index < ipmutePuns.size(); index++ ) {
                if ( ipmutePuns.get( index ).getExpirationDate() > highestExpire ) {
                    highest = ipmutePuns.get( index );
                    highestExpire = highest.getExpirationDate();
                }
            }

            Utils.sendDeniedMsg( event.getPlayer(), highest );
            return;
        }

        ArrayList<Punishment> mutePuns = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true );
        if ( mutePuns.size() >= 1 ) {
            event.setCancelled( true );

            long highestExpire = mutePuns.get( 0 ).getExpirationDate();
            Punishment highest = mutePuns.get( 0 );
            for ( int index = 1; index < mutePuns.size(); index++ ) {
                if ( mutePuns.get( index ).getExpirationDate() > highestExpire ) {
                    highest = mutePuns.get( index );
                    highestExpire = highest.getExpirationDate();
                }
            }

            Utils.sendDeniedMsg( event.getPlayer(), highest );
            return;
        }

        if ( hadActiveIpMute ) {
            if ( ConfigUtils.checkListNotEmpty( "ipmute.expire" ) ) {
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "ipmute.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "ipmute.expire-staff" ) ) {
                String msg = ConfigUtils.getColoredStrFromList( "ipmute.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg.replace( "[TARGET]", event.getPlayer().getName() ) );
                    }
                }
            }
        }

        if ( hadActiveMute ) {
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
