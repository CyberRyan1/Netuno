package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.netuno.guis.punish.utils.MultiPunishButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.utils.SinglePunishButton;
import com.github.cyberryan1.netuno.managers.StaffPlayerPunishManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IpMutePunishGUI {

    private final GUI gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final MultiPunishButton punishButtons;
    private final int rowCount;

    public IpMutePunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;
        this.punishButtons = PunishSettings.IPMUTE_BUTTONS.multiButton();

        this.rowCount = determineRowCount();
        this.gui = new GUI( PunishSettings.IPMUTE_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                this.rowCount, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // Warn buttons are at 10-12 and 14-16, then repeat every row as needed
        final List<SinglePunishButton> buttonsList = this.punishButtons.getButtons();

        for ( SinglePunishButton button : buttonsList ) {
            if ( button.getItemMaterial().isAir() ) { continue; }

            GUIItem item = new GUIItem( button.getItem( this.target ), button.getIndex(), () -> {
                button.executePunish( this.staff, this.target );
                staff.closeInventory();
            } );

            this.gui.addItem( item );
        }

        gui.createInventory();
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseAction( () -> {
                StaffPlayerPunishManager.removeStaff( this.staff );
                StaffPlayerPunishManager.removeStaffSilent( this.staff );
            } );
        } );
    }

    private int determineRowCount() {
        int highestIndex = 0;
        for ( SinglePunishButton button : this.punishButtons.getButtons() ) {
            if ( button.getItemMaterial().isAir() == false && button.getIndex() > highestIndex ) {
                highestIndex = button.getIndex();
            }
        }

        if ( highestIndex <= 16 ) { return 3; }
        if ( highestIndex <= 25 ) { return 4; }
        return 5;
    }
}