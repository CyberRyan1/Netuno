package com.github.cyberryan1.netuno.models.commands;

public interface GenericHelpableCommand {

    int getHelpOrder();

    String getHelpMsg();
}