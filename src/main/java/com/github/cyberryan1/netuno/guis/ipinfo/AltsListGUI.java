package com.github.cyberryan1.netuno.guis.ipinfo;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.events.GUIEventInterface;
import com.github.cyberryan1.netuno.guis.events.GUIEventManager;
import com.github.cyberryan1.netuno.guis.events.GUIEventType;
import com.github.cyberryan1.netuno.guis.history.HistoryListGUI;
import com.github.cyberryan1.netuno.guis.utils.GUIUtils;
import com.github.cyberryan1.netuno.guis.utils.SortBy;
import com.github.cyberryan1.netuno.utils.CommandErrors;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AltsListGUI implements Listener {

    private final Database DATA = Utils.getDatabase();

    private final Inventory gui;
    private final Player staff;
    private final OfflinePlayer target;
    private final int page;
    private SortBy sort;
    private List<OfflinePlayer> alts;
    private final ArrayList<OfflinePlayer> punishedAlts;

    public AltsListGUI( OfflinePlayer target, Player staff, int page, SortBy sort ) {
        this.target = target;
        this.staff = staff;
        this.page = page;
        this.sort = sort;
        this.alts = DATA.getAllAlts( target.getUniqueId().toString() );
        this.punishedAlts = DATA.getPunishedAltList( target.getUniqueId().toString() );

        sortAlts();

        String guiName = CoreUtils.getColored( "&p" + target.getName() + "&s's alts" );
        gui = Bukkit.createInventory( null, 54, guiName );
        insertItems();

        GUIEventManager.addEvent( this );
    }

    public AltsListGUI( OfflinePlayer target, Player staff, int page ) {
        this( target, staff, page, SortBy.ALPHABETICAL );
    }

    private void sortAlts() {
        if ( sort == SortBy.ALPHABETICAL ) {
            alts = alts.stream()
                    .filter( ( alt ) -> ( alt.getName() != null ) )
                    .sorted( Comparator.comparing( OfflinePlayer::getName ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_DATE || sort == SortBy.LAST_DATE ) {
            alts = alts.stream()
                    .filter( ( alt ) -> ( alt.getName() != null ) )
                    .sorted( ( a1, a2 ) -> ( int ) (
                            sort == SortBy.FIRST_DATE ? ( a1.getFirstPlayed() - a2.getFirstPlayed() )
                                    : ( a2.getFirstPlayed() - a1.getFirstPlayed() )
                    ) )
                    .collect( Collectors.toList() );
        }

        else if ( sort == SortBy.FIRST_PUNISHED || sort == SortBy.LAST_PUNISHED ) {
            alts = alts.stream()
                    .filter( ( alt ) -> ( alt.getName() != null ) )
                    .sorted( ( a1, a2 ) -> (
                            sort == SortBy.FIRST_PUNISHED ?
                                    ( punishedAlts.contains( a1 ) == punishedAlts.contains( a2 ) ? 0 :
                                            ( punishedAlts.contains( a1 ) ? -1 : 1 ) ) :
                                    ( punishedAlts.contains( a1 ) == punishedAlts.contains( a2 ) ? 0 :
                                            ( punishedAlts.contains( a1 ) ? 1 : -1 ) )
                    ) )
                    .collect( Collectors.toList() );
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
                if ( altIndex >= alts.size() ) {
                    items[guiIndex] = GUIUtils.setItemName( GUIUtils.getColoredItemForVersion( "LIGHT_GRAY_STAINED_GLASS_PANE" ), "&f" );
                }
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

        if ( page >= 2 ) { items[47] = GUIUtils.createItem( Material.BOOK, "&pPrevious Page" ); }
        int maxPage = ( int ) Math.ceil( alts.size() / 21.0 );
        if ( page < maxPage ) { items[51] = GUIUtils.createItem( Material.BOOK, "&pNext Page" ); }

        gui.setContents( items );
    }

    private ItemStack getAlphabeticalPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( CoreUtils.getColored( "&sCurrent Sort: &pA -> Z" ) );
        lore.add( CoreUtils.getColored( "&sNext Sort: &pFirst Join -> Last Join" ) );
        lore.add( CoreUtils.getColored( "&sClick to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getFirstDatePaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( CoreUtils.getColored( "&sSort: &pFirst Join -> Last Join" ) );
        lore.add( CoreUtils.getColored( "&sNext Sort: &pLast Join -> First Join" ) );
        lore.add( CoreUtils.getColored( "&sClick to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getLastDatePaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( CoreUtils.getColored( "&sSort: &pLast Join -> First Join" ) );
        lore.add( CoreUtils.getColored( "&sNext Sort: &pPunished -> Not Punished" ) );
        lore.add( CoreUtils.getColored( "&sClick to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getFirstPunPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( CoreUtils.getColored( "&sSort: &pPunished -> Not Punished" ) );
        lore.add( CoreUtils.getColored( "&sNext Sort: &pNot Punished -> Punished" ) );
        lore.add( CoreUtils.getColored( "&sClick to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getLastPunPaper() {
        ItemStack toReturn = GUIUtils.createItem( Material.PAPER, "&sPage &p#" + page );
        ArrayList<String> lore = new ArrayList<>();
        lore.add( "" );
        lore.add( CoreUtils.getColored( "&sSort: &pNot Punished -> Punished" ) );
        lore.add( CoreUtils.getColored( "&sNext Sort: &pA -> Z" ) );
        lore.add( CoreUtils.getColored( "&sClick to change sort method" ) );
        return GUIUtils.setItemLore( toReturn, lore );
    }

    private ItemStack getAltSkull( int index ) {
        OfflinePlayer account = alts.get( index );
        ItemStack skull = GUIUtils.getPlayerSkull( account );

        if ( punishedAlts.contains( account ) ) {
            skull = GUIUtils.setItemName( skull, "&c" + account.getName() );
            ArrayList<String> lore = new ArrayList<>();
            ArrayList<Punishment> accountPuns = DATA.getAllActivePunishments( account.getUniqueId().toString() );

            for ( Punishment pun : accountPuns ) {
                if ( pun.getType().equalsIgnoreCase( "mute" ) && lore.contains( CoreUtils.getColored( "&8- &sMuted" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sMuted" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ban" ) && lore.contains( CoreUtils.getColored( "&8- &sBanned" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sBanned" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ipmute" ) && lore.contains( CoreUtils.getColored( "&8- &sIP Muted" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sIP Muted" ) );
                }
                else if ( pun.getType().equalsIgnoreCase( "ipban" ) && lore.contains( CoreUtils.getColored( "&8- &sIP Banned" ) ) == false ) {
                    lore.add( CoreUtils.getColored( "&8- &sIP Banned" ) );
                }
            }
            skull = GUIUtils.setItemLore( skull, lore );
        }
        else { skull = GUIUtils.setItemName( skull, "&s" + account.getName() ); }

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
        if ( event.getView().getTitle().equals( CoreUtils.getColored( "&p" + target.getName() + "&s's alts" ) ) == false ) { return; }

        event.setCancelled( true );
        if ( event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER ) { return; }

        ItemStack clicked = event.getCurrentItem();
        if ( clicked == null || clicked.getType() == Material.AIR ) { return; }

        if ( clicked.getItemMeta().getLore() != null && clicked.getItemMeta().getLore().size() > 0 && GUIUtils.getPlayerSkull( target ).getType() == clicked.getType() ) {
            staff.closeInventory();
            String playerName = clicked.getItemMeta().getDisplayName().replace( CoreUtils.getColored( "&c" ), "" );
            OfflinePlayer target = Bukkit.getOfflinePlayer( playerName );
            if ( target != null ) {
                HistoryListGUI gui = new HistoryListGUI( target, staff, 1 );
                gui.openInventory( staff );
                staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
            }

            else {
                CommandErrors.sendPlayerNotFound( staff, playerName );
            }
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&pPrevious Page" ) ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page - 1 );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( clicked.equals( GUIUtils.createItem( Material.BOOK, "&pNext Page" ) ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page + 1 );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "BLOCK_DISPENSER_FAIL", "NOTE_PLING" ), 10, 2 );
        }

        else if ( clicked.equals( getAlphabeticalPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.FIRST_DATE );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }

        else if ( clicked.equals( getFirstDatePaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.LAST_DATE );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }

        else if ( clicked.equals( getLastDatePaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.FIRST_PUNISHED );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }

        else if ( clicked.equals( getFirstPunPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.LAST_PUNISHED );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }

        else if ( clicked.equals( getLastPunPaper() ) ) {
            AltsListGUI newGUI = new AltsListGUI( target, staff, page, SortBy.ALPHABETICAL );
            newGUI.openInventory( staff );
            staff.playSound( staff.getLocation(), GUIUtils.getSoundForVersion( "ITEM_BOOK_PAGE_TURN", "NOTE_PLING" ), 10, 1 );
        }
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_DRAG )
    public void onInventoryDrag( InventoryDragEvent event ) {
        if ( staff.getName().equals( event.getWhoClicked().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( CoreUtils.getColored( "&p" + target.getName() + "&s's alts" ) ) == false ) { return; }
        event.setCancelled( true );
    }

    @GUIEventInterface( type = GUIEventType.INVENTORY_CLOSE )
    public void onInventoryClose( InventoryCloseEvent event ) {
        if ( staff.getName().equals( event.getPlayer().getName() ) == false ) { return; }
        if ( event.getView().getTitle().equals( CoreUtils.getColored( "&p" + target.getName() + "&s's alts" ) ) == false ) { return; }

        GUIEventManager.removeEvent( this );
    }
}