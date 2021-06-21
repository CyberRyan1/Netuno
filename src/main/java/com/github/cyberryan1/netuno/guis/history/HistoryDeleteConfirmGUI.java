package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HistoryDeleteConfirmGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final Punishment punishment;
    private boolean cooldown = false;
    //                      Staff Name | Target UUID
//    private final static HashMap<String, UUID> STAFF_TARGETS = new HashMap<>();
    //                      Staff Name | Target Punishment
//    private final static HashMap<String, Punishment> STAFF_PUNS = new HashMap<>();
    //                             Staff
//    private final static ArrayList<Player> COOLDOWN = new ArrayList<>();

    public HistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, Punishment pun ) {
        this.target = target;
        this.staff = staff;
        this.punishment = pun;

        String guiName = Utils.getColored( "&7Confirm Deletion" );
        gui = Bukkit.createInventory( null, 45, guiName );
        insertItems();

        GUIEventManager.addEvent( this );
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

        items[13] = punishment.getPunishmentAsSign();
        items[30] = GUIUtils.createItem( Material.LIME_WOOL, "&aConfirm" );
        items[32] = GUIUtils.createItem( Material.RED_WOOL, "&cCancel" );

        gui.setContents( items );
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) ) {
            event.setCancelled( true );

            if ( cooldown == false ) {
                ItemStack itemClicked = event.getCurrentItem();
                if ( itemClicked == null || itemClicked.getType().isAir() ) { return; }

                if ( itemClicked.equals( GUIUtils.createItem( Material.LIME_WOOL, "&aConfirm" ) ) ) {
                    staff.closeInventory();
                    DATA.deletePunishment( punishment.getID() );
                    staff.sendMessage( Utils.getColored( "&7Successfully deleted punishment &6#" + punishment.getID() ) );
                }

                else if ( itemClicked.equals( GUIUtils.createItem( Material.RED_WOOL, "&cCancel" ) ) ) {
                    staff.closeInventory();
                }
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
