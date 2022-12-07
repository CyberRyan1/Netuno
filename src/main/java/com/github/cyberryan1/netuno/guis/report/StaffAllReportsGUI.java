package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.models.NetunoCombinedReport;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StaffAllReportsGUI implements Listener {

    private final Gui gui;
    private final Player staff;
    private final int page;
    private final SortBy sort;

    private List<NReport> allSingularReports;
    private List<NetunoCombinedReport> combinedReports = new ArrayList<>();

    public StaffAllReportsGUI( Player staff, int page, SortBy sort ) {
        this.staff = staff;
        this.page = page;
        this.sort = sort;

        allSingularReports = ApiNetuno.getData().getNetunoReports().getCache();

        compressReports();
        sort();

        gui = new Gui( "&pReports", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public StaffAllReportsGUI( Player staff, int page ) {
        this( staff, page, SortBy.ONLINE );
    }

    public void insertItems() {
        // light background glass: 10-16, 19-25, 28-34
        // reports: 10-16, 19-25, 28-34
        // sort hopper: 40
        // back book: 47 || next book: 51

        int reportIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( reportIndex < combinedReports.size() ) {
                    final int reportIndexFinal = reportIndex;
                    gui.addItem( new GuiItem( combinedReports.get( reportIndex ).getAsItem(), guiIndex, ( item ) -> {
                        staff.closeInventory();
                        staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
                        StaffPlayerReportsGUI playerReports = new StaffPlayerReportsGUI( staff, combinedReports.get( reportIndexFinal ).getTarget() );
                        playerReports.open();
                    } ) );
                }

                else {
                    gui.addItem( new GuiItem( Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7", guiIndex ) );
                }

                guiIndex++;
                reportIndex++;
            }

            guiIndex += 2;
        }

        gui.addItem( new GuiItem( getSortHopper(), 40, ( item ) -> {
            staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
            SortBy newSort = switch ( sort ) {
                case FIRST_DATE -> SortBy.LAST_DATE;
                case LAST_DATE -> SortBy.ONLINE;
                case ONLINE -> SortBy.OFFLINE;
                default -> SortBy.FIRST_DATE;
            };

            StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page, newSort );
            newGUI.open();
        } ) );

        if ( page >= 2 ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pPrevious Page", 47, ( item ) -> {
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page - 1, sort );
                newGUI.open();
            } ) );
        }

        int maxPage = ( int ) Math.ceil( combinedReports.size() / 21.0 );
        if ( page < maxPage ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page + 1, sort );
                newGUI.open();
            } ) );
        }

        gui.openInventory( staff );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
    }

    private ItemStack getSortHopper() {
        ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sPage &p#" + page );
        if ( sort == SortBy.FIRST_DATE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pFirst Created -> Last Created",
                "&sNext Sort: &pLast Created -> First Created", "&sClick to change sort method" ); }
        else if ( sort == SortBy.LAST_DATE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pLast Created -> First Created",
                "&sNext Sort: &pOnline -> Offline", "&sClick to change sort method" ); }
        else if ( sort == SortBy.ONLINE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pOnline -> Offline",
                "&sNext Sort: &pOffline -> Online", "&sClick to change sort method" ); }
        else if ( sort == SortBy.OFFLINE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pOffline -> Online",
                "&sNext Sort: &pFirst Created -> Last Created", "&sClick to change sort method" ); }

        return null;
    }

    private void compressReports() {
        if ( allSingularReports.size() == 0 ) { return; }

        ArrayList<UUID> playersReported = new ArrayList<>();
        for ( int index = allSingularReports.size() - 1; index >= 0; index-- ) {
            UUID uuid = allSingularReports.get( index ).getPlayer().getUniqueId();
            if ( playersReported.contains( uuid ) == false ) { playersReported.add( uuid ); }
        }

        for ( UUID uuid : playersReported ) {
            combinedReports.add( new NetunoCombinedReport( Bukkit.getOfflinePlayer( uuid ) ) );
        }

        ArrayList<NetunoCombinedReport> tempReports = new ArrayList<>();
        final int START = 21 * ( page - 1 );
        final int END = 21 * page;
        for ( int index = START; index <= END; index++ ) {
            if ( index >= combinedReports.size() ) { break; }
            tempReports.add( combinedReports.get( index ) );
        }
        combinedReports = tempReports;
    }

    private void sort() {
        if ( allSingularReports.size() == 0 ) { return; }
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
}