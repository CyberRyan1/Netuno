package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.netuno.classes.Report;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ReportsGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final int page;

    private SortBy sort;
    private ArrayList<Report> reports = new ArrayList<>();

    public ReportsGUI( Player staff, int page, SortBy sort ) {
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        reports = DATA.getAllReports( 21 * ( page - 1 ), 21 * page );
        sort();

        String guiName = Utils.getColored( "&6Reports" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();
    }

    public ReportsGUI( Player staff, int page ) {
        this( staff, page, SortBy.ONLINE );
    }

    private void sort() {
        if ( reports.size() == 0 ) { return; }

        if ( sort == SortBy.FIRST_DATE ) {
            ArrayList<Report> newReports = new ArrayList<>();
            newReports.add( reports.remove( 0 ) );
            for ( Report report : reports ) {
                for ( int index = 0; index < newReports.size(); index++ ) {
                    if ( report.getDate() < reports.get( index ).getDate() ) {
                        newReports.add( index, report );
                        break;
                    }
                }

                if ( newReports.contains( report ) == false ) { newReports.add( report ); }
            }

            reports = newReports;
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ArrayList<Report> newReports = new ArrayList<>();
            newReports.add( reports.remove( 0 ) );
            for ( Report report : reports ) {
                for ( int index = 0; index < newReports.size(); index++ ) {
                    if ( report.getDate() > reports.get( index ).getDate() ) {
                        newReports.add( index, report );
                        break;
                    }
                }

                if ( newReports.contains( report ) == false ) { newReports.add( report ); }
            }

            reports = newReports;
        }

        else if ( sort == SortBy.ONLINE ) {
            ArrayList<Report> newReports = new ArrayList<>();
            newReports.add( reports.remove( 0 ) );
            for ( Report report : reports ) {
                for ( int index = 0; index < newReports.size(); index++ ) {
                    if ( report.getTarget().isOnline() && newReports.get( index ).getTarget().isOnline() == false ) {
                        newReports.add( index, report );
                        break;
                    }
                }

                if ( newReports.contains( report ) == false ) { newReports.add( report ); }
            }

            reports = newReports;
        }

        else if ( sort == SortBy.OFFLINE ) {
            ArrayList<Report> newReports = new ArrayList<>();
            newReports.add( reports.remove( 0 ) );
            for ( Report report : reports ) {
                for ( int index = 0; index < newReports.size(); index++ ) {
                    if ( report.getTarget().isOnline() == false && newReports.get( index ).getTarget().isOnline() ) {
                        newReports.add( index, report );
                        break;
                    }
                }

                if ( newReports.contains( report ) == false ) { newReports.add( report ); }
            }

            reports = newReports;
        }
    }

    public void insertItems() {
        // dark background glass: everywhere
        // light background glass: 10-16, 19-25, 28-34
        // reports: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40
        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = GUIUtils.getBackgroundGlass();
        }

        int reportIndex = 21 * ( page - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( reportIndex >= reports.size() ) { items[guiIndex] = GUIUtils.createItem( Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7" ); }
                else { items[guiIndex] = reports.get( reportIndex ).getAsItem(); }

                guiIndex++; reportIndex++;
            }

            guiIndex += 2;
        }

        items[40] = getPaper();

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&6Previous Page" ); }
        int maxPage = ( int ) Math.ceil( reports.size() / 21.0 );
        if ( page < maxPage ) { items[51] = GUIUtils.createItem( Material.BOOK, "&6Next Page" ); }

        gui.setContents( items );
    }

    private ItemStack getPaper() {
        ItemStack paper = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        if ( sort == SortBy.FIRST_DATE ) { return GUIUtils.setItemLore( paper, "", "&7Sort: &6First Created -> Last Created",
                    "&7Next Sort: &6Last Created -> First Created", "&7Click to change sort method" ); }
        else if ( sort == SortBy.LAST_DATE ) { return GUIUtils.setItemLore( paper, "", "&7Sort: &6Last Created -> First Created",
                "&7Next Sort: &6Online -> Offline", "&7Click to change sort method" ); }
        else if ( sort == SortBy.ONLINE ) { return GUIUtils.setItemLore( paper, "", "&7Sort: &6Online -> Offline",
                "&7Next Sort: &6Offline -> Online", "&7Click to change sort method" ); }
        else if ( sort == SortBy.OFFLINE ) { return GUIUtils.setItemLore( paper, "", "&7Sort: &6Offline -> Online",
                "&7Next Sort: &6First Created -> Last Created", "&7Click to change sort method" ); }

        return null;
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6Reports" ) ) == false ) { return; }

        event.setCancelled( true );
        ItemStack clicked = event.getCurrentItem();
        if ( clicked == null || clicked.getType().isAir() ) { return; }

        if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&6Previous Page" ) ) ) {
            ReportsGUI newGUI = new ReportsGUI( staff, page - 1, sort );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&7Next Page" ) ) ) {
            ReportsGUI newGUI = new ReportsGUI( staff, page + 1, sort );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getPaper() ) ) {
            ReportsGUI newGUI;
            if ( sort == SortBy.FIRST_DATE ) { newGUI = new ReportsGUI ( staff, page, SortBy.LAST_DATE ); }
            else if ( sort == SortBy.LAST_DATE ) { newGUI = new ReportsGUI( staff, page, SortBy.ONLINE ); }
            else if ( sort == SortBy.ONLINE ) { newGUI = new ReportsGUI( staff, page, SortBy.OFFLINE ); }
            else { newGUI = new ReportsGUI( staff, page, SortBy.FIRST_DATE ); }

            newGUI.openInventory( staff );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6Reports" ) ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6Reports" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}


















