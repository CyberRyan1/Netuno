//package com.github.cyberryan1.netuno.skriptelements.expressions;
//
//import ch.njol.skript.lang.Expression;
//import ch.njol.skript.lang.SkriptParser;
//import ch.njol.util.Kleenean;
//import com.github.cyberryan1.netuno.skriptelements.expressions.types.StringExpression;
//import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
//import org.bukkit.event.Event;
//
//import javax.annotation.Nullable;
//
//public class ExprPunLength extends StringExpression {
//
//    private Expression<NPunishment> punishment;
//
//    @Override
//    public Class<? extends String> getReturnType() {
//        return String.class;
//    }
//
//    @Override
//    public boolean isSingle() {
//        return true;
//    }
//
//    @SuppressWarnings( "unchecked" )
//    @Override
//    public boolean init( Expression<?> exprs[], int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser ) {
//        punishment = ( Expression<NPunishment> ) exprs[0];
//        return true;
//    }
//
//    @Override
//    public String toString(  Event event, boolean debug ) {
//        return "length of %punishment%" + punishment.toString( event, debug );
//    }
//
//    @Override
//    
//    public String[] get( Event event ) {
//        NPunishment pun = punishment.getSingle( event );
//        return pun == null ? null : new String[] { pun.getLengthRemaining().asFullLength() };
//    }
//}