package com.github.cyberryan1.netuno.guis.punish.models;

import com.github.cyberryan1.cybercore.spigot.config.YmlReader;
import com.github.cyberryan1.cybercore.spigot.gui.Gui;
import com.github.cyberryan1.cybercore.spigot.gui.GuiItem;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.punish.ChangeDurationGui;
import com.github.cyberryan1.netuno.guis.punish.PunishmentGuiExecutor;
import com.github.cyberryan1.netuno.guis.punish.PunishmentSpecificGui;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a punishment button with each punish GUI (excluding
 * the main GUI). This allows easy access to the item, settings
 * related to this punishment, and executing this punishment
 *
 * @author Ryan
 */
public class SinglePunishButton {

    public static final int DEFAULT_PUNISH_AFTER = -1;

    /**
     * @return The item to display while a punishment button is loading
     */
    public static ItemStack getLoadingPlaceholderItem() {
        return CyberItemUtils.createItem(
                Settings.PUNISH_LOADING_ITEM_MATERIAL.material(), Settings.PUNISH_LOADING_ITEM_NAME.coloredString() );
    }



    private final String pathKey;
    private PunGuiType punGuiType;
    private final String buttonType;
    private final int index;
    private final String itemName;
    private final List<String> itemLore;
    private final Material itemMaterial;
    private final String startingTime;
    private final boolean autoscale;
    private final String instantKey;

    private int previousPunCount = -1;

    // Below variables only apply to warns
    //      Will be -1 or null if not applicable
    private int punishAfter;
    private ApiPunishment.PunType punishTypeAfter;

    /**
     * Initializes this punishment button with the settings
     * found in the config
     *
     * @param pathKey The path key
     * @param ymlName The name of the YML file for the settings
     */
    public SinglePunishButton( String pathKey, String ymlName ) {
        this.pathKey = pathKey;
        this.punGuiType = PunGuiType.valueOf( pathKey.substring( 0, pathKey.indexOf( "-" ) ).toUpperCase() );
        this.buttonType = pathKey.substring( pathKey.indexOf( "." ) + 1 );

        final YmlReader YML_MANAGER = YMLUtils.fromName( ymlName );

        this.index = Integer.parseInt( this.buttonType );
        this.itemName = YML_MANAGER.getStr( pathKey + ".item-name" );
        this.itemLore = List.of( YML_MANAGER.getStr( pathKey + ".item-lore" ).split( "\\\\n" ) );
        this.itemMaterial = Material.valueOf( YML_MANAGER.getStr( pathKey + ".material" ) );
        this.startingTime = YML_MANAGER.getStr( pathKey + ".starting-time" );
        this.autoscale = YML_MANAGER.getBool( pathKey + ".autoscale" );
        this.instantKey = YML_MANAGER.getStr( pathKey + ".instant-key" );

        // Below variables only apply to warns
        this.punishAfter = DEFAULT_PUNISH_AFTER;
        this.punishTypeAfter = null;
        if ( this.punGuiType == PunGuiType.WARN ) {
            this.punishAfter = YML_MANAGER.getInt( pathKey + ".punish-after" );
            this.punishTypeAfter = ApiPunishment.PunType.valueOf( YML_MANAGER.getStr( pathKey + ".punishment" ).toUpperCase() );
        }
    }

    /**
     * Gets the item with data related to the provided player
     * <b>Note</b> Everything ran by this method is done async
     *
     * @param offlinePlayer The player targeted by this punishment
     * @return An ItemStack
     */
    public CompletableFuture<ItemStack> getItem( OfflinePlayer offlinePlayer ) {
        return generatePreviousPunCount( offlinePlayer ).thenApply( count -> {
            this.previousPunCount = count;
            ItemStack toReturn = CyberItemUtils.createItem( this.itemMaterial, PunishmentGuiExecutor.replaceVariables( this.itemName, offlinePlayer, this.previousPunCount ) );

            List<String> lore = new ArrayList<>();
            for ( String str : this.itemLore ) {
                lore.add( PunishmentGuiExecutor.replaceVariables( str, offlinePlayer, this.previousPunCount ) );
            }
            toReturn = CyberItemUtils.setItemLore( toReturn, CyberColorUtils.getColored( lore ) );
            return toReturn;
        } );
    }

