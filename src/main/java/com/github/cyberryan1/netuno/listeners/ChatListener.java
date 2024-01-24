package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.MutechatManager;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    private final HashMap<Player, Long> CHAT_SLOW = new HashMap<>();

    @EventHandler( priority = EventPriority.HIGH )
    public void onPlayerChatEvent( AsyncPlayerChatEvent event ) {
        if ( event.isCancelled() ) { return; }

        // IP Mute and Mute Handling
        final List<NPunishment> all = NetunoPlayerCache.getOrLoad( event.getPlayer().getUniqueId().toString() ).getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.MUTE || pun.getPunishmentType() == PunishmentType.IPMUTE )
                .collect( Collectors.toList() );
        final List<NPunishment> active = PunishmentUtils.getActive( all );

        if ( active.size() >= 1 ) {
            event.setCancelled( true );

            final NPunishment highestIpmute = PunishmentUtils.getHighestActive( active, PunishmentType.IPMUTE );
            final NPunishment highestMute = PunishmentUtils.getHighestActive( active, PunishmentType.MUTE );

            if ( highestIpmute != null ) {
                Settings.SOUND_PUNISHED_ATTEMPT_IPMUTED.sound().playSound( event.getPlayer() );
                Utils.sendDeniedMsg( event.getPlayer(), highestIpmute );
            }
            else {
                Settings.SOUND_PUNISHED_ATTEMPT_MUTED.sound().playSound( event.getPlayer() );
                Utils.sendDeniedMsg( event.getPlayer(), highestMute );
            }
            return;
        }

        // IP Mute and Mute Notification Handling
        final List<NPunishment> allActiveInDatabase = all.stream()
                .filter( NPunishment::dataIsActive )
                .collect( Collectors.toList() );
        if ( allActiveInDatabase.size() > 0 ) {
            boolean wasIpmuted = allActiveInDatabase.stream()
                    .anyMatch( pun -> pun.getPunishmentType() == PunishmentType.IPMUTE );

            String expireMsg = null;
            String staffExpireMsg = null;

            if ( wasIpmuted ) {
                expireMsg = Utils.getCombinedString( Settings.IPMUTE_EXPIRE.coloredStringlist() );
                staffExpireMsg = Utils.getCombinedString( Settings.IPMUTE_EXPIRE_STAFF.coloredStringlist() );
            }
            else {
                expireMsg = Utils.getCombinedString( Settings.MUTE_EXPIRE.coloredStringlist() );
                staffExpireMsg = Utils.getCombinedString( Settings.MUTE_EXPIRE_STAFF.coloredStringlist() );
            }

            if ( expireMsg != null && expireMsg.isBlank() == false ) {
                Utils.sendAnyMsg( event.getPlayer(), expireMsg );
            }

            if ( staffExpireMsg != null && staffExpireMsg.isBlank() == false ) {
                staffExpireMsg = staffExpireMsg.replace( "[TARGET]", event.getPlayer().getName() );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) ) {
                        Utils.sendAnyMsg( p, staffExpireMsg );
                    }
                }
            }

            for ( NPunishment pun : allActiveInDatabase ) {
                pun.setActive( false );
                ApiNetuno.getData().getNetunoPuns().updatePunishment( pun );
            }
        }

        // Mutechat handling
        if ( MutechatManager.chatIsMuted() ) {
            if ( CyberVaultUtils.hasPerms( event.getPlayer(), Settings.STAFF_PERMISSION.string() ) == false ) {
                if ( CyberVaultUtils.hasPerms( event.getPlayer(), Settings.MUTECHAT_BYPASS_PERMISSION.string() ) == false ) {
                    event.setCancelled( true );

                    String mutechatAttemptList[] = YMLUtils.getConfig().getColoredStrList( "mutechat.attempt" );
                    if ( mutechatAttemptList != null && mutechatAttemptList.length > 0 ) {
                        Utils.sendAnyMsg( event.getPlayer(), Utils.getCombinedString( mutechatAttemptList ) );
                    }
                    return;
                }
            }
        }

        // Chatslow handling
        if ( ChatslowManager.getSlow() != 0
                && CyberVaultUtils.hasPerms( event.getPlayer(), Settings.STAFF_PERMISSION.string() ) == false
                && CyberVaultUtils.hasPerms( event.getPlayer(), Settings.CHATSLOW_BYPASS_PERMISSION.string() ) == false ) {
            if ( CHAT_SLOW.containsKey( event.getPlayer() ) ) {
                long timeSince = TimeUtils.getCurrentTimestamp() - CHAT_SLOW.get( event.getPlayer() );
                if ( timeSince < ChatslowManager.getSlow() ) {
                    event.setCancelled( true );

                    if ( Settings.CHATSLOW_MESSAGE.string().equals( "" ) == false ) {
                        event.getPlayer().sendMessage( Settings.CHATSLOW_MESSAGE.coloredString().replace( "[AMOUNT]", ChatslowManager.getSlow() + "" ) );
                    }
                    return;
                }
            }

            CHAT_SLOW.put( event.getPlayer(), TimeUtils.getCurrentTimestamp() );
        }

        if ( event.isCancelled() ) { return; }

        // Watchlist handling
