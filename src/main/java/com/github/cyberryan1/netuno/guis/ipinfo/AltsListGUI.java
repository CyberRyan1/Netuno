package com.github.cyberryan1.netuno.guis.ipinfo;

import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
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
    private SortBy sort;
    private ArrayList<OfflinePlayer> alts;
    private final ArrayList<OfflinePlayer> punishedAlts;

    public AltsListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.sort = sort;
        this.alts = DATA.getAllAlts( target.getUniqueId().toString() );
        this.punishedAlts = DATA.getPunishedAltList( target.getUniqueId().toString() );

        sortAlts();

        String guiName = Utils.getColored( "&6" + target.getName() + "&7's alts" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();

        GUIEventManager.addEvent( this );
    }

    public AltsListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.ALPHABETICAL );
    }

    private void sortAlts() {
        if ( sort == SortBy.ALPHABETICAL ) {
            ArrayList<OfflinePlayer> accounts = new ArrayList<>();
            accounts.add( alts.remove( 0 ) );
            for ( OfflinePlayer player : alts ) {
                for ( int index = 0; index < accounts.size(); index++ ) {
                    if ( player.getName().compareTo( accounts.get( index ).getName() ) < 0 ) {
                        accounts.add( index, player );
                        break;
                    }
                }

                if ( accounts.contains( player ) == false ) { accounts.add( player ); }
            }

            alts = accounts;
        }

        else if ( sort == SortBy.FIRST_DATE ) {
            ArrayList<OfflinePlayer> accounts = new ArrayList<>();
            accounts.add( alts.remove( 0 ) );
            for ( OfflinePlayer player : alts ) {
                for ( int index = 0; index < accounts.size(); index++ ) {
                    if ( player.getFirstPlayed() < accounts.get( index ).getFirstPlayed() ) {
                        accounts.add( index, player );
                        break;
                    }
                }

                if ( accounts.contains( player ) == false ) { accounts.add( player ); }
            }

            alts = accounts;
        }

        else if ( sort == SortBy.LAST_DATE ) {
            ArrayList<OfflinePlayer> accounts = new ArrayList<>();
            accounts.add( alts.remove( 0 ) );
            for ( OfflinePlayer player : alts ) {
                for ( int index = 0; index < accounts.size(); index++ ) {
                    if ( player.getFirstPlayed() > accounts.get( index ).getFirstPlayed() ) {
                        accounts.add( index, player );
                        break;
                    }
                }

                if ( accounts.contains( player ) == false ) { accounts.add( player ); }
            }

            alts = accounts;
        }

        else if ( sort == SortBy.FIRST_PUNISHED ) {
            ArrayList<OfflinePlayer> accounts = new ArrayList<>();
            accounts.addAll( punishedAlts );
            for ( OfflinePlayer player : alts ) {
                if ( accounts.contains( player ) == false ) {
                    accounts.add( player );
                }
            }

            alts = accounts;
        }

        else if ( sort == SortBy.LAST_PUNISHED ) {
            ArrayList<OfflinePlayer> accounts = new ArrayList<>();
            accounts.addAll( alts );
            for ( int index = accounts.size() - 1; index >= 0; index-- ) {
                if ( punishedAlts.contains( accounts.get( index ) ) ) { accounts.remove( index ); }
            }

            accounts.addAll( punishedAlts );
            alts = accounts;
        }
    }

    public void insertItems() {
        // dark background glass: everywhere
        // light background glass: 10-16, 19-25, 38-34
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
            for ( int col = 0; col < 7; col++ ) {
                if ( altIndex >= alts.size() ) { items[guiIndex] = GUIUtils.createItem( Material.LIGHT_GRAY_STAINED_GLASS_PANE, "&7" ); }
                else { items[guiIndex] = getAltSkull( altIndex ); }

                guiIndex++;
                altIndex++;
            }

            guiIndex += 2;
        }

        if ( sort == SortBy.ALPHABETICAL ) { items[40] = getAlphabeticalPaper(); }
        else if ( sort == SortBy.FIRST_DATE ) { items[40] = getFirstDatePaper(); }
        else if ( sort == SortBy.LAST_DATE ) { items[40] = getLastDatePaper(); }
        else if ( sort == SortBy.FIRST_PUNISHED ) { items[40] = getFirstPunPaper(); }
        else if ( sort == SortBy.LAST_PUNISHED ) { items[40] = getLastPunPaper(); }

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&6Previous Page" ); }
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        if ( page < maxPage ) { items[51] = GUIUtils.createItem( Material.BOOK, "&6Next Page" ); }

        gui.setContents( items );
    }

    private ItemStack getAlphabeticalPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( Utils.getColored( "&7Current Sort: &6A -> Z" ) );
        lore.add( Utils.getColored( "&7Next Sort: &6First Join -> Last Join" ) );
        lore.add( Utils.getColored( "&7Click to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getFirstDatePaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( Utils.getColored( "&7Sort: &6First Join -> Last Join" ) );
        lore.add( Utils.getColored( "&7Next Sort: &6Last Join -> First Join" ) );
        lore.add( Utils.getColored( "&7Click to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getLastDatePaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( Utils.getColored( "&7Sort: &6Last Join -> First Join" ) );
        lore.add( Utils.getColored( "&7Next Sort: &6Punished -> Not Punished" ) );
        lore.add( Utils.getColored( "&7Click to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getFirstPunPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( Utils.getColored( "&7Sort: &6Punished -> Not Punished" ) );
        lore.add( Utils.getColored( "&7Next Sort: &6Not Punished -> Punished" ) );
        lore.add( Utils.getColored( "&7Click to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getLastPunPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&7Page &6#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( Utils.getColored( "&7Sort: &6Not Punished -> Punished" ) );
        lore.add( Utils.getColored( "&7Next Sort: &6A -> Z" ) );
        lore.add( Utils.getColored( "&7Click to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
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
                if ( pun.getType().equalsIgnoreCase( "mute" ) && lore.contains( Utils.getColored( "&8- &7Muted" ) ) == false ) {
                    lore.add( Utils.getColored( "&8- &7Muted" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ban" ) && lore.contains( Utils.getColored( "&8- &7Banned" ) ) == false ) {
                    lore.add( Utils.getColored( "&8- &7Banned" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ipmute" ) && lore.contains( Utils.getColored( "&8- &7IP Muted" ) ) == false ) {
                    lore.add( Utils.getColored( "&8- &7IP Muted" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ipban" ) && lore.contains( Utils.getColored( "&8- &7IP Banned" ) ) == false ) {
                    lore.add( Utils.getColored( "&8- &7IP Banned" ) );
                }
            }
            skull = GUIUtils.setItemLore( skull, lore );
        }
        else { skull = GUIUtils.setItemName( skull, "&7" + account.getName() ); }

        return skull;
    }

    public void openInventory( Player staff ) {
        if ( alts.size() == 0 ) { CommandErrors.sendNoAltAccounts( staff, target.getName() ); }
        else {
            staff.openInventory( gui );
            if ( GUIEventManager.getObjects().contains( this ) == false ) { GUIEventManager.addEvent( this ); }
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLICK )
    public void onInventoryClick( InventoryClickEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( Utils.getColored( "&6" + target.getName() + "&7's alts" ) ) == false ) { return; }

        event.setCancelled( true );

        ItemStack clicked = event.getCurrentItem();
        if ( clicked == null || clicked.getType().isAir() ) { return; }

        if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&6Previous Page" ) ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page - 1 );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&6Next Page" ) ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page + 1 );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getAlphabeticalPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.FIRST_DATE );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getFirstDatePaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.LAST_DATE );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getLastDatePaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.FIRST_PUNISHED );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getFirstPunPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.LAST_PUNISHED );
            newGUI.openInventory( staff );
        }

        else if ( clicked.equals( getLastPunPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.ALPHABETICAL );
            newGUI.openInventory( staff );
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