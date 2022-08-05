package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.cybercore.utils.CoreGUIUtils;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.time.NDate;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GUIUtils {

    public static ItemStack setItemLore( ItemStack item, ArrayList<String> lore ) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore( lore );
        item.setItemMeta( meta );
        return item;
    }

    public static ItemStack setItemLore( ItemStack item, String ... lore ) {
        ArrayList<String> loreLines = new ArrayList<>();
        for ( String str : lore ) { loreLines.add( CoreUtils.getColored( str ) ); }
        return setItemLore( item, loreLines );
    }

    public static ItemStack addItemLore( ItemStack item, String ... lore ) {
        ArrayList<String> loreLines = new ArrayList<>();
        if ( item.getItemMeta().getLore() != null ) {
            for ( String str : item.getItemMeta().getLore() ) { loreLines.add( CoreUtils.getColored( str ) ); }
        }
        for ( String str : lore ) { loreLines.add( CoreUtils.getColored( str ) ); }
        return setItemLore( item, loreLines );
    }

    public static ItemStack setItemName( ItemStack item, String name ) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName( CoreUtils.getColored( name ) );
        item.setItemMeta( meta );
        return item;
    }

    public static ItemStack getBackgroundGlass() {
        if ( Bukkit.getServer().getVersion().contains ( "1.8" ) ) {
            return setItemName( new ItemStack( Material.matchMaterial( "STAINED_GLASS_PANE" ), 1, ( short ) 7 ), "&7" );
        }
        return setItemName( new ItemStack( Material.matchMaterial( "GRAY_STAINED_GLASS_PANE" ) ), "&7" );
    }

    public static ItemStack createItem( Material material, String name ) {
        return setItemName( new ItemStack( material ), name );
    }

    public static ItemStack getPlayerSkull( OfflinePlayer player ) {
        boolean isNewVersion = Arrays.stream( Material.values() )
                .map( Material::name ).collect( Collectors.toList() ).contains( "PLAYER_HEAD" );

        Material type = Material.matchMaterial( isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM" );
        ItemStack item = new ItemStack( type, 1 );

        if ( isNewVersion == false ) {
            item.setDurability( ( short ) 3 );
        }

        SkullMeta meta = ( SkullMeta ) item.getItemMeta();
        meta.setOwner( player.getName() );

        item.setItemMeta( meta );
        return item;
    }

    public static ItemStack getItemIfNotOneDotEight( String materialName ) {
        if ( Bukkit.getServer().getVersion().contains ( "1.8" ) ) {
            return new ItemStack( Material.AIR );
        }
        return new ItemStack( Material.matchMaterial( materialName ) );
    }

    public static ItemStack getAnyMaterial( String materialName ) {
        return new ItemStack( Material.matchMaterial( materialName ) );
    }

    public static ItemStack getItemForVersion( String newerMaterial, String olderMaterial ) {
        if ( Bukkit.getServer().getVersion().contains ( "1.8" ) ) {
            return new ItemStack( Material.matchMaterial( olderMaterial ) );
        }
        return new ItemStack( Material.matchMaterial( newerMaterial ) );
    }

    public static ItemStack getItemForVersion( String newerMaterial, String olderMaterial, int olderMaterialMeta ) {
        if ( Utils.getServerVersion() == 8 ) {
            return new ItemStack( Material.matchMaterial( olderMaterial), 1, ( short ) olderMaterialMeta );
        }
        return new ItemStack( Material.matchMaterial( newerMaterial ) );
    }

    public static boolean checkItemEqualsForVersion( ItemStack checking, String newerMaterial, String olderMaterial, int olderMaterialMeta ) {
        if ( Utils.getServerVersion() == 8 ) {
            if ( Material.matchMaterial( olderMaterial ) == checking.getType() ) {
                if ( checking.getDurability() == olderMaterialMeta ) {
                    return true;
                }
            }
            return false;
        }
        return checking.getType() == Material.matchMaterial( newerMaterial );
    }

    public static Sound getSoundForVersion( String newerSound, String olderSound ) {
        if ( Bukkit.getServer().getVersion().contains( "1.8" ) ) {
            return Sound.valueOf( olderSound );
        }
        return Sound.valueOf( newerSound );
    }

    public static boolean isColorable( String material ) {
        if ( Utils.getServerVersion() > 8 ) { return false; }
        return ( material.toLowerCase().contains( "wool" ) || material.toLowerCase().contains( "terracotta" )
                || material.toLowerCase().contains( "glass" ) || material.toLowerCase().contains( "carpet" ) );
    }

    public static ItemStack getColoredItemForVersion( String material ) {
        if ( Utils.getServerVersion() >= 10 ) { return new ItemStack( Material.matchMaterial( material ) ); }
        String baseType = "WOOL";
        String baseTypeStr = "_WOOL";
        if ( material.toUpperCase().contains( "_GLASS_PANE" ) ) { baseType = "STAINED_GLASS_PANE"; baseTypeStr = "_STAINED_GLASS_PANE"; }
        else if ( material.toUpperCase().contains( "_GLASS" ) ) { baseType = "STAINED_GLASS"; baseTypeStr = "_GLASS"; }
        else if ( material.toUpperCase().contains( "STAINED_CLAY" ) ) { baseType = "STAINED_CLAY"; baseTypeStr = "_TERRACOTTA"; }
        else if ( material.toUpperCase().contains( "CARPET" ) ) { baseType = "CARPET"; baseTypeStr = "_CARPET"; }

        if ( material.equalsIgnoreCase( "WHITE" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 0 ); }
        if ( material.equalsIgnoreCase( "ORANGE" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 1 ); }
        if ( material.equalsIgnoreCase( "MAGENTA" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 2 ); }
        if ( material.equalsIgnoreCase( "LIGHT_BLUE" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 3 ); }
        if ( material.equalsIgnoreCase( "YELLOW" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 4 ); }
        if ( material.equalsIgnoreCase( "LIME" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 5 ); }
        if ( material.equalsIgnoreCase( "PINK" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 6 ); }
        if ( material.equalsIgnoreCase( "GRAY" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 7 ); }
        if ( material.equalsIgnoreCase( "LIGHT_GRAY" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 8 ); }
        if ( material.equalsIgnoreCase( "CYAN" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 9 ); }
        if ( material.equalsIgnoreCase( "PURPLE" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 10 ); }
        if ( material.equalsIgnoreCase( "BLUE" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 11 ); }
        if ( material.equalsIgnoreCase( "BROWN" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 12 ); }
        if ( material.equalsIgnoreCase( "GREEN" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 13 ); }
        if ( material.equalsIgnoreCase( "RED" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 14 ); }
        if ( material.equalsIgnoreCase( "BLACK" + baseTypeStr ) ) { return new ItemStack( Material.matchMaterial( baseType ), 1, ( short ) 15 ); }
        return null;
    }

    public static ItemStack getPunishmentItem( NPunishment pun ) {
        Material itemMaterial = Material.REDSTONE;
        if ( pun.isActive() ) { itemMaterial = Material.EMERALD; }
        if ( pun.getPunishmentType().hasNoReason() ) { itemMaterial = Material.GUNPOWDER; }

        ItemStack sign = CoreGUIUtils.createItem( itemMaterial, "&sPunishment &p#" + pun.getId() );

        CoreGUIUtils.addItemLore( sign, "&pPlayer: &s" + pun.getPlayer().getName() );
        CoreGUIUtils.addItemLore( sign, "&pDate: &s" + new NDate( pun.getTimestamp() ).getDateString() );
        CoreGUIUtils.addItemLore( sign, "&pType: &s" + pun.getPunishmentType().name().toUpperCase() );

        if ( pun.getPunishmentType().hasNoLength() == false ) {
            CoreGUIUtils.addItemLore( sign, "&pLength: &s" + new NDuration( pun.getLength() ).formatted() );
        }

        if ( pun.getStaffUuid().equalsIgnoreCase( "console" ) ) { CoreGUIUtils.addItemLore( sign, "&pStaff: &sCONSOLE" ); }
        else { CoreGUIUtils.addItemLore( sign, "&pStaff: &s" + pun.getStaff().getName() ); }

        if ( pun.getPunishmentType().isIpPunishment() ) {
            String originalPlayerName = pun.getPlayer().getName();
            int originalPunishmentId = pun.getId();

            if ( pun.getReferencePunId() != -1 ) {
                NPunishment originalPunishment = ApiNetuno.getData().getNetunoPuns().getPunishment( pun.getReferencePunId() );
                originalPlayerName = originalPunishment.getPlayer().getName();
                originalPunishmentId = originalPunishment.getId();
            }

            CoreGUIUtils.addItemLore( sign, "&pOriginal Player: &s" + originalPlayerName
                    + " &p(Pun ID: &s" + originalPunishmentId + "&p)" );
        }

        if ( pun.getPunishmentType().hasNoReason() == false ) {
            CoreGUIUtils.addItemLore( sign, "&pReason: &s" + pun.getReason() );
        }

        if ( pun.isActive() ) {
            ItemMeta meta = sign.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            meta.addEnchant( Enchantment.DURABILITY, 1, true );
            sign.setItemMeta( meta );
        }

        return sign;
    }
}