package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.NPlayer;
import com.github.cyberryan1.netuno.models.Punishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.settings.SoundSettingEntry;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
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
        // Loading the player from NetunoService
        final NPlayer player;
        try {
            player = ( NPlayer ) Netuno.SERVICE.getPlayer( event.getPlayer().getUniqueId() ).get();
        } catch ( ExecutionException | InterruptedException e ) {
            throw new RuntimeException( e );
        }

        // If the player has any mute or IP mute punishments,
        //      we disallow them from chatting
        final List<Punishment> activePunishments = player.getActivePunishments().stream()
                .map( pun -> ( Punishment ) pun )
                .collect( Collectors.toList() );
        if ( activePunishments.stream().anyMatch( pun -> pun.getType() == ApiPunishment.PunType.MUTE
                || pun.getType() == ApiPunishment.PunType.IPMUTE ) ) {
            final Punishment highestPunishment = PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activePunishments );
            denyChat_becausePunished( event, highestPunishment );
            return;
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
    }

    private void denyChat_becausePunished( AsyncChatEvent event, Punishment pun ) {
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