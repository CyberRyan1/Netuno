package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.models.NetunoMultiReport;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffPlayerReportsGUI implements Listener {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final int page;
    private final SortBy sort;

    private List<NReport> allSingularReports;
    private List<NetunoMultiReport> multiReports = new ArrayList<>();

    public StaffPlayerReportsGUI( Player staff, OfflinePlayer target, int page, SortBy sort ) {
        this.staff = staff;
        this.target = target;
        this.page = page;
        this.sort = sort;

        allSingularReports = ApiNetuno.getData().getNetunoReports().getReports( target );
        compressReports();
        sort();

        gui = new Gui( "&p" + target.getName() + "&s's Reports", 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public StaffPlayerReportsGUI( Player staff, OfflinePlayer target ) {
        this( staff, target, 1, SortBy.FIRST_DATE );
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
                if ( reportIndex < multiReports.size() ) {
                    gui.addItem( new GuiItem( multiReports.get( reportIndex ).getAsItem(), guiIndex ) );
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

        int maxPage = ( int ) Math.ceil( multiReports.size() / 21.0 );
        if ( page < maxPage ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                StaffAllReportsGUI newGUI = new StaffAllReportsGUI( staff, page + 1, sort );
                newGUI.open();
            } ) );
        }
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> gui.openInventory( staff ) );
    }

    private ItemStack getSortHopper() {
        ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sPage &p#" + page );
        if ( sort == SortBy.FIRST_DATE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pFirst Created -> Last Created",
                "&sNext Sort: &pLast Created -> First Created", "&sClick to change sort method" ); }
        else if ( sort == SortBy.LAST_DATE ) { return CyberItemUtils.setItemLore( hopper, "", "&sSort: &pLast Created -> First Created",
                "&sNext Sort: &pFirst Created -> Last Created", "&sClick to change sort method" ); }

        return null;
    }

    private void compressReports() {
        HashMap<Long, ArrayList<NReport>> datesToReport = new HashMap<>();
        for ( NReport sr : allSingularReports ) {
            if ( datesToReport.containsKey( sr.getTimestamp() ) ) {
                ArrayList<NReport> newSingleReports = datesToReport.get( sr.getTimestamp() );
                newSingleReports.add( sr );
                datesToReport.replace( sr.getTimestamp(), newSingleReports );
            }

            else {
                ArrayList<NReport> singleReports = new ArrayList<>();
                singleReports.add( sr );
                datesToReport.put( sr.getTimestamp(), singleReports );
            }
        }

        for ( ArrayList<NReport> srList : datesToReport.values() ) {
            multiReports.add( new NetunoMultiReport( srList ) );
        }

        ArrayList<NetunoMultiReport> tempReports = new ArrayList<>();
        final int START = 21 * ( page - 1 );
        final int END = 21 * page;
        for ( int index = START; index <= END; index++ ) {
            if ( index >= multiReports.size() ) { break; }
            tempReports.add( multiReports.get( index ) );
        }
        multiReports = tempReports;
    }

    private void sort() {
        if ( allSingularReports.size() == 0 ) { return; }

        ArrayList<NetunoMultiReport> newReports = new ArrayList<>();
        newReports.add( multiReports.remove( 0 ) );
        switch ( sort ) {
            case FIRST_DATE: {
                for ( NetunoMultiReport mr : multiReports ) {
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
                for ( NetunoMultiReport mr : multiReports ) {
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
}