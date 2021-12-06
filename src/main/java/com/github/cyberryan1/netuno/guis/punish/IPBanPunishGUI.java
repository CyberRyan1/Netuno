package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.managers.StaffPlayerPunishManager;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class IPBanPunishGUI {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final String guiName;

    private int guiSize;
    private ArrayList<String> reasons;

    public IPBanPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        setReasons();
        setGuiSize();

        this.guiName = PunishGUIUtils.getColoredStr( "ipban.inventory_name" ).replace( "[TARGET]", target.getName() );
        this.gui = Bukkit.createInventory( null, this.guiSize, this.guiName );
        insertItems();
    }

    private void setReasons() {
        this.reasons = PunishGUIUtils.getKeys( "ipban." );
        for ( int index = reasons.size() - 1; index >= 0; index-- ) {
            if ( reasons.get( index ).equals( "ipban.inventory_name" ) || reasons.get( index ).equals( "ipban.permission" ) ) {
                reasons.remove( index );
            }
            else {
                reasons.set( index, reasons.get( index ).replace( "ipban.", "" ) );
            }
        }
    }

    private void setGuiSize() {
        if ( reasons.size() <= 6 ) { this.guiSize = 27; }
        else if ( reasons.size() <= 12 ) { this.guiSize = 36; }
        else { this.guiSize = 45; }
    }

    public void insertItems() {
        // background glass: everywhere
        // punishments: start at 19-21 and 23-24, every row as needed
        ItemStack items[] = new ItemStack[guiSize];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        int punIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < ( int ) Math.ceil( guiSize / 9.0 ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( punIndex >= reasons.size() ) { break; }

                String path = "ipban." + reasons.get( punIndex );
                Material material = Material.matchMaterial( PunishGUIUtils.getStr( path + ".material" ) );
                if ( material.isAir() == false ) {
                    int punCount = DATA.getGUIPunCount( target, "ipban", reasons.get( punIndex ) );

                    String name = PunishGUIUtils.getColoredStr( path + ".item-name" );
                    name = PunishGUIUtils.replaceVariables( name, target, punCount );
                    ItemStack toAdd = GUIUtils.createItem( material, name );

                    String lore = PunishGUIUtils.getColoredStr( path + ".item-lore" );
                    if ( lore.equals( "" ) ) { items[guiIndex] = toAdd; }
                    else {
                        String split[] = PunishGUIUtils.replaceVariables( lore, target, punCount ).split( "\n" );
                        items[guiIndex] = GUIUtils.setItemLore( toAdd, split );
                    }
                }

                if ( guiIndex % 9 == 3 ) { guiIndex++; }
                guiIndex++; punIndex++;
            }

            guiIndex += 2;
        }

        gui.setContents( items );
    }

    public void openInventory() {
        if ( PunishGUIUtils.getStr( "ipban.permission" ).equals( "" ) == false &&
                VaultUtils.hasPerms( staff, PunishGUIUtils.getStr( "ipban.permission" ) ) == false ) {
            CommandErrors.sendInvalidPerms( staff );
            return;
        }

        if ( StaffPlayerPunishManager.getWhoPunishingTarget( target ) != null ) {
            Player otherStaff = StaffPlayerPunishManager.getWhoPunishingTarget( target );
            if ( otherStaff.equals( staff ) == false ) {
                CommandErrors.sendTargetAlreadyBeingPunished( staff, target.getName(), otherStaff.getName() );
                return;
            }
        }

        StaffPlayerPunishManager.addStaffTarget( staff, target );
        staff.openInventory( gui );
        GUIEventManager.addEvent( this );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack item = event.getCurrentItem();
        if ( item == null || item.getType().isAir() || item.equals( GUIUtils.getBackgroundGlass() ) ) { return; }
        int eventSlot = event.getSlot();

        int list[] = { 10, 11, 12, 14, 15, 16, 19, 20, 21, 23, 24, 25, 28, 29, 30, 32, 33, 34 };
        ArrayList<Integer> indexToSlot = new ArrayList<>();
        for ( int i : list ) { indexToSlot.add( i ); }

        int punClickedIndex = indexToSlot.indexOf( eventSlot );
        String punClickedReason = reasons.get( punClickedIndex );
        int currentPuns = DATA.getGUIPunCount( target, "ipban", punClickedReason );

        String punishmentReason = Utils.removeColorCodes( item.getItemMeta().getDisplayName() );
        String punishmentOffense = " (" + Utils.formatIntIntoAmountString( currentPuns + 1 ) + " Offense)";

        String punishmentLength = PunishGUIUtils.getStr( "ipban." + punClickedReason + ".starting-time" );
        if ( punishmentLength.equals( "HIGHEST_MUTED_ALT" ) || punishmentLength.equals( "HIGHEST_BANNED_ALT" ) ) {
            String punType = "mute";
            if ( punishmentLength.equals( "HIGHEST_BANNED_ALT" ) ) { punType = "ban"; }

            ArrayList<OfflinePlayer> punishedAlts = DATA.getPunishedAltsByType( target.getUniqueId().toString(), punType );

            Punishment highest = new Punishment( null, null, null, 0L, -1L, null, false );
            for ( OfflinePlayer alt : punishedAlts ) {
                for ( Punishment pun : DATA.getPunishment( alt.getUniqueId().toString(), punType, true ) ) {
                    if ( pun.getLength() >= highest.getLength() ) {
                        highest = pun;
                    }
                }
            }

            if ( highest.getLength() == -1L ) {
                CommandErrors.sendTargetHasNoPunishedAlts( staff, target.getName() );
                staff.closeInventory();
                return;
            }

            punishmentLength = Time.getUnformattedLengthFromTimestamp( highest.getLength() );
        }

        else {
            punishmentLength = Time.getScaledTime( punishmentLength, currentPuns + 1 );
        }

        staff.performCommand( "ipban " + target.getName() + " " + punishmentLength + " " + punishmentReason + punishmentOffense );
        staff.closeInventory();
        DATA.addGUIPun( target, "ipban", punClickedReason, DATA.getMostRecentPunishmentID() );
        staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.equals( event.getPlayer() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }

        GUIEventManager.removeEvent( this );
        StaffPlayerPunishManager.removeStaff( staff );
    }
}