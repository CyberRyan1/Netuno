package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import com.github.cyberryan1.netuno.guis.report.models.CondensedReport;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The GUI that is displayed to a staff that shows all reports
 * against a certain player. This will be shown when the staff
 * member executes <code>/viewreports (player)</code>
 *
 * @author Ryan
 */
public class ViewPlayerReportsGui {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final List<ApiReport> reports;

    public ViewPlayerReportsGui( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;
        this.reports = Netuno.REPORT_SERVICE.getReportsAgainst( target );

        int rowSize = 4 + ( ReportUtils.AVAILABLE_REASONS.size() / 6 );
        this.gui = new Gui( "&p" + target.getName() + "&s's Reports", rowSize, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // target's skull: 13
        // reports: start at 19-21 and 23-24, every row as needed
        // delete all reports: second row from bottom in the middle

        // Target skull
        ItemStack skull = CyberItemUtils.getPlayerSkull( target );
        skull = CyberItemUtils.setItemName( skull, "&p" + target.getName() );
        int reportCount = 0;
        for ( ApiReport report : reports ) {
            reportCount += report.getReasons().size();
        }
        skull = CyberItemUtils.setItemLore( skull, "&p" + reportCount + " &stotal reports" );
        gui.addItem( new GuiItem( skull, 13 ) );

        // Report reasons
        final CondensedReport condensedReport = new CondensedReport( target.getUniqueId() );

        int guiIndex = 19;
        int reasonsIndex = 0;
        for ( int row = 0; row < ( 1 + ( ReportUtils.AVAILABLE_REASONS.size() / 6 ) ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( reasonsIndex >= ReportUtils.AVAILABLE_REASONS.size() ) { break; }
                final String CURRENT_REASON = ReportUtils.AVAILABLE_REASONS.get( reasonsIndex );

                Material material = switch ( condensedReport.getReasonCount( CURRENT_REASON ) ) {
                    case 1 -> Settings.VIEW_REPORT_GUI_ONE_REPORT.material();
                    case 2 -> Settings.VIEW_REPORT_GUI_TWO_REPORTS.material();
                    case 3 -> Settings.VIEW_REPORT_GUI_THREE_REPORTS.material();
                    default -> Settings.VIEW_REPORT_GUI_ZERO_REPORTS.material();
                };
                ItemStack item = CyberItemUtils.createItem( material, "&p" + condensedReport.getReasonCount( CURRENT_REASON ) + "x &s" + CURRENT_REASON );
                if ( condensedReport.getReasonCount( CURRENT_REASON ) > 0 ) item.setAmount( condensedReport.getReasonCount( CURRENT_REASON ) );
                gui.addItem( new GuiItem( item, guiIndex ) );

                reasonsIndex++;
                guiIndex++;
                if ( guiIndex % 9 == 4 ) guiIndex++;
            }

            guiIndex += 2;
        }

        // Delete all reports
        int deleteIndex = ( this.gui.getSize() * 9 ) - 14;
        gui.addItem( new GuiItem( Settings.VIEW_REPORT_GUI_DELETE_REPORTS.material(), "&pDelete Reports", deleteIndex, item -> {
            staff.closeInventory();
            for ( int index = reports.size() - 1; index >= 0; index-- ) {
                Netuno.REPORT_SERVICE.deleteReport( reports.get( index ) );
            }

            CyberMsgUtils.sendMsg( staff, "&sSuccessfully deleted all reports against &p" + target.getName() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );
    }

    public void open() {
        if ( ReportUtils.checkAndLogErrors() ) {
            CommandErrors.sendConfigError( staff );
            return;
        }
        gui.openInventory( staff );
    }
}