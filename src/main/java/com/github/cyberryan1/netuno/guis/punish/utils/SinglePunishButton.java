package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.api.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.models.NetunoPrePunishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SinglePunishButton {

    private String pathKey;
    private String guiType;

    private String buttonType;
    private String itemName;
    private String itemLore;
    private Material itemMaterial;
    private String startingTime;
    private boolean autoscale;
    private int previousPunCount;

    // Below variables only apply to warns
    //      Will be -1 or null if not applicable
    private int punishAfter;
    private String punishTypeAfter;

    public SinglePunishButton( String pathKey ) {
        this.pathKey = pathKey;
        this.guiType = pathKey.substring( 0, pathKey.indexOf( "-" ) );
        this.buttonType = pathKey.substring( pathKey.indexOf( "." ) + 1 );

        this.itemName = YMLUtils.getConfig().getStr( pathKey + ".item-name" );
        this.itemLore = YMLUtils.getConfig().getStr( pathKey + ".item-lore" );
        this.itemMaterial = Material.valueOf( YMLUtils.getConfig().getStr( pathKey + ".material" ) );
        this.startingTime = YMLUtils.getConfig().getStr( pathKey + ".starting-time" );
        this.autoscale = YMLUtils.getConfig().getBool( pathKey + ".autoscale" );

        // Below variables only apply to warns
        this.punishAfter = -1;
        this.punishTypeAfter = null;
        if ( this.guiType.equalsIgnoreCase( "warn" ) ) {
            this.punishAfter = YMLUtils.getConfig().getInt( pathKey + ".punish-after" );
            this.punishTypeAfter = YMLUtils.getConfig().getStr( pathKey + ".punishment" );
        }
    }

    public String getPathKey() { return this.pathKey; }

    public String getButtonType() { return this.buttonType; }

    public String getItemName() { return this.itemName; }

    public String getItemLore() { return this.itemLore; }

    public Material getItemMaterial() { return this.itemMaterial; }

    public String getStartingTime() { return this.startingTime; }

    public boolean getAutoscale() { return this.autoscale; }

    public int getPunishAfter() { return this.punishAfter; }

    public String getPunishTypeAfter() { return this.punishTypeAfter; }

    public ItemStack getItem( OfflinePlayer target ) {
        NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( target.getUniqueId().toString() );
        this.previousPunCount = ( int ) nPlayer.getPunishments().stream()
                .filter( pun -> pun.isGuiPun() && pun.getReason().contains( CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) ) ) )
                .count();

        ItemStack toReturn = CoreGUIUtils.createItem( this.itemMaterial, replaceVariables( this.itemName, target, this.previousPunCount ) );
        return CoreGUIUtils.setItemLore( toReturn, replaceVariables( this.itemLore, target, this.previousPunCount ) );
    }

    public void executePunish( Player staff, OfflinePlayer player ) {
        final NetunoPrePunishment pun = new NetunoPrePunishment();
        pun.setPlayer( player );
        pun.setStaff( staff );
        pun.setTimestamp( TimeUtils.getCurrentTimestamp() );
        pun.setGuiPun( true );

        String reason = CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) );
        String offense = " (" + Utils.formatIntIntoAmountString( this.previousPunCount + 1 ) + " Offense)";
        pun.setReason( reason + offense );

        if ( this.guiType.equalsIgnoreCase( "warn" ) ) {
            if ( this.previousPunCount < this.punishAfter ) {
                pun.setActive( false );
                pun.setPunishmentType( PunishmentType.WARN );
                pun.setLength( 0 );
            }

            else {
                pun.setPunishmentType( PunishmentType.valueOf( this.punishTypeAfter ) );
                pun.setActive( pun.getPunishmentType().hasNoLength() == false );
                if ( pun.isActive() ) {
                    NDuration length = TimeUtils.durationFromUnformatted( this.startingTime );
                    if ( this.autoscale ) {
                        length = TimeUtils.getScaledDuration(
                                length,
                                2,
                                this.previousPunCount + 1 - this.punishAfter
                        );
                    }

                    pun.setLength( length.timestamp() );
                }
            }
        }

        else {
            pun.setPunishmentType( PunishmentType.valueOf( this.guiType ) );

            NDuration length = TimeUtils.durationFromUnformatted( this.startingTime );
            if ( this.autoscale ) {
                length = TimeUtils.getScaledDuration(
                        length,
                        2,
                        this.previousPunCount + 1 - this.punishAfter
                );
            }
            pun.setLength( length.timestamp() );
        }

        staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
        pun.executePunishment();
    }

    private String replaceVariables( String str, OfflinePlayer target, int punCount ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", punCount + "" );
    }

}