package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final HashMap<Player, Long> CHAT_SLOW = new HashMap<>();

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

        List<OfflinePlayer> accountsIpmuted = DATA.getPunishedAltsByType( event.getPlayer().getUniqueId().toString(), "ipmute" );
        if ( accountsIpmuted.size() >= 1 ) {
            List<IPPunishment> ipmutePunishments = new ArrayList<>();
            for ( OfflinePlayer account : accountsIpmuted ) {
                ipmutePunishments.addAll( DATA.getIPPunishment( account.getUniqueId().toString(), "ipmute", true ) );
            }

            ipmutePunishments = ipmutePunishments.stream()
                    .sorted( ( p1, p2 ) -> ( int ) (
                            p2.getExpirationDate() - p1.getExpirationDate()
                    ) )
                    .collect( Collectors.toList() );

            event.setCancelled( true );
            Utils.sendDeniedMsg( event.getPlayer(), ipmutePunishments.get( 0 ) );
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

        List<Punishment> mutePuns = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true );
        if ( mutePuns.size() >= 1 ) {
            event.setCancelled( true );

            Collections.sort( mutePuns );
            for ( Punishment pun : mutePuns ) {
                Bukkit.broadcastMessage( pun.toString() ); // ! debug
            }


            Utils.sendDeniedMsg( event.getPlayer(), mutePuns.get( 0 ) );
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

        if ( ChatslowManager.getSlow() != 0
                && VaultUtils.hasPerms( event.getPlayer(), ConfigUtils.getStr( "general.staff-perm" ) ) == false
                && VaultUtils.hasPerms( event.getPlayer(), ConfigUtils.getStr( "chatslow.bypass-perm" ) ) == false ) {
            if ( CHAT_SLOW.containsKey( event.getPlayer() ) ) {
                long timeSince = Time.getCurrentTimestamp() - CHAT_SLOW.get( event.getPlayer() );
                if ( timeSince < ChatslowManager.getSlow() ) {
                    event.setCancelled( true );

                    if ( ConfigUtils.getStr( "chatslow.msg" ).equals( "" ) == false ) {
                        event.getPlayer().sendMessage( ConfigUtils.getColoredStr( "chatslow.msg" ).replace( "[AMOUNT]", ChatslowManager.getSlow() + "" ) );
                    }
                    return;
                }
            }

            CHAT_SLOW.put( event.getPlayer(), Time.getCurrentTimestamp() );
        }
    }
}
