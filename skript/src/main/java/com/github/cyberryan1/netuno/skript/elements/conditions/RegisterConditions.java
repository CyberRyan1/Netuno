package com.github.cyberryan1.netuno.skript.elements.conditions;

import ch.njol.skript.Skript;

public final class RegisterConditions {

    public static void register() {
        // CondPlayerNetunoBanned
        Skript.registerCondition( CondPlayerNetunoBanned.class, "%offlineplayer% (1¦is|2¦is(n't| not)) [currently] netuno banned" );

        // CondPlayerNetunoMuted
        Skript.registerCondition( CondPlayerNetunoMuted.class, "%offlineplayer% (1¦is|2¦is(n't| not)) [currently] netuno muted" );

        // CondPlayerNetunoIPMuted
        Skript.registerCondition( CondPlayerNetunoIpMuted.class, "%offlineplayer% (1¦is|2¦is(n't| not)) [currently] netuno ipmuted" );

        // CondPlayerNetunoIPBanned
        Skript.registerCondition( CondPlayerNetunoIpBanned.class, "%offlineplayer% (1¦is|2¦is(n't| not)) [currently] netuno ipbanned" );
    }
}