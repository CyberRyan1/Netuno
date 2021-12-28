package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.netuno.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        for ( String str : lore ) { loreLines.add( Utils.getColored( str ) ); }
        return setItemLore( item, loreLines );
    }

    public static ItemStack addItemLore( ItemStack item, String ... lore ) {
        ArrayList<String> loreLines = new ArrayList<>();
        if ( item.getItemMeta().getLore() != null ) {
            for ( String str : item.getItemMeta().getLore() ) { loreLines.add( Utils.getColored( str ) ); }
        }
        for ( String str : lore ) { loreLines.add( Utils.getColored( str ) ); }
        return setItemLore( item, loreLines );
    }

    public static ItemStack setItemName( ItemStack item, String name ) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName( Utils.getColored( name ) );
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
}
