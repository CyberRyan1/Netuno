package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class NewHistoryDeleteConfirmGUI {

    private final GUI gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final Punishment punishment;

    public NewHistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, Punishment punishment ) {
        this.target = target;
        this.staff = staff;
        this.punishment = punishment;

        this.gui = new GUI( "&sConfirm Deletion", 4, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public void open() {
        gui.openInventory( staff );
    }

    private void insertItems() {
        // punishment info: 13
        // green wool confirm: 30
        // red wool cancel: 32

        gui.setItem( 13, new GUIItem( punishment.getPunishmentAsItem(), 13 ) );

        // Green Wool Confirm
        gui.setItem( 30, new GUIItem( Material.LIME_WOOL, "&aConfirm", 30, () -> {
            staff.closeInventory();
            Utils.getDatabase().deletePunishment( punishment.getID() );
            CoreUtils.sendMsg( staff, "&sSuccessfully deleted punishment &p#" + punishment.getID() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );

        // Red Wool Cancel
        gui.setItem( 32, new GUIItem( Material.RED_WOOL, "&cCancel", 32, () -> {
            staff.closeInventory();
            CoreUtils.sendMsg( staff, "&sCancelled deletion of punishment &p#" + punishment.getID() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );

        gui.createInventory();
    }
}