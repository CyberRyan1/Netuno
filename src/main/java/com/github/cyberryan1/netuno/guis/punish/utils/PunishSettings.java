package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import org.bukkit.Material;

public enum PunishSettings {

    //
    // Main Inventory
    //
    MAIN_INVENTORY_NAME( "main-gui.inventory-name", "string" ),
    MAIN_SKULL_BUTTON( "main-gui.skull", "mainbutton" ),
    MAIN_HISTORY_BUTTON( "main-gui.history", "mainbutton" ),
    MAIN_ALTS_BUTTON( "main-gui.alts", "mainbutton" ),
    MAIN_WARN_BUTTON( "main-gui.warn", "mainbutton" ),
    MAIN_MUTE_BUTTON( "main-gui.mute", "mainbutton" ),
    MAIN_BAN_BUTTON( "main-gui.ban", "mainbutton" ),
    MAIN_IPMUTE_BUTTON( "main-gui.ipmute", "mainbutton" ),
    MAIN_IPBAN_BUTTON( "main-gui.ipban", "mainbutton" ),

    //
    // Warn Inventory
    //
    WARN_INVENTORY_NAME( "warn-gui.inventory_name", "string" ),
    WARN_PERMISSION( "warn-gui.permission", "string" ),
    WARN_BUTTONS( "warn-gui", "multi" ),

    //
    // Mute Inventory
    //
    MUTE_INVENTORY_NAME( "mute-gui.inventory_name", "string" ),
    MUTE_PERMISSION( "mute-gui.permission", "string" ),
    MUTE_BUTTONS( "mute-gui", "multi" ),

    //
    // Mute Inventory
    //
    BAN_INVENTORY_NAME( "ban-gui.inventory_name", "string" ),
    BAN_PERMISSION( "ban-gui.permission", "string" ),
    BAN_BUTTONS( "ban-gui", "multi" ),

    //
    // IP Mute Inventory
    //
    IPMUTE_INVENTORY_NAME( "ipmute-gui.inventory_name", "string" ),
    IPMUTE_PERMISSION( "ipmute-gui.permission", "string" ),
    IPMUTE_BUTTONS( "ipmute-gui", "multi" ),

    //
    // IP Ban Inventory
    //
    IPBAN_INVENTORY_NAME( "ipban-gui.inventory_name", "string" ),
    IPBAN_PERMISSION( "ipban-gui.permission", "string" ),
    IPBAN_BUTTONS( "ipban-gui", "multi" ),

    ;

    private String path;
    private PunishSettingsEntry value;
    PunishSettings( String path, String valueType ) {
        this.path = path;
        this.value = new PunishSettingsEntry( path, valueType );
    }

    public String getPath() { return this.path; }

    public PunishSettingsEntry getValue() { return this.value; }

    public int integer() { return value.integer(); }

    public String string() { return value.string(); }

    public String coloredString() { return CoreUtils.getColored( value.string() ); }

    public float getFloat() { return value.getFloat(); }

    public double getDouble() { return value.getDouble(); }

    public long getLong() { return value.getLong(); }

    public boolean bool() { return value.bool(); }

    public Material material() { return value.material(); }

    public MainButton mainButton() { return value.mainButton(); }

    public MultiPunishButton multiButton() { return value.multiButton(); }
}