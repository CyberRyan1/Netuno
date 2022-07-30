package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class MainButton {

    private String buttonName;
    private int index;
    private String name;
    private Material material;

    public MainButton( String buttonName ) {
        this.buttonName = buttonName;

        this.index = YMLUtils.getConfig().getInt( "main-gui." + buttonName + ".index" );
        this.material = null;
        if ( buttonName.equalsIgnoreCase( "skull" ) == false ) {
            this.name = YMLUtils.getConfig().getStr( "main-gui." + buttonName + ".name" );
            this.material = Material.valueOf( YMLUtils.getConfig().getStr( "main-gui." + buttonName + ".item" ) );
        }
    }

    public String getButtonName() {
        return this.buttonName;
    }

    public int getIndex() {
        return this.index;
    }

    public ItemStack getItem( OfflinePlayer target ) {
        if ( this.buttonName.equalsIgnoreCase( "skull" ) ) {
            return GUIUtils.getPlayerSkull( target );
        }
        else {
            return CoreGUIUtils.createItem( this.material, this.name.replace( "[TARGET]", target.getName() ) );
        }
    }
}