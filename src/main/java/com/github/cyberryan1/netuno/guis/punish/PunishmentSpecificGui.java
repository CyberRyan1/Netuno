package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.netuno.guis.punish.managers.ActiveGuiManager;
import com.github.cyberryan1.netuno.guis.punish.models.GuiType;
import com.github.cyberryan1.netuno.guis.punish.models.MultiPunishButton;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.models.SinglePunishButton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

// TODO javadoc
public class PunishmentSpecificGui {

    private final Gui gui;
    private final GuiType type;
    private final Player staff;
    private final OfflinePlayer target;
    private final MultiPunishButton punishButtons;
    private final int rowCount;

    public PunishmentSpecificGui( GuiType type, Player staff, OfflinePlayer target ) {
        this.type = type;
        this.staff = staff;
        this.target = target;
        this.punishButtons = switch ( type ) {
            case WARN -> PunishSettings.WARN_BUTTONS.multiButton();
            case MUTE -> PunishSettings.MUTE_BUTTONS.multiButton();
            case BAN -> PunishSettings.BAN_BUTTONS.multiButton();
            case IPMUTE -> PunishSettings.IPMUTE_BUTTONS.multiButton();
            case IPBAN -> PunishSettings.IPBAN_BUTTONS.multiButton();
        };

        this.rowCount = determineRowCount();

        String guiName = switch ( type ) {
            case WARN -> PunishSettings.WARN_INVENTORY_NAME.coloredString();
            case MUTE -> PunishSettings.MUTE_INVENTORY_NAME.coloredString();
            case BAN -> PunishSettings.BAN_INVENTORY_NAME.coloredString();
            case IPMUTE -> PunishSettings.IPMUTE_INVENTORY_NAME.coloredString();
            case IPBAN -> PunishSettings.IPBAN_INVENTORY_NAME.coloredString();
        };
        this.gui = new Gui( guiName.replace( "[TARGET]", target.getName() ), this.rowCount, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    public void insertItems() {
        final List<SinglePunishButton> buttonsList = this.punishButtons.getButtons();
        for ( SinglePunishButton button : buttonsList ) {
            if ( button.getItemMaterial().isAir() ) { continue; }

            button.getItem( this.target ).thenAccept( itemstack -> {
                GuiItem item = new GuiItem( itemstack, button.getIndex(), ( i ) -> {
                    button.executePunish( this.staff, this.target );
                    staff.closeInventory();
                } );
                this.gui.addItem( item );
            } );
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