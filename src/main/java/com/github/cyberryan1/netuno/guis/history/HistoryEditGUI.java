package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.events.NetunoEventDispatcher;
import com.github.cyberryan1.netunoapi.events.history.HistoryEditAction;
import com.github.cyberryan1.netunoapi.events.history.NetunoHistoryEditEvent;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HistoryEditGUI {

    private final Gui gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final NPunishment punishment;

    private boolean editingLength = false;
    private boolean editingReason = false;

    public HistoryEditGUI( OfflinePlayer target, Player staff, int punId ) {
        this.target = target;
        this.staff = staff;
        this.punishment = ApiNetuno.getData().getNetunoPuns().getPunishment( punId );

        this.gui = new Gui( "&sEdit Punishment &p#" + punId, 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public HistoryEditGUI( OfflinePlayer target, Player staff, NPunishment punishment ) {
        this.target = target;
        this.staff = staff;
        this.punishment = punishment;

        this.gui = new Gui( "&sEdit Punishment &p#" + punishment.getId(), 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // info: 13
        // back to history list: 49
        // unpunish layout:
        //      delete punishment barrier: 31
        // no-length layout:
        //      edit reason paper: 30 || delete punishment barrier: 32
        // default:
        //      edit length clock: 29 || edit reason paper: 31
        //      delete punishment barrier: 33

        // Punishment Info
        gui.addItem( new GuiItem( GUIUtils.getPunishmentItem( punishment ), 13 ) );

        if ( punishment.getPunishmentType().hasNoReason() ) {
            // Delete Punishment
            gui.addItem( getDeleteBarrier( 31 ) );
        }

        else if ( punishment.getPunishmentType().hasNoLength() || punishment.isActive() == false ) {
            // Edit Reason
            gui.addItem( getEditReasonPaper( 30 ) );
            // Delete Punishment
            gui.addItem( getDeleteBarrier( 32 ) );
        }

        else {
            // Edit Length
            gui.addItem( getEditLengthClock( 29 ) );
            // Edit Reason
            gui.addItem( getEditReasonPaper( 31 ) );
            // Delete Punishment
            gui.addItem( getDeleteBarrier( 33 ) );
        }

        gui.openInventory( staff );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
    }

    public void onReasonEditInput( String newReason ) {
        HistoryEditManager.removeEditing( staff );
        editingReason = false;
        if ( newReason.equalsIgnoreCase( "cancel" ) == false ) {
            NPunishment oldPun = punishment.copy();
            punishment.setReason( newReason );
            ApiNetuno.getData().getNetunoPuns().updatePunishment( punishment );

            NetunoEventDispatcher.dispatch( new NetunoHistoryEditEvent( oldPun, punishment, staff, HistoryEditAction.EDIT_REASON ) );
        }

        HistoryEditGUI newGui = new HistoryEditGUI( target, staff, punishment );
        newGui.open();
    }

    public void onLengthEditInput( String newLength ) {
        HistoryEditManager.removeEditing( staff );
        editingLength = false;
        if ( newLength.equalsIgnoreCase( "cancel" ) == false ) {
            if ( TimeUtils.isAllowableLength( newLength ) ) {
                NPunishment oldPun = punishment.copy();
                punishment.setLength( TimeUtils.durationFromUnformatted( newLength ).timestamp() );
                ApiNetuno.getData().getNetunoPuns().updatePunishment( punishment );

                NetunoEventDispatcher.dispatch( new NetunoHistoryEditEvent( oldPun, punishment, staff, HistoryEditAction.EDIT_LENGTH ) );
            }
            else {
                CommandErrors.sendInvalidTimespan( staff, newLength );
                HistoryEditManager.addEditing( staff, this );
                editingLength = true;
                CyberMsgUtils.sendMsg( staff, "&sTry again, or type &p\"cancel\"&s to cancel" );
                return;
            }
        }

        HistoryEditGUI newGui = new HistoryEditGUI( target, staff, punishment );
        newGui.open();
    }

    private GuiItem getDeleteBarrier( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.BARRIER, "&sDelete Punishment" ), slot, ( item ) -> {
            staff.closeInventory();

            if ( CyberVaultUtils.hasPerms( staff, Settings.HISTORY_DELETE_PERMISSION.string() ) ) {
                HistoryDeleteConfirmGUI deleteGui = new HistoryDeleteConfirmGUI( target, staff, punishment );
                deleteGui.open();
                staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
            }

            else {
                CyberMsgUtils.sendMsg( staff, Settings.PERM_DENIED_MSG.string() );
            }
        } );
    }

    private GuiItem getEditReasonPaper( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.PAPER, "&sEdit Reason" ), slot, ( item ) -> {
            HistoryEditManager.addEditing( staff, this );
            staff.closeInventory();
            editingReason = true;
            CyberMsgUtils.sendMsg( staff, "&sPlease enter the new reason for punishment &p#" + punishment.getId() );
            CyberMsgUtils.sendMsg( staff, "&sTo cancel, type &p\"cancel\"" );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } );
    }

    private GuiItem getEditLengthClock( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.CLOCK, "&sEdit Length" ), slot, ( item ) -> {
            HistoryEditManager.addEditing( staff, this );
            staff.closeInventory();
            editingLength = true;
            CyberMsgUtils.sendMsg( staff, "&sPlease enter the new length for punishment &p#" + punishment.getId() );
            CyberMsgUtils.sendMsg( staff, "&sTo cancel, type &p\"cancel\"" );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } );
    }

    //
    // Getters & Setters
    //

    public OfflinePlayer getTarget() { return target; }

    public Player getStaff() { return staff; }

    public boolean isEditingLength() { return editingLength; }

    public boolean isEditingReason() { return editingReason; }

    public void setEditingLength( boolean editingLength ) { this.editingLength = editingLength; }

    public void setEditingReason( boolean editingReason ) { this.editingReason = editingReason; }
}