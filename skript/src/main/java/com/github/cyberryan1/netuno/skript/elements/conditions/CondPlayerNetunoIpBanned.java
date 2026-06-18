package com.github.cyberryan1.netuno.skript.elements.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.skript.NetunoSkript;
import com.github.cyberryan1.netuno.skript.elements.conditions.types.RegularCondition;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

public class CondPlayerNetunoIpBanned extends RegularCondition {

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

        if ( p.isOnline() )
            return NetunoSkript.SERVICE.getPlayerNow( p.getPlayer() ).get().isPunished( ApiPunishment.PunType.IPBAN ) ? isNegated() : !isNegated();
        else
            return NetunoSkript.SERVICE.getPlayer( p ).join().isPunished( ApiPunishment.PunType.IPBAN ) ? isNegated() : !isNegated();
    }

    @Override
    public String toString( Event event, boolean debug ) {
        return "%offlineplayer% (1¦is|2¦is(n't¦ not)) [currently] netuno ipbanned" + player.toString( event, debug );
    }
}