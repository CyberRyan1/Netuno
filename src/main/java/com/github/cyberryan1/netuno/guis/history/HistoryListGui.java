package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.guis.utils.Sorter;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists the provided target's punishments to the staff member
 * in a nice GUI format.
 *
 * @author Ryan
 */
public class HistoryListGui {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final int pageNumber;
    private final SortBy sort;

    private List<ApiPunishment> history = null;

    /**
     * @param staff The staff member
     * @param target The target player
     * @param pageNumber The page number (starts at 1)
     *
     */
    public HistoryListGui( Player staff, OfflinePlayer target, int pageNumber, SortBy sort ) {
        this.staff = staff;
        this.target = target;
        this.pageNumber = pageNumber;
        this.sort = sort;

        this.gui = new Gui( "&p" + target.getName() + "&s's History", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    /**
     * Alias to {@link #HistoryListGui(Player, OfflinePlayer, int, SortBy)},
     * but sets the page number to 1 (the starting page) and
     * uses {@link SortBy#FIRST_DATE} for sorting
     *
     * @param staff The staff member
     * @param target The target player
     */
    public HistoryListGui( Player staff, OfflinePlayer target ) {
        this( staff, target, 1, SortBy.FIRST_DATE );
    }

    /**
     * Inserts all items into the GUI
     */
    private void insertItems() {
        final ItemStack loadingItem = CyberItemUtils.createItem(
                Settings.PUNISH_LOADING_ITEM_MATERIAL.material(), Settings.PUNISH_LOADING_ITEM_NAME.coloredString() );

        // Filling all GUI punishment slots with the loading item
        for ( int index : getPunishmentSlots() ) {
            GuiItem item = new GuiItem( loadingItem, index );
            gui.addItem( item );
        }

        // Querying the target's punishments and updating the
        //      GUI punishment slots as needed
        Netuno.SERVICE.getPlayer( target ).thenAccept( netunoTarget -> {
            history = Sorter.sortPuns( netunoTarget.getPunishments(), sort );

            int punIndex = 21 * ( pageNumber - 1 );
            int guiIndex = 10;
            for ( int row = 0; row < 3; row++ ) {
                for ( int col = 0; col < 7; col++ ) {
                    GuiItem item;

                    if ( punIndex >= history.size() ) {
                        item = new GuiItem( CyberItemUtils.createItem( Material.WHITE_STAINED_GLASS_PANE, "&f" ), guiIndex );
                    }

                    else {
                        final int finalPunIndex = punIndex;
                        item = new GuiItem( HistoryUtils.getPunishmentItem( history.get( punIndex ) ),
                                guiIndex, ( i ) -> {
                            int punId = history.get( finalPunIndex ).getId();
                            // TODO open history edit GUI for this punishment
                            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                        } );
                    }

                    gui.updateItem( item );
                    guiIndex++;
                    punIndex++;
                }

                guiIndex += 2;
            }

            // Next Page Item
            // (must be done here because this needs access to the history variable)
            int maxPages = ( int ) Math.ceil( history.size() / 21.0 );
            if ( pageNumber < maxPages ) {
                gui.updateItem( new GuiItem( Material.BOOK, "&pNext Page", 52, ( item ) -> {
                    HistoryListGui listGui = new HistoryListGui( target.getPlayer(), staff, pageNumber + 1, sort );
                    listGui.open();
                    staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                } ) );
            }
        } );

        // Current page item
        ItemStack paper = CyberItemUtils.createItem( Material.PAPER, "&sPage &p#" + pageNumber );
        CyberItemUtils.setItemLore( paper, "&sClick any item to edit the punishment!" );
        gui.addItem( new GuiItem( paper, 40 ) );

        // Sort Hopper Item
        gui.addItem( new GuiItem( getSortHopper(), 49, ( item ) -> {
            SortBy next = SortBy.FIRST_DATE;
            if ( sort == SortBy.FIRST_DATE ) { next = SortBy.LAST_DATE; }
            else if ( sort == SortBy.LAST_DATE ) { next = SortBy.FIRST_ACTIVE; }
            else if ( sort == SortBy.FIRST_ACTIVE ) { next = SortBy.LAST_ACTIVE; }
            else if ( sort == SortBy.LAST_ACTIVE ) { next = SortBy.FIRST_DATE; }

            HistoryListGui listGui = new HistoryListGui( target.getPlayer(), staff, pageNumber, next );
            listGui.open();
            staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
        } ) );

        // Previous Page Item
        if ( pageNumber >= 2 ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pPrevious Page", 46, ( item ) -> {
                HistoryListGui listGui = new HistoryListGui( target.getPlayer(), staff, pageNumber - 1, sort );
                listGui.open();
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            } ) );
        }
    }

    /**
     * Opens the GUI to the staff member
     */
    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
        } );
    }

    /**
     * @return A list of all slots that will be used to display
     *         punishments
     */
    private List<Integer> getPunishmentSlots() {
        List<Integer> toReturn = new ArrayList<>();
        // returning 10 -> 16, 19 -> 25, and 28 -> 34
        for ( int i = 10; i <= 16; i++ ) {
            toReturn.add( i );
            toReturn.add( i + 9 );
            toReturn.add( i + 18 );
        }

        return toReturn;
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