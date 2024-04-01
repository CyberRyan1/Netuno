package com.github.cyberryan1.netuno.skriptelements.events;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.events.NetunoEvent;
import com.github.cyberryan1.netunoapi.events.NetunoEventListener;
import com.github.cyberryan1.netunoapi.events.punish.NetunoPrePunishEvent;
import org.bukkit.Bukkit;

public class PunishmentNetunoApiListener implements NetunoEventListener {

    @Override
    public void onEvent( NetunoEvent event ) {
        CyberLogUtils.logError( "PunishmentNetunoApiListener onEvent" );
        if ( event instanceof NetunoPrePunishEvent == false ) { return; }
        NetunoPrePunishEvent prePunish = ( NetunoPrePunishEvent ) event;
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent( new PunishmentBukkitEvent( prePunish.getPunishment() ) );
        } );
    }
}