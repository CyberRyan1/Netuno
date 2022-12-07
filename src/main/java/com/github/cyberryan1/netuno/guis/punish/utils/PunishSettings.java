package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import org.bukkit.Material;

public enum PunishSettings {

    //
    // Main Inventory
    //
    MAIN_INVENTORY_NAME( "main-gui.inventory-name", "string", "main" ),
    MAIN_SKULL_BUTTON( "main-gui.skull", "mainbutton", "main" ),
    MAIN_HISTORY_BUTTON( "main-gui.history", "mainbutton", "main" ),
    MAIN_ALTS_BUTTON( "main-gui.alts", "mainbutton", "main" ),
    MAIN_SILENT_ENABLED_BUTTON( "main-gui.silent.true", "mainbutton", "main" ),
    MAIN_SILENT_DISABLED_BUTTON( "main-gui.silent.false", "mainbutton", "main" ),
    MAIN_SILENT_NO_PERMS_BUTTON( "main-gui.silent.no-perms", "mainbutton", "main" ),
    MAIN_WARN_BUTTON( "main-gui.warn", "mainbutton", "main" ),
    MAIN_MUTE_BUTTON( "main-gui.mute", "mainbutton", "main" ),
    MAIN_BAN_BUTTON( "main-gui.ban", "mainbutton", "main" ),
    MAIN_IPMUTE_BUTTON( "main-gui.ipmute", "mainbutton", "main" ),
    MAIN_IPBAN_BUTTON( "main-gui.ipban", "mainbutton", "main" ),

    MAIN_IN_USE_NAME( "main-gui.already-being-punished.name", "string", "main" ),
    MAIN_IN_USE_LORE( "main-gui.already-being-punished.lore", "string", "main" ),
    MAIN_IN_USE_ITEM( "main-gui.already-being-punished.item", "material", "main" ),
    MAIN_IN_USE_GLOW( "main-gui.already-being-punished.enchant-glow", "boolean", "main" ),

    //
    // Warn Inventory
    //
    WARN_INVENTORY_NAME( "warn-gui.inventory_name", "string", "warn" ),
    WARN_PERMISSION( "warn-gui.permission", "string", "warn" ),
    WARN_BUTTONS( "warn-gui", "multi", "warn" ),

    //
    // Mute Inventory
    //
    MUTE_INVENTORY_NAME( "mute-gui.inventory_name", "string", "mute" ),
    MUTE_PERMISSION( "mute-gui.permission", "string", "mute" ),
    MUTE_BUTTONS( "mute-gui", "multi", "mute" ),

    //
    // Ban Inventory
    //
    BAN_INVENTORY_NAME( "ban-gui.inventory_name", "string", "ban" ),
    BAN_PERMISSION( "ban-gui.permission", "string", "ban" ),
    BAN_BUTTONS( "ban-gui", "multi", "ban" ),

    //
    // IP Mute Inventory
    //
    IPMUTE_INVENTORY_NAME( "ipmute-gui.inventory_name", "string", "ipmute" ),
    IPMUTE_PERMISSION( "ipmute-gui.permission", "string", "ipmute" ),
    IPMUTE_BUTTONS( "ipmute-gui", "multi", "ipmute" ),

    //
    // IP Ban Inventory
    //
    IPBAN_INVENTORY_NAME( "ipban-gui.inventory_name", "string", "ipban" ),
    IPBAN_PERMISSION( "ipban-gui.permission", "string", "ipban" ),
    IPBAN_BUTTONS( "ipban-gui", "multi", "ipban" ),

    ;

    private String path;
    private PunishSettingsEntry value;
    PunishSettings( String path, String valueType, String ymlName ) {
        this.path = path;
        this.value = new PunishSettingsEntry( path, valueType, ymlName );
    }

    public void reload() {
        this.value = new PunishSettingsEntry( this.path, this.value.getValueType(), this.value.getYmlName() );
    }

    public String getPath() { return this.path; }

    public PunishSettingsEntry getValue() { return this.value; }

    public int integer() { return value.integer(); }

    public String string() { return value.string(); }

    public String coloredString() { return CyberColorUtils.getColored( value.string() ); }

    public float getFloat() { return value.getFloat(); }

    public double getDouble() { return value.getDouble(); }

    public long getLong() { return value.getLong(); }

    public boolean bool() { return value.bool(); }

    public Material material() { return value.material(); }

    public MainButton mainButton() { return value.mainButton(); }

    public MultiPunishButton multiButton() { return value.multiButton(); }
}