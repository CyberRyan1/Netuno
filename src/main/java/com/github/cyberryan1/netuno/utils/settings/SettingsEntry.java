package com.github.cyberryan1.netuno.utils.settings;

import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;

public class SettingsEntry {
    
    public enum EntryType {
        INT,
        STRING,
        FLOAT,
        DOUBLE,
        LONG,
        BOOLEAN,
        MATERIAL,
        STRING_LIST,
        SOUND
    }

    //
    // Class methods
    //

    private String path;
    private EntryType valueType;
    private int i;
    private String str;
    private float f;
    private double d;
    private long l;
    private boolean b;
    private Material mat;
    private String strList[];
    private SoundSettingEntry sound;


    public SettingsEntry( String path, EntryType valueType ) {
        this.path = path;
        this.valueType = valueType;

        switch ( valueType ) {
            case INT ->
                    this.i = YMLUtils.getConfig().getInt( path );
            case STRING ->
                    this.str = YMLUtils.getConfig().getStr( path );
            case FLOAT ->
                    this.f = YMLUtils.getConfig().getFloat( path );
            case DOUBLE ->
                    this.d = YMLUtils.getConfig().getDouble( path );
            case LONG ->
                    this.l = YMLUtils.getConfig().getLong( path );
            case BOOLEAN ->
                    this.b = YMLUtils.getConfig().getBool( path );
            case MATERIAL ->
                    this.mat = Material.valueOf( YMLUtils.getConfig().getStr( path ) );
            case STRING_LIST ->
                    this.strList = YMLUtils.getConfig().getStrList( path );
            case SOUND ->
                    this.sound = new SoundSettingEntry( path );
        }
    }

    public String getPath() { return this.path; }

    public EntryType getValueType() { return this.valueType; }

    public int integer() { return this.i; }

    public String string() { return this.str; }

    public float getFloat() { return this.f; }

    public double getDouble() { return this.d; }

    public long getLong() { return this.l; }

    public boolean bool() { return this.b; }

    public Material material() { return this.mat; }

    public String[] stringlist() { return this.strList; }

    public SoundSettingEntry sound() { return this.sound; }
}