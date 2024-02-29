package com.github.cyberryan1.netuno.skriptelements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;

public final class RegisterExpressions {

    public static void register() {

        // ExprBanLength
        Skript.registerExpression( ExprBanLength.class, String.class, ExpressionType.COMBINED, "length of netuno ban [of] %offlineplayer%" );

        // ExprIPBanLength
        Skript.registerExpression( ExprIPBanLength.class, String.class, ExpressionType.COMBINED, "length of netuno ipban [of] %offlineplayer%" );

        // ExprMuteLength
        Skript.registerExpression( ExprMuteLength.class, String.class, ExpressionType.COMBINED, "length of netuno mute [of] %offlineplayer%" );

        // ExprIPMuteLength
        Skript.registerExpression( ExprIPMuteLength.class, String.class, ExpressionType.COMBINED, "length of netuno ipmute [of] %offlineplayer%" );

//        // ExprPunishment
//        Skript.registerExpression(
//                ExprPunishment.class,
//                NPunishment.class,
//                ExpressionType.SIMPLE,
//                "[the] pun[ishment]"
//        );
    }
}
