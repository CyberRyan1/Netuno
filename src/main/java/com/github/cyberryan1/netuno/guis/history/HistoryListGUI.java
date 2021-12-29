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
import org.bukkit.Sound;
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
import java.util.List;
import java.util.stream.Collectors;

public class HistoryListGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final int page;
    private final SortBy sort;
    private List<Punishment> history = new ArrayList<>();

    public HistoryListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        history.addAll( DATA.getAllPunishments( target.getUniqueId().toString() ) );
        sort();

        String guiName = Utils.getColored( "&g" + target.getName() + "&h's history" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();
    }

    public HistoryListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.FIRST_DATE );
    }

    private void sort() {
        if ( history.size() == 0 ) { return; }

        if ( sort == SortBy.FIRST_DATE || sort == SortBy.LAST_DATE ) {
            history = history.stream()
                    .sorted( ( o1, o2 ) -> ( int ) (
                            sort == SortBy.FIRST_DATE ? o1.getDate() - o2.getDate() : o2.getDate() - o1.getDate()
                    ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_ACTIVE || sort == SortBy.LAST_ACTIVE ) {
            history = history.stream()
                    .sorted( ( o1, o2 ) -> (
                            sort == SortBy.FIRST_ACTIVE ? ( o1.getActive() == o2.getActive() ? 0 : -1 )
                                    : ( o1.getActive() == o2.getActive() ? 1 : 0 )
                            ) )
                    .collect( Collectors.toList() );
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
            items[47] = GUIUtils.createItem( Material.BOOK, "&gPrevious Page" );
        }
        int maxPage = ( int ) Math.ceil( history.size() / 21.0 ) ;
        if ( page < maxPage ) {
            items[51] = GUIUtils.createItem( Material.BOOK, "&gNext Page" );
        }

        gui.setContents( items );
    }

    private ItemStack getPunishmentItem( int index ) {
        Punishment current = history.get( index );
        return current.getPunishmentAsItem();
    }

    private ItemStack getCurrentPagePaper() {
        ItemStack paper = new ItemStack( Material.PAPER );
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&hPage &g#" + page ) );

        ArrayList<String> lore = new ArrayList<>();
        lore.add( Utils.getColored( "&h&oClick any sign to edit the punishment!" ) );
        meta.setLore( lore );

        paper.setItemMeta( meta );
        return paper;
    }

    private ItemStack getSortHopper() {
        if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&hCurrent Sort: &gOldest -> Newest" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&hNext Sort: &gNewest -> Oldest" ) );
            lore.add( Utils.getColored( "&hClick to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&hCurrent Sort: &gNewest -> Oldest" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&hNext Sort: &gActive -> Not Active" ) );
            lore.add( Utils.getColored( "&hClick to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&hCurrent Sort: &gActive -> Not Active" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&hNext Sort: &gNot Active -> Active" ) );
            lore.add( Utils.getColored( "&hClick to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ItemStack hopper = GUIUtils.createItem( Material.HOPPER, "&hCurrent Sort: &gNot Active -> Active" );
            ArrayList<String> lore = new ArrayList<>();
            lore.add( Utils.getColored( "&hNext Sort: &gNewest -> Oldest" ) );
            lore.add( Utils.getColored( "&hClick to change sort method" ) );
            return GUIUtils.setItemLore( hopper, lore );
        }

        return null;
    }

    public void openInventory( Player staff ) {
        if ( gui.contains( Material.EMERALD ) == false && gui.contains( Material.REDSTONE ) == false && page == 1 ) {
            CommandErrors.sendNoPreviousPunishments( staff, target.getName() );
        }
        else {
            staff.openInventory( gui );
            GUIEventManager.addEvent( this );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( event.getWhoClicked().getName().equals( staff.getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&g" + target.getName() + "&h's history" ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack itemClicked = event.getCurrentItem();
        if ( itemClicked == null || itemClicked.getType() == Material.AIR ) { return; }

        if ( itemClicked.equals( GUIUtils.createItem( Material.BOOK, "&gNext Page" ) ) ) {
            HistoryListGUI next = new HistoryListGUI( target, staff, page + 1, sort );
            next.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( itemClicked.equals( GUIUtils.createItem( Material.BOOK, "&gPrevious Page" ) ) ) {
            HistoryListGUI previous = new HistoryListGUI( target, staff, page - 1, sort );
            previous.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( ( itemClicked.getType() == Material.EMERALD || itemClicked.getType() == Material.REDSTONE )
                && itemClicked.getItemMeta().getDisplayName().contains( Utils.getColored( "&hPunishment &g#" ) ) ) {
            int punClicked = ( ( page - 1 ) * 21 ) + event.getSlot() - 10;
            if ( event.getSlot() >= 18 ) { punClicked -= 2; }
            if ( event.getSlot() >= 27 ) { punClicked -= 2; }

            int punID = history.get( punClicked ).getID();
            HistoryEditGUI editGUI = new HistoryEditGUI( target, staff, punID );
            editGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( itemClicked.equals( getSortHopper() ) ) {
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
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
        if ( event.getView().getTitle().equals( Utils.getColored( "&g" + target.getName() + "&h's history" ) ) == false ) { return; }

        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( event.getPlayer().equals( staff ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&g" + target.getName() + "&h's history" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}
