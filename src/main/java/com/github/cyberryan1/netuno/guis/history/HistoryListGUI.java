package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.guis.utils.Sorter;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HistoryListGUI {

    private final Gui gui;
    private final NetunoPlayer target;
    private final Player staff;
    private final int page;
    private final SortBy sort;
    private final List<NPunishment> history;

    public HistoryListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = NetunoPlayerCache.getOrLoad( target.getUniqueId().toString() );
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        this.history = this.target.getPunishments();
        Sorter.sortPuns( this.history, sort );

        this.gui = new Gui( "&p" + target.getName() + "&s's History", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public HistoryListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.FIRST_DATE );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
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
                GuiItem item;
                if ( punIndex >= history.size() ) {
                    item = new GuiItem( CyberItemUtils.createItem( Material.WHITE_STAINED_GLASS_PANE, "&f" ), guiIndex );
                }

                else {
                    final int finalPunIndex = punIndex;
                    item = new GuiItem( GUIUtils.getPunishmentItem( history.get( punIndex ) ), guiIndex, ( i ) -> {
                        int punId = history.get( finalPunIndex ).getId();
                        HistoryEditGUI editGui = new HistoryEditGUI( target.getPlayer(), staff, punId );
                        editGui.open();
                        staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                    } );
                }

                gui.addItem( item );
                guiIndex++;
                punIndex++;
            }

            guiIndex += 2;
        }

        // Current Page Item
        gui.addItem( new GuiItem( getCurrentPagePaper(), 40 ) );

        // Sort Hopper Item
        gui.addItem( new GuiItem( getSortHopper(), 49, ( item ) -> {
            SortBy next = SortBy.FIRST_DATE;
            if ( sort == SortBy.FIRST_DATE ) { next = SortBy.LAST_DATE; }
            else if ( sort == SortBy.LAST_DATE ) { next = SortBy.FIRST_ACTIVE; }
            else if ( sort == SortBy.FIRST_ACTIVE ) { next = SortBy.LAST_ACTIVE; }
            else if ( sort == SortBy.LAST_ACTIVE ) { next = SortBy.FIRST_DATE; }

            HistoryListGUI listGui = new HistoryListGUI( target.getPlayer(), staff, page, next );
            listGui.open();
            staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
        } ) );

        // Previous Page Item
        if ( page >= 2 ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pPrevious Page", 47, ( item ) -> {
                HistoryListGUI listGui = new HistoryListGUI( target.getPlayer(), staff, page - 1, sort );
                listGui.open();
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            } ) );
        }

        // Next Page Item
        int maxPages = ( int ) Math.ceil( history.size() / 21.0 );
        if ( page < maxPages ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                HistoryListGUI listGui = new HistoryListGUI( target.getPlayer(), staff, page + 1, sort );
                listGui.open();
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            } ) );
        }

        gui.openInventory( staff );
    }

    private ItemStack getCurrentPagePaper() {
        ItemStack paper = CyberItemUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        return CyberItemUtils.setItemLore( paper, "&sClick any item to edit the punishment!" );
    }

    private ItemStack getSortHopper() {
        if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pOldest -> Newest" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pNewest -> Oldest", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNewest -> Oldest" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pActive -> Not Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pActive -> Not Active" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pNot Active -> Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNot Active -> Active" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pOldest -> Newest", "&sClick to change sort method" );
        }

        return null;
    }
}