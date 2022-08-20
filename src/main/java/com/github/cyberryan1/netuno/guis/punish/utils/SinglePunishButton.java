package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.utils.CoreItemUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.cybercore.utils.yml.YMLReadTemplate;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.managers.StaffPlayerPunishManager;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
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
    private int index;
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

    public SinglePunishButton( String pathKey, String ymlName ) {
        this.pathKey = pathKey;
        this.guiType = pathKey.substring( 0, pathKey.indexOf( "-" ) );
        this.buttonType = pathKey.substring( pathKey.indexOf( "." ) + 1 );

        final YMLReadTemplate YML_MANAGER = YMLUtils.fromName( ymlName );

        this.index = Integer.parseInt( this.buttonType );
        this.itemName = YML_MANAGER.getStr( pathKey + ".item-name" );
        this.itemLore = YML_MANAGER.getStr( pathKey + ".item-lore" );
        this.itemMaterial = Material.valueOf( YML_MANAGER.getStr( pathKey + ".material" ) );
        this.startingTime = YML_MANAGER.getStr( pathKey + ".starting-time" );
        this.autoscale = YML_MANAGER.getBool( pathKey + ".autoscale" );

        // Below variables only apply to warns
        this.punishAfter = -1;
        this.punishTypeAfter = null;
        if ( this.guiType.equalsIgnoreCase( "warn" ) ) {
            this.punishAfter = YML_MANAGER.getInt( pathKey + ".punish-after" );
            this.punishTypeAfter = YML_MANAGER.getStr( pathKey + ".punishment" );
        }
    }

    public String getPathKey() { return this.pathKey; }

    public String getButtonType() { return this.buttonType; }

    public int getIndex() { return this.index; }

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
                .filter( pun -> {
                    if ( pun.isGuiPun() == false || pun.getReason().contains( " (" ) == false ) { return false; }
                    String reason = pun.getReason().substring( 0, pun.getReason().lastIndexOf( " (" ) );
                    String thisReason = CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) );
                    return reason.equalsIgnoreCase( thisReason );
                } )
                .count();

        ItemStack toReturn = CoreItemUtils.createItem( this.itemMaterial, replaceVariables( this.itemName, target, this.previousPunCount ) );
        return CoreItemUtils.setItemLore( toReturn, replaceVariables( this.itemLore, target, this.previousPunCount ) );
    }

    public void executePunish( Player staff, OfflinePlayer player ) {
        final NetunoPrePunishment prePun = new NetunoPrePunishment();
        prePun.getPunishment().setPlayer( player );
        prePun.getPunishment().setStaff( staff );
        prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
        prePun.getPunishment().setGuiPun( true );

        String reason = CoreUtils.removeColor( CoreUtils.getColored( this.itemName ) );
        reason += " (" + Utils.formatIntIntoAmountString( this.previousPunCount + 1 ) + " Offense)";
        if ( StaffPlayerPunishManager.getStaffSilent( staff ) ) { reason += " -s"; }
        prePun.getPunishment().setReason( reason );

        if ( this.guiType.equalsIgnoreCase( "warn" ) ) {
            if ( this.previousPunCount < this.punishAfter ) {
                prePun.getPunishment().setActive( false );
                prePun.getPunishment().setPunishmentType( PunishmentType.WARN );
                prePun.getPunishment().setLength( 0 );
            }

            else {
                prePun.getPunishment().setPunishmentType( PunishmentType.fromString( this.punishTypeAfter ) );
                prePun.getPunishment().setActive( false );
                if ( prePun.getPunishment().getPunishmentType().hasNoLength() == false ) {
                    NDuration length = TimeUtils.durationFromUnformatted( this.startingTime );
                    if ( this.autoscale ) {
                        length = TimeUtils.getScaledDuration(
                                length,
                                2,
                                this.previousPunCount - this.punishAfter + 1
                        );
                    }

                    prePun.getPunishment().setLength( length.timestamp() );
                    prePun.getPunishment().setActive( true );
                }
            }
        }

        else {
            prePun.getPunishment().setPunishmentType( PunishmentType.fromString( this.guiType ) );

            if ( prePun.getPunishment().getPunishmentType() == PunishmentType.IPMUTE && this.startingTime.equalsIgnoreCase( "HIGHEST_MUTED_ALT" ) ) {
                NPunishment highest = null;
                for ( OfflinePlayer alt : ApiNetuno.getData().getNetunoAlts().getAlts( prePun.getPunishment().getPlayer() ) ) {
                    NPlayer nAlt = NetunoPlayerCache.getOrLoad( alt );
                    for ( NPunishment altPun : nAlt.getPunishments() ) {
                        if ( altPun.isActive() && altPun.getPunishmentType() == PunishmentType.MUTE ) {
                            if ( highest == null || altPun.getLength() > highest.getLength() ) {
                                highest = altPun;
                            }
                        }
                    }
                }

                if ( highest == null ) {
                    CoreUtils.sendMsg( staff, "&p" + prePun.getPunishment().getPlayer().getName() + "&7 has no alts with active mutes" );
                    return;
                }

                prePun.getPunishment().setLength( highest.getLength() );
            }

            else if ( prePun.getPunishment().getPunishmentType() == PunishmentType.IPBAN && this.startingTime.equalsIgnoreCase( "HIGHEST_BANNED_ALT" )  ) {
                NPunishment highest = null;
                for ( OfflinePlayer alt : ApiNetuno.getData().getNetunoAlts().getAlts( prePun.getPunishment().getPlayer() ) ) {
                    NPlayer nAlt = NetunoPlayerCache.getOrLoad( alt );
                    for ( NPunishment altPun : nAlt.getPunishments() ) {
                        if ( altPun.isActive() && altPun.getPunishmentType() == PunishmentType.BAN ) {
                            if ( highest == null || altPun.getLength() > highest.getLength() ) {
                                highest = altPun;
                            }
                        }
                    }
                }

                if ( highest == null ) {
                    CoreUtils.sendMsg( staff, "&p" + prePun.getPunishment().getPlayer().getName() + "&7 has no alts with active bans" );
                    return;
                }

                prePun.getPunishment().setLength( highest.getLength() );
            }

            else {
                NDuration length = TimeUtils.durationFromUnformatted( this.startingTime );
                if ( this.autoscale ) {
                    length = TimeUtils.getScaledDuration(
                            length,
                            2,
                            this.previousPunCount - this.punishAfter
                    );
                }
                prePun.getPunishment().setLength( length.timestamp() );
            }

            prePun.getPunishment().setActive( true );
        }

        staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
        prePun.executePunishment();
    }

    private String replaceVariables( String str, OfflinePlayer target, int punCount ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", punCount + "" );
    }

}