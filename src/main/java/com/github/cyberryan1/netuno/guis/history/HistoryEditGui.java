package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows a staff member to edit a certain punishment's
 * details, such as its reason, duration, etc.
 *
 * @author Ryan
 */
public class HistoryEditGui {

    private final Gui gui;
    private final Player staff;
    private final int punishmentId;

    private ApiPunishment punishment;
    private boolean editingLength = false;
    private boolean editingReason = false;

    /**
     * @param staff The staff member
     * @param punId The punishment ID to edit
     */
    public HistoryEditGui( Player staff, int punId ) {
        this.staff = staff;
        this.punishmentId = punId;

        this.gui = new Gui( "&sEdit Punishment &p#" + punId, 6, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    /**
     * Inserts all items into the GUI
     */
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

        // Getting the punishment
        Netuno.PUNISHMENT_SERVICE.getPunishment( this.punishmentId ).thenAccept( pun -> {
            if ( pun.isEmpty() ) {
                CyberMsgUtils.sendMsg( staff, "&sPunishment &p#" + punishmentId + " &sdoes not exist" );
                staff.closeInventory();
                return;
            }

            this.punishment = pun.get();

            // Punishment Info
            ItemStack punishmentItem = HistoryUtils.getPunishmentItem( punishment );
            gui.addOrUpdateItem( new GuiItem( punishmentItem, 13 ) );

            if ( punishment.getType().hasNoReason() ) {
                // Delete Punishment
                gui.addOrUpdateItem( getDeleteBarrier( 31 ) );
            }

            else if ( punishment.getType().hasNoLength() || punishment.isActive() == false ) {
                // Edit Reason
                gui.addOrUpdateItem( getEditReasonPaper( 30 ) );
                // Delete Punishment
                gui.addOrUpdateItem( getDeleteBarrier( 32 ) );
            }

            else {
                // Edit Length
                gui.addOrUpdateItem( getEditLengthClock( 29 ) );
                // Edit Reason
                gui.addOrUpdateItem( getEditReasonPaper( 31 ) );
                // Delete Punishment
                gui.addOrUpdateItem( getDeleteBarrier( 33 ) );
            }
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }

    /**
     * Opens the GUI to the staff member
     */
    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
    }

    public void onReasonEditInput( String newReason ) {
        HistoryEditManager.removeEditing( staff );
        editingReason = false;
        if ( newReason.equalsIgnoreCase( "cancel" ) == false ) {
            ApiPunishment oldPun = punishment.copy();
            punishment.setReason( newReason );
            Netuno.PUNISHMENT_SERVICE.updatePunishment( punishment );

            // TODO dispatch history edit event
            //ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoHistoryEditEvent( oldPun, punishment, staff, HistoryEditAction.EDIT_REASON ) );
        }

        HistoryEditGui newGui = new HistoryEditGui( staff, punishmentId );
        newGui.open();
    }

    public void onLengthEditInput( String newLength ) {
        HistoryEditManager.removeEditing( staff );
        editingLength = false;
        if ( newLength.equalsIgnoreCase( "cancel" ) == false ) {
            if ( TimestampUtils.isAllowableLength( newLength ) ) {
                ApiPunishment oldPun = punishment.copy();
                punishment.setLength( TimestampUtils.getTimestampFromUnformulatedLength( newLength ) );
                Netuno.PUNISHMENT_SERVICE.updatePunishment( punishment );

                // TODO dispatch history edit event
                //ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoHistoryEditEvent( oldPun, punishment, staff, HistoryEditAction.EDIT_LENGTH ) );
            }
            else {
                CommandErrors.sendInvalidTimespan( staff, newLength );
                HistoryEditManager.addEditing( staff, this );
                editingLength = true;
                CyberMsgUtils.sendMsg( staff, "&sTry again, or type &p\"cancel\"&s to cancel" );
                return;
            }
        }

        HistoryEditGui newGui = new HistoryEditGui( staff, punishmentId );
        newGui.open();
    }

    private GuiItem getDeleteBarrier( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.BARRIER, "&sDelete Punishment" ), slot, ( item ) -> {
            staff.closeInventory();

            if ( CyberVaultUtils.hasPerms( staff, Settings.HISTORY_DELETE_PERMISSION.string() ) ) {
                HistoryConfirmDeleteGui confirmGui = new HistoryConfirmDeleteGui( staff, punishment );
                confirmGui.open();
                staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
            }

            else {
                CyberMsgUtils.sendMsg( staff, Settings.PERM_DENIED_MSG.string() );
            }
        } );
    }

    private GuiItem getEditReasonPaper( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.PAPER, "&sEdit Reason" ), slot, ( item ) -> {
            if ( CyberVaultUtils.hasPerms( staff, Settings.HISTORY_REASON_PERMISSION.string() ) == false ) {
                CyberMsgUtils.sendMsg( staff, Settings.PERM_DENIED_MSG.string() );
                return;
            }

            HistoryEditManager.addEditing( staff, this );
            staff.closeInventory();
            editingReason = true;
            CyberMsgUtils.sendMsg( staff, "&sPlease enter the new reason for punishment &p#" + punishmentId );
            CyberMsgUtils.sendMsg( staff, "&sTo cancel, type &p\"cancel\"" );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } );
    }

    private GuiItem getEditLengthClock( int slot ) {
        return new GuiItem( CyberItemUtils.createItem( Material.CLOCK, "&sEdit Length" ), slot, ( item ) -> {
            if ( CyberVaultUtils.hasPerms( staff, Settings.HISTORY_TIME_PERMISSION.string() ) == false ) {
                CyberMsgUtils.sendMsg( staff, Settings.PERM_DENIED_MSG.string() );
                return;
            }

            HistoryEditManager.addEditing( staff, this );
            staff.closeInventory();
            editingLength = true;
            CyberMsgUtils.sendMsg( staff, "&sPlease enter the new length for punishment &p#" + punishmentId );
            CyberMsgUtils.sendMsg( staff, "&sTo cancel, type &p\"cancel\"" );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 10, 2 );
        } );
    }

    //
    // Getters & Setters
    //

    public OfflinePlayer getTarget() { return this.punishment.getPlayer(); }

    public Player getStaff() { return staff; }

    public boolean isEditingLength() { return editingLength; }

    public boolean isEditingReason() { return editingReason; }

    public void setEditingLength( boolean editingLength ) { this.editingLength = editingLength; }

    public void setEditingReason( boolean editingReason ) { this.editingReason = editingReason; }
}