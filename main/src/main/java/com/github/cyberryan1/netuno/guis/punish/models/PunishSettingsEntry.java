package com.github.cyberryan1.netuno.guis.punish.models;

import com.github.cyberryan1.cybercore.spigot.config.YmlReader;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;

/**
 * Represents a setting that is used for the punishment GUI or
 * any of its sub-GUIs
 *
 * @author Ryan
 */
public class PunishSettingsEntry {

    private String path;
    private String valueType;
    private String guiType;

    private int i;
    private String str;
    private float f;
    private double d;
    private long l;
    private boolean b;
    private Material mat;
    private MainButton main;
    private MultiPunishButton multi;

    /**
     *
     * @param path The YML path to this setting in the config file
     * @param valueType The type of value this setting represents
     * @param guiType The type of GUI this setting is for
     */
    public PunishSettingsEntry( String path, String valueType, String guiType ) {
        this.path = path;
        this.valueType = valueType;
        this.guiType = guiType;

        final YmlReader YML_MANAGER = YMLUtils.fromName( guiType );

        switch ( valueType.toLowerCase() ) {
            case "int" -> this.i = YML_MANAGER.getInt( path );
            case "string" -> this.str = YML_MANAGER.getStr( path );
            case "float" -> this.f = YML_MANAGER.getFloat( path );
            case "double" -> this.d = YML_MANAGER.getDouble( path );
            case "long" -> this.l = YML_MANAGER.getYmlLoader().getConfig().getLong( path );
            case "boolean" -> this.b = YML_MANAGER.getBool( path );
            case "material" -> this.mat = Material.valueOf( YML_MANAGER.getStr( path ) );
            case "mainbutton" -> this.main = new MainButton( path.substring( path.indexOf( "." ) + 1 ) );
            case "multi" -> this.multi = new MultiPunishButton( path, guiType );
        }
    }

    /**
     * @return The YML path to this setting in the config file
     */
    public String getPath() { return this.path; }

    /**
     * @return The type of value this setting represents
     */
    public String getValueType() { return this.valueType; }

    /**
     * @return The type of GUI this setting is for
     */
    public String getGuiType() { return this.guiType; }


    public int integer() { return this.i; }

    public String string() { return this.str; }

    public float getFloat() { return this.f; }

    public double getDouble() { return this.d; }

    public long getLong() { return this.l; }

    public boolean bool() { return this.b; }

    public Material material() { return this.mat; }

    public MainButton mainButton() { return this.main; }

    public MultiPunishButton multiButton() { return this.multi; }
}