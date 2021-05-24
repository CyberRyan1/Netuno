package com.github.cyberryan1.netuno.utils;

import java.io.*;

public class Punishment implements Serializable {

    private String playerUUID;
    private String staffUUID;
    private String type;
    private long date;
    private long length;
    private String reason;
    private boolean active;

    public Punishment() {
        playerUUID = null;
        staffUUID = null;
        type = null;
        date = -1;
        length = -1;
        reason = null;
        active = false;
    }

    public Punishment( String playerUUID, String staffUUID, String type, long date, long length, String reason, boolean active ) {
        this.playerUUID = playerUUID;
        this.staffUUID = staffUUID;
        this.type = type;
        this.date = date;
        this.length = length;
        this.reason = reason;
        this.active = active;
    }

    public String getPlayerUUID() { return playerUUID; }

    public String getStaffUUID() { return staffUUID; }

    public String getType() { return type; }

    public long getDate() { return date; }

    public long getLength() { return length; }

    public String getReason() { return reason; }

    public boolean getActive() { return active; }

    public void setPlayerUUID( String str ) { playerUUID = str; }

    public void setStaffUUID( String str ) { staffUUID = str; }

    public void setType( String str ) { type = str; }

    public void setDate( Long num ) { date = num; }

    public void setLength( Long num ) { length = num; }

    public void setReason( String str ) { reason = str; }

    public void setActive( boolean act ) { active = act; }
}
