package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.netuno.guis.punish.managers.ActiveGuiManager;
import com.github.cyberryan1.netuno.guis.punish.utils.MultiPunishButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.utils.SinglePunishButton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IpMutePunishGUI {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final MultiPunishButton punishButtons;
    private final int rowCount;

    public IpMutePunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;
        this.punishButtons = PunishSettings.IPMUTE_BUTTONS.multiButton();

        this.rowCount = determineRowCount();
        this.gui = new Gui( PunishSettings.IPMUTE_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                this.rowCount, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // Warn buttons are at 10-12 and 14-16, then repeat every row as needed
        final List<SinglePunishButton> buttonsList = this.punishButtons.getButtons();

        for ( SinglePunishButton button : buttonsList ) {
            if ( button.getItemMaterial().isAir() ) { continue; }

            GuiItem item = new GuiItem( button.getItem( this.target ), button.getIndex(), ( i ) -> {
                button.executePunish( this.staff, this.target );
                staff.closeInventory();
            } );

            this.gui.addItem( item );
        }
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseEvent( ( inventory ) -> {
                ActiveGuiManager.attemptRemoveActiveGui( this.staff );
            } );
            ActiveGuiManager.addActiveGui( this.staff, this.target );
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