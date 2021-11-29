package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.skriptelements.expressions.types.StringExpression;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;

public class ExprMuteLength extends StringExpression {

    Expression<OfflinePlayer> player;

    @SuppressWarnings( "unchecked" )
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
            ArrayList<Punishment> punishments = Utils.getDatabase().getPunishment( p.getUniqueId().toString(), "mute", true );
            if ( punishments.size() == 0 ) { return null; }

            Punishment highest = punishments.get( 0 );
            for ( Punishment pun : punishments ) {
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
        return "length of netuno mute [of] %offlineplayer%" + player.toString( event, debug );
    }
}