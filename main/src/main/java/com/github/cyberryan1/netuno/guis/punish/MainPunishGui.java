package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberGuiUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.guis.punish.models.MainButton;
import com.github.cyberryan1.netuno.guis.punish.models.PunGuiType;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The main punishment GUI that is displayed to staff when they
 * execute the <code>/punish (player)</code> command
 *
 * @author Ryan
 */
public class MainPunishGui {

    private final Gui gui;
    private final Player staff;
    private final OfflinePlayer target;

    /**
     * @param staff The staff member
     * @param target The target player
     */
    public MainPunishGui( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        this.gui = new Gui( PunishSettings.MAIN_INVENTORY_NAME.coloredString().replace( "[TARGET]", target.getName() ),
                5, CyberGuiUtils.getBackgroundGlass() );
        insertItems();
    }

    /**
     * Inserts all the items into this GUI
     */
    public void insertItems() {
        // Player skull (not a button, more for decor)
        MainButton skull = PunishSettings.MAIN_SKULL_BUTTON.mainButton();
        if ( skull.getIndex() != -1 ) {
            gui.addItem( new GuiItem( skull.getItem( this.target ), skull.getIndex() ) );
        }

        // History button
        MainButton history = PunishSettings.MAIN_HISTORY_BUTTON.mainButton();
        if ( history.getIndex() != -1 ) {
            gui.addItem( new GuiItem( history.getItem( this.target ), history.getIndex(), ( itemClicked ) -> {
                staff.closeInventory();

                if ( CyberVaultUtils.hasPerms( this.staff, Settings.HISTORY_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    return;
                }

                // TODO open the history list GUI
                //HistoryListGUI historyList = new HistoryListGUI( this.target, this.staff, 1 );
                //historyList.open();
            } ) );
        }

        // Alts button
        MainButton alts = PunishSettings.MAIN_ALTS_BUTTON.mainButton();
        if ( alts.getIndex() != -1 ) {
            gui.addItem( new GuiItem( alts.getItem( this.target ), alts.getIndex(), ( itemClicked ) -> {
                staff.closeInventory();

                if ( CyberVaultUtils.hasPerms( this.staff, Settings.IPINFO_PERMISSION.string() ) == false ) {
                    CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                    return;
                }

                // TODO open the alts list GUI
//                AltsListGUI altsList = new AltsListGUI( this.staff, this.target, 1 );
//                altsList.open();
            } ) );
        }

        // Silent button (toggle)
        final MainButton silent = ( CyberVaultUtils.hasPerms( this.staff, Settings.SILENT_PERMISSION.string() ) ) ?
                PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton() :
                PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton();
        if ( silent.getIndex() != -1 ) {
            gui.addItem( new GuiItem( silent.getItem( this.target ), silent.getIndex(),
                    ( guiItem ) -> {
                        GuiItem clickedItem = gui.getItem( guiItem.getSlot() );

                        if ( clickedItem.getType() == PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton().getItem( this.target ).getType() ) {
                            return;
                        }

                        if ( clickedItem.getType() == PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ).getType() ) {
                            clickedItem.setItem( PunishSettings.MAIN_SILENT_ENABLED_BUTTON.mainButton().getItem( this.target ) );
                        }
                        else {
                            clickedItem.setItem( PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton().getItem( this.target ) );
                        }

                        gui.updateItem( clickedItem );
                    } ) );
        }

        // Generating the different punishment GUI buttons
        for ( PunGuiType type : PunGuiType.values() ) {
            if ( type == PunGuiType.MAIN ) continue; // skip main GUI
            generatePunishmentSpecificButton( type );
        }
    }

    /**
     * Generates the button for the provided type of punishment
     * GUI and adds it to the current GUI
     *
     * @param type The punishment GUI type
     */
    private void generatePunishmentSpecificButton( PunGuiType type ) {
        if ( type == PunGuiType.MAIN ) throw new IllegalArgumentException( "Cannot generate main punishment specific button" );

        final MainButton button = getPunishmentButton( type );
        // If index of button is -1, don't add it to the GUI
        if ( button.getIndex() == -1 ) return;

        final ItemStack item = getPunishmentGuiButtonItem( type );
        this.gui.addItem( new GuiItem( item, button.getIndex(), ( itemClicked ) -> {
            // Checking if the staff has permission
            if ( staffCanAccessPunishmentGui( type ) == false ) {
                CyberMsgUtils.sendMsg( this.staff, Settings.PERM_DENIED_MSG.string() );
                staff.closeInventory();
                return;
            }

            // Checking if there are any other staff punishing this
            //      target for this punishment type
            final PunishmentSpecificGui otherOpenGui = Netuno.ACTIVE_PUNISH_GUIS
                    .searchByTargetAndType( target, type ).orElse( null );
            if ( otherOpenGui != null ) {
                CyberMsgUtils.sendMsg( this.staff, "&p" + this.target.getName() +
                        " &sis already being IP banned by &p" + otherOpenGui.getStaff().getName() );
                return;
            }

            // Checking if the staff wants this to be a silent
            //      punishment or not
            boolean silent;
            final MainButton silentButton = ( CyberVaultUtils.hasPerms( this.staff, Settings.SILENT_PERMISSION.string() ) ) ?
                    PunishSettings.MAIN_SILENT_DISABLED_BUTTON.mainButton() :
                    PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton();
            if ( silentButton.getIndex() == -1 ) { silent = false; }
            else if ( silentButton == PunishSettings.MAIN_SILENT_NO_PERMS_BUTTON.mainButton() ) { silent = false; }
            else {
                GuiItem silentItem = gui.getItem( silentButton.getIndex() );
                silent = silentItem.getType() == PunishSettings.MAIN_SILENT_ENABLED_BUTTON.mainButton().getItem( this.target ).getType();
            }

            // Closing the staff's current inventory
            staff.closeInventory();
            // Opening the next GUI for the staff
            PunishmentSpecificGui nextGui = new PunishmentSpecificGui( type, staff, target, silent );
            nextGui.open();
        } ) );
    }

