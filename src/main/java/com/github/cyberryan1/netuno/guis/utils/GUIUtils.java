package com.github.cyberryan1.netuno.guis.utils;

import com.github.cyberryan1.cybercore.utils.CoreItemUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
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

        ItemStack sign = CoreItemUtils.createItem( itemMaterial, "&sPunishment &p#" + pun.getId() );

        CoreItemUtils.addItemLore( sign,
                "&pPlayer: &s" + pun.getPlayer().getName(),
                "&pDate: &s" + new NDate( pun.getTimestamp() ).getDateString(),
                "&pType: &s" + pun.getPunishmentType().name().toUpperCase() );

        if ( pun.getPunishmentType().hasNoLength() == false ) {
            CoreItemUtils.addItemLore( sign, "&pLength: &s" + new NDuration( pun.getLength() ).formatted() );
        }

        if ( pun.getStaffUuid().equalsIgnoreCase( "console" ) ) { CoreItemUtils.addItemLore( sign, "&pStaff: &sCONSOLE" ); }
        else { CoreItemUtils.addItemLore( sign, "&pStaff: &s" + pun.getStaff().getName() ); }

        if ( pun.getPunishmentType().isIpPunishment() ) {
            String originalPlayerName = pun.getPlayer().getName();
            int originalPunishmentId = pun.getId();

            if ( pun.getReferencePunId() != -1 ) {
                NPunishment originalPunishment = ApiNetuno.getData().getNetunoPuns().getPunishment( pun.getReferencePunId() );
                originalPlayerName = originalPunishment.getPlayer().getName();
                originalPunishmentId = originalPunishment.getId();
            }

            CoreItemUtils.addItemLore( sign, "&pOriginal Player: &s" + originalPlayerName
                    + " &p(Pun ID: &s" + originalPunishmentId + "&p)" );
        }

        if ( pun.getPunishmentType().hasNoReason() == false ) {
            CoreItemUtils.addItemLore( sign, "&pReason: &s" + pun.getReason() );
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