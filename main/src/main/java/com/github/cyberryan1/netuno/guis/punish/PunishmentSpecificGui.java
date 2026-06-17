package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.guis.punish.models.MultiPunishButton;
import com.github.cyberryan1.netuno.guis.punish.models.PunGuiType;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.models.SinglePunishButton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A class that represents each of the different punishments'
 * respective GUIs within the /punish GUI
 *
 * @author Ryan
 */
public class PunishmentSpecificGui {

    private final Gui gui;
    private final PunGuiType type;
    private final Player staff;
    private final OfflinePlayer target;
    private final boolean silent;
    private final MultiPunishButton punishButtons;
    private final int rowCount;

    /**
     * Creating a punishment's GUI
     *
     * @param type   The type of punishment this GUI is for
     * @param staff  The staff executing the command
     * @param target The target
     */
    public PunishmentSpecificGui( PunGuiType type, Player staff, OfflinePlayer target, boolean silent ) {
        this.type = type;
        this.staff = staff;
        this.target = target;
        this.silent = silent;
        this.punishButtons = switch ( type ) {
            case MAIN -> throw new IllegalArgumentException();
            case WARN -> PunishSettings.WARN_BUTTONS.multiButton();
            case MUTE -> PunishSettings.MUTE_BUTTONS.multiButton();
            case BAN -> PunishSettings.BAN_BUTTONS.multiButton();
            case IPMUTE -> PunishSettings.IPMUTE_BUTTONS.multiButton();
            case IPBAN -> PunishSettings.IPBAN_BUTTONS.multiButton();
        };

        this.rowCount = determineRowCount();

        this.gui = new Gui( getGuiName(), this.rowCount, CyberGuiUtils.getBackgroundGlass() );

        insertItems();
    }

    /**
     * Inserting items that were defined in the config into this
     * GUI.
     * <b>Note:</b> this is not done instantly, as we have
     * to obtain how many punishments the player has of each
     * specific punishment.
     */
    public void insertItems() {
//        // Creating the loading placeholder item
//        final ItemStack LOADING_PLACEHOLDER_ITEM = getLoadingPlaceholderItem();

        final List<SinglePunishButton> buttonsList = this.punishButtons.getButtons();
        // Collecting all CompletableFutures from the button inserts
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for ( SinglePunishButton button : buttonsList ) {
            if ( button.getItemMaterial().isAir() ) {
                continue;
            }

            button.loadIntoInventory( this.gui, button.getIndex(), this, true );
//            // temporarily setting the item slot to the loading item
//            GuiItem loadingGuiItem = new GuiItem( LOADING_PLACEHOLDER_ITEM, button.getIndex() );
//            gui.addItem( loadingGuiItem );
//
//            // loading the button
//            futures.add(
//                    button.getItem( this.target ).thenAccept( itemstack -> {
//                        GuiItem item = new GuiItem( itemstack, button.getIndex(), i -> {
//                            if ( i.getEvent().getAction() == InventoryAction.PICKUP_HALF ) { // left click
//                                ChangeDurationGui changeDurationGui = new ChangeDurationGui( this, button );
//                                changeDurationGui.open();
//                            }
//                            else {
//                                PunishmentGuiExecutor.executePunish( button, this.staff, this.target, this.silent );
//                                staff.closeInventory();
//                            }
//                        } );
//                        gui.addItem( item );
//                    } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING )
//            );
        }

//        // Wait for all futures to complete and then update the GUI
//        CompletableFuture.allOf( futures.toArray( new CompletableFuture[0] ) )
//                .thenRun( () -> {
//                    for ( int i = 0; i < this.gui.getSize() * 9; i++ ) {
//                        this.gui.updateItem( this.gui.getItem( i ) );
//                    }
//                } );
    }

    /**
     * Opens this GUI to the staff
     */
    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
            gui.setCloseEvent( ( inventory ) -> {
                Netuno.ACTIVE_PUNISH_GUIS.removeOpenGuiByStaff( this.staff );
            } );
            Netuno.ACTIVE_PUNISH_GUIS.addOpenGui( this );
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

    public Gui getGui() { return gui; }

    public PunGuiType getType() { return type; }

    public Player getStaff() { return staff; }

    public OfflinePlayer getTarget() { return target; }

    public boolean isSilent() { return silent; }

    public String getGuiName() {
        String guiName = switch ( type ) {
            case MAIN -> throw new IllegalArgumentException();
            case WARN -> PunishSettings.WARN_INVENTORY_NAME.coloredString();
            case MUTE -> PunishSettings.MUTE_INVENTORY_NAME.coloredString();
            case BAN -> PunishSettings.BAN_INVENTORY_NAME.coloredString();
            case IPMUTE -> PunishSettings.IPMUTE_INVENTORY_NAME.coloredString();
            case IPBAN -> PunishSettings.IPBAN_INVENTORY_NAME.coloredString();
        };
        return guiName.replace( "[TARGET]", target.getName() );
    }
}