package com.github.cyberryan1.netuno.guis.punish.models;

import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a button within the main punishment GUI
 *
 * @see com.github.cyberryan1.netuno.guis.punish.MainPunishGui
 * @author Ryan
 */
public class MainButton {

    private String buttonName;
    private int index;
    private String name;
    private Material material;

    /**
     * @param buttonName What type of button this button represents
     *                   within the GUI
     */
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

    /**
     * Gets the item that is specific to the target. Replaces
     * any [TARGET] setting variables within the name of the item.
     * Also, if {@link #getButtonName()} is "<code>skull</code>",
     * sets the type of this item to be the target's skull.
     *
     * @param target The target
     * @return The item
     */
    public ItemStack getItem( OfflinePlayer target ) {
        if ( this.buttonName.equalsIgnoreCase( "skull" ) ) {
            return CyberItemUtils.getPlayerSkull( target );
        }
        else {
            return CyberItemUtils.createItem( this.material, this.name.replace( "[TARGET]", target.getName() ) );
        }
    }
}