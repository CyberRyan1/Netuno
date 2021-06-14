package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.utils.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HistoryListGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    //                      Staff name | Target UUID
    private final static HashMap<String, UUID> staffTargets = new HashMap<>();
    //                      Staff name | Page #
    private final static HashMap<String, Integer> staffPages = new HashMap<>();
    //                          Staff name
    private final static ArrayList<String> inventoryClickCooldown = new ArrayList<>();

    private ArrayList<Punishment> punHistory = new ArrayList<>();
    private ArrayList<IPPunishment> ippunHistory = new ArrayList<>();
    // (below) is the combined version of punHistory and ippunHistory, organized by date
    private final ArrayList<Punishment> history = new ArrayList<>();


    public HistoryListGUI( OfflinePlayer target, Player staff, int page ) {
        staffTargets.remove( staff.getName() );
        staffTargets.put( staff.getName(), target.getUniqueId() );

        staffPages.remove( staff.getName() );
        staffPages.put( staff.getName(), page );

        initializeLists( staff );

        String guiName = Utils.getColored( "&6" + target.getName() + "&7's history" );
        gui = Bukkit.createInventory( null, 54, guiName );

        insertItems( staff );
    }

    private void initializeLists( Player staff ) {
        OfflinePlayer target = Bukkit.getOfflinePlayer( staffTargets.get( staff.getName() ) );
        punHistory = DATA.getPunishment( target.getUniqueId().toString() );
        history.addAll( punHistory );

        ippunHistory = DATA.getIPPunishment( target.getUniqueId().toString() );
        ArrayList<IPPunishment> ipPuns = new ArrayList<>( ippunHistory );

        for ( int index = 0; index < history.size(); index++ ) {
            if ( ipPuns.size() > 0 && history.get( index ).getDate() > ipPuns.get( 0 ).getDate() ) {
                history.add( index, ipPuns.remove( 0 ) );
            }
        }
        history.addAll( ipPuns );
    }

    public void insertItems( Player staff ) {
        // glass: 0-8; 9, 18, 27, 17, 26, 35, 36-44, 45, 46, 48-50, 52-53
        // signs: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40
        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = getBackgroundGlass();
        }

        int page = staffPages.get( staff.getName() );
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

        items[40] = getCurrentPagePaper( page );

        if ( page >= 2 ) {
            items[47] = getPreviousPageBook();
        }
        int maxPage = ( history.size() / 21 ) + 1;
        if ( page < maxPage ) {
            items[51] = getNextPageBook();
        }

        gui.setContents( items );
    }

    private ItemStack getPunishmentItem( int index ) {
        Punishment current = history.get( index );
        return current.getPunishmentAsSign();
    }

    private ItemStack getBackgroundGlass() {
        ItemStack glass = new ItemStack( Material.GRAY_STAINED_GLASS_PANE );
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName( "" );
        glass.setItemMeta( meta );
        return glass;
    }

    private ItemStack getNextPageBook() {
        ItemStack book = new ItemStack( Material.BOOK );
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&6Next Page" ) );
        book.setItemMeta( meta );
        return book;
    }

    private ItemStack getPreviousPageBook() {
        ItemStack book = new ItemStack( Material.BOOK );
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&6Previous Page" ) );
        book.setItemMeta( meta );
        return book;
    }

    private ItemStack getCurrentPagePaper( int pageNumber ) {
        ItemStack paper = new ItemStack( Material.PAPER );
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Page &6#" + pageNumber ) );

        ArrayList<String> lore = new ArrayList<>();
        lore.add( Utils.getColored( "&7&oClick any sign to edit the punishment!" ) );
        meta.setLore( lore );

        paper.setItemMeta( meta );
        return paper;
    }

    public void openInventory( Player staff ) {
        OfflinePlayer target = Bukkit.getOfflinePlayer( staffTargets.get( staff.getName() ) );
        if ( gui.contains( Material.OAK_SIGN ) == false && staffPages.get( staff.getName() ) == 0 ) { CommandErrors.sendNoPreviousPunishments( staff, target.getName() ); }
        else if ( gui.contains( Material.OAK_SIGN ) == false ) { CommandErrors.sendUnexpectedError( staff ); }
        else { staff.openInventory( gui ); }
    }

    @EventHandler
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staffTargets.containsKey( event.getWhoClicked().getName() ) == false ) { return; }
        Player staff = ( Player ) event.getWhoClicked();
        if ( inventoryClickCooldown.contains( staff.getName() ) ) { return; }

        OfflinePlayer target = Bukkit.getOfflinePlayer( staffTargets.get( staff.getName() ) );
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's history" ) ) == false ) { return; }

        event.setCancelled( true );

        ItemStack itemClicked = event.getCurrentItem();
        if ( itemClicked == null || itemClicked.getType().isAir() ) { return; }

        if ( itemClicked.getType() == Material.BOOK ) {
            int page = staffPages.get( staff.getName() );
            String name = itemClicked.getItemMeta().getDisplayName();

            if ( name.contains( "Next Page" ) ) {
                HistoryListGUI next = new HistoryListGUI( target, staff, page + 1 );
                staff.closeInventory();
                next.openInventory( staff );

                inventoryClickCooldown.add( staff.getName() );
                Bukkit.getScheduler().runTaskLater( Utils.getPlugin(), () -> {
                    inventoryClickCooldown.remove( staff.getName() );
                }, 5L );
            }
            else {
                HistoryListGUI previous = new HistoryListGUI( target, staff, page - 1 );
                staff.closeInventory();
                previous.openInventory( staff );

                inventoryClickCooldown.add( staff.getName() );
                Bukkit.getScheduler().runTaskLater( Utils.getPlugin(), () -> {
                    inventoryClickCooldown.remove( staff.getName() );
                }, 5L );
            }
        }
    }

    @EventHandler
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staffTargets.containsKey( event.getWhoClicked().getName() ) == false ) { return; }
        Player staff = ( Player ) event.getWhoClicked();
        OfflinePlayer target = Bukkit.getOfflinePlayer( staffTargets.get( staff.getName() ) );
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's history" ) ) == false ) { return; }

        event.setCancelled( true );
    }
}
