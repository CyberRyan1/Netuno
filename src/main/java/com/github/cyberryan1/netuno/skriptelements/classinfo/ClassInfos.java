package com.github.cyberryan1.netuno.skriptelements.classinfo;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

public class ClassInfos {

    static {
        Classes.registerClass( new ClassInfo<>( NPunishment.class, "punishment" )
                .user( "punishment" )
                .name( "Punishment" )
                .description( "Represents a netuno punishment" )
                .defaultExpression( new EventValueExpression<>( NPunishment.class ) )
                .parser( new Parser<>() {
                    @Override
                    public boolean canParse( ParseContext context ) {
                        return false;
                    }

                    @Override
                    public String toVariableNameString( NPunishment pun ) {
                        return "netuno punishment";
                    }

                    @Override
                    public String toString( NPunishment pun, int flags ) {
                        return toVariableNameString( pun );
                    }
                } )
        );
    }

}