package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.*;
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

        // ipmute handling
        ArrayList<IPPunishment> allIPPunishments = DATA.getIPPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActiveIpMute = false;
        for ( IPPunishment pun : allIPPunishments ) {
            if ( pun.getActive() && pun.getType().equalsIgnoreCase( "ipmute" ) ) {
                hadActiveIpMute = true;
                break;
            }
        }

        ArrayList<IPPunishment> ipmutePuns = DATA.getIPPunishment( event.getPlayer().getUniqueId().toString(), "ipmute", true );
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

        // mute handling
        ArrayList<Punishment> allPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActiveMute = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() && pun.getType().equalsIgnoreCase( "mute" ) ) {
                hadActiveMute = true;
                break;
            }
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

        // ipmute notification handling
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

        // mute notification handling
        else if ( hadActiveMute ) {
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

        // mutechat handling
        if ( MutechatManager.chatIsMuted() ) {
            if ( VaultUtils.hasPerms( event.getPlayer(), ConfigUtils.getStr( "general.staff-perm" ) ) == false ) {
                if ( VaultUtils.hasPerms( event.getPlayer(), ConfigUtils.getStr( "mutechat.bypass-perm" ) ) == false ) {
                    event.setCancelled( true );

                    if ( ConfigUtils.checkListNotEmpty( "mutechat.attempt" ) ) {
                        Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "mutechat.attempt" ) );
                    }
                    return;
                }
            }
        }
    }
}
