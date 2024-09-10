package com.github.cyberryan1.netuno.guis.punish.models;

import com.github.cyberryan1.cybercore.spigot.config.YmlReader;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.punish.managers.ActiveGuiManager;
import com.github.cyberryan1.netuno.models.Punishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.PrettyStringLibrary;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Represents a punishment button with each punish GUI (excluding
 * the main GUI). This allows easy access to the item, settings
 * related to this punishment, and executing this punishment
 *
 * @author Ryan
 */
public class SinglePunishButton {

    private static final String REASON_FORMAT = "[REASON] ([NUMBER] Offense)";
    private static final String HIGHEST_MUTED_ALT_CONFIG_VARIABLE = "HIGHEST_MUTED_ALT";
    private static final String HIGHEST_BANNED_ALT_CONFIG_VARIABLE = "HIGHEST_BANNED_ALT";
    private static final String LENGTH_REMAINING_CONFIG_VARIABLE = "LENGTH_REMAINING";

    private String pathKey;
    private GuiType guiType;

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
        this.guiType = GuiType.valueOf( pathKey.substring( 0, pathKey.indexOf( "-" ) ) );
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
        if ( this.guiType == GuiType.WARN ) {
            this.punishAfter = YML_MANAGER.getInt( pathKey + ".punish-after" );
            this.punishTypeAfter = ApiPunishment.PunType.valueOf( YML_MANAGER.getStr( pathKey + ".punishment" ) );
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
        return Netuno.SERVICE.getPlayer( offlinePlayer ).handleAsync( ( player, throwable ) -> {
            this.previousPunCount = ( int ) player.getPunishments().stream()
                    .filter( pun -> {
                        if ( pun.isGuiPun() == false ) return false;
                        // ? in below, .lastIndexOf( " (" ) is kind of bad, as REASON_FORMAT may change
                        // ?        to not include a " (" at the end. I don't know how to fix this,
                        // ?        so it's going to stay like this for now
                        String reason = pun.getReason().substring( 0, pun.getReason().lastIndexOf( " (" ) );
                        String reasonForThis = CyberColorUtils.deleteColor( CyberColorUtils.getColored( this.itemName ) );
                        return reason.equalsIgnoreCase( reasonForThis );
                    } )
                    .count();

            ItemStack toReturn = CyberItemUtils.createItem( this.itemMaterial, replaceVariables( this.itemName, offlinePlayer, this.previousPunCount ) );
            return CyberItemUtils.setItemLore( toReturn, replaceVariables( this.itemLore, offlinePlayer, this.previousPunCount ) );
        } );
    }

