package com.github.cyberryan1.netuno.guis.punish.utils;

import com.github.cyberryan1.cybercore.spigot.config.YmlReader;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.guis.punish.managers.ActiveGuiManager;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.alts.TempUuidIpEntry;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        final YmlReader YML_MANAGER = YMLUtils.fromName( ymlName );

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
                    String thisReason = CyberColorUtils.deleteColor( CyberColorUtils.getColored( this.itemName ) );
                    return reason.equalsIgnoreCase( thisReason );
                } )
                .count();

        ItemStack toReturn = CyberItemUtils.createItem( this.itemMaterial, replaceVariables( this.itemName, target, this.previousPunCount ) );
        return CyberItemUtils.setItemLore( toReturn, replaceVariables( this.itemLore, target, this.previousPunCount ) );
    }

    public void executePunish( Player staff, OfflinePlayer player ) {
        final NetunoPrePunishment prePun = new NetunoPrePunishment();
        prePun.getPunishment().setPlayer( player );
        prePun.getPunishment().setStaff( staff );
        prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
        prePun.getPunishment().setGuiPun( true );

        String reason = CyberColorUtils.deleteColor( CyberColorUtils.getColored( this.itemName ) );
        reason += " (" + Utils.formatIntIntoAmountString( this.previousPunCount + 1 ) + " Offense)";
        prePun.getPunishment().setReason( reason );

        ActiveGuiManager.searchByStaff( staff ).ifPresent( ( gui ) -> {
            if ( gui.isSilent() ) { prePun.getPunishment().setReason( prePun.getPunishment().getReason() + " -s" ); }
        } );

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
                final boolean lengthIsRemaining = PunishSettings.IPMUTE_SETTING_HIGHEST_MUTE_LENGTH.string().equalsIgnoreCase( "LENGTH_REMAINING" );

                NPunishment highest = null;
                String ip = "";
                if ( player.isOnline() ) { ip = player.getPlayer().getAddress().getAddress().getHostAddress(); }
                else {
                    List<TempUuidIpEntry> entries = new ArrayList<>( ApiNetuno.getData().getTempAltsDatabase().queryByUuid( player.getUniqueId() ) );
                    ip = entries.get( 0 ).getIp();
                }

                for ( OfflinePlayer alt : ApiNetuno.getInstance().getAltCache().queryAccounts( ip ).stream().map( Bukkit::getOfflinePlayer ).collect( Collectors.toList() ) ) {
                    NPlayer nAlt = NetunoPlayerCache.getOrLoad( alt );
                    for ( NPunishment altPun : nAlt.getPunishments() ) {
                        if ( altPun.isActive() == false || altPun.getPunishmentType() != PunishmentType.MUTE ) { continue; }
                        if ( highest == null ) { highest = altPun; continue; }

                        if ( lengthIsRemaining == false ) {
                            if ( altPun.getLength() > highest.getLength() ) { highest = altPun; }
                        }
                        else if ( altPun.getSecondsRemaining() > highest.getSecondsRemaining() ) { highest = altPun; }
                    }
                }

                if ( highest == null ) {
                    CyberMsgUtils.sendMsg( staff, "&p" + prePun.getPunishment().getPlayer().getName() + "&7 has no alts with active mutes" );
                    return;
                }

                prePun.getPunishment().setLength( highest.getSecondsRemaining() );
            }

            else if ( prePun.getPunishment().getPunishmentType() == PunishmentType.IPBAN && this.startingTime.equalsIgnoreCase( "HIGHEST_BANNED_ALT" )  ) {
                final boolean lengthIsRemaining = PunishSettings.IPBAN_SETTING_HIGHEST_BAN_LENGTH.string().equalsIgnoreCase( "LENGTH_REMAINING" );

                NPunishment highest = null;
                String ip = "";
                if ( player.isOnline() ) { ip = player.getPlayer().getAddress().getAddress().getHostAddress(); }
                else {
                    List<TempUuidIpEntry> entries = new ArrayList<>( ApiNetuno.getData().getTempAltsDatabase().queryByUuid( player.getUniqueId() ) );
                    ip = entries.get( 0 ).getIp();
                }

                for ( OfflinePlayer alt : ApiNetuno.getInstance().getAltCache().queryAccounts( ip ).stream().map( Bukkit::getOfflinePlayer ).collect( Collectors.toList() ) ) {
                    NPlayer nAlt = NetunoPlayerCache.getOrLoad( alt );
                    for ( NPunishment altPun : nAlt.getPunishments() ) {
                        if ( altPun.isActive() == false || altPun.getPunishmentType() != PunishmentType.BAN ) { continue; }
                        if ( highest == null ) { highest = altPun; continue; }

                        if ( lengthIsRemaining == false ) {
                            if ( altPun.getLength() > highest.getLength() ) { highest = altPun; }
                        }
                        else if ( altPun.getSecondsRemaining() > highest.getSecondsRemaining() ) { highest = altPun; }
                    }
                }

                if ( highest == null ) {
                    CyberMsgUtils.sendMsg( staff, "&p" + prePun.getPunishment().getPlayer().getName() + "&7 has no alts with active bans" );
                    return;
                }

                prePun.getPunishment().setLength( highest.getSecondsRemaining() );
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