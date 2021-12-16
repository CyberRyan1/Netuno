package com.github.cyberryan1.netuno.guis.punish;

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

public class WarnPunishGUI {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final String guiName;

    private int guiSize;
    private ArrayList<String> reasons;

    public WarnPunishGUI( Player staff, OfflinePlayer target ) {
        this.staff = staff;
        this.target = target;

        setReasons();
        setGuiSize();

        this.guiName = PunishGUIUtils.getColoredStr( "warn.inventory_name" ).replace( "[TARGET]", target.getName() );
        this.gui = Bukkit.createInventory( null, this.guiSize, this.guiName );
        insertItems();
    }

    private void setReasons() {
        this.reasons = PunishGUIUtils.getKeys( "warn." );
        for ( int index = reasons.size() - 1; index >= 0; index-- ) {
            if ( reasons.get( index ).equals( "warn.inventory_name" ) || reasons.get( index ).equals( "warn.permission" ) ) {
                reasons.remove( index );
            }
            else {
                reasons.set( index, reasons.get( index ).replace( "warn.", "" ) );
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
        ItemStack items[] = new ItemStack[this.guiSize];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        int punIndex = 0;
        int guiIndex = 10;
        for ( int row = 0; row < ( int ) Math.ceil( guiSize / 9.0 ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( punIndex >= reasons.size() ) { break; }
                String path = "warn." + reasons.get( punIndex );
                Material material = Material.matchMaterial( PunishGUIUtils.getStr( path + ".material" ) );
                if ( material.isAir() == false ) {
                    int punCount = DATA.getGUIPunCount( target, "warn", reasons.get( punIndex ) );
                    String name = PunishGUIUtils.getColoredStr( path + ".item-name" );
                    name = PunishGUIUtils.replaceVariables( name, target, punCount );
                    ItemStack toAdd = GUIUtils.createItem( material, name );

                    String lore = PunishGUIUtils.getColoredStr( path + ".item-lore" );
                    if ( lore.equals( "" ) ) { items[guiIndex] = toAdd; }
                    else {
                        lore = PunishGUIUtils.replaceVariables( lore, target, punCount );
                        String split[] = lore.split( "\n" );
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
        if ( PunishGUIUtils.getStr( "warn.permission" ).equals( "" ) == false &&
                VaultUtils.hasPerms( staff, PunishGUIUtils.getStr( "warn.permission" ) ) == false ) {
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
        int maxWarnsBeforePunish = PunishGUIUtils.getInt( "warn." + punClickedReason + ".punish-after" );
        int currentWarns = DATA.getGUIPunCount( target, "warn", punClickedReason );

        String punishmentReason = Utils.removeColorCodes( item.getItemMeta().getDisplayName() );
        String punishmentOffense = " (" + Utils.formatIntIntoAmountString( currentWarns + 1 ) + " Offense)";
        int punID = -1;

        if ( currentWarns >= maxWarnsBeforePunish ) {
            String punishmentType = PunishGUIUtils.getStr( "warn." + punClickedReason + ".punishment" );
            String punishmentLength = Time.getScaledTime( PunishGUIUtils.getStr( "warn." + punClickedReason + ".starting-time" ),
                    ( 1 + currentWarns - maxWarnsBeforePunish ) );
            if ( punishmentType.equalsIgnoreCase( "kick" ) ||
                    punishmentType.equalsIgnoreCase( "warn" ) ) { punishmentLength = ""; }

            staff.performCommand( punishmentType + " " + target.getName() + " " + punishmentLength + " " + punishmentReason + punishmentOffense );
            staff.closeInventory();
        }

        else {
            staff.performCommand( "warn " + target.getName() + " " + punishmentReason + punishmentOffense );
            staff.closeInventory();
        }

        staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
        DATA.addGUIPun( target, "warn", punClickedReason, DATA.getMostRecentPunishmentID() );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( guiName ) == false ) { return; }

        GUIEventManager.removeEvent( this );
        StaffPlayerPunishManager.removeStaff( staff );
    }
}