    // TODO javadoc
    /**
     * Executes this punishment on the provided player as if
     * it was ran by the provided staff
     * <b>Note</b> Everything ran by this method is done async
     *
     * @param staff The staff member
     * @param offlinePlayer The target player
     */
    public void executePunish( Player staff, OfflinePlayer offlinePlayer ) {
        Netuno.SERVICE.getPlayer( offlinePlayer ).thenAcceptAsync( player -> {
            String reason = REASON_FORMAT.replace( "[REASON]", CyberColorUtils.deleteColor( CyberColorUtils.getColored( this.itemName ) ) );
            reason = reason.replace( "[OFFENSE]", PrettyStringLibrary.getIntegerAsAmount( this.previousPunCount + 1 ) );

            ApiPunishment.PunType punType = ApiPunishment.PunType.valueOf( this.guiType.name() );
            long duration = ApiPunishment.PUNISHMENT_NO_LENGTH;

            // Handling warns
            if ( this.guiType == GuiType.WARN ) {
                // If the amount of warns the player has had is greater then
                //      or equal to the number of warns to start punishing
                //      after, execute the higher tier punish on them
                if ( this.previousPunCount < this.punishAfter ) {
                    punType = this.punishTypeAfter;

                    // If the punishment has a length, calculate the duration
                    //      and scale it, if autoscaling is enabled for this
                    //      punishment
                    if ( punType.hasNoLength() == false ) {
                        duration = TimestampUtils.getTimestampFromUnformulatedLength( this.startingTime );
                        if ( this.autoscale ) duration = getScaledDuration();
                    }
                }
            }

            // Handling everything else
            // Else if the punishment type is an IP punishment and contains
            //      an IP punishment related config variable, handle that
            else if ( punType.isIpPunishment() && ( this.startingTime.equalsIgnoreCase( HIGHEST_MUTED_ALT_CONFIG_VARIABLE )
                        || this.startingTime.equalsIgnoreCase( HIGHEST_BANNED_ALT_CONFIG_VARIABLE ) ) ) {
                // Whether the length to punish this player for is the length remaining on the
                //      highest punishment (true) or the original length of the highest
                //      punishment (false)
                boolean highestDurationIsRemaining = isHighestPunishDurationRemaining( this.guiType, this.punishTypeAfter );

                // Getting all active punishments from all alts, provided
                //      the punishment type is equal to this.punishTypeAfter
                List<Punishment> activeAltPunishments = new ArrayList<>();
                try {
                    List<ApiPlayer> altsList = Netuno.ALT_SERVICE.getAlts( ( ApiPlayer ) player ).get();
                    for ( ApiPlayer alt : altsList ) {
                        activeAltPunishments.addAll( alt.getActivePunishments().stream()
                                .filter( pun -> pun.getType() == this.punishTypeAfter )
                                .map( pun -> ( Punishment ) pun )
                                .collect( Collectors.toList() ) );
                    }
                } catch ( InterruptedException | ExecutionException e ) {
                    throw new RuntimeException( e );
                }

                // If the player has no alts with active
                if ( activeAltPunishments.isEmpty() ) {
                    String punTypeString = this.punishTypeAfter.name().toLowerCase() + "s";
                    CyberMsgUtils.sendMsg( staff, "&p" + player.getPlayer().getName() + " &shas no alts with active " + punTypeString );
                    return;
                }

                Punishment highestPunishment = highestDurationIsRemaining
                        ? PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activeAltPunishments )
                        : PunishmentLibrary.getPunishmentWithHighestOriginalLength( activeAltPunishments );
                duration = highestPunishment.getDurationRemaining();
            }

            // Handling everything else
            else {
                // Setting the duration
                duration = TimestampUtils.getTimestampFromUnformulatedLength( this.startingTime );
                if ( this.autoscale ) duration = getScaledDuration();
            }

            // Whether to handle the punishment silently or not
            boolean silent = ActiveGuiManager.searchByStaff( staff ).get().isSilent();

            // Executing the punishment
            Netuno.PUNISHMENT_SERVICE.punishmentBuilder()
                    .setPlayer( player.getUuid() )
                    .setStaff( staff )
                    .setType( punType )
                    .setLength( duration )
                    .setReason( reason )
                    .markAsGuiPunishment( true )
                    .build()
                    .execute( silent );
            staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
        } );
    }

    /**
     * @param type The type of the GUI
     * @param punType The punishment type
     * @return True if the punish duration is set to be the punishment
     * of an alt with the longest duration remaining, false if the
     * duration is specified to be the punishment of an alt with the
     * longest length
     */
    private boolean isHighestPunishDurationRemaining( GuiType type, ApiPunishment.PunType punType ) {
        PunishSettings setting =  switch ( type ) {
            case IPMUTE -> punType == ApiPunishment.PunType.MUTE ? PunishSettings.IPMUTE_SETTING_HIGHEST_MUTE_LENGTH : PunishSettings.IPMUTE_SETTING_HIGHEST_BAN_LENGTH;
            case IPBAN -> punType == ApiPunishment.PunType.MUTE ? PunishSettings.IPBAN_SETTING_HIGHEST_MUTE_LENGTH : PunishSettings.IPBAN_SETTING_HIGHEST_BAN_LENGTH;
            default -> throw new RuntimeException();
        };
        return setting.string().equalsIgnoreCase( LENGTH_REMAINING_CONFIG_VARIABLE );
    }

    /**
     * Scales the starting time
     * <code>this.previousPunCount - this.punishAfter + 1</code>
     * times with a scale of 2
     * @return The scaled duration
     */
    private long getScaledDuration() {
        long originalDuration = TimestampUtils.getTimestampFromUnformulatedLength( this.startingTime );
        return TimestampUtils.getScaledDuration( originalDuration, 2, this.previousPunCount - this.punishAfter + 1 );
    }

    /**
     * Replaces the [TARGET] and [PREVIOUS] config settings
     * within the provided string with the correct data
     * @param str The config string
     * @param target The target
     * @param punCount The amount of previous punishments
     * @return The replaced string
     */
    private String replaceVariables( String str, OfflinePlayer target, int punCount ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", punCount + "" );
    }

    public String getPathKey() { return this.pathKey; }

    public GuiType getGuiType() { return this.guiType; }

    public String getButtonType() { return this.buttonType; }

    public int getIndex() { return this.index; }

    public String getItemName() { return this.itemName; }

    public String getItemLore() { return this.itemLore; }

    public Material getItemMaterial() { return this.itemMaterial; }

    public String getStartingTime() { return this.startingTime; }

    public boolean getAutoscale() { return this.autoscale; }

    public String getInstantKey() { return this.instantKey; }

    public int getPunishAfter() { return this.punishAfter; }

    public ApiPunishment.PunType getPunishTypeAfter() { return this.punishTypeAfter; }
}