package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Some utility methods for the history GUIs
 *
 * @author Ryan
 */
public class HistoryUtils {

    /**
     * Converts the provided punishment into an itemstack that
     * displays all the information about the punishment
     *
     * @param pun The punishment to convert
     * @return The itemstack that represents the punishment
     */
    public static ItemStack getPunishmentItem( ApiPunishment pun ) {
        Material itemMaterial = Material.REDSTONE;
        if ( pun.isActive() ) { itemMaterial = Material.EMERALD; }
        if ( pun.getType().hasNoReason() ) { itemMaterial = Material.GUNPOWDER; }

        ItemStack sign = CyberItemUtils.createItem( itemMaterial, "&sPunishment &p#" + pun.getId() );

        String dateString = new java.sql.Timestamp( pun.getTimestamp() ).toGMTString();
        CyberItemUtils.addItemLore( sign,
                "&pPlayer: &s" + pun.getPlayer().getName(),
                "&pDate: &s" + dateString,
                "&pType: &s" + pun.getType().name().toUpperCase() );

        if ( pun.getType().hasNoLength() == false ) {
            CyberItemUtils.addItemLore( sign, "&pDuration: &s" + TimestampUtils.durationToString( pun.getLength() ) );
            if ( pun.isActive() ) {
                CyberItemUtils.addItemLore( sign, "&pRemaining: &s" + TimestampUtils.durationToString( pun.getDurationRemaining(), 3 ) );
            }
        }

        if ( pun.getStaffUuid() == ApiPunishment.CONSOLE_UUID ) { CyberItemUtils.addItemLore( sign, "&pStaff: &sCONSOLE" ); }
        else { CyberItemUtils.addItemLore( sign, "&pStaff: &s" + pun.getStaff().getName() ); }

        if ( pun.getType().isIpPunishment() ) {
            int originalPunishmentId = pun.getId();
            if ( pun.getReferenceId() != -1 ) { originalPunishmentId = pun.getReferenceId(); }

            CyberItemUtils.addItemLore( sign, "&pOriginal Punishment ID: &s" + originalPunishmentId );
        }

        if ( pun.getType().hasNoReason() == false ) {
            CyberItemUtils.addItemLore( sign, "&pReason: &s" + pun.getReason() );
        }

        if ( pun.isActive() ) {
            ItemMeta meta = sign.getItemMeta();
            meta.addItemFlags( ItemFlag.HIDE_ENCHANTS );
            meta.addEnchant( Enchantment.PROTECTION, 1, true );
            sign.setItemMeta( meta );
        }

        return sign;
    }

    /**
     * @return The item to use while loading data
     */
    public static ItemStack getLoadingItem() {
        return CyberItemUtils.createItem( Settings.PUNISH_LOADING_ITEM_MATERIAL.material(),
                                    Settings.PUNISH_LOADING_ITEM_NAME.coloredString() );
    }

    /**
     * @param sort The sorting used for a GUI
     * @return The correct sort hopper to display in a GUI
     */
    public static ItemStack getSortHopper( SortBy sort ) {
        if ( sort == SortBy.FIRST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pOldest -> Newest" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pNewest -> Oldest", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNewest -> Oldest" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pActive -> Not Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.FIRST_ACTIVE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pActive -> Not Active" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pNot Active -> Active", "&sClick to change sort method" );
        }

        else if ( sort == SortBy.LAST_ACTIVE ) {
            ItemStack hopper = CyberItemUtils.createItem( Material.HOPPER, "&sCurrent Sort: &pNot Active -> Active" );
            return CyberItemUtils.setItemLore( hopper, "&sNext Sort: &pOldest -> Newest", "&sClick to change sort method" );
        }

        return null;
    }
}