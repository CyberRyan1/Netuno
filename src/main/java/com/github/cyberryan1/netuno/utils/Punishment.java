package com.github.cyberryan1.netuno.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Punishment implements Serializable {

    private int id = -1;
    private String playerUUID;
    private String staffUUID;
    private String type;
    private long date;
    private long length;
    private String reason;
    private boolean active;

    public Punishment() {
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

    public int getID() { return id; }

    public String getPlayerUUID() { return playerUUID; }

    public String getStaffUUID() { return staffUUID; }

    public String getType() { return type; }

    public long getDate() { return date; }

    public long getLength() { return length; }

    public String getReason() { return reason; }

    public boolean getActive() { return active; }

    public long getExpirationDate() { return length + date; }

    public void setID( int id ) { this.id = id; }

    public void setPlayerUUID( String str ) { playerUUID = str; }

    public void setStaffUUID( String str ) { staffUUID = str; }

    public void setType( String str ) { type = str; }

    public void setDate( Long num ) { date = num; }

    public void setLength( Long num ) { length = num; }

    public void setReason( String str ) { reason = str; }

    public void setActive( boolean act ) { active = act; }

    // Checks if a punishment's type is to unpunish (unmute/unban/unipmute/unipban)
    public boolean checkIsUnpunish() {
        return type.equalsIgnoreCase( "unmute" ) || type.equalsIgnoreCase( "unipmute" ) ||
                type.equalsIgnoreCase( "unban" ) || type.equalsIgnoreCase( "unipban" );
    }

    // Checks if a punishment would have no time set on purpose (warns/kicks)
    public boolean checkHasNoTime() {
        return type.equalsIgnoreCase( "kick" ) || type.equalsIgnoreCase( "warn" );
    }

    // Checks if a punishment is an ip-punishment (ipmute/unipmute/ipban/unipban)
    public boolean checkIsIPPun() {
        return type.equalsIgnoreCase( "ipmute" ) || type.equalsIgnoreCase( "unipmute" ) ||
                type.equalsIgnoreCase( "ipban" ) || type.equalsIgnoreCase( "unipban" );
    }

    // Typically used for debug purposes
    public String toString() {
        String toReturn = "\tPunishment #" + id + " | playerUUID = " + playerUUID;
        toReturn += "\n\tstaffUUID = " +  staffUUID + " | type = " + type;
        toReturn += "\n\tdate = " + date + " | length = " + length;
        toReturn += "\n\tactive = " + active + " | reason = " + reason;
        return toReturn;
    }

    // Returns the punishment as it would be seen on a sign in a GUI
    public ItemStack getPunishmentAsSign() {
        ItemStack sign = new ItemStack( Material.OAK_SIGN );
        ItemMeta meta = sign.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Punishment &6#" + id ) );

        ArrayList<String> lore = new ArrayList<>();
        lore.add( Utils.getColored( "&6Date: &7" + Time.getDateFromTimestamp( date ) ) );
        lore.add( Utils.getColored( "&6Type: &7" + type.toUpperCase() ) );

        if ( checkIsUnpunish() == false && checkHasNoTime() == false ) {
            lore.add( Utils.getColored( "&6Length: &7" + Time.getLengthFromTimestamp( length ) ) );
        }

        if ( staffUUID.equals( "CONSOLE" ) ) { lore.add( Utils.getColored( "&6Staff: &7CONSOLE" ) ); }
        else {
            String staffName = Bukkit.getOfflinePlayer( UUID.fromString( staffUUID ) ).getName();
            lore.add( Utils.getColored( "&6Staff: &7" + staffName ) );
        }

        if ( checkIsIPPun() ) {
            String originalPlayerName = Bukkit.getOfflinePlayer( UUID.fromString( playerUUID ) ).getName();
            lore.add( Utils.getColored( "&6Original Player: &7" + originalPlayerName ) );
        }

        if ( checkIsUnpunish() == false ) { lore.add( Utils.getColored( "&6Reason: &7" + reason ) ); }

        meta.setLore( lore );

        if ( active ) {
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            meta.addEnchant( Enchantment.DURABILITY, 1, false );
        }

        sign.setItemMeta( meta );
        return sign;
    }
}
