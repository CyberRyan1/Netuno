package com.github.cyberryan1.netuno.skriptelements.conditions;

import ch.njol.skript.Skript;

public final class RegisterConditions {

    public static void register() {
        // CondPlayerNetunoBanned
        Skript.registerCondition( CondPlayerNetunoBanned.class, "%offlineplayer% (1¦is|2¦is(n't¦ not)) [currently] netuno banned" );
    }
}