    public void open() {
        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            gui.openInventory( this.staff );
        } );
    }

    /**
     * Gets the item that will be used for the provided type of a
     * punishment GUI within this (main) GUI.
     *
     * @param type The type of punishment GUI
     * @return The item to use for the button
     */
    private ItemStack getPunishmentGuiButtonItem( PunGuiType type ) {
        MainButton button = getPunishmentButton( type );
        ItemStack item = button.getItem( this.target ).clone();

        final PunishmentSpecificGui otherOpenGui = Netuno.ACTIVE_PUNISH_GUIS
                .searchByTargetAndType( target, type ).orElse( null );

        // If there is another staff with this type of punishment
        //      GUI open for this specific target, add info to
        //      this item to let this staff member know
        if ( otherOpenGui != null ) {
            // Setting the item's new name
            final String name = PunishSettings.MAIN_IN_USE_NAME.coloredString()
                    .replace( "[STAFF]", otherOpenGui.getStaff().getName() )
                    .replace( "[TYPE]", getPastTense( type ) );
            if ( name.isEmpty() == false ) { CyberItemUtils.setItemName( item, name ); }

            // Setting the item's new lore
            final String lore = PunishSettings.MAIN_IN_USE_LORE.coloredString()
                    .replace( "[STAFF]", otherOpenGui.getStaff().getName() )
                    .replace( "[TYPE]", getPastTense( type ) );
            if ( lore.isEmpty() == false ) { CyberItemUtils.setItemLore( item, lore ); }

            // Setting the item's new material
            final Material material = PunishSettings.MAIN_IN_USE_ITEM.material();
            if ( material != Material.AIR ) { item.setType( material ); }

            // Setting the item's new glow
            if ( PunishSettings.MAIN_IN_USE_GLOW.bool() ) {
                item.addUnsafeEnchantment( Enchantment.PROTECTION, 1 );
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
                item.setItemMeta( meta );
            }
        }

        // If this staff cannot access this punishment GUI, change
        //      some characteristics of the item
        if ( staffCanAccessPunishmentGui( type ) == false ) {
            // Adding to the item's lore
            String lore = PunishSettings.MAIN_INVALID_PERMS_MSG.coloredString();
            if ( lore.isEmpty() == false ) CyberItemUtils.addItemLore( item, lore );

            // If wanted, we can change the material of the item
            //      as well
            if ( PunishSettings.MAIN_INVALID_PERMS_CHANGE_MATERIAL_ACTIVE.bool() ) {
                item.setType( PunishSettings.MAIN_INVALID_PERMS_CHANGE_MATERIAL.material() );
            }
        }

        return item;
    }

    /**
     * @param type The type of punishment GUI
     * @return The button affiliated with the provided type
     */
    private MainButton getPunishmentButton( PunGuiType type ) {
        return switch ( type ) {
            case MAIN -> throw new IllegalArgumentException( "Punishment buttons for the main GUI do not exist" );
            case WARN -> PunishSettings.MAIN_WARN_BUTTON.mainButton();
            case MUTE -> PunishSettings.MAIN_MUTE_BUTTON.mainButton();
            case BAN -> PunishSettings.MAIN_BAN_BUTTON.mainButton();
            case IPMUTE -> PunishSettings.MAIN_IPMUTE_BUTTON.mainButton();
            case IPBAN -> PunishSettings.MAIN_IPBAN_BUTTON.mainButton();
        };
    }

    /**
     * @param type The type of punishment GUI
     * @return True if either (a) the permission to access the
     * provided type of punishment GUI is blank, or (b) this
     * staff has permission to use this type of punishment GUI,
     * otherwise returns false.
     */
    private boolean staffCanAccessPunishmentGui( PunGuiType type ) {
        String perm = switch ( type ) {
            case MAIN -> throw new IllegalArgumentException( "Main GUI does not have a permission" );
            case WARN -> PunishSettings.WARN_PERMISSION.string();
            case MUTE -> PunishSettings.MUTE_PERMISSION.string();
            case BAN -> PunishSettings.BAN_PERMISSION.string();
            case IPMUTE -> PunishSettings.IPMUTE_PERMISSION.string();
            case IPBAN -> PunishSettings.IPBAN_PERMISSION.string();
        };

        return perm.isBlank() || CyberVaultUtils.hasPerms( this.staff, perm );
    }

    /**
     * @return The past tense form of a GUI type, in lowercase
     */
    private String getPastTense( PunGuiType type ) {
        if ( type.name().toLowerCase().endsWith( "e" ) ) return type.name().toLowerCase() + "d";
        else return type.name().toLowerCase() + "ed";
    }
}