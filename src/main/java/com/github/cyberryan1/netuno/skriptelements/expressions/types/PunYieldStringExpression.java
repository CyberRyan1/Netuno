package com.github.cyberryan1.netuno.skriptelements.expressions.types;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.event.Event;

/**
 * This class is used to further abstract the expressions that
 * return the data of a Netuno Punishment ({@link NPunishment}),
 * which is stored in the Skript class info "punishment"
 * <i>(see {@link com.github.cyberryan1.netuno.skriptelements.classinfo.ClassInfos})</i>.
 * This class should only be used for data that is a String, such as
 * the reason of the punishment, the time remaining of a punishment, etc.
 */
public abstract class PunYieldStringExpression extends StringExpression {

    protected Expression<NPunishment> punishment;

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
    public boolean init( Expression<?> exprs[], int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser ) {
        punishment = ( Expression<NPunishment> ) exprs[0];
        return true;
    }

    public abstract String toString(  Event event, boolean debug );

    
    public abstract String[] get( Event event );
}