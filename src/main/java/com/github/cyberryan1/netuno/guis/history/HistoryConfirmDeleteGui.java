package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * When a staff member wants to delete a punishment, this GUI
 * opens up and confirms their decision
 *
 * @author Ryan
 */
public class HistoryConfirmDeleteGui {

    private final Gui gui;
    private final Player staff;
    private final ApiPunishment punishment;

    /**
     * @param staff The staff member
     * @param punishment The punishment to delete
     */
    public HistoryConfirmDeleteGui( Player staff, ApiPunishment punishment ) {
        this.staff = staff;
        this.punishment = punishment;

        this.gui = new Gui( "&sConfirm Deletion", 5, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    /**
     * Opens the GUI to the staff member
     */
    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
    }

    /**
     * Inserts all items into the GUI
     */
    private void insertItems() {
        // punishment info: 13
        // green wool confirm: 30
        // red wool cancel: 32

        gui.addItem( new GuiItem( HistoryUtils.getPunishmentItem( punishment ), 13 ) );

        // Green Wool Confirm
        gui.addItem( new GuiItem( Material.LIME_WOOL, "&aConfirm", 30, ( item ) -> {
            staff.closeInventory();
            Netuno.PUNISHMENT_SERVICE.deletePunishment( punishment.getId() );
            CyberMsgUtils.sendMsg( staff, "&sSuccessfully deleted punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );

            // TODO dispatch history delete event
            //ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoHistoryDeleteEvent( punishment, staff ) );
        } ) );

        // Red Wool Cancel
        gui.addItem( new GuiItem( Material.RED_WOOL, "&cCancel", 32, ( item ) -> {
            staff.closeInventory();
            CyberMsgUtils.sendMsg( staff, "&sCancelled deletion of punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );
    }
}