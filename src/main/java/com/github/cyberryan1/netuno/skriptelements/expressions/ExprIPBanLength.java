package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.skriptelements.expressions.types.StringExpression;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;

public class ExprIPBanLength extends StringExpression {

    Expression<OfflinePlayer> player;

    @Nullable
    @Override
    protected String[] get( Event event ) {
        OfflinePlayer p = player.getSingle( event );
        if ( p != null ) {
            ArrayList<IPPunishment> punishments = Utils.getDatabase().getIPPunishment( p.getUniqueId().toString(), "ipban", true );
            if ( punishments.size() == 0 ) { return null; }

            IPPunishment highest = punishments.get( 0 );
            for ( IPPunishment pun : punishments ) {
                if ( pun.getExpirationDate() > highest.getExpirationDate() ) {
                    highest = pun;
                }
            }

            return new String[] { Time.getLengthFromTimestamp( highest.getLength() ) };
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString( @Nullable Event event, boolean debug ) {
        return "length of netuno ipban [of] %offlineplayer%: " + player.toString( event, debug );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean init( Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult ) {
        player = ( Expression<OfflinePlayer> ) exprs[0];
        return true;
    }
}
