package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.NetunoPlayer;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.ExecutionException;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand( PlayerCommandPreprocessEvent event ) {
        // Loading the player from NetunoService
        // Since the player must be online to send a command, and since
        //      we cache all online players, this should always work
        final NetunoPlayer player;
        try {
            player = ( NetunoPlayer ) Netuno.SERVICE.getPlayer( event.getPlayer().getUniqueId() ).get();
        } catch ( ExecutionException | InterruptedException e ) {
            throw new RuntimeException( e );
        }

        // If the player has any mute or IP mute punishments, then
        //      we may need to cancel the command
        if ( player.isPunished( ApiPunishment.PunType.MUTE, ApiPunishment.PunType.IPMUTE ) ) {
            // Getting the command sent by the player
            final String command = event.getMessage().split( " " )[0]; // including the slash at the start

            for ( String blockedCmd : Settings.MUTE_BLOCKED_COMMANDS.stringlist() ) {
                if ( blockedCmd.equalsIgnoreCase( command ) ) {
                    // if this is true, then we need to block the command
                    event.setCancelled( true );
                    event.getPlayer().sendMessage( Settings.MUTE_BLOCKED_COMMAND_MESSAGE.coloredString() );
                    return;
                }
            }
        }
    }
}