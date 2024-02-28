package com.github.cyberryan1.netuno.models.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;

public abstract class HelpableSuperCommand extends CyberSuperCommand implements GenericHelpableCommand {

    private int helpOrder = -1; // -1 means it will be skipped
    private String cmdExplanation = null;

    public HelpableSuperCommand( int helpOrder, String name, String permission, String permissionMsg, String usage, String cmdExplanation ) {
        super( name, permission, permissionMsg, usage );
        this.helpOrder = helpOrder;
        this.cmdExplanation = cmdExplanation;
    }

    public HelpableSuperCommand( int helpOrder, String name, String permission, String usage, String cmdExplanation ) {
        super( name, permission, usage );
        this.helpOrder = helpOrder;
        this.cmdExplanation = cmdExplanation;
    }

    public HelpableSuperCommand( int helpOrder, String name, String usage, String cmdExplanation ) {
        super( name, usage );
        this.helpOrder = helpOrder;
        this.cmdExplanation = cmdExplanation;
    }

    protected void setHelpOrder( int helpOrder ) { this.helpOrder = helpOrder; }

    public int getHelpOrder() { return this.helpOrder; }

    public String getCmdUsage() { return super.getUsage(); }

    public String getCmdExplanation() {
        return this.cmdExplanation == null ? null : CyberColorUtils.getColored( this.cmdExplanation );
    }
}