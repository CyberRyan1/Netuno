package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberItemUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.time.NDate;
import com.github.cyberryan1.netunoapi.models.time.NDuration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIUtils {

    public static ItemStack getPunishmentItem( NPunishment pun ) {
        Material itemMaterial = Material.REDSTONE;
        if ( pun.isActive() ) { itemMaterial = Material.EMERALD; }
        if ( pun.getPunishmentType().hasNoReason() ) { itemMaterial = Material.GUNPOWDER; }

        ItemStack sign = CyberItemUtils.createItem( itemMaterial, "&sPunishment &p#" + pun.getId() );

        CyberItemUtils.addItemLore( sign,
                "&pPlayer: &s" + pun.getPlayer().getName(),
                "&pDate: &s" + new NDate( pun.getTimestamp() ).getDateString(),
                "&pType: &s" + pun.getPunishmentType().name().toUpperCase() );

        if ( pun.getPunishmentType().hasNoLength() == false ) {
            CyberItemUtils.addItemLore( sign, "&pLength: &s" + new NDuration( pun.getLength() ).formatted() );
        }

        if ( pun.getStaffUuid().equalsIgnoreCase( "console" ) ) { CyberItemUtils.addItemLore( sign, "&pStaff: &sCONSOLE" ); }
        else { CyberItemUtils.addItemLore( sign, "&pStaff: &s" + pun.getStaff().getName() ); }

        if ( pun.getPunishmentType().isIpPunishment() ) {
            int originalPunishmentId = pun.getId();
            if ( pun.getReferencePunId() != -1 ) { originalPunishmentId = pun.getReferencePunId(); }

            CyberItemUtils.addItemLore( sign, "&pOriginal Punishment ID: &s" + originalPunishmentId );
        }

        if ( pun.getPunishmentType().hasNoReason() == false ) {
            CyberItemUtils.addItemLore( sign, "&pReason: &s" + pun.getReason() );
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