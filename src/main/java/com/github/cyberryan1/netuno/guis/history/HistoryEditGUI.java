package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HistoryEditGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory GUI;
    private final OfflinePlayer target;
    private final Player staff;
    private final Punishment punishment;

    private boolean editingLength = false;
    private boolean editingReason = false;

    public HistoryEditGUI( OfflinePlayer target, Player staff, int punID ) {
        this.target = target;
        this.staff = staff;

        Punishment workingWith;
        if ( DATA.checkPunIDExists( punID ) ) { punishment = DATA.getPunishment( punID ); }
        // else is an ip punishment
        else { punishment = DATA.getIPPunishment( punID ); }

        String guiName = Utils.getColored( "&hEdit Punishment &g#" + punID );
        GUI = Bukkit.createInventory( null, 54, guiName );
        insertItems();

        GUIEventManager.addEvent( this );
    }

    public void insertItems() {
        // glass: everything else
        // pun info sign: 13
        // back to history list: 49
        // unpunish layout:
        //      delete punishment barrier: 31
        // no-length layout:
        //      edit reason paper: 30 || delete punishment barrier: 32
        // default:
        //      edit length clock: 29 || edit reason paper: 31
        //      delete punishment barrier: 33

        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = GUIUtils.getBackgroundGlass();
        }

        items[13] = punishment.getPunishmentAsItem();

        if ( punishment.checkIsUnpunish() ) {
            items[31] = GUIUtils.createItem( Material.BARRIER, "&hDelete punishment" );
        }
        else if ( punishment.checkHasNoTime() || punishment.getActive() == false ) {
            items[30] = GUIUtils.createItem( Material.PAPER, "&hEdit reason" );
            items[32] = GUIUtils.createItem( Material.BARRIER, "&hDelete punishment" );
        }
        else {
            items[29] = GUIUtils.setItemName( GUIUtils.getItemForVersion( "CLOCK", "WATCH" ), "&hEdit length" );
            items[31] = GUIUtils.createItem( Material.PAPER, "&hEdit reason" );
            items[33] = GUIUtils.createItem( Material.BARRIER, "&hDelete punishment" );
        }

        items[49] = GUIUtils.createItem( Material.ARROW, "&hGo back" );
        GUI.setContents( items );
    }

    public void openInventory( Player player ) {
        player.openInventory( GUI );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&hEdit Punishment &g#" + punishment.getID() ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack itemClicked = event.getCurrentItem();
        if ( itemClicked == null || itemClicked.getType() == Material.AIR ) { return; }
        String itemName = itemClicked.getItemMeta().getDisplayName();
        if ( itemName.equals( Utils.getColored( "&hEdit length" ) ) == false
            && itemName.equals( Utils.getColored( "&hEdit reason" ) ) == false
            && itemName.equals( Utils.getColored( "&hDelete punishment" ) ) == false
            && itemName.equals( Utils.getColored( "&hGo back" ) ) == false ) { return; }

        if ( itemClicked.equals( GUIUtils.setItemName( GUIUtils.getItemForVersion( "CLOCK", "WATCH" ), "&hEdit length" ) ) ) {
            if ( VaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "history.time.perm" ) ) == false ) {
                CommandErrors.sendInvalidPerms( staff );
            }

            else if ( editingLength == false ) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage( Utils.getColored( "&hPlease type the new length for punishment &g#" + punishment.getID() ) );
                event.getWhoClicked().sendMessage( Utils.getColored( "&hTo cancel, type &g\"cancel\"" ) );
                editingLength = true;
            }
        }

        else if ( itemClicked.equals( GUIUtils.createItem( Material.PAPER, "&hEdit reason" ) ) ) {
            if ( VaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "history.reason.perm" ) ) == false ) {
                CommandErrors.sendInvalidPerms( staff );
            }

            else if ( editingReason == false ) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage( Utils.getColored( "&hPlease type the new reason for punishment &g#" + punishment.getID() ) );
                event.getWhoClicked().sendMessage( Utils.getColored( "&hTo cancel, type &g\"cancel\"" ) );
                editingReason = true;
            }
        }

        else if ( itemClicked.equals( GUIUtils.createItem( Material.BARRIER, "&hDelete punishment" ) ) ) {
            if ( VaultUtils.hasPerms( staff, YMLUtils.getConfig().getStr( "history.delete.perm" ) ) == false ) {
                CommandErrors.sendInvalidPerms( staff );
            }

            else {
                event.getWhoClicked().closeInventory(); // like this close inventory here, helps prevent accidental deletes
                HistoryDeleteConfirmGUI deleteGUI = new HistoryDeleteConfirmGUI( target, staff, punishment );
                deleteGUI.openInventory( staff );
                staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
            }
        }

        else if ( itemClicked.equals( GUIUtils.createItem( Material.ARROW, "&hGo back" ) ) ) {
            event.getWhoClicked().closeInventory();
            HistoryListGUI gui = new HistoryListGUI( target, staff, 1 );
            gui.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );

            GUIEventManager.removeEvent( this );
        }

    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&hEdit Punishment &g#" + punishment.getID() ) ) == false ) { return; }

        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( event.getPlayer().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&hEdit Punishment &g#" + punishment.getID() ) ) == false ) { return; }

        if ( editingLength == false || editingReason == false ) { return; }

        GUIEventManager.removeEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.PLAYER_CHAT )
    public void onPlayerChatEvent( PlayerChatEvent event ) {
        if ( event.getPlayer().getName().equals( staff.getName() ) == false ) { return; }
        if ( editingLength ) {
            event.setCancelled( true );

            if ( event.getMessage().equalsIgnoreCase( "cancel" ) ) {
                HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), punishment.getID() );
                newGUI.openInventory( event.getPlayer() );
                editingLength = false;
            }

            else if ( Time.isAllowableLength( event.getMessage() ) ) {
                punishment.setLength( Time.getTimestampFromLength( event.getMessage() ) );
                DATA.setPunishmentLength( punishment.getID(), punishment.getLength() );

                GUIEventManager.removeEvent( this );
                HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), punishment.getID() );
                newGUI.openInventory( event.getPlayer() );
                editingLength = false;
            }

            else {
                CommandErrors.sendInvalidTimespan( event.getPlayer(), event.getMessage() );
                event.getPlayer().sendMessage( Utils.getColored( "&hTry again, or say &g\"cancel\"&h to cancel" ) );
            }
        }

        else if ( editingReason ) {
            event.setCancelled( true );

            if ( event.getMessage().equalsIgnoreCase( "cancel" ) == false ) {
                punishment.setReason( event.getMessage() );
                DATA.setPunishmentReason( punishment.getID(), punishment.getReason() );
            }

            GUIEventManager.removeEvent( this );
            HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), punishment.getID() );
            newGUI.openInventory( event.getPlayer() );
            editingReason = false;
        }
    }

    @GUIEventInterface( type = GUIEventType.PLAYER_COMMAND )
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
        if ( event.getPlayer().getName().equals( staff.getName() ) == false ) { return; }
        if ( editingLength || editingReason ) {
            editingLength = false;
            editingReason = false;
            event.getPlayer().sendMessage( Utils.getColored( "&hThe punishment edit has been cancelled" ) );
            GUIEventManager.removeEvent( this );
        }
    }
}
