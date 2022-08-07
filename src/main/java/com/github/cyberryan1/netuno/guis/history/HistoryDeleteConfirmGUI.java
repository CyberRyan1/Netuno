package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netunoapi.events.NetunoEventDispatcher;
import com.github.cyberryan1.netunoapi.events.history.NetunoHistoryDeleteEvent;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HistoryDeleteConfirmGUI {

    private final GUI gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final NPunishment punishment;

    public HistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, NPunishment punishment ) {
        this.target = target;
        this.staff = staff;
        this.punishment = punishment;

        this.gui = new GUI( "&sConfirm Deletion", 5, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( staff );
        } );
    }

    private void insertItems() {
        // punishment info: 13
        // green wool confirm: 30
        // red wool cancel: 32

        gui.setItem( 13, new GUIItem( GUIUtils.getPunishmentItem( punishment ), 13 ) );

        // Green Wool Confirm
        gui.setItem( 30, new GUIItem( Material.LIME_WOOL, "&aConfirm", 30, () -> {
            staff.closeInventory();
            ApiNetuno.getData().getNetunoPuns().removePunishment( punishment.getId() );
            CoreUtils.sendMsg( staff, "&sSuccessfully deleted punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );

            NetunoEventDispatcher.dispatch( new NetunoHistoryDeleteEvent( punishment, staff ) );
        } ) );

        // Red Wool Cancel
        gui.setItem( 32, new GUIItem( Material.RED_WOOL, "&cCancel", 32, () -> {
            staff.closeInventory();
            CoreUtils.sendMsg( staff, "&sCancelled deletion of punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );

        gui.createInventory();
    }
}