package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.netuno.classes.CombinedReport;
import com.github.cyberryan1.netuno.classes.SingleReport;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player player;
    private final OfflinePlayer target;
    private final ArrayList<String> reasons;

    private int guiSize;

    public ReportGUI( Player player, OfflinePlayer target ) {
        this.player = player;
        this.target = target;
        this.reasons = ConfigUtils.getStrList( "report.reasons" );
        //this.guiSize = 18 + ( ( int ) ( Math.ceil( reasons.size() / 6.0 ) ) * 9 );

        setGuiSize();
        String guiName = Utils.getColored( "&gReporting " + target.getName() );
        gui = Bukkit.createInventory( null, guiSize, guiName );
        insertItems();
    }

    private void setGuiSize() {
        if ( reasons.size() <= 6 ) { this.guiSize = 36; }
        else if ( reasons.size() <= 12 ) { this.guiSize = 45; }
        else if ( reasons.size() <= 18 ) { this.guiSize = 54; }
        else { this.guiSize = 63; }
    }

    public void insertItems() {
        // background glass: everywhere
        // reset selections: 11 || submit: 15
        // reports: start at 19-21 and 23-24, every row as needed
        ItemStack items[] = new ItemStack[guiSize];
        for ( int index = 0; index < items.length; index++ ) { items[index] = GUIUtils.getBackgroundGlass(); }

        int reasonIndex = 0;
        int guiIndex = 19;
        for ( int row = 0; row < ( int ) Math.ceil( guiSize / 9.0 ); row++ ) {
            for ( int col = 0; col < 6; col++ ) {
                if ( reasonIndex >= reasons.size() ) { break; }
                if ( reasons.get( reasonIndex ).equals( "" ) == false ) {
                    items[guiIndex] = GUIUtils.createItem( Material.LIGHT_GRAY_WOOL, "&7" + reasons.get( reasonIndex ) );
                }

                if ( guiIndex % 9 == 3 ) { guiIndex++; }
                guiIndex++; reasonIndex++;
            }

            guiIndex += 2;
        }

        items[11] = GUIUtils.createItem( Material.RED_WOOL, "&cReset Selections" );
        items[15] = GUIUtils.createItem( Material.GREEN_WOOL, "&2Submit Report" );

        gui.setContents( items );
    }

    public void openInventory( Player player ) {
        if ( reasons.size() > 18 ) {
            CommandErrors.sendConfigError( player );
            Utils.logError( "CONFIG ERROR >> The list \"report.reasons\" is greater than the limit, 18. Reports will not work until this is fixed." );
        }
        else {
            player.openInventory( gui );
            GUIEventManager.addEvent( this );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( player.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReporting " + target.getName() ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack item = event.getCurrentItem();
        if ( item == null || item.getType().isAir() ) { return; }

        if ( item.getType() == Material.LIGHT_GRAY_WOOL ) {
            item.setType( Material.LIME_WOOL );
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName( meta.getDisplayName().replace( Utils.getColored( "&7" ), Utils.getColored( "&a" ) ) );
            item.setItemMeta( meta );
        }

        else if ( item.getType() == Material.LIME_WOOL ) {
            item.setType( Material.LIGHT_GRAY_WOOL );
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName( meta.getDisplayName().replace( Utils.getColored( "&a" ), Utils.getColored( "&7" ) ) );
            item.setItemMeta( meta );
        }

        else if ( item.equals( GUIUtils.createItem( Material.RED_WOOL, "&cReset Selections" ) ) ) {
            insertItems();
            openInventory( player );
        }

        else if ( item.equals( GUIUtils.createItem( Material.GREEN_WOOL, "&2Submit Report" ) ) ) {
            player.closeInventory();

            ArrayList<SingleReport> reports = new ArrayList<>();
            for ( ItemStack i : event.getClickedInventory().getContents() ) {
                if ( i.getType() == Material.LIME_WOOL ) {
                    SingleReport sr = new SingleReport();
                    sr.setReporter( player );
                    sr.setTarget( target );
                    sr.setReason( i.getItemMeta().getDisplayName().replace( Utils.getColored( "&a" ), "") );
                    reports.add( sr );
                }
            }

            if ( reports.size() == 0 ) {
                CommandErrors.sendReportNeedsOneReason( player );
                return;
            }

            String reasonText[] = new String[ reports.size() ];
            for ( int index = 0; index < reasonText.length; index++ ) {
                reasonText[index] = reports.get( index ).getReason();
            }

            String msg = ConfigUtils.getColoredStrFromList( "report.confirm-msg" );
            msg = msg.replace( "[TARGET]", target.getName() ).replace( "[REASON]", Utils.formatListIntoAmountString( reasonText ) );
            if ( msg.equals( "" ) == false ) { Utils.sendAnyMsg( player, msg ); }

            msg = ConfigUtils.getColoredStrFromList( "report.staff-msg" ).replace( "[PLAYER]", player.getName() );
            msg = msg.replace( "[TARGET]", target.getName() ).replace( "[REASON]", Utils.formatListIntoAmountString( reasonText ) );
            if ( msg.equals( "" ) == false ) {
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg );
                    }
                }
            }

            for ( SingleReport sr : reports ) {
                DATA.addReport( sr );
            }
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( player.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReporting " + target.getName() ) ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( player.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&gReporting " + target.getName() ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}