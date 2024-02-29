package com.github.cyberryan1.netuno.skriptelements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.skriptelements.events.bukkitevents.NetunoPunishmentBukkitEvent;
import com.github.cyberryan1.netunoapi.events.NetunoEvent;
import com.github.cyberryan1.netunoapi.events.NetunoEventListener;
import com.github.cyberryan1.netunoapi.events.punish.NetunoPrePunishEvent;
import org.bukkit.Bukkit;

public class EvtPlayerNetunoPunished /*extends SkriptEvent*/ {

    static {
        Skript.registerEvent(
                "Player Netuno Punished",
                SimpleEvent.class,
                NetunoPunishmentBukkitEvent.class,
                "netuno punish[ment] [[of] player]"
        );

//        // Registering an event value for this event, in this case, the punishment
//        EventValues.registerEventValue( NetunoPunishmentBukkitEvent.class, NPunishment.class,
//                new Getter<>() {
//                    @Override
//                    public  NPunishment get( NetunoPunishmentBukkitEvent event ) {
//                        return event.getPunishment();
//                    }
//        }, 0 ); // 0 = present, -1 = past, 1 = future. If unknown, pick 0 lol

        ApiNetuno.getInstance().getEventDispatcher().addListener( new PostPunishmentApiEventListener() );
    }

    public static void initialize() {
////        Skript.registerEvent(
////                "Player Netuno Punished",
////                EvtPlayerNetunoPunished.class,
////                NetunoPostPunishmentBukkitEvent.class,
////                "netuno punish[ment] [[of] player]"
////        );
//        Skript.registerEvent(
//                "Player Netuno Punished",
//                SimpleEvent.class,
//                NetunoPunishmentBukkitEvent.class,
//                "netuno punish[ment] [[of] player]"
//        );
//
//        // Registering an event value for this event, in this case, the punishment
//        EventValues.registerEventValue( NetunoPunishmentBukkitEvent.class, NPunishment.class,
//                new Getter<>() {
//                    @Override
//                    public  NPunishment get( NetunoPunishmentBukkitEvent event ) {
//                        return event.getPunishment();
//                    }
//        }, 0 ); // 0 = present, -1 = past, 1 = future. If unknown, pick 0 lol
//
//        ApiNetuno.getInstance().getEventDispatcher().addListener( new PostPunishmentApiEventListener() );
    }

//    @Override
//    public boolean init( Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult ) {
//        return true;
//    }
//
//    @Override
//    public boolean check( Event event ) {
//        return true; // we always want the event to run, no matter what
//    }
//
//    @Override
//    public String toString(  Event event, boolean b ) {
//        return "Player netuno punished event";
//    }

//    public static class NetunoPostPunishmentBukkitEvent extends Event {
//
//        private final NPunishment punishment;
//
//        protected NetunoPostPunishmentBukkitEvent( NPunishment punishment ) {
//            this.punishment = punishment;
//        }
//
//        public NPunishment getPunishment() { return punishment; }
//
//        private static final HandlerList handlers = new HandlerList();
//
//        @Override
//        public @NotNull HandlerList getHandlers() {
//            return handlers;
//        }
//
//        public static HandlerList getHandlerList() {
//            return handlers;
//        }
//    }

    private static class PostPunishmentApiEventListener implements NetunoEventListener {
        @Override
        public void onEvent( NetunoEvent event ) {
            if ( event instanceof NetunoPrePunishEvent == false ) { return; }
            NetunoPrePunishEvent prePunish = ( NetunoPrePunishEvent ) event;
            Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
                Bukkit.getPluginManager().callEvent( new NetunoPunishmentBukkitEvent( prePunish.getPunishment() ) );
            } );
        }
    }
}
