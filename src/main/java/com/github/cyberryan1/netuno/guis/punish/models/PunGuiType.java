package com.github.cyberryan1.netuno.guis.punish.models;

/**
 * The type of punishment a punishment specific GUI represents
 *
 * @author Ryan
 */
public enum PunGuiType {
    WARN,
    MUTE,
    BAN,
    IPMUTE,
    IPBAN;

    /**
     * @return The past tense form of this GUI type, in lowercase
     */
    public String getPastTense() {
        if ( this.name().toLowerCase().endsWith( "e" ) ) return this.name().toLowerCase() + "d";
        else return this.name().toLowerCase() + "ed";
    }
}