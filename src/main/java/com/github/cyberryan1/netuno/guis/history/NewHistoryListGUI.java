package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NewHistoryListGUI {

    private final GUI gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final int page;
    private final SortBy sort;
    private final List<Punishment> history = new ArrayList<>();

    public NewHistoryListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        history.addAll( Utils.getDatabase().getAllPunishments( target.getUniqueId().toString() ) );
        Punishment.sortPuns( history, sort );

        gui = new GUI( "&p" + target.getName() + "&s's History", 6, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public NewHistoryListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.FIRST_DATE );
    }

    public void open() {
        gui.openInventory( staff );
    }

    private void insertItems() {
        // punishments: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40

        // Punishment Items
        int punIndex = 21 * ( page - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                GUIItem item;
                if ( punIndex >= history.size() ) {
                    item = new GUIItem( CoreGUIUtils.createItem( Material.WHITE_STAINED_GLASS_PANE, "&f" ), guiIndex );
                }

                else {
                    final int finalPunIndex = punIndex;
                    item = new GUIItem( history.get( punIndex ).getPunishmentAsItem(), guiIndex, () -> {
                        int punId = history.get( finalPunIndex ).getID();
                        NewHistoryEditGUI editGui = new NewHistoryEditGUI( target, staff, punId );
                        editGui.open();
                        staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                    } );
                }

                gui.setItem( guiIndex, item );
                guiIndex++;
                punIndex++;
            }

            guiIndex += 2;
        }

        // Current Page Item
        gui.setItem( 40, new GUIItem( getCurrentPagePaper(), 40 ) );

        // Sort Hopper Item
        gui.setItem( 49, new GUIItem( getSortHopper(), 49, () -> {
            SortBy next = SortBy.FIRST_DATE;
            if ( sort == SortBy.FIRST_DATE ) { next = SortBy.LAST_DATE; }
            else if ( sort == SortBy.LAST_DATE ) { next = SortBy.FIRST_ACTIVE; }
            else if ( sort == SortBy.FIRST_ACTIVE ) { next = SortBy.LAST_ACTIVE; }
            else if ( sort == SortBy.LAST_ACTIVE ) { next = SortBy.FIRST_DATE; }

            NewHistoryListGUI listGui = new NewHistoryListGUI( target, staff, page, next );
            listGui.open();
            staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
        } ) );

        // Previous Page Item
        if ( page >= 2 ) {
            gui.setItem( 47, new GUIItem( Material.BOOK, "&pPrevious Page", 47, () -> {
                NewHistoryListGUI listGui = new NewHistoryListGUI( target, staff, page - 1, sort );
                listGui.open();
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            } ) );
        }

        // Next Page Item
        int maxPages = ( int ) Math.ceil( history.size() / 21.0 );
        if ( page < maxPages ) {
            gui.setItem( 51, new GUIItem( Material.BOOK, "&pNext Page", 51, () -> {
                NewHistoryListGUI listGui = new NewHistoryListGUI( target, staff, page + 1, sort );
                listGui.open();
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            } ) );
        }

        gui.createInventory();
    }

    private ItemStack getCurrentPagePaper() {
        ItemStack paper = CoreGUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        return CoreGUIUtils.setItemLore( paper, "&s&Click any item to edit the punishment!" );
    }

    private ItemStack getSortHopper() {
        if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pOldest -> Newest" );
            return CoreGUIUtils.setItemLore( hopper, "&sNext Sort: &pNewest -> Oldest", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNewest -> Oldest" );
            return CoreGUIUtils.setItemLore( hopper, "&sNext Sort: &pActive -> Not Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pActive -> Not Active" );
            return CoreGUIUtils.setItemLore( hopper, "&sNext Sort: &pNot Active -> Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ItemStack hopper = CoreGUIUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNot Active -> Active" );
            return CoreGUIUtils.setItemLore( hopper, "&sNext Sort: &pOldest -> Newest", "&sClick to change sort method" );
        }

        return null;
    }
}