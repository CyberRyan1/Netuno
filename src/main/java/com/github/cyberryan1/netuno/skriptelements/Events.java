package com.github.cyberryan1.netuno.skriptelements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.skriptelements.events.PunishmentBukkitEvent;
import com.github.cyberryan1.netuno.skriptelements.events.PunishmentNetunoApiListener;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.jetbrains.annotations.Nullable;

public class Events {

    static {
        // Add Netuno Punishment Event
        Skript.registerEvent(
                "netuno punish player",
                SimpleEvent.class,
                PunishmentBukkitEvent.class,
                "netuno punish[ment] [[of] player]"
        );
        EventValues.registerEventValue( PunishmentBukkitEvent.class, NPunishment.class, new Getter<NPunishment, PunishmentBukkitEvent>() {
            @Override
            @Nullable
            public NPunishment get( PunishmentBukkitEvent event ) {
                return event.getPunishment();
            }
        }, 0 );

        // Registers this so that we can convert netuno api events to bukkit events
        ApiNetuno.getInstance().getEventDispatcher().addListener( new PunishmentNetunoApiListener() );
        CyberLogUtils.logError( "events static thingy" );

//         		EventValues.registerEventValue(TownPreAddResidentEvent.class, Resident.class, new Getter<Resident, TownPreAddResidentEvent>() {
//        			@Override
//        			@Nullable
//        			public Resident get(TownPreAddResidentEvent event) {
//        				return event.getResident();
//        			}
//        		}, 0);
    }

}