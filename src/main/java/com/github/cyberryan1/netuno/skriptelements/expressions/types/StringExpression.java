package com.github.cyberryan1.netuno.skriptelements.expressions.types;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

public abstract class StringExpression extends SimpleExpression<String> {

    @SuppressWarnings( "unchecked" )
    @Override
    public abstract boolean init( Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult );

    
    @Override
    protected abstract String[] get( Event event );

    @Override
    public abstract boolean isSingle();

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public abstract String toString(  Event event, boolean debug );
}