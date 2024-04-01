package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

public class ExprPunishmentDuration extends SimplePropertyExpression<NPunishment, Number> {

    static {
        register( ExprPunishmentDuration.class, Number.class, "duration", "punishment" );
        CyberLogUtils.logError( "registered ExprPunishmentLength.class" );
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public Number convert( NPunishment punishment ) {
        CyberLogUtils.logError( "converting punishment to length : length == " + punishment.getLength() );
        return punishment.getLength();
    }

    @Override
    protected String getPropertyName() {
        return "duration";
    }
}