package com.github.cyberryan1.netuno.skript.elements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.skript.elements.expressions.types.StringExpression;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class ExprBanLength extends StringExpression {

    private Expression<OfflinePlayer> player;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean init( Expression<?> exprs[], int matchedPattern, Kleenean isDelayed, ParseResult parser ) {
        player = ( Expression<OfflinePlayer> ) exprs[0];
        return true;
    }

    @Override
    public String toString( @Nullable Event event, boolean debug ) {
        return "length of netuno ban [of] %offlineplayer%" + player.toString( event, debug );
    }

    @Override
    @Nullable
    public String[] get( Event event ) {
        OfflinePlayer p = player.getSingle( event );
        if ( p != null ) {
            if ( p.isOnline() ) {

            }

//            List<ApiPunishment> punishments = ApiNetuno.getData().getPun().getPunishments( p );
//            if ( punishments.size() == 0 ) {
//                return null;
//            }
//
//            // returns the length of the punishment with the highest amount of time remaining
//            NPunishment longest = PunishmentUtils.getHighestActive( punishments, PunishmentType.BAN );
//            if ( longest == null ) { return null; }
//            return new String[] { longest.getLengthRemaining().asFullLength() };
        }
        return null;
    }
}