package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.netuno.classes.MultiReport;
import com.github.cyberryan1.netuno.classes.SingleReport;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffPlayerReportsGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Player staff;
    private final OfflinePlayer target;
    private final Inventory gui;
    private final int page;
    private final String guiName;

    private SortBy sort;
    private ArrayList<SingleReport> reports;
    private ArrayList<MultiReport> multiReports = new ArrayList<>();

    public StaffPlayerReportsGUI( Player staff, OfflinePlayer target, int page, SortBy sort ) {
        this.staff = staff;
        this.target = target;
        this.page = page;
        this.sort = sort;

        DATA.deleteAllExpiredReports();
        reports = DATA.getReport( target.getUniqueId().toString() );
        compressReports();
        sort();

        this.guiName = Utils.getColored( "&6" + target.getName() + "&7's Reports" );
        gui = Bukkit.createInventory( null, 54, this.guiName );
        insertItems();
    }

    public StaffPlayerReportsGUI( Player staff, OfflinePlayer target ) {
        this( staff, target, 1, SortBy.FIRST_DATE );
    }

    private void compressReports() {
        HashMap<Long, ArrayList<SingleReport>> datesToReport = new HashMap<>();
        for ( SingleReport sr : reports ) {
            if ( datesToReport.containsKey( sr.getDate() ) ) {
                ArrayList<SingleReport> newSingleReports = datesToReport.get( sr.getDate() );
                newSingleReports.add( sr );
                datesToReport.replace( sr.getDate(), newSingleReports );
            }

            else {
                ArrayList<SingleReport> singleReports = new ArrayList<>();
                singleReports.add( sr );
                datesToReport.put( sr.getDate(), singleReports );
            }
        }

        for ( ArrayList<SingleReport> srList : datesToReport.values() ) {
            multiReports.add( new MultiReport( srList ) );
        }
    }

    private void sort() {
        if ( reports.size() == 0 ) { return; }

        ArrayList<MultiReport> newReports = new ArrayList<>();
        newReports.add( multiReports.remove( 0 ) );

        switch ( sort ) {
            case FIRST_DATE: {
                for ( MultiReport mr : multiReports ) {
                    for ( int index = 0; index < newReports.size(); index++ ) {
                        if ( mr.getDate() < newReports.get( index ).getDate() ) {
                            newReports.add( mr );
                            break;
                        }
                    }

                    if ( newReports.contains( mr ) == false ) { newReports.add( mr ); }
                }

                break;
            }

            case LAST_DATE: {
                for ( MultiReport mr : multiReports ) {
                    for ( int index = 0; index < newReports.size(); index++ ) {
                        if ( mr.getDate() > newReports.get( index ).getDate() ) {
                            newReports.add( mr );
                            break;
                        }
                    }

                    if ( newReports.contains( mr ) == false ) { newReports.add( mr ); }
                }

                break;
            }
        }

        multiReports = newReports;
    }

    public void insertItems() {
        // dark background glass: everywhere
        // light background glass: 10-16, 19-25, 28-34
        // reports: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40
        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        int reportIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( reportIndex >= multiReports.size() ) { items[guiIndex] = GUIUtils.createItem( Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7" ); }
                else { items[guiIndex] = multiReports.get( reportIndex ).getAsItem(); }

                guiIndex++; reportIndex++;
            }

            guiIndex += 2;
        }

        items[40] = getPaper();

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&6Previous Page" ); }
        if ( page < ( ( int ) Math.ceil( reports.size() / 21.0 ) ) ) { items[51] = GUIUtils.createItem( Material.BOOK, "&6Next Page" ); }

        gui.setContents( items );
    }

    private ItemStack getPaper() {
        ItemStack paper = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        paper = GUIUtils.addItemLore( paper, "" );

        switch ( sort ) {
            case FIRST_DATE: {
                paper = GUIUtils.addItemLore( paper, "&7Sort: &6First Created -> Last Created", "&7Next Sort: &6Last Created -> First Created" );
                break;
            }

            case LAST_DATE: {
                paper = GUIUtils.addItemLore( paper, "&7Sort: &6Last Created -> First Created", "&7Next Sort: &6First Created -> Last Created" );
                break;
            }

            default: return null;
        }

        return GUIUtils.addItemLore( paper, "&7Click to change sort method" );
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( this.guiName ) == false ) { return; }

        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( this.guiName ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( this.guiName ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}