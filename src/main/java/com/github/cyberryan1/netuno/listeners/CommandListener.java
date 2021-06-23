package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class CommandListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
        if ( DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "mute", true ).size() >= 1
                || DATA.getIPPunishment( event.getPlayer().getUniqueId().toString(), "ipmute", true ).size() >= 1 ) {
            if ( ConfigUtils.checkListNotEmpty( "mute.blocked-cmds" ) ) {
                ArrayList<String> blockedCmds = ConfigUtils.getColoredStrList( "mute.blocked-cmds" );
                String cmd = event.getMessage().split( " " )[0];

                if ( blockedCmds.contains( cmd ) ) {
                    event.setCancelled( true );
                    event.getPlayer().sendMessage( ConfigUtils.getColoredStr( "mute.blocked-cmd-msg" ) );
                }
            }
        }
    }
}