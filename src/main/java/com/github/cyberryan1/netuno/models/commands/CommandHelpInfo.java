package com.github.cyberryan1.netuno.models.commands;

import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandHelpInfo {

    private static final List<CommandHelpInfo> REGISTRY = new ArrayList<>();

    public static List<CommandHelpInfo> getRegistry() {
        return REGISTRY;
    }

    // Class methods

    private final BaseCommand command;
    private final int helpOrder;

    public CommandHelpInfo( BaseCommand command, int helpOrder ) {
        this.command = command;
        this.helpOrder = helpOrder;

        for ( int index = 0; index < REGISTRY.size(); index++ ) {
            if ( REGISTRY.get( index ).getHelpOrder() > helpOrder ) {
                REGISTRY.add( index, this );
            }
        }
    }

    public BaseCommand getCommand() {
        return command;
    }

    public int getHelpOrder() {
        return helpOrder;
    }
}