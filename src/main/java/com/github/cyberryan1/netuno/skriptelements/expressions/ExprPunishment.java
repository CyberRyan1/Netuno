//package com.github.cyberryan1.netuno.skriptelements.expressions;
//
//import ch.njol.skript.lang.Expression;
//import ch.njol.skript.lang.SkriptParser;
//import ch.njol.skript.lang.util.SimpleExpression;
//import ch.njol.util.Kleenean;
//import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
//import org.bukkit.event.Event;
//
//
//public class ExprPunishment extends SimpleExpression<NPunishment> {
//
//    private Expression<NPunishment> pun;
//
//    @Override
//    public boolean init( Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult ) {
//        pun = ( Expression<NPunishment> ) exprs[0];
//        return true;
//    }
//
//    @Override
//    public boolean isSingle() {
//        return true;
//    }
//
//    @Override
//    public Class<? extends NPunishment> getReturnType() {
//        return NPunishment.class;
//    }
//
//    
//    @Override
//    protected NPunishment[] get( Event event ) {
//        NPunishment p = pun.getSingle( event );
//        return p == null ? null : new NPunishment[] { p };
//    }
//
//    @Override
//    public String toString(  Event event, boolean debug ) {
//        return "Punishment expression " + pun.toString( event, debug );
//    }
//}