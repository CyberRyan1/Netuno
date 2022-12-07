package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
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

        if ( buttonName.toLowerCase().contains( "silent" ) ) {
            this.index = YMLUtils.getMainGui().getInt( "main-gui.silent.index" );
        }
        else {
            this.index = YMLUtils.getMainGui().getInt( "main-gui." + buttonName + ".index" );
        }

        this.material = null;
        if ( buttonName.equalsIgnoreCase( "skull" ) == false ) {
            this.name = YMLUtils.getMainGui().getStr( "main-gui." + buttonName + ".name" );
            this.material = Material.valueOf( YMLUtils.getMainGui().getStr( "main-gui." + buttonName + ".item" ) );
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
            return CyberItemUtils.getPlayerSkull( target );
        }
        else {
            return CyberItemUtils.createItem( this.material, this.name.replace( "[TARGET]", target.getName() ) );
        }
    }
}