//        boolean onWatchlist = false;
        if ( WatchlistManager.getWordsList().stream()
                .anyMatch( word -> event.getMessage().contains( word ) )
                || WatchlistManager.getPatternsList().stream()
                .anyMatch( pattern -> event.getMessage().matches( pattern ) ) ) {
//            onWatchlist = true;
            event.setFormat( Settings.WATCHLIST_NOTIFS_PREFIX.coloredString() + event.getFormat() );
            Settings.WATCHLIST_NOTIFS_SOUND.sound()
                    .playSoundMany( player -> CyberVaultUtils.hasPerms( player, Settings.WATCHLIST_VIEW_PERMISSION.string() ) );
        }

//        // If click to command messages is enabled, we have to send the messages a bit of a special way
//        if ( Settings.CHATMOD_COMMAND_CLICK_ENABLED.bool() ) {
//            event.setCancelled( true );
//
//            String FORMATTED_MSG = event.getFormat().replaceFirst( "%s", "%t" )
//                    .replaceFirst( "%s", event.getMessage() )
//                    .replaceFirst( "%t", "%s" );
//
//            // Removing all uncolored color codes from the message
//            final String COLOR_CODES[] = { "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0",
//                    "&a", "&b", "&c", "&d", "&e", "&f", "&r", "&m", "&n", "&o", "&k", "&l" };
//            for ( String color : COLOR_CODES ) {
//                FORMATTED_MSG = FORMATTED_MSG.replace( color, "" );
//            }
//
//            String msgSending = FORMATTED_MSG.replace( "%s", event.getPlayer().getDisplayName() );
//            for ( Player player : Bukkit.getOnlinePlayers() ) {
//                if ( CyberVaultUtils.hasPerms( player, Settings.CHATMOD_COMMAND_CLICK_PERMISSION.string() ) == false ) {
//                    CyberMsgUtils.sendMsg( player, msgSending );
//                }
//            }
//
//            // Adding the watchlist prefix to the message, if needed
//            String prefix = onWatchlist ? Settings.CHATMOD_WATCHLIST_NOTIFS_PREFIX.coloredString() : "";
//            FORMATTED_MSG = prefix + FORMATTED_MSG;
//
//            final BaseComponent MSG_COMPONENTS[] = TextComponent.fromLegacyText( FORMATTED_MSG );
//            final BaseComponent DISPLAY_NAME_COMPONENTS[] = TextComponent.fromLegacyText( event.getPlayer().getDisplayName() );
//
//            // Adding the click event to the display name components
//            for ( BaseComponent component : DISPLAY_NAME_COMPONENTS ) {
//                component.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND,
//                        Settings.CHATMOD_COMMAND_CLICK_COMMAND.string()
//                                .replace( "[TARGET]", event.getPlayer().getName() )
//                ) );
//            }
//
//            // Combining the components into one list
//            List<BaseComponent> COMBINED_COMPONENTS = new ArrayList<>();
//            for ( int index = 0; index < MSG_COMPONENTS.length; index++ ) {
//                BaseComponent current = MSG_COMPONENTS[ index ];
//                if ( current.toPlainText().equals( CyberColorUtils.getUncolored( event.getPlayer().getDisplayName() ) ) ) {
//                    COMBINED_COMPONENTS.addAll( List.of( DISPLAY_NAME_COMPONENTS ) );
//                }
//                else {
//                    COMBINED_COMPONENTS.add( current );
//                }
//            }
//
//            for ( Player player : Bukkit.getOnlinePlayers() ) {
//                if ( CyberVaultUtils.hasPerms( player, Settings.CHATMOD_COMMAND_CLICK_PERMISSION.string() ) ) {
//                    player.spigot().sendMessage( COMBINED_COMPONENTS.toArray( new BaseComponent[0] ) );
//                }
//            }
//        }
    }
}
