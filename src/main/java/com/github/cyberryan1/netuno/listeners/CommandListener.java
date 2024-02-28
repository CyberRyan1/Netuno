package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements Listener {

    @EventHandler
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
        NPlayer nPlayer = ApiNetuno.getInstance().getPlayerLoader().load( event.getPlayer() );
        boolean anyActive = nPlayer.getPunishments().stream()
                .anyMatch( pun -> pun.isActive()
                        && ( pun.getPunishmentType() == PunishmentType.MUTE || pun.getPunishmentType() == PunishmentType.IPMUTE )
                );

        if ( anyActive ) {
            String blockedCmdsList[] = YMLUtils.getConfig().getStrList( "mute.blocked-cmds" );
            if ( blockedCmdsList != null && blockedCmdsList.length > 0 ) {
                ArrayList<String> blockedCmds = new ArrayList<>( List.of( blockedCmdsList ) );
                String cmd = event.getMessage().split( " " )[0];

                if ( blockedCmds.contains( cmd ) ) {
                    event.setCancelled( true );
                    event.getPlayer().sendMessage( YMLUtils.getConfig().getColoredStr( "mute.blocked-cmd-msg" ) );
                }
            }
        }
    }
}