    public CompletableFuture<Integer> generatePreviousPunCount( OfflinePlayer offlinePlayer ) {
        return Netuno.SERVICE.getPlayer( offlinePlayer ).handleAsync( ( player, throwable ) -> {
            this.previousPunCount = ( int ) player.getPunishments().stream()
                .filter( pun -> {
                    if ( pun.isGuiPun() == false ) return false;
                    // ? in below, .lastIndexOf( " (" ) is kind of bad, as REASON_FORMAT may change
                    // ?        to not include a " (" at the end. I don't know how to fix this,
                    // ?        so it's going to stay like this for now
                    // ensuring index is still valid-- staff could have edited the reason
                    int index = pun.getReason().lastIndexOf( " (" );
                    if ( index < 0 ) return false;

                    String reason = pun.getReason().substring( 0, index );
                    String reasonForThis = CyberColorUtils.deleteColor( CyberColorUtils.getColored( this.itemName ) );
                    return reason.equalsIgnoreCase( reasonForThis );
                } )
                .count();
            return this.previousPunCount;
        } );
    }

    /**
     * Loads this punishment button into the provided GUI at the
     * selected slot. Sets the slot to the loading placeholder
     * item (see {@link #getLoadingPlaceholderItem()}) while the
     * punishment button is being loaded.
     *
     * @param gui                   The GUI to load this
     *                              punishment button into
     * @param index                 The index of the slot to load*
     * @param punishmentSpecificGui The punishment GUI this is
     *                              being executed from
     * @param loadWithClickAction   Whether or not the slot will
     *                              have a click action
     */
    public void loadIntoInventory( final Gui gui, final int index, PunishmentSpecificGui punishmentSpecificGui, boolean loadWithClickAction ) {
        // temporarily setting the item slot to the loading item
        gui.addItem( new GuiItem( getLoadingPlaceholderItem(), index ) );

        // loading the button
        CompletableFuture<Void> future = this.getItem( punishmentSpecificGui.getTarget() ).thenAccept( itemstack -> {
            GuiItem item = new GuiItem( itemstack, index, i -> {
                if ( i.getEvent().getAction() == InventoryAction.PICKUP_HALF ) { // left click
                    // * pretty sure we can safely assume that this.previousPunCount has been loaded by this point

                    // if the punishment GUI's type is for warns and the target hasn't met the punish after count, then we
                    //      dont open the gui
                    if ( this.punGuiType == PunGuiType.WARN && this.previousPunCount < this.punishAfter ) {
                        CyberMsgUtils.sendMsg( punishmentSpecificGui.getStaff(), "&p" + punishmentSpecificGui.getTarget().getName()
                                + " &sdoes not have enough warns to warrant a mute yet!" );
                        return;
                    }

                    ChangeDurationGui changeDurationGui = new ChangeDurationGui( punishmentSpecificGui, this, this.previousPunCount );
                    changeDurationGui.open();
                }
                else {
                    PunishmentGuiExecutor.executePunish( this, punishmentSpecificGui.getStaff(), punishmentSpecificGui.getTarget(), 1.0f, punishmentSpecificGui.isSilent() );
                    punishmentSpecificGui.getStaff().closeInventory();
                }
            } );

            if ( loadWithClickAction == false ) item.setExecuteOnClick( i -> {} ); // do nothing
            gui.addItem( item );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );

        // wait for the future to complete and then update the GUI
        future.thenRun( () -> {
            gui.updateItem( gui.getItem( index ) );
        } );
    }

    public String getPathKey() { return this.pathKey; }

    public PunGuiType getGuiType() { return this.punGuiType; }

    public String getButtonType() { return this.buttonType; }

    public int getIndex() { return this.index; }

    public String getItemName() { return this.itemName; }

    public List<String> getItemLore() { return this.itemLore; }

    public Material getItemMaterial() { return this.itemMaterial; }

    public String getStartingTime() { return this.startingTime; }

    public boolean isAutoscaleEnabled() { return this.autoscale; }

    public String getInstantKey() { return this.instantKey; }

    public int getPunishAfter() { return this.punishAfter; }

    public ApiPunishment.PunType getPunishTypeAfter() { return this.punishTypeAfter; }

    public int getPreviousPunCount() { return this.previousPunCount; }
}