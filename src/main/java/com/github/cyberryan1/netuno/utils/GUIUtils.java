package com.github.cyberryan1.netuno.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIUtils {

    public static ItemStack setItemLore( ItemStack item, ArrayList<String> lore ) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore( lore );
        item.setItemMeta( meta );
        return item;
    }

    public static ItemStack setItemName( ItemStack item, String name ) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName( Utils.getColored( name ) );
        item.setItemMeta( meta );
        return item;
    }

    public static ItemStack getBackgroundGlass() {
        return setItemName( new ItemStack( Material.GRAY_STAINED_GLASS_PANE ), "&7" );
    }

    public static ItemStack createItem( Material material, String name ) {
        return setItemName( new ItemStack( material ), name );
    }
}
