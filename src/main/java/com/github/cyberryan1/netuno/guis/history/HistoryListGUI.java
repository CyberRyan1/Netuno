package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.*;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class HistoryListGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final int page;
    private final SortBy sort;
    private ArrayList<Punishment> history = new ArrayList<>();


    public HistoryListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        history.addAll( DATA.getAllPunishments( target.getUniqueId().toString() ) );
        sort();

        String guiName = Utils.getColored( "&6" + target.getName() + "&7's history" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();
    }

    public HistoryListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.FIRST_DATE );
    }

    private void sort() {
        if ( history.size() == 0 ) { return; }

        if ( sort == SortBy.FIRST_DATE ) {
            ArrayList<Punishment> newHistory = new ArrayList<>();
            newHistory.add( history.remove( 0 ) );
            for ( Punishment pun : history ) {
                for ( int index = 0; index < newHistory.size(); index++ ) {
                    if ( pun.getDate() < newHistory.get( index ).getDate() ) {
                        newHistory.add( index, pun );
                        break;
                    }
                }

                if ( newHistory.contains( pun ) == false ) { newHistory.add( pun ); }
            }

            history = newHistory;
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ArrayList<Punishment> newHistory = new ArrayList<>();
            newHistory.add( history.remove( 0 ) );
            for ( Punishment pun : history ) {
                for ( int index = 0; index < newHistory.size(); index++ ) {
                    if ( pun.getDate() > newHistory.get( index ).getDate() ) {
                        newHistory.add( index, pun );
                        break;
                    }
                }

                if ( newHistory.contains( pun ) == false ) { newHistory.add( pun ); }
            }

            history = newHistory;
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ArrayList<Punishment> newHistory = new ArrayList<>();
            newHistory.add( history.remove( 0 ) );
            for ( Punishment pun : history ) {
                if ( pun.getActive() ) {
                    for ( int index = 0; index < newHistory.size(); index++ ) {
                        if ( newHistory.get( index ).getActive() == false ) {
                            newHistory.add( index, pun );
                            break;
                        }
                    }
                }

                if ( newHistory.contains( pun ) == false ) { newHistory.add( pun ); }
            }

            history = newHistory;
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ArrayList<Punishment> newHistory = new ArrayList<>();
            newHistory.add( history.remove( 0 ) );
            for ( Punishment pun : history ) {
                if ( pun.getActive() == false ) {
                    for ( int index = 0; index < newHistory.size(); index++ ) {
                        if ( newHistory.get( index ).getActive() ) {
                            newHistory.add( index, pun );
                            break;
                        }
                    }
                }

                if ( newHistory.contains( pun ) == false ) { newHistory.add( pun ); }
            }

            history = newHistory;
        }
    }

    public void insertItems() {
        // glass: 0-8; 9, 18, 27, 17, 26, 35, 36-44, 45, 46, 48-50, 52-53
        // signs: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40
        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = GUIUtils.getBackgroundGlass();
        }

        int punIndex = 21 * ( page - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( punIndex >= history.size() ) { break; }

                items[guiIndex] = getPunishmentItem( punIndex );
                guiIndex++;
                punIndex++;
            }

            guiIndex += 2;
        }

        items[40] = getCurrentPagePaper();
        items[49] = getSortHopper();

        if ( page >= 2 ) {
            items[47] = GUIUtils.createItem( Material.BOOK, "&6Previous Page" );
        }
        int maxPage = ( int ) Math.ceil( history.size() / 21.0 ) ;
        if ( page < maxPage ) {
            items[51] = GUIUtils.createItem( Material.BOOK, "&6Next Page" );
        }

        gui.setContents( items );
    }

    private ItemStack getPunishmentItem( int index ) {
        Punishment current = history.get( index );
        return current.getPunishmentAsSign();
    }

    private ItemStack getCurrentPagePaper() {
        ItemStack paper = new ItemStack( Material.PAPER );
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Page &6#" + page ) );

        ArrayList<String> lore = new ArrayList<>();
        lore.add( Utils.getColored( "&7&oClick any sign to edit the punishment!" ) );
        meta.setLore( lore );

        paper.setItemMeta( meta );
        return paper;
    }

    private ItemStack getSortHopper() {
        if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&7Current Sort: &6Oldest -> Newest" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&7Next Sort: &6Newest -> Oldest" ) );
            lore.add( Utils.getColored( "&7Click to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&7Current Sort: &6Newest -> Oldest" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&7Next Sort: &6Active -> Not Active" ) );
            lore.add( Utils.getColored( "&7Click to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&7Current Sort: &6Active -> Not Active" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&7Next Sort: &6Not Active -> Active" ) );
            lore.add( Utils.getColored( "&7Click to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&7Current Sort: &6Not Active -> Active" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&7Next Sort: &6Newest -> Oldest" ) );
            lore.add( Utils.getColored( "&7Click to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        return null;
    }

    public void openInventory( Player staff ) {
        if ( gui.contains( Material.OAK_SIGN ) == false && page == 1 ) { CommandErrors.sendNoPreviousPunishments( staff, target.getName() ); }
        else {
            staff.openInventory( gui );
            GUIEventManager.addEvent( this );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's history" ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack itemClicked = event.getCurrentItem();
        if ( itemClicked == null || itemClicked.getType().isAir() ) { return; }

        if ( itemClicked.equals( GUIUtils.createItem( Material.BOOK, "&6Next Page" ) ) ) {
            HistoryListGUI next = new HistoryListGUI( target, staff, page + 1, sort );
            next.openInventory( staff );
        }

        else if ( itemClicked.equals( GUIUtils.createItem( Material.BOOK, "&6Previous Page" ) ) ) {
            HistoryListGUI previous = new HistoryListGUI( target, staff, page - 1, sort );
            previous.openInventory( staff );
        }

        else if ( itemClicked.getType() == Material.OAK_SIGN
                && itemClicked.getItemMeta().getDisplayName().contains( Utils.getColored( "&7Punishment &6#" ) ) ) {
            int punClicked = ( ( page - 1 ) * 21 ) + event.getSlot() - 10;
            if ( event.getSlot() >= 18 ) { punClicked -= 2; }
            if ( event.getSlot() >= 27 ) { punClicked -= 2; }

            int punID = history.get( punClicked ).getID();
            HistoryEditGUI editGUI = new HistoryEditGUI( target, staff, punID );
            editGUI.openInventory( staff );
        }

        else if ( itemClicked.equals( getSortHopper() ) ) {
            if ( sort == SortBy.FIRST_DATE ) {
                HistoryListGUI gui = new HistoryListGUI( target, staff, page, SortBy.LAST_DATE );
                gui.openInventory( staff );
            }

            else if ( sort == SortBy.LAST_DATE ) {
                HistoryListGUI gui = new HistoryListGUI( target, staff, page, SortBy.FIRST_ACTIVE );
                gui.openInventory( staff );
            }

            else if ( sort == SortBy.FIRST_ACTIVE ) {
                HistoryListGUI gui = new HistoryListGUI( target, staff, page, SortBy.LAST_ACTIVE );
                gui.openInventory( staff );
            }

            else if ( sort == SortBy.LAST_ACTIVE ) {
                HistoryListGUI gui = new HistoryListGUI( target, staff, page, SortBy.FIRST_DATE );
                gui.openInventory( staff );
            }
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's history" ) ) == false ) { return; }

        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( event.getPlayer().equals( staff ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's history" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}
