package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netunoapi.events.history.NetunoHistoryDeleteEvent;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HistoryDeleteConfirmGUI {

    private final Gui gui;
    private final OfflinePlayer target;
    private final Player staff;
    private final NPunishment punishment;

    public HistoryDeleteConfirmGUI( OfflinePlayer target, Player staff, NPunishment punishment ) {
        this.target = target;
        this.staff = staff;
        this.punishment = punishment;

        this.gui = new Gui( "&sConfirm Deletion", 5, CyberGuiUtils.getBackgroundGlass() );
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

        gui.addItem( new GuiItem( GUIUtils.getPunishmentItem( punishment ), 13 ) );

        // Green Wool Confirm
        gui.addItem( new GuiItem( Material.LIME_WOOL, "&aConfirm", 30, ( item ) -> {
            staff.closeInventory();
            ApiNetuno.getData().getNetunoPuns().removePunishment( punishment.getId() );
            CyberMsgUtils.sendMsg( staff, "&sSuccessfully deleted punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );

            ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoHistoryDeleteEvent( punishment, staff ) );
        } ) );

        // Red Wool Cancel
        gui.addItem( new GuiItem( Material.RED_WOOL, "&cCancel", 32, ( item ) -> {
            staff.closeInventory();
            CyberMsgUtils.sendMsg( staff, "&sCancelled deletion of punishment &p#" + punishment.getId() );
            staff.playSound( staff.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 2 );
        } ) );
    }
}