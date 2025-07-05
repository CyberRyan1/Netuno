package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.guis.report.models.CondensedReport;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The GUI that is displayed to a staff that shows all reports.
 * This GUI will be shown when the staff executes
 * <code>/viewreports</code>
 *
 * @author Ryan
 */
public class ViewAllReportsGui {

    private final Gui gui;
    private final Player staff;
    private final int pageNumber;
    private final List<CondensedReport> sortedCondensedReports = new ArrayList<>();

    public ViewAllReportsGui( Player staff, int pageNumber ) {
        this.staff = staff;
        this.pageNumber = pageNumber;

        for ( UUID uuid : Netuno.REPORT_SERVICE.getCache().keySet() ) {
            this.sortedCondensedReports.add( new CondensedReport( uuid ) );
        }
        // sorting this by online players first
        this.sortedCondensedReports.sort( ( a, b ) -> {
            if ( a.getPlayer().isOnline() == b.getPlayer().isOnline() ) {
                return a.getPlayer().getName().compareToIgnoreCase( b.getPlayer().getName() );
            }
            return a.getPlayer().isOnline() ? -1 : 1;
        } );

        this.gui = new Gui( "&pReports", 5, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public ViewAllReportsGui( Player staff ) {
        this( staff, 1 );
    }

    public void insertItems() {
        // reports: 10-16, 19-25, 28-34 (meaning 21 reports per page)
        // back book: 47 || next book: 51

        // Reports
        int reportIndex = 21 * ( pageNumber - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 7; col++ ) {
                if ( reportIndex < sortedCondensedReports.size() ) {
                    final int reportIndexFinal = reportIndex;
                    gui.addItem( new GuiItem( sortedCondensedReports.get( reportIndex ).asGuiItem(), guiIndex, ( item ) -> {
                        staff.closeInventory();
                        staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );

                        ViewPlayerReportsGui gui = new ViewPlayerReportsGui( staff, sortedCondensedReports.get( reportIndexFinal ).getPlayer() );
                        gui.open();
                    } ) );
                }
                else {
                    gui.addItem( new GuiItem( Material.WHITE_STAINED_GLASS_PANE, "&7", guiIndex ) );
                }

                guiIndex++;
                reportIndex++;
            }

            guiIndex += 2;
        }

        // Previous Page
        if ( pageNumber >= 2 ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pPrevious Page", 47, ( item ) -> {
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                ViewAllReportsGui newGUI = new ViewAllReportsGui( staff, this.pageNumber - 1 );
                newGUI.open();
            } ) );
        }

        // Next Page
        int maxPage = ( int ) Math.ceil( sortedCondensedReports.size() / 21.0 );
        if ( pageNumber < maxPage ) {
            gui.addItem( new GuiItem( Material.BOOK, "&pNext Page", 51, ( item ) -> {
                staff.playSound( staff.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 10, 1 );
                ViewAllReportsGui newGUI = new ViewAllReportsGui( staff, this.pageNumber + 1 );
                newGUI.open();
            } ) );
        }
    }

    public void open() {
        gui.openInventory( staff );
    }
}