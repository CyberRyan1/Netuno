package com.github.cyberryan1.netuno.guis.ipinfo;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.GUIUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class AltsListGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final int page;
    private final ArrayList<OfflinePlayer> alts;
    private final ArrayList<OfflinePlayer> punishedAlts;

    public AltsListGUI( OfflinePlayer target, Player staff, int page ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.alts = DATA.getAllAlts( target.getUniqueId().toString() );
        this.punishedAlts = DATA.getPunishedAltList( target.getUniqueId().toString() );

        String guiName = Utils.getColored( "&6" + target.getName() + "&7's alts" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();

        GUIEventManager.addEvent( this );
    }

    public void insertItems() {
        // glass: everywhere
        // alts: 10-16, 19-25, 28-34
        // back book: 47 || next book: 51
        // paper: 40
        ItemStack items[] = new ItemStack[54];
        for ( int index = 0; index < items.length; index++ ) {
            items[index] = GUIUtils.getBackgroundGlass();
        }

        int altIndex = 21 * ( page - 1 );
        int guiIndex = 10;
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 3; col++ ) {
                if ( altIndex >= alts.size() ) { break; }

                items[guiIndex] = getAltSkull( altIndex );
                guiIndex++;
                altIndex++;
            }

            guiIndex += 2;
        }

        items[40] = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&6Previous Page" ); }
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        if ( page < maxPage ) { items[51] = GUIUtils.createItem( Material.BOOK, "&6Next Page" ); }

        gui.setContents( items );
    }

    private ItemStack getAltSkull( int index ) {
        OfflinePlayer account = alts.get( index );
        ItemStack skull = new ItemStack( Material.PLAYER_HEAD );
        SkullMeta meta = ( SkullMeta ) skull.getItemMeta();
        meta.setOwningPlayer( account );
        skull.setItemMeta( meta );

        if ( punishedAlts.contains( account ) ) {
            skull = GUIUtils.setItemName( skull, "&c" + account.getName() );
            ArrayList<String> lore = new ArrayList<>();
            ArrayList<Punishment> accountPuns = DATA.getAllActivePunishments( account.getUniqueId().toString() );

            for ( Punishment pun : accountPuns ) {
                if ( pun.getType().equalsIgnoreCase( "mute" ) ) { lore.add( Utils.getColored( "&8- &7Muted" ) ); }
                else if ( pun.getType().equalsIgnoreCase( "ban" ) ) { lore.add( Utils.getColored( "&8- &7Banned" ) ); }
                else if ( pun.getType().equalsIgnoreCase( "ipmute" ) ) { lore.add( Utils.getColored( "&8- &7IP Muted" ) ); }
                else if ( pun.getType().equalsIgnoreCase( "ipban" ) ) { lore.add( Utils.getColored( "&8- &7IP Banned" ) ); }
            }
            skull = GUIUtils.setItemLore( skull, lore );
        }
        else { skull = GUIUtils.setItemName( skull, "&7" + account.getName() ); }

        return skull;
    }

    public void openInventory( Player staff ) {
        if ( alts.size() == 0 ) { CommandErrors.sendNoAltAccounts( staff, target.getName() ); }
        else { staff.openInventory( gui ); }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's alts" ) ) == false ) { return; }

        event.setCancelled( true );

        ItemStack clicked = event.getCurrentItem();
        if ( clicked == null || clicked.getType().isAir() ) { return; }

        if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&6Previous Page" ) ) ) {
            staff.closeInventory();
            AltsListGUI newGUI = new AltsListGUI( target, staff, page - 1 );
            newGUI.openInventory( staff );
            Utils.getPlugin().getServer().getPluginManager().registerEvents( newGUI, Utils.getPlugin() );
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&6Next Page" ) ) ) {
            staff.closeInventory();
            AltsListGUI newGUI = new AltsListGUI( target, staff, page + 1 );
            newGUI.openInventory( staff );
            Utils.getPlugin().getServer().getPluginManager().registerEvents( newGUI, Utils.getPlugin() );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's alts" ) ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's alts" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}