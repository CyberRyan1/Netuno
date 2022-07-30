package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;

public class PunishSettingsEntry {

    private String path;
    private int i;
    private String str;
    private float f;
    private double d;
    private long l;
    private boolean b;
    private Material mat;
    private MainButton main;
    private MultiPunishButton multi;

    public PunishSettingsEntry( String path, String valueType ) {
        this.path = path;

        switch ( valueType.toLowerCase() ) {
            case "int" -> this.i = YMLUtils.getConfig().getInt( path );
            case "string" -> this.str = YMLUtils.getConfig().getStr( path );
            case "float" -> this.f = YMLUtils.getConfig().getFloat( path );
            case "double" -> this.d = YMLUtils.getConfig().getDouble( path );
            case "long" -> this.l = YMLUtils.getConfig().getYMLManager().getConfig().getLong( path );
            case "boolean" -> this.b = YMLUtils.getConfig().getBool( path );
            case "material" -> this.mat = Material.valueOf( YMLUtils.getConfig().getStr( path ) );
            case "mainbutton" -> this.main = new MainButton( path.substring( path.indexOf( "." ) + 1 ) );
            case "multi" -> this.multi = new MultiPunishButton( path );
        }
    }

    public String getPath() { return this.path; }

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