package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.PunishGUIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class WarnPunishGUI {

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final String guiName;

    private int guiSize;
    private ArrayList<String> reasons;

    public WarnPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        setReasons();
        setGuiSize();

        this.guiName = PunishGUIUtils.getColoredStr( "warn.inventory_name" );
        this.gui = Bukkit.createInventory( null, this.guiSize, this.guiName );
        insertItems();
    }

    private void setReasons() {
        this.reasons = PunishGUIUtils.getKeys( "warn." );
        for ( int index = reasons.size() - 1; index >= 0; index-- ) {
            if ( reasons.get( index ).equals( "warn.inventory_name" ) ) {
                reasons.remove( index );
            }
            else {
                reasons.set( index, reasons.get( index ).replace( "warn.", "" ) );
            }
        }
    }

    private void setGuiSize() {
        if ( reasons.size() <= 6 ) { this.guiSize = 36; }
        else if ( reasons.size() <= 12 ) { this.guiSize = 45; }
        else { this.guiSize = 54; }
    }

    public void insertItems() {
        // background glass: everywhere
        // punishments: start at 19-21 and 23-24, every row as needed
        ItemStack items[] = new ItemStack[this.guiSize];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        int punIndex = 0;
        int guiIndex = 19;
        for ( int row = 0; row < ( int ) Math.ceil( guiSize / 9.0 ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( punIndex >= reasons.size() ) { break; }
                if ( reasons.get( punIndex ).equals( "air" ) == false ) {
                    String path = "warn." + reasons.get( punIndex );
                    Material material = Material.matchMaterial( PunishGUIUtils.getStr( path + ".material" ) );
                    String name = PunishGUIUtils.getColoredStr( path + ".item-name" );
                    items[guiIndex] = GUIUtils.createItem( material, name );
                }

                if ( guiIndex % 9 == 3 ) { guiIndex++; }
                guiIndex++; punIndex++;
            }

            guiIndex += 2;
        }

        gui.setContents( items );
    }
}
