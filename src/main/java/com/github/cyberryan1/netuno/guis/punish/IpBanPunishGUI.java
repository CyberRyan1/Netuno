package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.helpers.gui.GUI;
import com.github.cyberryan1.cybercore.helpers.gui.GUIItem;
import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.netuno.guis.punish.utils.MultiPunishButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.utils.SinglePunishButton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IpBanPunishGUI {

    private final GUI gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final MultiPunishButton punishButtons;
    private final int rowCount;

    public IpBanPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;
        this.punishButtons = PunishSettings.IPBAN_BUTTONS.multiButton();

        this.rowCount = 3 + ( this.punishButtons.getButtons().size() / 9 );
        this.gui = new GUI( PunishSettings.IPBAN_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                this.rowCount, CoreGUIUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        // Warn buttons are at 10-12 and 14-16, then repeat every row as needed
        final List<SinglePunishButton> buttons = this.punishButtons.getButtons();

        int buttonIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < this.rowCount; row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( buttonIndex >= buttons.size() ) { break; }

                final SinglePunishButton button = buttons.get( buttonIndex );
                if ( button.getItemMaterial().isAir() == false ) {
                    GUIItem item = new GUIItem( button.getItem( this.target ), guiIndex, () -> {
                        staff.closeInventory();
                        button.executePunish( this.staff, this.target );
                    } );

                    this.gui.setItem( guiIndex, item );
                }

                guiIndex++;
                if ( col == 2 ) { guiIndex++; }
                buttonIndex++;
            }

            guiIndex += 2;
        }

        gui.createInventory();
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
        } );
    }
}