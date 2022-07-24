package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
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

            Collections.sort( ipmutePunishments );

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

            Utils.sendDeniedMsg( event.getPlayer(), mutePuns.get( 0 ) );
            return;
        }

        // ipmute notification handling
        if ( hadActiveIpMute ) {
            String ipmuteExpireList[] = YMLUtils.getConfig().getColoredStrList( "ipmute.expire-list" );
            if ( ipmuteExpireList != null && ipmuteExpireList.length > 0 ) {
                Utils.sendAnyMsg( event.getPlayer(), Utils.getCombinedString( ipmuteExpireList ) );
            }

            String ipmuteExpireStaffList[] = YMLUtils.getConfig().getColoredStrList( "ipmute.expire-staff" );
            if ( ipmuteExpireStaffList != null && ipmuteExpireStaffList.length > 0 ) {
                String msg = Utils.getCombinedString( ipmuteExpireStaffList ).replace( "[TARGET]", event.getPlayer().getName() );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }
            }
        }

        // mute notification handling
        else if ( hadActiveMute ) {
            String muteExpireList[] = YMLUtils.getConfig().getColoredStrList( "mute.expire-list" );
            if ( muteExpireList != null && muteExpireList.length > 0 ) {
                Utils.sendAnyMsg( event.getPlayer(), Utils.getCombinedString( muteExpireList ) );
            }

            String muteExpireStaffList[] = YMLUtils.getConfig().getColoredStrList( "mute.expire-staff" );
            if ( muteExpireStaffList != null && muteExpireStaffList.length > 0 ) {
                String msg = Utils.getCombinedString( muteExpireStaffList ).replace( "[TARGET]", event.getPlayer().getName() );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }
            }
        }

        // mutechat handling
        if ( MutechatManager.chatIsMuted() ) {
            if ( VaultUtils.hasPerms( event.getPlayer(), YMLUtils.getConfig().getStr( "general.staff-perm" ) ) == false ) {
                if ( VaultUtils.hasPerms( event.getPlayer(), YMLUtils.getConfig().getStr( "mutechat.bypass-perm" ) ) == false ) {
                    event.setCancelled( true );

                    String mutechatAttemptList[] = YMLUtils.getConfig().getColoredStrList( "mutechat.attempt" );
                    if ( mutechatAttemptList != null && mutechatAttemptList.length > 0 ) {
                        Utils.sendAnyMsg( event.getPlayer(), Utils.getCombinedString( mutechatAttemptList ) );
                    }
                    return;
                }
            }
        }

        if ( ChatslowManager.getSlow() != 0
                && VaultUtils.hasPerms( event.getPlayer(), YMLUtils.getConfig().getStr( "general.staff-perm" ) ) == false
                && VaultUtils.hasPerms( event.getPlayer(), YMLUtils.getConfig().getStr( "chatslow.bypass-perm" ) ) == false ) {
            if ( CHAT_SLOW.containsKey( event.getPlayer() ) ) {
                long timeSince = Time.getCurrentTimestamp() - CHAT_SLOW.get( event.getPlayer() );
                if ( timeSince < ChatslowManager.getSlow() ) {
                    event.setCancelled( true );

                    if ( YMLUtils.getConfig().getStr( "chatslow.msg" ).equals( "" ) == false ) {
                        event.getPlayer().sendMessage( YMLUtils.getConfig().getColoredStr( "chatslow.msg" ).replace( "[AMOUNT]", ChatslowManager.getSlow() + "" ) );
                    }
                    return;
                }
            }

            CHAT_SLOW.put( event.getPlayer(), Time.getCurrentTimestamp() );
        }
    }
}
