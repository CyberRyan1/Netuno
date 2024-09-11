package com.github.cyberryan1.netuno.listeners;

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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

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
            denyChat( event, highestPunishment );
            return;
        }
    }

    private void denyChat( AsyncChatEvent event, Punishment pun ) {
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