package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.classes.PrePunishment;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
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
        final int punCount = Utils.getDatabase().getGUIPunCount( target, this.guiType, this.buttonType );
        ItemStack toReturn = CoreGUIUtils.createItem( this.itemMaterial, replaceVariables( this.itemName, target, punCount ) );
        return CoreGUIUtils.setItemLore( toReturn, replaceVariables( this.itemLore, target, punCount ) );
    }

    public void executePunish( Player staff, OfflinePlayer target ) {
        final int punCount = Utils.getDatabase().getGUIPunCount( target, this.guiType, this.buttonType );

        if ( this.guiType.equalsIgnoreCase( "warn" ) ) {
            String reason = CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) );
            String offense = " (" + Utils.formatIntIntoAmountString( punCount + 1 ) + " Offense)";

            if ( punCount < this.punishAfter ) {
                PrePunishment pun = new PrePunishment(
                        target,
                        "warn",
                        reason + offense
                );
                pun.setStaff( staff );
                pun.executePunishment();
            }

            else {
                PrePunishment pun = new PrePunishment(
                        target,
                        this.punishTypeAfter,
                        reason + offense
                );
                pun.setStaff( staff );

                if ( this.punishTypeAfter.equalsIgnoreCase( "kick" ) == false ) {
                    String length = this.startingTime;
                    if ( this.autoscale ) { length = Time.getScaledTime( this.startingTime, punCount + 1 - this.punishAfter ); }
                    pun.setLength( length );
                }

                pun.executePunishment();
            }
        }

        else {
            String reason = CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) );
            String offense = " (" + Utils.formatIntIntoAmountString( punCount + 1 ) + " Offense)";

            PrePunishment pun = new PrePunishment(
                    target,
                    this.guiType,
                    reason + offense
            );
            pun.setStaff( staff );

            String length = this.startingTime;
            if ( this.autoscale ) { length = Time.getScaledTime( this.startingTime, punCount + 1 ); }
            pun.setLength( length );

            pun.executePunishment();
        }

        staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
        Utils.getDatabase().addGUIPun( target, this.guiType, this.buttonType, Utils.getDatabase().getMostRecentPunishmentID() );
    }

    private String replaceVariables( String str, OfflinePlayer target, int punCount ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", punCount + "" );
    }

}