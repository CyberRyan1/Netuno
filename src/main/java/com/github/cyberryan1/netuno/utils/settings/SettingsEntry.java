package com.github.cyberryan1.netuno.utils.settings;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;

public class SettingsEntry {

    //
    // Class methods
    //

    private String path;
    private String valueType;
    private int i;
    private String str;
    private float f;
    private double d;
    private long l;
    private boolean b;
    private Material mat;
    private String strList[];


    public SettingsEntry( String path, String valueType ) {
        this.path = path;
        this.valueType = valueType;

        switch ( valueType.toLowerCase() ) {
            case "int" -> this.i = YMLUtils.getConfig().getInt( path );
            case "string" -> this.str = YMLUtils.getConfig().getStr( path );
            case "float" -> this.f = YMLUtils.getConfig().getFloat( path );
            case "double" -> this.d = YMLUtils.getConfig().getDouble( path );
            case "long" -> this.l = YMLUtils.getConfig().getYMLManager().getConfig().getLong( path );
            case "boolean" -> this.b = YMLUtils.getConfig().getBool( path );
            case "material" -> this.mat = Material.valueOf( YMLUtils.getConfig().getStr( path ) );
            case "strlist" -> this.strList = YMLUtils.getConfig().getStrList( path );
        }
    }

    public String getPath() { return this.path; }

    public String getValueType() { return this.valueType; }

    public int integer() { return this.i; }

    public String string() { return this.str; }

    public float getFloat() { return this.f; }

    public double getDouble() { return this.d; }

    public long getLong() { return this.l; }

    public boolean bool() { return this.b; }

    public Material material() { return this.mat; }

    public String[] stringlist() { return this.strList; }
}