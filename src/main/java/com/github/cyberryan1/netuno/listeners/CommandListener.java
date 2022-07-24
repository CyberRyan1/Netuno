package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
        if ( DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true ).size() >= 1
                || DATA.getIPPunishment( event.getPlayer().getUniqueId().toString(), "ipmute", true ).size() >= 1 ) {
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