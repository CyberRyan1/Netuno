package com.github.cyberryan1.netuno.models.commands;

public interface GenericHelpableCommand {

    /**
     * @return The priority of this command in the help command.
     * Commands with lower priority numbers will be displayed first.
     * Commands with a priority of -1 will not be displayed.
     */
    int getHelpOrder();

    /**
     * @return The usage for this command. May or may not be colored.
     */
    String getCmdUsage();

    /**
     * @return The explanation for what this command does. May or may
     * not be colored.
     */
    String getCmdExplanation();
}