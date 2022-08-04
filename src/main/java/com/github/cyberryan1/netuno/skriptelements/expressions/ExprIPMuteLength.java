package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.skriptelements.expressions.types.StringExpression;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class ExprIPMuteLength extends StringExpression {

    Expression<OfflinePlayer> player;

    @Override
    public boolean init( Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult ) {
        player = ( Expression<OfflinePlayer> ) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected String[] get( Event event ) {
        OfflinePlayer p = player.getSingle( event );
        if ( p != null ) {
            List<NPunishment> punishments = ApiNetuno.getData().getPun().getPunishments( p );
            if ( punishments.size() == 0 ) { return null; }

            NPunishment highest = PunishmentUtils.getHighestActive( punishments, PunishmentType.IPMUTE );
            if ( highest == null ) { return null; }
            return new String[] { highest.getLengthRemaining().asFullLength() };
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString( @Nullable Event event, boolean debug ) {
        return "length of netuno ipmute [of] %offlineplayer%" + player.toString( event, debug );
    }
}