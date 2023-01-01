package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.*;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.events.report.NetunoReportEvent;
import com.github.cyberryan1.netunoapi.models.reports.NReport;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ReportGUI implements Listener {

    private final Gui gui;
    private final Player player;
    private final OfflinePlayer target;
    private final List<String> reasons;
    private final int rowSize;

    private List<String> selections = new ArrayList<>();

    public ReportGUI( Player player, OfflinePlayer target ) {
        this.player = player;
        this.target = target;
        this.reasons = Settings.REPORT_REASONS_LIST.arraylist();
        this.rowSize = 4 + ( reasons.size() / 6 );

        String guiTitle = CyberColorUtils.getColored( "&sReporting &p" + target.getName() );
        gui = new Gui( guiTitle, rowSize, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // reset selections: 11 || submit: 15
        // reports: start at 19-21 and 23-24, every row as needed

        gui.addItem( new GuiItem( Material.RED_WOOL, "&cReset Selections", 11, ( item ) -> {
            player.closeInventory();
            ReportGUI newGui = new ReportGUI( this.player, this.target );
            newGui.open();
            player.playSound( player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } ) );

        gui.addItem( new GuiItem( Material.GREEN_WOOL, "&aSubmit Report", 15, ( item ) -> {
            player.closeInventory();
            player.playSound( player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );

            if ( selections.isEmpty() ) {
                CommandErrors.sendReportNeedsOneReason( player );
                return;
            }

            String reasonsList[] = selections.toArray( new String[selections.size()] );
            String reasons = Utils.formatListIntoAmountString( reasonsList );

            String sentMsg = Utils.getCombinedString( Settings.REPORT_CONFIRM_MESSAGE.coloredStringlist() );
            sentMsg = sentMsg.replace( "[TARGET]", target.getName() ).replace( "[REASON]", reasons );
            if ( sentMsg.replace( "\n", "" ).isBlank() == false ) { CyberMsgUtils.sendMsg( player, sentMsg ); }

            String staffMsg = Utils.getCombinedString( Settings.REPORT_STAFF_MESSAGE.coloredStringlist() );
            staffMsg = staffMsg.replace( "[TARGET]", target.getName() )
                    .replace( "[REASON]", reasons )
                    .replace( "[PLAYER]", player.getName() );
            boolean endsWithNewLine = staffMsg.endsWith( "\n" );
            if ( staffMsg.replace( "\n", "" ).isBlank() == false ) {
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) ) {
                        CyberMsgUtils.sendMsg( p, staffMsg );
                        if ( endsWithNewLine ) CyberMsgUtils.sendMsg( p, "" ); // Unfortunately had to "hard-code" this one
                    }
                }
            }

            List<NReport> reports = new ArrayList<>();
            for ( String reason : reasonsList ) {
                NReport report = new NReport();
                report.setPlayer( this.target );
                report.setReporter( this.player );
                report.setReason( reason );
                report.setTimestamp( TimeUtils.getCurrentTimestamp() );
                ApiNetuno.getData().getNetunoReports().addReport( report );
                reports.add( report );
            }

            ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoReportEvent( reports ) );
        } ) );

        int guiIndex = 19;
        int reasonsIndex = 0;
        for ( int row = 0; row < ( 1 + ( reasons.size() / 6 ) ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( reasonsIndex >= reasons.size() ) { break; }

                final int finalReasonIndex = reasonsIndex;
                gui.addItem( new GuiItem( Material.LIGHT_GRAY_WOOL,
                        "&7" + reasons.get( reasonsIndex ), guiIndex, ( item ) -> {

                    player.playSound( player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );

                    if ( item.getItem().getType() == Material.LIGHT_GRAY_WOOL ) {
                        selections.add( reasons.get( finalReasonIndex ) );
                        item.setItem( CyberItemUtils.createItem( Material.LIME_WOOL, "&a" + reasons.get( finalReasonIndex ) ) );
                        gui.updateItem( item );
                    }

                    else if ( item.getItem().getType() == Material.LIME_WOOL ) {
                        selections.remove( reasons.get( finalReasonIndex ) );
                        item.setItem( CyberItemUtils.createItem( Material.LIGHT_GRAY_WOOL, "&7" + reasons.get( finalReasonIndex ) ) );
                        gui.updateItem( item );
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
        if ( reasons.size() > 18 ) {
            CommandErrors.sendConfigError( player );
            CyberLogUtils.logError( "CONFIG ERROR >> The list \"report.reasons\" is greater than the limit, 18. Reports will not work until this is fixed." );
        }
        else {
            Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
                gui.openInventory( this.player );
            } );
        }
    }
}