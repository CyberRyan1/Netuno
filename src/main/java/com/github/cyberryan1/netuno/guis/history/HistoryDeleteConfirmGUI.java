package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HistoryDeleteConfirmGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final Punishment punishment;

    public HistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, Punishment pun ) {
        this.target = target;
        this.staff = staff;
        this.punishment = pun;

        String guiName = Utils.getColored( "&hConfirm Deletion" );
        gui = Bukkit.createInventory( null, 45, guiName );
        insertItems();
    }

    private void insertItems() {
        // glass: everywhere
        // sign: 13
        // green wool confirm: 30
        // red wool cancel: 32

        ItemStack items[] = new ItemStack[45];
        for ( int index = 0; index < 45; index++ ) {
            items[index] = GUIUtils.getBackgroundGlass();
        }

        items[13] = punishment.getPunishmentAsItem();
        items[30] = GUIUtils.setItemName( GUIUtils.getColoredItemForVersion( "LIME_WOOL" ), "&aConfirm" );
        items[32] = GUIUtils.setItemName( GUIUtils.getColoredItemForVersion( "RED_WOOL" ), "&cCancel" );

        gui.setContents( items );
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) ) {
            event.setCancelled( true );
            if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

            ItemStack itemClicked = event.getCurrentItem();
            if ( itemClicked == null || itemClicked.getType() == Material.AIR ) { return; }

            if ( itemClicked.equals( GUIUtils.setItemName( GUIUtils.getColoredItemForVersion( "LIME_WOOL" ), "&aConfirm" ) ) ) {
                staff.closeInventory();
                DATA.deletePunishment( punishment.getID() );
                staff.sendMessage( Utils.getColored( "&hSuccessfully deleted punishment &g#" + punishment.getID() ) );
                staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
            }

            else if ( itemClicked.equals( GUIUtils.setItemName( GUIUtils.getColoredItemForVersion( "RED_WOOL" ), "&cCancel" ) ) ) {
                HistoryEditGUI editGUI = new HistoryEditGUI( target, staff, punishment.getID() );
                editGUI.openInventory( staff );
                staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
            }
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) ) {
            event.setCancelled( true );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( event.getPlayer().getName().equals( staff.getName() ) ) { GUIEventManager.removeEvent( this ); }
    }
}
