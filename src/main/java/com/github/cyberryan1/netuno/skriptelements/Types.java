package com.github.cyberryan1.netuno.skriptelements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;

public class Types {

    static {
        Classes.registerClass( new ClassInfo<>( NPunishment.class, "punishment" )
                .user( "punishment" )
                .name( "Punishment" )
                .defaultExpression( new EventValueExpression<>( NPunishment.class ) )
                .parser( new Parser<NPunishment>() {
                    @Override
                    public boolean canParse( ParseContext context ) {
                        return false;
                    }

                    @Override
                    public NPunishment parse( String input, ParseContext context ) {
                        return null;
                    }

                    @Override
                    public String toString( NPunishment punishment, int flags ) {
                        return "Punishment: " + punishment.toString();
                    }

                    @Override
                    public String toVariableNameString( NPunishment punishment ) {
                        return "punishment_" + punishment.getId();
                    }
                } ) );
    }

    // 		Classes.registerClass(new ClassInfo<>(Town.class, "town")
    //				.user("towns?")
    //				.name("Town")
    //				.defaultExpression(new EventValueExpression<>(Town.class))
    //				.parser(new Parser<Town>() {
    //
    //					@Override
    //					@Nullable
    //					public Town parse(String input, ParseContext context) {
    //						return TownyAPI.getInstance().getTown(input);
    //					}
    //
    //					@Override
    //					public boolean canParse(ParseContext context) {
    //						return true;
    //					}
    //
    //					@Override
    //					public String toString(Town town, int flags) {
    //						return "Town: " + town.getName();
    //					}
    //
    //					@Override
    //					public String toVariableNameString(Town town) {
    //						return town.getName();
    //					}
    //
    //		}));
}