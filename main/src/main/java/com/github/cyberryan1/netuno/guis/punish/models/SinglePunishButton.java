package com.github.cyberryan1.netuno.guis.punish.models;

import com.github.cyberryan1.cybercore.spigot.config.YmlReader;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.punish.PunishmentGuiExecutor;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a punishment button with each punish GUI (excluding
 * the main GUI). This allows easy access to the item, settings
 * related to this punishment, and executing this punishment
 *
 * @author Ryan
 */
public class SinglePunishButton {

//    private static final String REASON_FORMAT = "[REASON] ([NUMBER] Offense)";
//    private static final String HIGHEST_MUTED_ALT_CONFIG_VARIABLE = "HIGHEST_MUTED_ALT";
//    private static final String HIGHEST_BANNED_ALT_CONFIG_VARIABLE = "HIGHEST_BANNED_ALT";
//    private static final String LENGTH_REMAINING_CONFIG_VARIABLE = "LENGTH_REMAINING";

    private String pathKey;
    private PunGuiType punGuiType;

    private String buttonType;
    private int index;
    private String itemName;
    private String itemLore;
    private Material itemMaterial;
    private String startingTime;
    private boolean autoscale;
    private String instantKey;
    private int previousPunCount;

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
        this.itemLore = YML_MANAGER.getStr( pathKey + ".item-lore" );
        this.itemMaterial = Material.valueOf( YML_MANAGER.getStr( pathKey + ".material" ) );
        this.startingTime = YML_MANAGER.getStr( pathKey + ".starting-time" );
        this.autoscale = YML_MANAGER.getBool( pathKey + ".autoscale" );
        this.instantKey = YML_MANAGER.getStr( pathKey + ".instant-key" );

        // Below variables only apply to warns
        this.punishAfter = -1;
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
            toReturn = CyberItemUtils.setItemLore( toReturn, PunishmentGuiExecutor.replaceVariables( this.itemLore, offlinePlayer, this.previousPunCount ) );
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
                    CyberMsgUtils.broadcast( "&3reason.equalsIgnoreCase( reasonForThis ) == " + ( reason.equalsIgnoreCase( reasonForThis ) ? "true" : "false" ) ); // ! debug
                    return reason.equalsIgnoreCase( reasonForThis );
                } )
                .count();
            return this.previousPunCount;
        } );
    }

    public String getPathKey() { return this.pathKey; }

    public PunGuiType getGuiType() { return this.punGuiType; }

    public String getButtonType() { return this.buttonType; }

    public int getIndex() { return this.index; }

    public String getItemName() { return this.itemName; }

    public String getItemLore() { return this.itemLore; }

    public Material getItemMaterial() { return this.itemMaterial; }

    public String getStartingTime() { return this.startingTime; }

    public boolean isAutoscaleEnabled() { return this.autoscale; }

    public String getInstantKey() { return this.instantKey; }

    public int getPunishAfter() { return this.punishAfter; }

    public ApiPunishment.PunType getPunishTypeAfter() { return this.punishTypeAfter; }

    public int getPreviousPunCount() { return this.previousPunCount; }
}