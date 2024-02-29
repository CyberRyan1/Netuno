package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;


public class ExprPunishmentLength extends SimplePropertyExpression<NPunishment, Timespan> {

    static {
        register( ExprPunishmentLength.class, Timespan.class, "length", "punishment" );
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected String getPropertyName() {
        return "length";
    }

    @Override
    
    public Timespan convert( NPunishment punishment ) {
        // punishment.getLength() returns the amount of seconds the length was,
        //      but the timespan class uses milliseconds
        return new Timespan( punishment.getLength() * 1000L );
    }

}