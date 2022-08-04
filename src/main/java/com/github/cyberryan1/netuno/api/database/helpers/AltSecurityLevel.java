package com.github.cyberryan1.netuno.api.database.helpers;

public enum AltSecurityLevel {

    /**
     * Searches only the player's current IP
     */
    LOW,

    /**
     * Searches by the player's current IP and the player's previously used IPs
     */
    MEDIUM,

    /**
     * Gets the player's current IP and the player's previously used IPs and adds them to a list.
     * Then, all accounts related who have used any of the IPs in the list are searched. If any
     * account has an IP that is not in the list, it is added to the list. This process is repeated
     * until there is no more accounts to search
     */
    HIGH;
}