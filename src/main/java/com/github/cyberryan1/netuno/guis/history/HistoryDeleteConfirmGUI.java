package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class HistoryDeleteConfirmGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    //                      Staff Name | Target UUID
    private final static HashMap<String, UUID> STAFF_TARGETS = new HashMap<>();
    //                      Staff Name | Target Punishment
    private final static HashMap<String, Punishment> STAFF_PUNS = new HashMap<>();

    public HistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, Punishment pun ) {
        STAFF_TARGETS.remove( staff.getName() );
        STAFF_TARGETS.put( staff.getName(), target.getUniqueId() );

        STAFF_PUNS.remove( staff.getName() );
        STAFF_PUNS.put( staff.getName(), pun );

        String guiName = Utils.getColored( "&7Confirm Deletion" );
        gui = Bukkit.createInventory( null, 45, guiName );

        insertItems( staff );
    }

    private void insertItems( Player staff ) {
        // glass: everywhere
        // sign: 13
        // green wool confirm: 30
        // red wool cancel: 32

        ItemStack items[] = new ItemStack[45];
        for ( int index = 0; index < 45; index++ ) {
            items[index] = getBackgroundGlass();
        }

        items[13] = STAFF_PUNS.get( staff.getName() ).getPunishmentAsSign();
        items[30] = getConfirmGreenWool();
        items[32] = getCancelRedWool();

        gui.setContents( items );
    }

    private ItemStack getBackgroundGlass() {
        ItemStack glass = new ItemStack( Material.GRAY_STAINED_GLASS_PANE );
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName( "" );
        glass.setItemMeta( meta );
        return glass;
    }

    private ItemStack getConfirmGreenWool() {
        ItemStack wool = new ItemStack( Material.LIME_WOOL );
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&aConfirm" ) );
        wool.setItemMeta( meta );
        return wool;
    }

    private ItemStack getCancelRedWool() {
        ItemStack wool = new ItemStack( Material.RED_WOOL );
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&cCancel" ) );
        wool.setItemMeta( meta );
        return wool;
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
    }

    @EventHandler
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( STAFF_TARGETS.containsKey( event.getWhoClicked().getName() ) ) {
            event.setCancelled( true );

            ItemStack itemClicked = event.getCurrentItem();
            if ( itemClicked == null || itemClicked.getType().isAir() ) { return; }
            String itemName = itemClicked.getItemMeta().getDisplayName();
            if ( itemName.equals( Utils.getColored( "&aConfirm" ) ) == false
                    && itemName.equals( Utils.getColored( "&cCancel" ) ) == false ) { return; }

            Player staff = ( Player ) event.getWhoClicked();

            if ( itemClicked.getType() == Material.LIME_WOOL ) {
                staff.closeInventory();
                int punID = STAFF_PUNS.get( staff.getName() ).getID();
                DATA.deletePunishment( punID );
                staff.sendMessage( Utils.getColored( "&7Successfully deleted punishment &6#" + punID ) );
            }

            else if ( itemClicked.getType() == Material.RED_WOOL ) {
                staff.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( STAFF_TARGETS.containsKey( event.getWhoClicked().getName() ) ) {
            event.setCancelled( true );
        }
    }
}
