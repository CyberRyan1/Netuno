package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.netuno.classes.CombinedReport;
import com.github.cyberryan1.netuno.classes.SingleReport;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StaffAllReportsGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final int page;

    private SortBy sort;
    private ArrayList<SingleReport> reports;
    private List<CombinedReport> combinedReports = new ArrayList<>();

    public StaffAllReportsGUI( Player staff, int page, SortBy sort ) {
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        DATA.deleteAllExpiredReports();
        if ( Utils.getJavaVersion() >= 10 ) {
            reports = DATA.getAllReports( 21 * ( page - 1 ), 21 * page );
        }
        else {
            reports = DATA.getAllReports();
            ArrayList<SingleReport> tempReports = new ArrayList<>();
            for ( int i = 21 * ( page - 1 ); i <= 21 * page; i++ ) {
                if ( reports.size() <= i ) { break; }
                tempReports.add( reports.get( i ) );
            }
            reports = tempReports;
        }
        compressReports();
        sort();

        String guiName = Utils.getColored( "&gReports" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();
    }

    public StaffAllReportsGUI( Player staff, int page ) {
        this( staff, page, SortBy.ONLINE );
    }

    private void compressReports() {
        if ( reports.size() == 0 ) { return; }

        ArrayList<UUID> playersReported = new ArrayList<>();
        for ( int index = reports.size() - 1; index >= 0; index-- ) {
            UUID uuid = reports.get( index ).getTarget().getUniqueId();
            if ( playersReported.contains( uuid ) == false ) { playersReported.add( uuid ); }
        }

        for ( UUID uuid : playersReported ) {
            combinedReports.add( new CombinedReport( Bukkit.getOfflinePlayer( uuid ) ) );
        }
    }

    private void sort() {
        if ( reports.size() == 0 ) { return; }
        if ( sort == SortBy.ONLINE || sort == SortBy.OFFLINE ) {
            combinedReports = combinedReports.stream()
                    .sorted( ( r1, r2 ) -> (
                            sort == SortBy.ONLINE ?
                                    r1.getTarget().isOnline() && r2.getTarget().isOnline() ? 0 :
                                            ( r1.getTarget().isOnline() ? -1 : 1 ) :
                                    r1.getTarget().isOnline() && r2.getTarget().isOnline() ? 0 :
                                            ( r1.getTarget().isOnline() ? 1 : -1 )
                    ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_DATE || sort == SortBy.LAST_DATE ) {
            combinedReports = combinedReports.stream()
                    .sorted( ( r1, r2 ) -> ( ( int )
                            ( sort == SortBy.FIRST_DATE ?
                                    r1.getMostRecentDate() - r2.getMostRecentDate() :
                                    r2.getMostRecentDate() - r1.getMostRecentDate()
                            )
                    ) )
                    .collect( Collectors.toList() );
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

        int reportIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( reportIndex >= combinedReports.size() ) { items[guiIndex] = GUIUtils.getBackgroundGlass(); }
                else { items[guiIndex] = combinedReports.get( reportIndex ).getAsItem(); }

                guiIndex++; reportIndex++;
            }

            guiIndex += 2;
        }

        items[40] = getPaper();

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&gPrevious Page" ); }
        int maxPage = ( int ) Math.ceil( DATA.getReportsCount() / 21.0 );
        if ( page < maxPage ) { items[51] = GUIUtils.createItem( Material.BOOK, "&gNext Page" ); }

        gui.setContents( items );
    }

    private ItemStack getPaper() {
        ItemStack paper = GUIUtils.createItem( Material.PAPER, "&hPage &g#" + page );
        if ( sort == SortBy.FIRST_DATE ) { return GUIUtils.setItemLore( paper, "", "&hSort: &gFirst Created -> Last Created",
                    "&hNext Sort: &gLast Created -> First Created", "&hClick to change sort method" ); }
        else if ( sort == SortBy.LAST_DATE ) { return GUIUtils.setItemLore( paper, "", "&hSort: &gLast Created -> First Created",
                "&hNext Sort: &gOnline -> Offline", "&hClick to change sort method" ); }
        else if ( sort == SortBy.ONLINE ) { return GUIUtils.setItemLore( paper, "", "&hSort: &gOnline -> Offline",
                "&hNext Sort: &gOffline -> Online", "&hClick to change sort method" ); }
        else if ( sort == SortBy.OFFLINE ) { return GUIUtils.setItemLore( paper, "", "&hSort: &gOffline -> Online",
                "&hNext Sort: &gFirst Created -> Last Created", "&hClick to change sort method" ); }

        return null;
    }

    public void openInventory( Player staff ) {
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReports" ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack clicked = event.getCurrentItem();
        if ( clicked == null || clicked.getType() == Material.AIR ) { return; }

        if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&gPrevious Page" ) ) ) {
            StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page - 1, sort );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&gNext Page" ) ) ) {
            StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page + 1, sort );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( clicked.equals( getPaper() ) ) {
            StaffAllReportsGUI newGUI;
            if ( sort == SortBy.FIRST_DATE ) { newGUI = new StaffAllReportsGUI( staff, page, SortBy.LAST_DATE ); }
            else if ( sort == SortBy.LAST_DATE ) { newGUI = new StaffAllReportsGUI( staff, page, SortBy.ONLINE ); }
            else if ( sort == SortBy.ONLINE ) { newGUI = new StaffAllReportsGUI( staff, page, SortBy.OFFLINE ); }
            else { newGUI = new StaffAllReportsGUI( staff, page, SortBy.FIRST_DATE ); }

            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }

        else if ( clicked.getType().equals( GUIUtils.getItemForVersion( "PLAYER_HEAD", "SKULL_ITEM" ).getType() ) ) {
            String itemName = clicked.getItemMeta().getDisplayName();
            itemName = Utils.removeColorCodes( itemName );
            OfflinePlayer target = Bukkit.getOfflinePlayer( itemName );

            if ( target != null ) {
                StaffPlayerReportsGUI newGui = new StaffPlayerReportsGUI( staff, target );
                staff.closeInventory();
                staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
                newGui.openInventory( staff );
            }

            else { // just in case
                CommandErrors.sendPlayerNotFound( staff, itemName );
            }
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReports" ) ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReports" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}