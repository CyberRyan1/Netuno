package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.NetunoPlayer;
import com.github.cyberryan1.netuno.models.NetunoPunishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.settings.SettingsVariableFactory;
import com.github.cyberryan1.netuno.utils.settings.SoundSettingEntry;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ChatListener implements Listener {
    
    private final HashMap<UUID, Long> chatSlowdown = new HashMap<>();

    @EventHandler
    public void onPlayerChat( AsyncChatEvent event ) {
        // Don't do anything if the event is cancelled
        if ( event.isCancelled() ) return;

        // Loading the player from NetunoService
        final NetunoPlayer player;
        try {
            player = ( NetunoPlayer ) Netuno.SERVICE.getPlayer( event.getPlayer().getUniqueId() ).get();
        } catch ( ExecutionException | InterruptedException e ) {
            throw new RuntimeException( e );
        }

        // If the player has any mute or IP mute punishments,
        //      we disallow them from chatting
        boolean hadActiveMutePunishment = player.getPunishments().stream()
                .anyMatch( pun ->
                        pun.getType() == ApiPunishment.PunType.MUTE
                                && ( ( NetunoPunishment ) pun ).isActive_silent()
                );
        boolean hadActiveIpmutePunishment = player.getPunishments().stream()
                .anyMatch( pun ->
                        pun.getType() == ApiPunishment.PunType.IPMUTE
                                && ( ( NetunoPunishment ) pun ).isActive_silent()
                );
        final List<NetunoPunishment> activePunishments = player.getActivePunishments().stream()
                .map( pun -> ( NetunoPunishment ) pun )
                .collect( Collectors.toList() );
        if ( activePunishments.stream().anyMatch( pun -> pun.getType() == ApiPunishment.PunType.MUTE
                || pun.getType() == ApiPunishment.PunType.IPMUTE ) ) {
            final NetunoPunishment highestPunishment = PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activePunishments );
            denyChat_becausePunished( event, highestPunishment );
            return;
        }
        else {
            // if this is true, then the player's mute has just expired
            if ( hadActiveMutePunishment ) {
                // sending a message to the player
                new SettingsVariableFactory( Settings.MUTE_EXPIRE ).sendMsg( event.getPlayer() );

                // sending a message to online staff
                new SettingsVariableFactory( Settings.MUTE_EXPIRE_STAFF )
                        .target( event.getPlayer() )
                        .sendMsg( p -> CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) );
            }

            // if this is true, then the player's IP mute has just expired
            if ( hadActiveIpmutePunishment ) {
                // sending a message to the player
                new SettingsVariableFactory( Settings.IPMUTE_EXPIRE ).sendMsg( event.getPlayer() );

                // sending a message to online staff
                new SettingsVariableFactory( Settings.IPMUTE_EXPIRE_STAFF )
                        .target( event.getPlayer() )
                        .sendMsg( p -> CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) );
            }
        }

        // checking if chat is muted
        if ( Netuno.CHAT_SERVICE.isChatDisabled() ) {
            // checking if the player is able to bypass the mutechat
            if ( CyberVaultUtils.hasPerms( event.getPlayer(), Settings.STAFF_PERMISSION.string() ) == false
                    && CyberVaultUtils.hasPerms( event.getPlayer(), Settings.MUTECHAT_BYPASS_PERMISSION.string() ) == false ) {
                event.setCancelled( true );
                CyberMsgUtils.sendMessage( event.getPlayer(), Settings.MUTECHAT_ATTEMPT.coloredStringlist() );
                return;
            }
        }

        // checking if there is a non-zero chatslow
        if ( Netuno.CHAT_SERVICE.getChatSlowdown() != 0 ) {
            // ensuring that the player doesn't have a bypass permission
            if ( CyberVaultUtils.hasPerms( event.getPlayer(), Settings.STAFF_PERMISSION.string() ) == false
                    && CyberVaultUtils.hasPerms( event.getPlayer(), Settings.CHATSLOW_BYPASS_PERMISSION.string() ) == false ) {
                // Get the last time the player chatted
                Long lastChat = chatSlowdown.get( event.getPlayer().getUniqueId() );
                if ( lastChat != null ) {
                    long timeDiff = System.currentTimeMillis() - lastChat;
                    if ( timeDiff < ( Netuno.CHAT_SERVICE.getChatSlowdown() * 1000L ) ) {
                        event.setCancelled( true );
                        int secondsRemaining = ( int ) ( ( Netuno.CHAT_SERVICE.getChatSlowdown() * 1000L - timeDiff ) / 1000 ) + 1;
                        String msg = Settings.CHATSLOW_MESSAGE.coloredString().replace( "[AMOUNT]", secondsRemaining + "" );
                        CyberMsgUtils.sendMessage( event.getPlayer(), msg );
                        return;
                    }
                }

                // Update the last time the player chatted
                chatSlowdown.put( event.getPlayer().getUniqueId(), System.currentTimeMillis() );
            }
        }

        // checking if the player's message meets any of the watchlist items
        if ( Netuno.CHAT_SERVICE.getWatchlist().isEmpty() == false ) {
//            List<String> sentMessageSplit = Arrays.asList(
//                    PlainTextComponentSerializer.plainText().serialize( event.originalMessage() )
//                            .split( " " ) );
//
//            boolean matchesWatchlist = false;
//            outer:
//            for ( String str : sentMessageSplit ) {
//                for ( String watchlistItem : Netuno.CHAT_SERVICE.getWatchlist() ) {
//                    if ( str.matches( watchlistItem ) ) {
//                        matchesWatchlist = true;
//                        break outer;
//                    }
//                }
//            }

            String originalMsg = PlainTextComponentSerializer.plainText().serialize( event.originalMessage() );
            boolean match = false;
            for ( String item : Netuno.CHAT_SERVICE.getWatchlist() ) {
                if ( originalMsg.matches( item ) ) {
                    match = true;
                    break;
                }
            }

            if ( match ) {
                // we will have two audiences: one of the staff members and one of the regular players
                // the regular players will be sent the original message, and the staff members will be sent a different message

                // removing all online staff from the original message
                event.viewers().removeIf( audience -> {
                    if ( audience instanceof Player == false ) return false;
                    return CyberVaultUtils.hasPerms( ( Player ) audience, Settings.WATCHLIST_NOTIFS_VIEW_PERMISSION.string() );
                } );

                // creating a new message for online staff
                Audience staffAudience = Audience.audience( Bukkit.getOnlinePlayers().stream()
                        .filter( p -> CyberVaultUtils.hasPerms( p, Settings.WATCHLIST_NOTIFS_VIEW_PERMISSION.string() ) )
                        .toList() );
                Component prefix = LegacyComponentSerializer.legacyAmpersand().deserialize( Settings.WATCHLIST_NOTIFS_PREFIX.coloredString() );
                Component playerDisplayName = event.getPlayer().displayName();
                Component sentMsg = event.renderer().render(
                        event.getPlayer(),
                        playerDisplayName,
                        event.message(),
                        staffAudience
                );
                Component staffMsg = prefix.append( sentMsg );
                staffAudience.sendMessage( staffMsg );
                Settings.WATCHLIST_NOTIFS_SOUND.sound().playSoundMany( pl -> CyberVaultUtils.hasPerms( pl, Settings.WATCHLIST_NOTIFS_VIEW_PERMISSION.string() ) );
            }
        }
    }

    private void denyChat_becausePunished( AsyncChatEvent event, NetunoPunishment pun ) {
        event.setCancelled( true );

        // Sending the attempt message to the player
        Settings settingToFill = PunishmentLibrary.getSettingForMessageType( pun.getType(), PunishmentLibrary.MessageSetting.ATTEMPT );
        Component component = pun.fillSettingMessage( settingToFill );
        event.getPlayer().sendMessage( component );

        // Playing a sound for the player
        SoundSettingEntry sound = Settings.SOUND_PUNISHED_ATTEMPT_IPMUTED.sound();
        if ( pun.getType() == ApiPunishment.PunType.MUTE ) sound = Settings.SOUND_PUNISHED_ATTEMPT_MUTED.sound();
        if ( sound == null ) return;
        sound.playSound( event.getPlayer() );
    }
}