package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;

public final class RegisterExpressions {

    public static void register() {

        // ExprBanLength
        Skript.registerExpression( ExprBanLength.class, String.class, ExpressionType.COMBINED, "length of netuno ban [of] %offlineplayer%" );
    }
}
