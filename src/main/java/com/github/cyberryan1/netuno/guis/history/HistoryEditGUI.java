package com.github.cyberryan1.netuno.guis.history;

import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Punishment;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HistoryEditGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory GUI;
    //                      Staff name | Target UUID
    private final static HashMap<String, UUID> STAFF_TARGETS = new HashMap<>();
    //                      Staff name | Target punishment
    private final static HashMap<String, Punishment> STAFF_PUNS = new HashMap<>();
    //                         Staff name
    private final static ArrayList<String> STAFF_EDITING_LENGTH = new ArrayList<>();
    //                         Staff name
    private final static ArrayList<String> STAFF_EDITING_REASON = new ArrayList<>();

    public HistoryEditGUI( OfflinePlayer target, Player staff, int punID ) {

        Punishment workingWith;
        if ( DATA.checkPunIDExists( punID ) ) { workingWith = DATA.getPunishment( punID ); }
        // else is an ip punishment
        else { workingWith = DATA.getIPPunishment( punID ); }

        STAFF_TARGETS.remove( staff.getName() );
        STAFF_TARGETS.put( staff.getName(), target.getUniqueId() );

        STAFF_PUNS.remove( staff.getName() );
        STAFF_PUNS.put( staff.getName(), workingWith );

        String guiName = Utils.getColored( "&7Edit Punishment &6#" + punID );
        GUI = Bukkit.createInventory( null, 54, guiName );

        insertItems( staff );
    }

    public void insertItems( Player staff ) {
        // glass: everything else
        // pun info sign: 13
        // back to history list: 49
        // unpunish layout:
        //      delete punishment barrier: 31
        // no-length layout:
        //      edit reason paper: 30 || delete punishment barrier: 32
        // default:
        //      edit length clock: 29 || edit reason paper: 31
        //      delete punishment barrier: 33

        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = getBackgroundGlass();
        }

        Punishment pun = STAFF_PUNS.get( staff.getName() );
        items[13] = pun.getPunishmentAsSign();

        if ( pun.checkIsUnpunish() ) {
            items[31] = getDeleteBarrier();
        }
        else if ( pun.checkHasNoTime() ) {
            items[30] = getEditReasonPaper();
            items[32] = getDeleteBarrier();
        }
        else {
            items[29] = getEditLengthClock();
            items[31] = getEditReasonPaper();
            items[33] = getDeleteBarrier();
        }

        items[49] = getBackArrow();
        GUI.setContents( items );
    }

    private ItemStack getBackgroundGlass() {
        ItemStack glass = new ItemStack( Material.GRAY_STAINED_GLASS_PANE );
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName( "" );
        glass.setItemMeta( meta );
        return glass;
    }

    private ItemStack getEditLengthClock() {
        ItemStack clock = new ItemStack( Material.CLOCK );
        ItemMeta meta = clock.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Edit length" ) );
        clock.setItemMeta( meta );
        return clock;
    }

    private ItemStack getEditReasonPaper() {
        ItemStack paper = new ItemStack( Material.PAPER );
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Edit reason" ) );
        paper.setItemMeta( meta );
        return paper;
    }

    private ItemStack getDeleteBarrier() {
        ItemStack barrier = new ItemStack( Material.BARRIER );
        ItemMeta meta = barrier.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Delete punishment" ) );
        barrier.setItemMeta( meta );
        return barrier;
    }

    private ItemStack getBackArrow() {
        ItemStack arrow = new ItemStack( Material.ARROW );
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName( Utils.getColored( "&7Go back" ) );
        arrow.setItemMeta( meta );
        return arrow;
    }

    public void openInventory( Player player ) {
        player.openInventory( GUI );
    }

    @EventHandler
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( STAFF_TARGETS.containsKey( event.getWhoClicked().getName() ) == false ) { return; }
        Player staff = ( Player ) event.getWhoClicked();

        Punishment targetPun = STAFF_PUNS.get( staff.getName() );
        if ( event.getView().getTitle().equals( Utils.getColored( "&7Edit Punishment &6#" + targetPun.getID() ) ) == false ) { return; }

        event.setCancelled( true );
        ItemStack itemClicked = event.getCurrentItem();
        if ( itemClicked == null || itemClicked.getType().isAir() ) { return; }
        String itemName = itemClicked.getItemMeta().getDisplayName();
        if ( itemName.equals( Utils.getColored( "&7Edit length" ) ) == false
            && itemName.equals( Utils.getColored( "&7Edit reason" ) ) == false
            && itemName.equals( Utils.getColored( "&7Delete punishment" ) ) == false
            && itemName.equals( Utils.getColored( "&7Go back" ) ) == false ) { return; }

        if ( itemClicked.equals( getEditLengthClock() ) ) {
            if ( STAFF_EDITING_LENGTH.contains( event.getWhoClicked().getName() ) == false ) {
                event.getWhoClicked().closeInventory();
                int punID = STAFF_PUNS.get( event.getWhoClicked().getName() ).getID();
                event.getWhoClicked().sendMessage( Utils.getColored( "&7Please type the new length for punishment &6#" + punID ) );
                event.getWhoClicked().sendMessage( Utils.getColored( "&7To cancel, type &6\"cancel\"" ) );
                STAFF_EDITING_LENGTH.add( event.getWhoClicked().getName() );
            }
        }

        else if ( itemClicked.equals( getEditReasonPaper() ) ) {
            if ( STAFF_EDITING_REASON.contains( event.getWhoClicked().getName() ) == false ) {
                event.getWhoClicked().closeInventory();
                int punID = STAFF_PUNS.get( event.getWhoClicked().getName() ).getID();
                event.getWhoClicked().sendMessage( Utils.getColored( "&7Please type the new reason for punishment &6#" + punID ) );
                event.getWhoClicked().sendMessage( Utils.getColored( "&7To cancel, type &6\"cancel\"" ) );
                STAFF_EDITING_REASON.add( event.getWhoClicked().getName() );
            }
        }

        else if ( itemClicked.equals( getDeleteBarrier() ) ) {
            event.getWhoClicked().closeInventory();
            OfflinePlayer target = Bukkit.getOfflinePlayer( STAFF_TARGETS.get( event.getWhoClicked().getName() ) );
            Punishment pun = STAFF_PUNS.get( event.getWhoClicked().getName() );
            HistoryDeleteConfirmGUI deleteGUI = new HistoryDeleteConfirmGUI( target, staff, pun );
            deleteGUI.openInventory( staff );
            Utils.getPlugin().getServer().getPluginManager().registerEvents( deleteGUI, Utils.getPlugin() );
        }

        else if ( itemClicked.equals( getBackArrow() ) ) {
            event.getWhoClicked().closeInventory();
            OfflinePlayer target = Bukkit.getOfflinePlayer( STAFF_TARGETS.get( event.getWhoClicked().getName() ) );
            HistoryListGUI gui = new HistoryListGUI( target, staff, 1 );
            gui.openInventory( staff );
            Utils.getPlugin().getServer().getPluginManager().registerEvents( gui, Utils.getPlugin() );
        }

    }

    @EventHandler
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( STAFF_TARGETS.containsKey( event.getWhoClicked().getName() ) == false ) { return; }
        Player staff = ( Player ) event.getWhoClicked();

        Punishment targetPun = STAFF_PUNS.get( staff.getName() );
        if ( event.getView().getTitle().equals( Utils.getColored( "&7Edit Punishment &6#" + targetPun.getID() ) ) == false ) { return; }

        event.setCancelled( true );
    }

    @EventHandler
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( STAFF_TARGETS.containsKey( event.getPlayer().getName() ) ) {
            STAFF_TARGETS.remove( event.getPlayer().getName() );
            STAFF_PUNS.remove( event.getPlayer().getName() );
        }
    }

    @EventHandler
    public void onPlayerChatEvent( PlayerChatEvent event ) {
        if ( STAFF_EDITING_LENGTH.contains( event.getPlayer().getName() ) ) {
            event.setCancelled( true );

            if ( event.getMessage().equalsIgnoreCase( "cancel" ) ) {
                OfflinePlayer target = Bukkit.getOfflinePlayer( STAFF_TARGETS.get( event.getPlayer().getName() ) );
                HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), STAFF_PUNS.get( event.getPlayer().getName() ).getID() );
                newGUI.openInventory( event.getPlayer() );
                STAFF_EDITING_LENGTH.remove( event.getPlayer().getName() );
            }

            else if ( Time.isAllowableLength( event.getMessage() ) ) {
                Punishment current = STAFF_PUNS.get( event.getPlayer().getName() );
                current.setLength( Time.getTimestampFromLength( event.getMessage() ) );
                DATA.setPunishmentLength( current.getID(), current.getLength() );

                OfflinePlayer target = Bukkit.getOfflinePlayer( STAFF_TARGETS.get( event.getPlayer().getName() ) );
                HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), current.getID() );
                newGUI.openInventory( event.getPlayer() );
                STAFF_EDITING_LENGTH.remove( event.getPlayer().getName() );
            }

            else {
                CommandErrors.sendInvalidTimespan( event.getPlayer(), event.getMessage() );
                event.getPlayer().sendMessage( Utils.getColored( "&7Try again, or say &6\"cancel\"&7 to cancel" ) );
            }
        }

        else if ( STAFF_EDITING_REASON.contains ( event.getPlayer().getName() ) ) {
            event.setCancelled( true );

            if ( event.getMessage().equalsIgnoreCase( "cancel" ) == false ) {
                Punishment current = STAFF_PUNS.get( event.getPlayer().getName() );
                current.setReason( event.getMessage() );
                DATA.setPunishmentReason( current.getID(), current.getReason() );
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( STAFF_TARGETS.get( event.getPlayer().getName() ) );
            HistoryEditGUI newGUI = new HistoryEditGUI( target, event.getPlayer(), STAFF_PUNS.get( event.getPlayer().getName() ).getID() );
            newGUI.openInventory( event.getPlayer() );
            STAFF_EDITING_REASON.remove( event.getPlayer().getName() );
        }
    }
}
