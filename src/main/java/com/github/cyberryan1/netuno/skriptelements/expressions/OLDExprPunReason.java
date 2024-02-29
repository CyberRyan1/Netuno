//package com.github.cyberryan1.netuno.skriptelements.expressions;
//
//import com.github.cyberryan1.netuno.skriptelements.expressions.types.PunYieldStringExpression;
//import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
//import org.bukkit.event.Event;
//import org.jetbrains.annotations.Nullable;
//
//public class ExprPunReason extends PunYieldStringExpression {
//
//    @Override
//    public String toString(  Event event, boolean debug ) {
//        return "reason of %punishment%" + punishment.toString( event, debug );
//    }
//
//    
//    @Override
//    public String[] get( Event event ) {
//        NPunishment pun = punishment.getSingle( event );
//        return pun == null ? null : new String[] { pun.getReason() };
//    }
//
//}