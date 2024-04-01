package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

public class ExprPunishmentReason extends SimplePropertyExpression<NPunishment, String> {

    static {
        register( ExprPunishmentReason.class, String.class, "reason", "punishment" );
        CyberLogUtils.logError( "registered ExprPunishmentReason.class" );
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String convert( NPunishment punishment ) {
        CyberLogUtils.logError( "converting punishment to reason : punishment.getReason() == " + punishment.getReason() );
        return punishment.getReason();
    }

    @Override
    protected String getPropertyName() {
        return "reason";
    }
}


/*
@Name("Town Level")
@Description("Get the level of a Town.")
public class ExprTownLevel extends SimplePropertyExpression<Town, Integer> {

	static {
		register(ExprTownLevel.class, Integer.class, "town level", "towns");
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public @Nullable Integer convert(Town town) {
		return town.getLevel();
	}

	@Override
	protected String getPropertyName() {
		return "town level";
	}

}
 */