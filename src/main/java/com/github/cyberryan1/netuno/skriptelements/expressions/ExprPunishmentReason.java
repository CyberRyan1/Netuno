package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;


public class ExprPunishmentReason extends SimplePropertyExpression<NPunishment, String> {

    static {
        register( ExprPunishmentReason.class, String.class, "reason", "punishment" );
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "reason";
    }

    @Override
    
    public String convert( NPunishment punishment ) {
        return punishment.getReason();
    }

}