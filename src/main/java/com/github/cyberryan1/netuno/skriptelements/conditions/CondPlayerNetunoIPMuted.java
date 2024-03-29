package com.github.cyberryan1.netuno.skriptelements.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.skriptelements.conditions.types.RegularCondition;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class CondPlayerNetunoIPMuted extends RegularCondition {

    Expression<OfflinePlayer> player;

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean init( Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult ) {
        player = ( Expression<OfflinePlayer> ) exprs[0];
        setNegated( parseResult.mark == 1 );
        return true;
    }

    @Override
    public boolean check( Event event ) {
        OfflinePlayer p = player.getSingle( event );
        if ( p == null ) { return isNegated(); }

        return ( ApiNetuno.getData().getPun()
                .getPunishments( p ).stream()
                .anyMatch( pun -> pun.getPunishmentType() == PunishmentType.IPMUTE && pun.isActive() ) )
                ? isNegated() : !isNegated();
    }

    @Override
    public String toString( @Nullable Event event, boolean debug ) {
        return "%offlineplayer% (1¦is|2¦is(n't¦ not)) [currently] netuno ipmuted" + player.toString( event, debug );
    }
}
