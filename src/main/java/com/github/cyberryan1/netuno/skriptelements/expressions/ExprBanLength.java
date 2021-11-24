package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ExprBanLength extends SimpleExpression<String> {

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
        return "length of netuno ban [of] %offlineplayer%";
    }

    @Override
    @Nullable
    public String[] get( Event event ) {
        OfflinePlayer p = player.getSingle( event );
        if ( p != null ) {
            ArrayList<Punishment> punishments = Utils.getDatabase().getPunishment( p.getUniqueId().toString(), "ban", true );
            if ( punishments.size() == 0 ) {
                 return null;
            }

            // returns the length of the punishment with the highest amount of time remaining
            Punishment longestRemainingTime = punishments.get( 0 );
            for ( Punishment pun : punishments ) {
                if ( pun.getExpirationDate() > longestRemainingTime.getExpirationDate() ) {
                    longestRemainingTime = pun;
                }
            }

            return new String[] { Time.getLengthFromTimestamp( longestRemainingTime.getLength() ) + "" };
        }
        return null;
    }
}
