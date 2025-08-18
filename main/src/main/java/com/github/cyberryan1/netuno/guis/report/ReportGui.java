package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.models.NetunoReport;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.PrettyStringLibrary;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.settings.SettingsVariableFactory;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The report GUI that is displayed to a player when they
 * execute <code>/report (player)</code>
 *
 * @author Ryan
 */
public class ReportGui {

    private final Gui gui;
    private final Player player;
    private final OfflinePlayer target;
    private final List<String> reasonSelections = new ArrayList<>();

    public ReportGui( Player player, OfflinePlayer target ) {
        this.player = player;
        this.target = target;
        ReportUtils.updateAvailableReasons();

        int rowSize = 3 + ( ReportUtils.AVAILABLE_REASONS.size() / 6 );
        this.gui = new Gui( "&sReporting &p" + target.getName(), rowSize, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // reset selections: 11 || submit selections: 15
        // reports: start at 19-21 and 23-24, every row as needed

        // Reset selections
        gui.addItem( new GuiItem( Settings.REPORT_GUI_RESET_SELECTIONS.material(), "&cReset Selections", 11, ( item ) -> {
            player.closeInventory();
            ReportGui newGui = new ReportGui( this.player, this.target );
            newGui.open();
            player.playSound( player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } ) );

        // Submit selections
        gui.addItem( new GuiItem( Settings.REPORT_GUI_SUBMIT_REPORT.material(), "&aSubmit Report", 15, ( item ) -> {
            player.closeInventory();
            Settings.REPORT_SOUND_PLAYER.sound().playSound( player );

            if ( reasonSelections.isEmpty() ) {
                CommandErrors.sendReportNeedsOneReason( player );
                return;
            }
            
            // getting the reasons msg
            String reasonsMsg = PrettyStringLibrary.getNonOxfordCommaListWithRemainder( reasonSelections, 3 );
            
            // Sending a message to the player
            new SettingsVariableFactory( Settings.REPORT_CONFIRM_MESSAGE )
                    .target( target )
                    .reason( reasonsMsg )
                    .sendMsg( player );

            // Sending a message to online staff
            final Predicate<? super Player> STAFF_PREDICATE = p -> CyberVaultUtils.hasPerms( p, Settings.VIEW_REPORT_PERMISSION.string() );
            new SettingsVariableFactory( Settings.REPORT_STAFF_MESSAGE )
                    .target( target )
                    .player( player )
                    .reason( reasonsMsg )
                    .sendMsg( STAFF_PREDICATE );
            Settings.REPORT_SOUND_STAFF.sound().playSoundMany( STAFF_PREDICATE );

            // Adding the report
            NetunoReport report = new NetunoReport( target.getUniqueId(), reasonSelections, player.getUniqueId(), TimestampUtils.getCurrentTimestamp() );
            Netuno.REPORT_SERVICE.addReport( report );
        } ) );

        // Report reasons
        int guiIndex = 19;
        int reasonsIndex = 0;
        for ( int row = 0; row < ( 1 + ( ReportUtils.AVAILABLE_REASONS.size() / 6 ) ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( reasonsIndex >= ReportUtils.AVAILABLE_REASONS.size() ) { break; }

                final Material unselectedMaterial = Settings.REPORT_GUI_UNSELECTED_REASON.material();
                final Material selectedMaterial = Settings.REPORT_GUI_SELECTED_REASON.material();
                final int finalReasonIndex = reasonsIndex;
                gui.addItem( new GuiItem( unselectedMaterial,
                        "&7" + ReportUtils.AVAILABLE_REASONS.get( reasonsIndex ), guiIndex, ( item ) -> {
                    GuiItem guiItem = gui.getItem( item.getSlot() );
                    player.playSound( player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );

                    if ( guiItem.getItem().getType() == unselectedMaterial ) {
                        reasonSelections.add( ReportUtils.AVAILABLE_REASONS.get( finalReasonIndex ) );
                        guiItem.setItem( CyberItemUtils.createItem( selectedMaterial, "&a" + ReportUtils.AVAILABLE_REASONS.get( finalReasonIndex ) ) );
                        gui.updateItem( guiItem );
                    }

                    else if ( guiItem.getItem().getType() == selectedMaterial ) {
                        reasonSelections.remove( ReportUtils.AVAILABLE_REASONS.get( finalReasonIndex ) );
                        guiItem.setItem( CyberItemUtils.createItem( unselectedMaterial, "&7" + ReportUtils.AVAILABLE_REASONS.get( finalReasonIndex ) ) );
                        gui.updateItem( guiItem );
                    }
                } ) );

                guiIndex++;
                if ( col == 2 ) { guiIndex++; }
                reasonsIndex++;
            }

            guiIndex += 2;
        }
    }

    public void open() {
        if ( ReportUtils.checkAndLogErrors() ) {
            CommandErrors.sendConfigError( player );
            return;
        }
        gui.openInventory( player );
    }
}